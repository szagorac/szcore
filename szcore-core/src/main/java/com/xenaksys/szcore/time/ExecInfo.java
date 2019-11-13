package com.xenaksys.szcore.time;

import com.xenaksys.szcore.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class ExecInfo implements Comparable<ExecInfo>{
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    static final Logger LOG = LoggerFactory.getLogger(ExecInfo.class);
    private static final long MILLION = 1000000;

    long startTime;
    long endTime;
    long date;
    List<String> callStack = new ArrayList<>();

    List<ExecInfo> subExecInfos = new ArrayList<>();

    public long getStartTime() {
        return startTime;
    }

    public long getStartMillis() {
        double start = 1.0*startTime/MILLION;
        return Math.round(start);
    }

    public Date getStartDate(){
        return new Date(date);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Double getDurationMillis(){
        long diff = endTime - startTime;
        return (diff*1.0)/MILLION;
    }

    public void addCallStack(String name){
        callStack.add(name);
    }

    public void resetCallStack(){
        callStack.clear();
    }

    public void add(ExecInfo ei){
        subExecInfos.add(ei);
    }

    public void log(){
        LOG.info(getLog());
    }

    public String getLog(){
        String subExecLog = "";
        if(!subExecInfos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(Consts.NEW_LINE);
            sb.append("SUB ExecInfo");
            for(ExecInfo ei : subExecInfos){
                sb.append(Consts.NEW_LINE);
                sb.append(ei.getLog());
            }
            subExecLog = sb.toString();
        }

        String stack = Arrays.toString(callStack.toArray())        ;
        return formatter.format(getStartDate()) + " lasted millis: " + getDurationMillis() + " call stack: " + stack + subExecLog;
    }

    @Override
    public int compareTo(ExecInfo o) {
        return getDurationMillis().compareTo(o.getDurationMillis());
    }
}
