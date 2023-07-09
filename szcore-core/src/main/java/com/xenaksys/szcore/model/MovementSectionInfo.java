package com.xenaksys.szcore.model;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovementSectionInfo {
    static final Logger LOG = LoggerFactory.getLogger(MovementSectionInfo.class);

    private final String sectionId;
    private final List<String> parts = new ArrayList<>();
    private final List<String> scoreParts = new ArrayList<>();
    private final VoteInfo voteInfo = new VoteInfo();
    private final List<ExtScoreInfo> maxConfigs = new ArrayList<>();
    private final List<ExtScoreInfo> webConfigs = new ArrayList<>();

    private IntRange pageRange;
    private IntRange playPageRange;
    private volatile boolean isActive;
    private volatile int currentPage;
    private volatile int currentPlayPage;
    private boolean isInterruptOnPageEnd;

    public MovementSectionInfo(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setPageRange(IntRange sectionPageRange) {
        this.pageRange = sectionPageRange;
    }

    public IntRange getPageRange() {
        return pageRange;
    }

    public void setPlayPageRange(IntRange sectionPageRange) {
        this.playPageRange = sectionPageRange;
    }

    public IntRange getPlayPageRange() {
        return playPageRange;
    }

    public int getEndPageNo() {
        IntRange intRange = getPageRange();
        if (intRange == null) {
            return -1;
        }
        return intRange.getEnd();
    }

    public int getStartPageNo() {
        IntRange intRange = getPageRange();
        if (intRange == null) {
            return -1;
        }
        return intRange.getStart();
    }

    public int getPlayEndPageNo() {
        IntRange intRange = getPlayPageRange();
        if (intRange == null) {
            return -1;
        }
        return intRange.getEnd();
    }

    public int getPlayStartPageNo() {
        IntRange intRange = getPlayPageRange();
        if (intRange == null) {
            return -1;
        }
        return intRange.getStart();
    }

    public VoteInfo getVoteInfo() {
        return voteInfo;
    }

    public void populateVoteInfo(int current, int min, int max, int avg, int voterNo) {
        voteInfo.populate(current, min, max, avg, voterNo);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPlayPage() {
        return currentPlayPage;
    }

    public void setCurrentPlayPage(int currentPlayPage) {
        if(this.currentPlayPage == currentPlayPage) {
            return;
        }
        if(playPageRange == null) {
            int start = currentPlayPage;
            int end = start + pageRange.getSize() - 1;
            playPageRange = new SequentalIntRange(start, end);
            LOG.info("setCurrentPlayPage: playPageRange start: {} end: {}", start, end);
        }
        if(!playPageRange.isInRange(currentPlayPage)) {
            LOG.error("setCurrentPlayPage: page is not in range {}", playPageRange);
        }
        this.currentPlayPage = currentPlayPage;
        LOG.info("setCurrentPlayPage: currentPlayPage {}", currentPlayPage);
        recalcCurrentPage();
    }

    private void recalcCurrentPage() {
        int diff = currentPlayPage - playPageRange.getStart();
        int nextCurrent = pageRange.getStart() + diff;
        LOG.info("recalcCurrentPage: setting current page: {} currentPlayPage: {} diff: {} ", nextCurrent, currentPlayPage, diff);
        currentPage = nextCurrent;
    }

    public List<ExtScoreInfo> getMaxConfigs() {
        return maxConfigs;
    }

    public void setMaxConfigs(List<ExtScoreInfo> maxConfigs) {
        if(maxConfigs == null) {
            return;
        }
        this.maxConfigs.addAll(maxConfigs);
    }

    public List<ExtScoreInfo> getWebConfigs() {
        return webConfigs;
    }

    public void setWebConfigs(List<ExtScoreInfo> webConfigs) {
        if(webConfigs == null) {
            return;
        }
        this.webConfigs.addAll(webConfigs);
    }

    public void setParts(List<String> parts) {
        if(parts == null) {
            return;
        }
        this.parts.addAll(parts);
    }

    public void setScoreParts(List<String> parts) {
        if(parts == null) {
            return;
        }
        this.scoreParts.addAll(parts);
    }

    public List<String> getParts() {
        return parts;
    }

    public List<String> getScoreParts() {
        return scoreParts;
    }

    public boolean isInterruptOnPageEnd() {
        return isInterruptOnPageEnd;
    }

    public void setInterruptOnPageEnd(boolean interruptOnPageEnd) {
        isInterruptOnPageEnd = interruptOnPageEnd;
    }

    public void recalculatePlayPageRange(int sectionStartPageNo) {
        int pageNo = getNumberOfPages();
        int end = sectionStartPageNo + pageNo - 1;
        this.playPageRange = new SequentalIntRange(sectionStartPageNo, end);
        LOG.info("recalculatePlayPageRange: playPageRange start: {} end: {}", sectionStartPageNo, end);
    }

    private int getNumberOfPages() {
        if(pageRange == null) {
            return 0;
        }
        return pageRange.getSize();
    }

    public int getNextPage() {
        int current = getCurrentPage();
        int next = current + 1;
        if(next > getEndPageNo()) {
//            next = getStartPageNo() + (next % pageRange.getSize());
            int count = next;
            next = getStartPageNo();
            LOG.info("getNextPage: next > getEndPageNo() calculated next page: {} count: {} current: {}  currentPlay: {} endPage: {} startPage: {}", next, count, current, getCurrentPlayPage(), getEndPageNo(),getStartPageNo());
        }
        return next;
    }

    public boolean isSectionPage(int currentPage) {
        if (playPageRange == null) {
            return false;
        }
        return playPageRange.isInRange(currentPage);
    }

    public boolean isSectionSourcePage(int currentPage) {
        if (pageRange == null) {
            return false;
        }
        return pageRange.isInRange(currentPage);
    }

    public boolean isPartInSection(String part) {
        if (part == null) {
            return false;
        }
        return parts.contains(part);
    }

    public void resetOnNewPosition() {
        playPageRange = null;
        isActive = false;
        currentPage = 0;
        currentPlayPage = 0;
        voteInfo.reset();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovementSectionInfo that = (MovementSectionInfo) o;
        return sectionId.equals(that.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId);
    }

    @Override
    public String toString() {
        return "MovementSectionInfo{" +
                "sectionId='" + sectionId + '\'' +
                ", pageRange=" + pageRange +
                ", playPageRange=" + playPageRange +
                ", isActive=" + isActive +
                ", currentPage=" + currentPage +
                ", currentPlayPage=" + currentPlayPage +
                ", isInterruptOnPageEnd=" + isInterruptOnPageEnd +
                '}';
    }

}