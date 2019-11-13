package com.xenaksys.szcore;


import java.util.concurrent.atomic.AtomicInteger;

public interface Consts {

    public static final AtomicInteger ID_SOURCE = new AtomicInteger(0);

    public static final String ERROR_TASK_QUEUE = " Failed to process task Queue";
    public static final String ERROR_SCHEDULED_TASKS = " Failed to process scheduled tasks";
    public static final String ERROR_TANSPORTS = " Failed to process transports";
    public static final String RESULT_OK = "OK";
    public static final String COMMA = ",";
    public static final String QUOTE = "'";
    public static final String PLUS = "+";
    public static final String PLUS_REGEX = "\\+";
    public static final String EMPTY = "";
    public static final String COLUMN = ":";
    public static final String SPACE = " ";
    public static final String BRACKET_SQUARE_OPEN = "[";
    public static final String BRACKET_SQUARE_CLOSE = "]";
    public static final String BRACKET_OPEN = "(";
    public static final String BRACKET_CLOSE = ")";
    public static final String SLASH = "/";
    public static final String NEW_LINE = "\n";
    public static final String EIGHTH = "/8";
    public final Integer ONE_I = 1;
    public final Double ONE_D = 1.0;
    public static final String DOUBLE_UNDERSCORE = "__";
    public static final String INSCORE_FILE_EXTENSION = ".inscore";
    public static final String PNG_FILE_EXTENSION = ".png";
    public static final String TXT_FILE_EXTENSION = ".txt";
    public static final String INSCORE_FILE_SUFFIX = "_InScoreMap";
    public static final String INSCORE_ADDR = "INScore";
    public static final String SZCORE_ADDR = "/SZCORE";
    public static final String OSC_INSCORE_ADDRESS_ROOT = "/ITL";
    public static final String OSC_INSCORE_ADDRESS_SCENE = OSC_INSCORE_ADDRESS_ROOT + "/scene";
    public static final String ERR_ADDR = "error:";
    public static final String ARG_HELLO = "HELLO";
    public static final String ARG_PING = "PING";
    public static final String ARG_SET_INSTRUMENT = "SET_INSTRUMENT";
    public static final String BLANK_PAGE_NAME = "blank";
    public static final String BLANK_PAGE_FILE = "blankStave";
    public static final String DEFAULT_FILE_NAME = "part" + INSCORE_FILE_EXTENSION;

    public static final String OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE1 = "/ITL/scene/slaveFollow";
    public static final String OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE2 = "/ITL/scene/slaveFollow2";
    public static final String OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE1 = "/ITL/scene/slaveBeat";
    public static final String OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE2 = "/ITL/scene/slaveBeat2";
    public static final String OSC_ADDRESS_SCORE_START_MARK_STAVE1 = "/ITL/scene/slaveStartMark";
    public static final String OSC_ADDRESS_SCORE_START_MARK_STAVE2 = "/ITL/scene/slaveStartMark2";

    public static final String OSC_ADDRESS_SCORE_JAVASCRIPT = "/ITL/scene/javascript";

    public static final String RSRC_DIR = "rsrc/";

    public static final int MILLIS_IN_MINUTE = 1000 * 60;

    public static final String DEFAULT_TRANSPORT_NAME = "DefaultTransport";
    public static final String DEFAULT_PAGE_PREFIX = "page";
    public static final String DEFAULT_BAR_PREFIX = "bar";
    public static final String DEFAULT_OSC_PORT_NAME = "DEFAULT_OSC_PORT";

    public static final String ALL_DESTINATIONS = "ALL";

    public static final int DEFAULT_OSC_PORT = 7000;
    public static final int DEFAULT_OSC_OUT_PORT = 7001;
    public static final int DEFAULT_OSC_ERR_PORT = 7002;
    public static final int DEFAULT_OSC_SERVER_PORT = 7777;
    public static final int DEFAULT_ALL_PORTS = Integer.MIN_VALUE;

    public static final String OSC_INSCORE_LOAD = "load";
    public static final String OSC_INSCORE_SET = "set";
    public static final String OSC_INSCORE_FILE = "file";
    public static final String OSC_INSCORE_MAPF = "mapf";
    public static final String OSC_INSCORE_TEMPO = "tempo";

    public static final String ADDR_TOKEN = "$ADDR";
    public static final String OSC_JS_ACTIVATE = "activate(" + ADDR_TOKEN + ")";
    public static final String OSC_JS_DEACTIVATE = "deactivate(" + ADDR_TOKEN + ")";

    public static final String INET_ADDR_TOKEN = "$INET_ADDR";
    public static final String OSC_JS_SERVER_HELLO = "serverHello('" + INET_ADDR_TOKEN + "')";

    public static final String SEND_TIME = "$SEND_TIME";
    public static final String OSC_JS_PING_CMD = "ping";
    public static final String OSC_JS_PING = OSC_JS_PING_CMD + "('" + SEND_TIME + "')";

    public static final String TITLE_TOKEN = "$TITLE";
    public static final String OSC_JS_SET_TITLE = "setTitle('" + TITLE_TOKEN + "')";

    public static final String STAVE_NO = "$STAVE_NO";
    public static final String BEAT_NO = "$BEAT_NO";
    public static final String OSC_JS_SET_DATE = "setDate(" + STAVE_NO + "," + BEAT_NO + ")";

    public static final String TEMPO = "$TEMPO";
    public static final String OSC_JS_SET_TEMPO = "setTempo(" + TEMPO + ")";

    public static final String OSC_JS_STOP = "stop()";

    public static final String PART_TOKEN = "$PART";
    public static final String OSC_JS_SET_PART = "setPart('" + PART_TOKEN + "')";

    public static final String CSV_INSTRUMENTS_TOKEN = "$INSTRUMENT";
    public static final String OSC_JS_SET_INSTRUMENTS = "setInstruments('" + CSV_INSTRUMENTS_TOKEN + "')";

    public static final String BEAT_TOKEN = "$BEAT";
    public static final String COLOUR_TOKEN = "$COLOUR";
    public static final String OSC_JS_BEATER_ON = "beatersOn(" + BEAT_TOKEN + "," + COLOUR_TOKEN + ")";
    public static final String OSC_JS_BEATER_OFF = "beatersOff(" + BEAT_TOKEN + ")";

    public static final String ALPHA_VALUE_TOKEN = "$ALPHA_VALUE";
    public static final String OSC_JS_SET_ALPHA = "setAlpha('" + ADDR_TOKEN + "'," + ALPHA_VALUE_TOKEN + ")";

    public static final String OSC_JS_RESET_SCORE = "resetScore()";
    public static final String OSC_JS_RESET_STAVES = "resetStaves()";
    public static final String OSC_JS_RESET_INSTRUMENT = "resetInstrument()";

    public static final double OSC_STAVE_BEATER_Y_MIN = -0.66;
    public static final double OSC_STAV_BEATER_Y_MAX = -0.9;

    public static final String OSC_ADDRESS_STAVE1 = OSC_INSCORE_ADDRESS_SCENE + "/stave";
    public static final double OSC_STAVE1_X = 0.0;
    public static final double OSC_STAVE1_Y = -0.32;
    public static final double OSC_STAVE1_BEATER_Y_MIN = OSC_STAVE_BEATER_Y_MIN;
    public static final double OSC_STAVE1_BEATER_Y_MAX =OSC_STAV_BEATER_Y_MAX;
    public static final double OSC_STAVE1_Z = 1.0;
    public static final double OSC_STAVE1_SCALE = 1.2;
    public static final int OSC_STAVE1_SHOW = 1;

    public static final String OSC_ADDRESS_STAVE2 = OSC_INSCORE_ADDRESS_SCENE + "/stave2";
    public static final double OSC_STAVE2_X = 0.0;
    public static final double OSC_STAVE2_Y = 0.32;
    public static final double OSC_STAVE2_BEATER_Y_MIN = OSC_STAVE_BEATER_Y_MIN;
    public static final double OSC_STAVE2_BEATER_Y_MAX = OSC_STAV_BEATER_Y_MAX;
    public static final double OSC_STAVE2_Z = 1.0;
    public static final double OSC_STAVE2_SCALE = 1.2;
    public static final int OSC_STAVE2_SHOW = 1;


    public static final double OSC_FULL_SCORE_STAVE_BEATER_Y_MIN = -0.26;
    public static final double OSC_FULL_SCORE_STAV_BEATER_Y_MAX = -0.35;
    public static final double OSC_FULL_SCORE_SCALE = 0.7;

    public static final double OSC_FULL_SCORE_STAVE1_X = -0.7;
    public static final double OSC_FULL_SCORE_STAVE1_Y = 0.0;
    public static final double OSC_FULL_SCORE_STAVE1_BEATER_Y_MIN = OSC_FULL_SCORE_STAVE_BEATER_Y_MIN;
    public static final double OSC_FULL_SCORE_STAVE1_BEATER_Y_MAX = OSC_FULL_SCORE_STAV_BEATER_Y_MAX;

    public static final double OSC_FULL_SCORE_STAVE2_X = 0.7;
    public static final double OSC_FULL_SCORE_STAVE2_Y = 0.0;
    public static final double OSC_FULL_SCORE_STAVE2_BEATER_Y_MIN = OSC_FULL_SCORE_STAVE_BEATER_Y_MIN;
    public static final double OSC_FULL_SCORE_STAVE2_BEATER_Y_MAX =OSC_FULL_SCORE_STAV_BEATER_Y_MAX;

    public static final String OSC_ARG_CLOCK = "clock";
    public static final String OSC_ARG_DATE = "date";
    public static final String OSC_ARG_DY = "dy";
    public static final String OSC_ARG_Y_POSITION = "y";
    ///ITL/scene/javascript run "activate('/ITL/scene/stave2')"
    public static final String RUN = "run";
    public static final String HELLO = "hello";

    public static final int OSC_COLOUR_GREEN = 1;
    public static final int OSC_COLOUR_YELLOW = 2;
    public static final int OSC_COLOUR_ORANGE = 3;
    public static final int OSC_COLOUR_RED = 4;

    public static final String REGEX_ALL = "*";

    public static final String NAME_LISTENER_SERVER_LOG_OUT_PORT = "ServerLogOutPort";
    public static final String NAME_LISTENER_SERVER_LOG_ERR_PORT = "ServerLogErrPort";
    public static final String NAME_LISTENER_CLIENT_LOG_OUT_PORT = "ClientLogOutPort";
    public static final String NAME_LISTENER_CLIENT_LOG_ERR_PORT = "ClientLogErrPort";
    public static final String NAME_LISTENER_SERVER_HELLO = "ServerHello";
    public static final String NAME_LISTENER_CLIENT_MAIN = "ClientMainListener";

    public static final String NAME_EVENT_TIME = "time: ";
    public static final String NAME_EVENT_HOST = "host: ";
    public static final String NAME_EVENT_OSC_ADDR = "URL: ";
    public static final String NAME_EVENT_OSC_ARGS = "arguments: ";
    public static final String NAME_EVENT_OSC_IN = "Event type: OSC In, ";
    public static final String NAME_NA = "N/A";

    public static final String NAME_FULL_SCORE = "FullScore";

    public static final int DEFAULT_THREAD_SLEEP_MILLIS = 10;

    public static final Double[] TEMPO_MULTIPLIERS = {0.1, 0.2, 0.3, 0.4, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85,
                                                        0.9, 0.91, 0.92, 0.93, 0.94, 0.95, 0.96, 0.97, 0.98, 0.99,
                                                        1.0,
                                                        1.01, 1.02, 1.03, 1.04, 1.05, 1.06, 1.07, 1.08, 1.09,
                                                        1.1, 1.15, 1.2, 1.25, 1.3, 1.35, 1.4, 1.45, 1.5,
                                                        1.6, 1.7, 1.8, 1.9, 2.0};

    public static final String DISRUPTOR_THREAD_FACTORY = "SZCore_Dsrptr";
    public static final String DISRUPTOR_OUT_THREAD_FACTORY = "SZCore_Out_Dsrptr";
    public static final String DISRUPTOR_IN_THREAD_FACTORY = "SZCore_In_Dsrptr";
    public static final String SCHEDULER_THREAD_FACTORY = "SZCore_Schedlr";
    public static final String DEFAULT_THREAD_SUFFIX = "-Thread-";

}
