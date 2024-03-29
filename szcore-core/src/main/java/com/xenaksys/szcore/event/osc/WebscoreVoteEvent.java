package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class WebscoreVoteEvent extends OscJavascriptEvent {
    private final int voteCount;
    private final int voterNo;
    private final int min;
    private final int max;
    private final int avg;

    public WebscoreVoteEvent(int voteCount, int min, int max, int avg, int voterNo, List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
        this.voteCount = voteCount;
        this.voterNo = voterNo;
        this.min = min;
        this.max = max;
        this.avg = avg;
        addCommandArg();
    }

    public void addCommandArg() {
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, Consts.OSC_JS_VOTE);
    }

    public int getVoteCount() {
        return voteCount;
    }

    public int getVoterNo() {
        return voterNo;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getAvg() {
        return avg;
    }

    public OscEventType getOscEventType() {
        return OscEventType.VOTE;
    }
}
