package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InscoreMapElement {
    static final Logger LOG = LoggerFactory.getLogger(InscoreMapElement.class);

    private final int xStart;
    private final int xEnd;
    private final int yStart;
    private final int yEnd;
    private final int beatStartNum;
    private final int beatStartDenom;
    private final int beatEndNum;
    private final int beatEndDenom;

    public InscoreMapElement(int xStart,
                             int xEnd,
                             int yStart,
                             int yEnd,
                             int beatStartNum,
                             int beatStartDenom,
                             int beatEndNum,
                             int beatEndDenom) {
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.yStart = yStart;
        this.yEnd = yEnd;
        this.beatStartNum = beatStartNum;
        this.beatStartDenom = beatStartDenom;
        this.beatEndNum = beatEndNum;
        this.beatEndDenom = beatEndDenom;
    }

    public int getXStart() {
        return xStart;
    }

    public int getXEnd() {
        return xEnd;
    }

    public int getYStart() {
        return yStart;
    }

    public int getYEnd() {
        return yEnd;
    }

    public int getBeatStartNum() {
        return beatStartNum;
    }

    public int getBeatStartDenom() {
        return beatStartDenom;
    }

    public int getBeatEndNum() {
        return beatEndNum;
    }

    public int getBeatEndDenom() {
        return beatEndDenom;
    }

    public String toInscoreString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Consts.BRACKET_OPEN);
        sb.append(Consts.SPACE);
        sb.append(Consts.BRACKET_SQUARE_OPEN);
        sb.append(xStart);
        sb.append(Consts.COMMA);
        sb.append(Consts.SPACE);
        sb.append(xEnd);
        sb.append(Consts.BRACKET_SQUARE_OPEN);
        sb.append(Consts.SPACE);
        sb.append(Consts.BRACKET_SQUARE_OPEN);
        sb.append(yStart);
        sb.append(Consts.COMMA);
        sb.append(Consts.SPACE);
        sb.append(yEnd);
        sb.append(Consts.BRACKET_SQUARE_OPEN);
        sb.append(Consts.SPACE);
        sb.append(Consts.BRACKET_CLOSE);
        sb.append(Consts.SPACE);
        sb.append(Consts.BRACKET_OPEN);
        sb.append(Consts.SPACE);
        sb.append(Consts.BRACKET_SQUARE_OPEN);
        sb.append(beatStartNum);
        sb.append(Consts.SLASH);
        sb.append(beatStartDenom);
        sb.append(Consts.COMMA);
        sb.append(Consts.SPACE);
        sb.append(beatEndNum);
        sb.append(Consts.SLASH);
        sb.append(beatEndDenom);
        sb.append(Consts.BRACKET_SQUARE_OPEN);
        sb.append(Consts.SPACE);
        sb.append(Consts.BRACKET_CLOSE);
        return sb.toString();
    }

    public static InscoreMapElement parseLine(String line) {
        if(line == null) {
            return null;
        }

        try {
            int indexStart = 0;
            int indexEnd = 0;
            Pair[] pairs = new Pair[3];

            for(int i = 0; i < 3; i++) {
                indexStart = getNextBracketPosition(line, indexStart);
                indexStart++;
                indexEnd = getNextBracketPosition(line, indexStart);
                String pairRaw = line.substring(indexStart, indexEnd);
                pairs[i] = parsePair(pairRaw);
                indexStart = indexEnd + 1;
            }

            int xStart = Integer.parseInt(pairs[0].getFirst());
            int xEnd = Integer.parseInt(pairs[0].getSecond());
            int yStart = Integer.parseInt(pairs[1].getFirst());
            int yEnd = Integer.parseInt(pairs[1].getSecond());
            Pair beatPair = pairs[2];
            String beatStart = beatPair.getFirst();
            String[] beatStartParts = beatStart.split(Consts.SLASH);
            String beatEnd = beatPair.getSecond();
            String[] beatEndParts = beatEnd.split(Consts.SLASH);
            int beatStartNum = Integer.parseInt(beatStartParts[0]);
            int beatStartDenom =  Integer.parseInt(beatStartParts[1]);
            int beatEndNum = Integer.parseInt(beatEndParts[0]);
            int beatEndDenom = Integer.parseInt(beatEndParts[1]);

            return new InscoreMapElement(xStart, xEnd, yStart, yEnd, beatStartNum, beatStartDenom, beatEndNum, beatEndDenom);
        } catch (NumberFormatException e) {
            LOG.error("Failed to process inscore map line {}", line, e);
        }
        return null;
    }

    private static Pair parsePair(String pairRaw) {
        if(pairRaw == null) {
            return null;
        }

        String[] parsed = pairRaw.split(Consts.COMMA);
        if(parsed.length < 2) {
            return null;
        }

        return new Pair(parsed[0].trim(), parsed[1].trim());
    }

    public static int getNextBracketPosition(String line, int index) {
        return line.indexOf(Consts.BRACKET_SQUARE_OPEN, index);
    }
}
