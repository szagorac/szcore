package com.xenaksys.szcore.model;

public enum NoteDuration {
    WHOLE(1),
    HALF(2), HALF_DOT(3),
    QUARTER(4), QUARTER_DOT(6),
    EIGHTH(8), EIGHTH_DOT(12),
    SIXTEENTH(16), SIXTEENTH_DOT(24),
    THIRTY_SECOND(32), THIRTY_SECOND_DOT(48),
    SIXTY_FOURTH(64), SIXTY_FOURTH_DOT(96),
    HUNDRED_TWENTY_EIGHTH(128), HUNDRED_TWENTY_EIGHTH_DOT(192),
    TWO_HUNDRED_FIFTY_SIXTH(256),
    MINIM(2), MINIM_DOT(3),
    CROTCHET(4), CROTCHET_DOT(6),
    QUAVER(8), QUAVER_DOT(12),
    SEMI_QUAVER(16), SEMI_QUAVER_DOT(24),
    DEMI_SEMI_QUAVER(32), DEMI_SEMI_QUAVER_DOT(48),
    HEMI_DEMI_SEMI_QUAVER(64), HEMI_DEMI_SEMI_QUAVER_DOT(96),
    SEMI_HEMI_DEMI_SEMI_QUAVER(128), SEMI_HEMI_DEMI_SEMI_QUAVER_DOT(192),
    DEMI_SEMI_HEMI_DEMI_SEMI_QUAVER_DOT(256);

    private final int numberInWhole;

    private NoteDuration(int numberInWhole) {
        this.numberInWhole = numberInWhole;
    }

    public int getNumberOfInWhole() {
        return numberInWhole;
    }

    public static NoteDuration get(int code) {
        switch (code) {
            case 1:
                return WHOLE;
            case 2:
                return MINIM;
            case 4:
                return CROTCHET;
            case 8:
                return QUAVER;
            case 16:
                return SEMI_QUAVER;
            case 32:
                return DEMI_SEMI_QUAVER;
        }
        return null;
    }

}
