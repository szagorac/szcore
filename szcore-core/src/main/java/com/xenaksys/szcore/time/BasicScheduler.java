package com.xenaksys.szcore.time;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.MusicTask;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.TaskId;
import com.xenaksys.szcore.model.TaskResult;
import com.xenaksys.szcore.model.TimeEventListener;
import com.xenaksys.szcore.model.Timer;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.TriggerTask;
import com.xenaksys.szcore.task.TaskIdImpl;
import com.xenaksys.szcore.task.TaskResultImpl;
import com.xenaksys.szcore.util.ThreadUtil;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BasicScheduler implements Scheduler {
    static final Logger LOG = LoggerFactory.getLogger(BasicScheduler.class);

    private final ConcurrentLinkedQueue<MusicTask> inQueue = new ConcurrentLinkedQueue<>();
    private final Clock clock;
    private final Timer timer;
    private final ProcessTask processTask = new ProcessTask();
    private final TLongArrayList processPlayTimes = new TLongArrayList();
    private final TLongObjectMap<List<MusicTask>> playTimeTasks = new TLongObjectHashMap<>();
    private final ConcurrentHashMap<Id, Transport> transports = new ConcurrentHashMap<>();
    private final int taskLatencyToleranceMillis = 10;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private TLongArrayList futurePlayTimes = new TLongArrayList();
    private TaskResultImpl taskResult = new TaskResultImpl();
    private String errorString = Consts.RESULT_OK;
    private TaskId taskId = new TaskIdImpl();
    private volatile long sleepMillis = 0L;

    private boolean isCollectExecInfo = true;
    private TreeSet<ExecInfo> execInfos = new TreeSet<>();
    private ExecInfo currentExecInfo = null;


    public BasicScheduler(Clock clock, Timer timer) {
        this.clock = clock;
        this.timer = timer;
        init();
    }

    public BasicScheduler(Clock clock, Timer timer, ExecutorService executor) {
        this.clock = clock;
        this.timer = timer;
        this.executor = executor;
        init();
    }

    @Override
    public void init() {
        TimerListener timerListener = new TimerListener();
        timer.registerListener(timerListener);
    }

    @Override
    public void start() {
        if(isCollectExecInfo){
            LOG.info("Clear Exec Times: ");
            execInfos.clear();
        }
        //process queued events before start
        timer.init();
        timer.start();
    }

    @Override
    public void addTransport(Transport transport) {
        transports.put(transport.getId(), transport);
    }

    @Override
    public void onTransportStopped(Id transportId) {
        boolean isStop = true;
        for(Transport transport : transports.values()){
            if(transport.isRunning()){
                isStop = false;
            }
        }

        if(isStop){
            LOG.info("Stopping scheduler:");
            stop();
        }
    }

    @Override
    public void stop() {
        timer.stop();
        while(isActive()){
            try {
                LOG.info("Waiting for timer: ...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Failed to sleep", e);
            }
        }
        LOG.info("Scheduler Stopped. ");
        if(isCollectExecInfo){
            LOG.info("Exec Times: ");
            for(ExecInfo info : execInfos){
                info.log();
            }
        }
    }

    @Override
    public boolean isActive() {
        return timer.isActive();
    }

    @Override
    public void setElapsedTimeMillis(long elapsedTimeMillis) {
        boolean isActive = timer.isActive();
        if (isActive) {
            stop();
        }
//LOG.info("Setting elapsedTimeMillis: " + elapsedTimeMillis);
        timer.setElapsedTimeMillis(elapsedTimeMillis);
        updateTaskPlayTimes(elapsedTimeMillis);

        if (isActive) {
            start();
        }
    }

    @Override
    public void setPrecountTimeMillis(long precountTimeMillis) {
        boolean isActive = timer.isActive();
        if (isActive) {
            stop();
        }

//LOG.info("Setting setPrecountTimeMillis: " + precountTimeMillis);
        timer.setPrecountTimeMillis(precountTimeMillis);

        if (isActive) {
            start();
        }
    }

    private void updateTaskPlayTimes(long elapsedTimeMillis) {
//        LOG.debug("old load times: " + futurePlayTimes);
        long[] playTimes = playTimeTasks.keys();
        TLongArrayList newPlayTimes = new TLongArrayList();
        for (long playTime : playTimes) {
            if (playTime >= elapsedTimeMillis) {
                newPlayTimes.add(playTime);
            }
        }
        newPlayTimes.sort();
        futurePlayTimes = newPlayTimes;
//        LOG.debug("new load times: " + futurePlayTimes);
    }

    @Override
    public void reset() {
        LOG.warn("RESETTING SCHEDULER ...");
        futurePlayTimes.clear();
        playTimeTasks.clear();
        inQueue.clear();
        processPlayTimes.clear();
        transports.clear();
        timer.reset();
    }

    @Override
    public void add(MusicTask task) {
//        LOG.debug("Adding task: " + task);
        inQueue.offer(task);
    }

    @Override
    public void processQueue() {
        processMusicTaskQueue();
        executeScheduledTasks();
    }

    @Override
    public void resetScheduledTasks() {
        playTimeTasks.clear();
        updateTaskPlayTimes(clock.getElapsedTimeMillis());
    }

    //must be on single thread
    private TaskResult processEvents() {
        ExecInfo ei = null;

        if(isCollectExecInfo){
            ei = createExecInfo();
            ei.addCallStack("updateTransports");
        }

        updateTransports();

        if(isCollectExecInfo){
            closeExecInfo(ei);
            currentExecInfo.add(ei);

            ei = createExecInfo();
            ei.addCallStack("processMusicTaskQueue");
        }

        processMusicTaskQueue();

        if(isCollectExecInfo){
            closeExecInfo(ei);
            currentExecInfo.add(ei);

            ei = createExecInfo();
            ei.addCallStack("executeScheduledTasks");
        }

        executeScheduledTasks();

        if(isCollectExecInfo){
            closeExecInfo(ei);
            currentExecInfo.add(ei);

//            ei = createExecInfo();
//            ei.addCallStack("taskQueueResult");
        }

//        TaskResult result = getTaskResult(taskQueueResult, scheduledTasksResult, transportsResult);

//        if(isCollectExecInfo){
//            closeExecInfo(ei);
//            currentExecInfo.add(ei);
//        }

        return null;
    }

    private TaskResult getTaskResult(boolean taskQueueResult, boolean scheduledTasksResult, boolean transportsResult) {
        taskResult.reset();
        boolean isError = false;
        errorString = Consts.EMPTY;
        if (!taskQueueResult) {
            errorString = Consts.ERROR_TASK_QUEUE;
            isError = true;
        }
        if (!scheduledTasksResult) {
            errorString += Consts.ERROR_SCHEDULED_TASKS;
            isError = true;
        }
        if (!transportsResult) {
            errorString += Consts.ERROR_SCHEDULED_TASKS;
            isError = true;
        }
        if (!isError) {
            errorString = Consts.RESULT_OK;
        }
        taskResult.setError(errorString);
        taskResult.setIsError(isError);
        taskResult.setTaskId(taskId);
        return taskResult;
    }

    private boolean updateTransports() {
        try {
            for (Transport transport : transports.values()) {
//                LOG.debug("Updating transport: " + transport.getId());
                transport.onSystemTick();
            }
        } catch (Exception e) {
            LOG.error("Failed to process Transports", e);
            return false;
        }
        return true;
    }

    private boolean  executeScheduledTasks() {
        try {
            long clockPlayTime = clock.getElapsedTimeMillis();
            processPlayTimes.clear();

            for (int i = 0; i < futurePlayTimes.size(); i++) {
                long taskPlayTime = futurePlayTimes.get(i);
                if (taskPlayTime <= clockPlayTime) {
                    processPlayTimes.add(taskPlayTime);
//                    LOG.debug("Added Execute Play Time: " + taskPlayTime);
                    continue;
                }

                break;
            }

            for (int i = 0; i < processPlayTimes.size(); i++) {
                long taskPlayTime = processPlayTimes.get(i);
                List<MusicTask> tasks = playTimeTasks.get(taskPlayTime);
                processTasks(tasks);
                futurePlayTimes.remove(taskPlayTime);
//                LOG.debug("Removed Play Time: " + taskPlayTime);
            }
        } catch (Exception e) {
            LOG.error("Failed to process scheduled tasks", e);
            return false;
        }

        return true;
    }

    private void processTasks(List<MusicTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        for (MusicTask task : tasks) {
            if(sleepMillis > 0){
                ThreadUtil.doSleep(Thread.currentThread(), sleepMillis);
            }
//            LOG.info("Playing task: " + task);
            if(isCollectExecInfo && currentExecInfo != null){
                currentExecInfo.addCallStack("MusicTask: " + task.getClass().getName());
            }

            task.play();
        }
    }

    public void setPublishSleep(long millis){
        sleepMillis = millis;
    }

    private boolean processMusicTaskQueue() {
        try {
            boolean isSortTimes = false;
            while (!inQueue.isEmpty()) {
                MusicTask task = inQueue.poll();
                //            LOG.debug("Processing Task: " + task);

                long taskPlayTime = task.getPlayTime();
                if (taskPlayTime == 0) {
                    taskPlayTime = clock.getElapsedTimeMillis();
                }
                long clockPlayTime = clock.getElapsedTimeMillis();
                if ((clockPlayTime - taskPlayTime) > taskLatencyToleranceMillis) {
                    LOG.warn("Task playtime in the past, diff millis: " + (clockPlayTime - taskPlayTime) + " task: " + task);
                    continue;
                }
//                LOG.debug("clockPlayTime: " + clockPlayTime);

                if (!futurePlayTimes.contains(taskPlayTime)) {
                    futurePlayTimes.add(taskPlayTime);
                    isSortTimes = true;
//                    LOG.debug("added taskPlayTime: " + taskPlayTime);
                }

                List<MusicTask> tasks = playTimeTasks.get(taskPlayTime);
                if (tasks == null) {
                    tasks = new ArrayList<>();
                    playTimeTasks.put(taskPlayTime, tasks);
                }

                tasks.add(task);
//                LOG.debug("added task: " + task);
            }

            if (isSortTimes) {
                futurePlayTimes.sort();
//                LOG.debug("Sorted times: " + futurePlayTimes);
            }
        } catch (Exception e) {
            LOG.error("Failed to process music task queue");
            return false;
        }

        return true;
    }

    private int findInsertOffset(TLongArrayList futurePlayTimes, long taskPlayTime) {
        if (futurePlayTimes.isEmpty()) {
            return 0;
        }

        return futurePlayTimes.binarySearch(taskPlayTime);
    }

    private void triggerProcessEvents() {
        Future<TaskResult> future = executor.submit(processTask);
        //TODO Track futures
    }

    private boolean isProcessEvents() {
        return !inQueue.isEmpty() || !futurePlayTimes.isEmpty() || !transports.isEmpty();
    }

    public ExecInfo createExecInfo(){
        long start = System.nanoTime();
        ExecInfo ei = new ExecInfo();
        ei.setDate(clock.getSystemTimeMillis());
        ei.setStartTime(start);
        return ei;
    }

    public void closeExecInfo(ExecInfo ei){
        long end = System.nanoTime();
        ei.setEndTime(end);
    }

    class TimerListener implements TimeEventListener {
        @Override
        public void onTimeEvent() {
            if (isProcessEvents()) {
                triggerProcessEvents();
            }
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onStart() {
        }
    }


    class ProcessTask implements TriggerTask {

        @Override
        public TaskResult call() throws Exception {

            if(isCollectExecInfo){
                currentExecInfo = createExecInfo();
                currentExecInfo.addCallStack("ProcessTask.call()");
            }

            TaskResult result = processEvents();

            if(isCollectExecInfo){
                closeExecInfo(currentExecInfo);

                if(execInfos.size() > 10){
                    ExecInfo first = execInfos.first();
                    if(currentExecInfo.getDurationMillis() > first.getDurationMillis()){
                        execInfos.pollFirst();
                        execInfos.add(currentExecInfo);
                    }
                } else {
                    execInfos.add(currentExecInfo);
                }
            }

            return result;
        }
    }

}
