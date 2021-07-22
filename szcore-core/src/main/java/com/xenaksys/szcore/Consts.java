package com.xenaksys.szcore;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface Consts {

    AtomicInteger ID_SOURCE = new AtomicInteger(0);

    long THOUSAND = 1000L;
    long HUNDRED = 100L;

    String ERROR_TASK_QUEUE = " Failed to process task Queue";
    String ERROR_SCHEDULED_TASKS = " Failed to process scheduled tasks";
    String ERROR_TANSPORTS = " Failed to process transports";
    String RESULT_OK = "OK";
    String COMMA = ",";
    String DOT = ".";
    String QUOTE = "'";
    String PLUS = "+";
    String PLUS_REGEX = "\\+";
    String DOT_REGEX = "\\.";
    String EMPTY = "";
    String COLON = ":";
    String SEMI_COLON = ";";
    String SPACE = " ";
    String BRACKET_SQUARE_OPEN = "[";
    String BRACKET_SQUARE_CLOSE = "]";
    String BRACKET_OPEN = "(";
    String BRACKET_CLOSE = ")";
    String SLASH = "/";
    String NEW_LINE = "\n";
    String SYSTEM_NEW_LINE = System.lineSeparator();
    String EIGHTH = "/8";
    final Integer ONE_I = 1;
    final Double ONE_D = 1.0;
    String UNDERSCORE = "_";
    String DOUBLE_UNDERSCORE = "__";
    String INSCORE_FILE_EXTENSION = ".inscore";
    String WAV_FILE_EXTENSION = ".wav";
    String PNG_FILE_EXTENSION = ".png";
    String TXT_FILE_EXTENSION = ".txt";
    String YAML_FILE_EXTENSION = ".yml";
    String INSCORE_FILE_SUFFIX = "_InScoreMap";
    String STRATEGY_CONFIG_FILE_SUFFIX = "strategyConfig";
    String WEBSCORE_PRESET_FILE_SUFFIX = "webscoreConfig";
    String SCRIPTING_ENGINE_PRESET_FILE_SUFFIX = "scriptingEngineConfig";
    String INSCORE_ADDR = "INScore";
    String SZCORE_ADDR = "/SZCORE";
    String OSC_INSCORE_ADDRESS_ROOT = "/ITL";
    String OSC_INSCORE_ADDRESS_SCENE = OSC_INSCORE_ADDRESS_ROOT + "/scene";
    String ERR_ADDR = "error:";
    String ARG_HELLO = "HELLO";
    String ARG_PING = "PING";
    String ARG_SET_INSTRUMENT = "SET_INSTRUMENT";
    String ARG_SELECT_INST_SLOT = "SELECT_ISLOT";
    String BLANK_PAGE_NAME = "blank";
    String BLANK_PAGE_FILE = "blankStave";
    String DEFAULT_FILE_NAME = "part" + INSCORE_FILE_EXTENSION;
    String INDEX_HTML = "index.html";
    String CSV_EXT = ".csv";
    String MINUTES_SHORT = "m";
    String SECONDS_SHORT = "s";
    String LOCALHOST = "localhost";

    char MINUS_CHAR = '-';
    char DOT_CHAR = '.';
    String TRUE = "true";
    String FALSE = "false";

    String OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE1 = "/ITL/scene/slaveFollow";
    String OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE2 = "/ITL/scene/slaveFollow2";
    String OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE1 = "/ITL/scene/slaveBeat";
    String OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE2 = "/ITL/scene/slaveBeat2";
    String OSC_ADDRESS_SCORE_START_MARK_STAVE1 = "/ITL/scene/slaveStartMark";
    String OSC_ADDRESS_SCORE_START_MARK_STAVE2 = "/ITL/scene/slaveStartMark2";
    String OSC_ADDRESS_SCORE_DYNAMICS_LINE_STAVE1 = "/ITL/scene/dynStaveValueLine";
    String OSC_ADDRESS_SCORE_DYNAMICS_LINE_STAVE2 = "/ITL/scene/dynStaveValueLine2";
    String OSC_ADDRESS_SCORE_DYNAMICS_MID_LINE_STAVE1 = "/ITL/scene/dynStaveMidLine";
    String OSC_ADDRESS_SCORE_DYNAMICS_MID_LINE_STAVE2 = "/ITL/scene/dynStaveMidLine2";
    String OSC_ADDRESS_SCORE_DYNAMICS_BOX_STAVE1 = "/ITL/scene/dynStaveBox";
    String OSC_ADDRESS_SCORE_DYNAMICS_BOX_STAVE2 = "/ITL/scene/dynStaveBox2";
    String OSC_ADDRESS_SCORE_PRESSURE_BOX_STAVE1 = "/ITL/scene/pressureStaveBox";
    String OSC_ADDRESS_SCORE_PRESSURE_BOX_STAVE2 = "/ITL/scene/pressureStaveBox2";
    String OSC_ADDRESS_SCORE_PRESSURE_LINE1 = "/ITL/scene/pressureStaveValueLine";
    String OSC_ADDRESS_SCORE_PRESSURE_LINE2 = "/ITL/scene/pressureStaveValueLine2";
    String OSC_ADDRESS_SCORE_PRESSURE_MID_LINE1 = "/ITL/scene/pressureStaveMidLine";
    String OSC_ADDRESS_SCORE_PRESSURE_MID_LINE2 = "/ITL/scene/pressureStaveMidLine2";
    String OSC_ADDRESS_SCORE_SPEED_BOX_STAVE1 = "/ITL/scene/speedStaveBox";
    String OSC_ADDRESS_SCORE_SPEED_BOX_STAVE2 = "/ITL/scene/speedStaveBox2";
    String OSC_ADDRESS_SCORE_SPEED_LINE1 = "/ITL/scene/speedStaveValueLine";
    String OSC_ADDRESS_SCORE_SPEED_LINE2 = "/ITL/scene/speedStaveValueLine2";
    String OSC_ADDRESS_SCORE_SPEED_MID_LINE1 = "/ITL/scene/speedStaveMidLine";
    String OSC_ADDRESS_SCORE_SPEED_MID_LINE2 = "/ITL/scene/speedStaveMidLine2";
    String OSC_ADDRESS_SCORE_POSITION_BOX_STAVE1 = "/ITL/scene/positionStaveBox";
    String OSC_ADDRESS_SCORE_POSITION_BOX_STAVE2 = "/ITL/scene/positionStaveBox2";
    String OSC_ADDRESS_SCORE_POSITION_LINE1 = "/ITL/scene/positionStaveValueLine";
    String OSC_ADDRESS_SCORE_POSITION_LINE2 = "/ITL/scene/positionStaveValueLine2";
    String OSC_ADDRESS_SCORE_POSITION_ORD_LINE1 = "/ITL/scene/positionStaveOrdLine";
    String OSC_ADDRESS_SCORE_POSITION_ORD_LINE2 = "/ITL/scene/positionStaveOrdLine2";
    String OSC_ADDRESS_SCORE_POSITION_BRIDGE_LINE1 = "/ITL/scene/positionStaveBridgeLine";
    String OSC_ADDRESS_SCORE_POSITION_BRIDGE_LINE2 = "/ITL/scene/positionStaveBridgeLine2";
    String OSC_ADDRESS_SCORE_CONTENT_BOX_STAVE1 = "/ITL/scene/contentStaveBox";
    String OSC_ADDRESS_SCORE_CONTENT_BOX_STAVE2 = "/ITL/scene/contentStaveBox2";
    String OSC_ADDRESS_SCORE_CONTENT_LINE1 = "/ITL/scene/contentStaveValueLine";
    String OSC_ADDRESS_SCORE_CONTENT_LINE2 = "/ITL/scene/contentStaveValueLine2";

    String OSC_ADDRESS_SCORE_JAVASCRIPT = "/ITL/scene/javascript";
    String OSC_ADDRESS_ZSCORE = "/zs/";
    String OSC_ADDRESS_JSUI = OSC_ADDRESS_ZSCORE + "jsui";
    String OSC_ADDR_BEAT_INFO = OSC_ADDRESS_JSUI;
    String OSC_CMD_BEAT_INFO = "beatInfo";
    String OSC_CMD_SET_TEMPO = "setTempo";

    int OSC_UPDATE_BEAT_COMPLETE_PERC_THRESHOLD = 1;

    String RSRC_DIR = "rsrc/";

    int MILLIS_IN_MINUTE = 1000 * 60;
    long PING_EXPIRY_MILLIS = 5 * 1000;

    int CONTINUOUS_PAGE_NO = 6666;
    String CONTINUOUS_PAGE_NAME = "pagex";

    String DEFAULT_TRANSPORT_NAME = "DefaultTransport";
    String DEFAULT_PAGE_PREFIX = "page";
    String DEFAULT_BAR_PREFIX = "bar";
    String DEFAULT_OSC_PORT_NAME = "DEFAULT_OSC_PORT";

    String ALL_DESTINATIONS = "ALL";
    String BROADCAST = "BROADCAST";
    String[] ALLOWED_DESTINATIONS_ARR = {ALL_DESTINATIONS, BROADCAST};
    List<String> ALLOWED_DESTINATIONS = new ArrayList<>(Arrays.asList(ALLOWED_DESTINATIONS_ARR));

    int DEFAULT_OSC_PORT = 7000;
    int DEFAULT_OSC_OUT_PORT = 7001;
    int DEFAULT_OSC_ERR_PORT = 7002;
    int DEFAULT_OSC_SERVER_PORT = 7777;
    int DEFAULT_OSC_MAX_PORT = 6666;
    int DEFAULT_ALL_PORTS = Integer.MIN_VALUE;
    String DEFAULT_SUBNET_MASK = "255.255.255.0";

    int MAX_ALLOWED_HIT_COUNT_10S = 50;
    int HISTOGRAM_MAX_BUCKETS_NO = 10;
    long HISTOGRAM_BUCKET_PERIOD_MS = 1000L;

    int MAX_WEB_REQ_CHART_SIZE_SEC = 60 * 10;

    int MIN_BPM = 1;
    int MAX_BPM = 200;

    String OSC_INSCORE_LOAD = "load";
    String OSC_INSCORE_SET = "set";
    String OSC_INSCORE_FILE = "file";
    String OSC_INSCORE_MAP = "map";
    String OSC_INSCORE_MAPF = "mapf";
    String OSC_INSCORE_TEMPO = "tempo";

    String ADDR_TOKEN = "$ADDR";
    String OSC_JS_ACTIVATE = "activate(" + ADDR_TOKEN + ")";
    String OSC_JS_DEACTIVATE = "deactivate(" + ADDR_TOKEN + ")";

    String INET_ADDR_TOKEN = "$INET_ADDR";
    String OSC_JS_SERVER_HELLO = "serverHello('" + INET_ADDR_TOKEN + "')";

    String SEND_TIME = "$SEND_TIME";
    String OSC_JS_PING_CMD = "ping";
    String OSC_JS_PING = OSC_JS_PING_CMD + "('" + SEND_TIME + "')";

    String TITLE_TOKEN = "$TITLE";
    String OSC_JS_SET_TITLE = "setTitle('" + TITLE_TOKEN + "')";

    String STAVE_NO = "$STAVE_NO";
    String BEAT_NO = "$BEAT_NO";
    String OSC_JS_SET_DATE = "setDate(" + STAVE_NO + "," + BEAT_NO + ")";

    String TEMPO = "$TEMPO";
    String OSC_JS_SET_TEMPO = "setTempo(" + TEMPO + ")";

    String OSC_JS_STOP = "stop()";

    String PART_TOKEN = "$PART";
    String OSC_JS_SET_PART = "setPart('" + PART_TOKEN + "')";

    String CSV_INSTRUMENTS_TOKEN = "$INSTRUMENT";
    String OSC_JS_SET_INSTRUMENTS = "setInstruments('" + CSV_INSTRUMENTS_TOKEN + "')";

    String CSV_INSTRUMENT_SLOTS_TOKEN = "$INSTRUMENT_SLOTS";
    String OSC_JS_SET_INSTRUMENT_SLOTS = "setInstrumentSlots('" + CSV_INSTRUMENT_SLOTS_TOKEN + "')";
    String OSC_JS_RESET_INSTRUMENT_SLOTS = "resetInstrumentSlots()";

    String SERVER_IP__TOKEN = "$SERVER_IP";
    String OSC_JS_SET_SERVER_IP = "setServerIp('" + SERVER_IP__TOKEN + "')";

    String BEAT_TOKEN = "$BEAT";
    String COLOUR_TOKEN = "$COLOUR";
    String OSC_JS_BEATER_ON = "beatersOn(" + BEAT_TOKEN + "," + COLOUR_TOKEN + ")";
    String OSC_JS_BEATER_OFF = "beatersOff(" + BEAT_TOKEN + ")";

    String ALPHA_VALUE_TOKEN = "$ALPHA_VALUE";
    String OSC_JS_SET_ALPHA = "setAlpha('" + ADDR_TOKEN + "'," + ALPHA_VALUE_TOKEN + ")";

    String OSC_JS_RESET_SCORE = "resetScore()";
    String OSC_JS_RESET_STAVES = "resetStaves()";
    String OSC_JS_RESET_INSTRUMENT = "resetInstrument()";

    double OSC_STAVE_BEATER_Y_MIN = -0.66;
    double OSC_STAV_BEATER_Y_MAX = -0.9;

    String OSC_ADDRESS_STAVE1 = OSC_INSCORE_ADDRESS_SCENE + "/stave";
    double OSC_STAVE1_X = 0.0;
    double OSC_STAVE1_Y = -0.32;
    double OSC_STAVE1_BEATER_Y_MIN = OSC_STAVE_BEATER_Y_MIN;
    double OSC_STAVE1_BEATER_Y_MAX =OSC_STAV_BEATER_Y_MAX;
    double OSC_STAVE1_Z = 1.0;
    double OSC_STAVE1_SCALE = 1.2;
    int OSC_STAVE1_SHOW = 1;

    String OSC_ADDRESS_STAVE2 = OSC_INSCORE_ADDRESS_SCENE + "/stave2";
    double OSC_STAVE2_X = 0.0;
    double OSC_STAVE2_Y = 0.32;
    double OSC_STAVE2_BEATER_Y_MIN = OSC_STAVE_BEATER_Y_MIN;
    double OSC_STAVE2_BEATER_Y_MAX = OSC_STAV_BEATER_Y_MAX;
    double OSC_STAVE2_Z = 1.0;
    double OSC_STAVE2_SCALE = 1.2;
    int OSC_STAVE2_SHOW = 1;


    double OSC_FULL_SCORE_STAVE_BEATER_Y_MIN = -0.26;
    double OSC_FULL_SCORE_STAV_BEATER_Y_MAX = -0.35;
    double OSC_FULL_SCORE_SCALE = 0.7;

    double OSC_FULL_SCORE_STAVE1_X = -0.7;
    double OSC_FULL_SCORE_STAVE1_Y = 0.0;
    double OSC_FULL_SCORE_STAVE1_BEATER_Y_MIN = OSC_FULL_SCORE_STAVE_BEATER_Y_MIN;
    double OSC_FULL_SCORE_STAVE1_BEATER_Y_MAX = OSC_FULL_SCORE_STAV_BEATER_Y_MAX;

    double OSC_FULL_SCORE_STAVE2_X = 0.7;
    double OSC_FULL_SCORE_STAVE2_Y = 0.0;
    double OSC_FULL_SCORE_STAVE2_BEATER_Y_MIN = OSC_FULL_SCORE_STAVE_BEATER_Y_MIN;
    double OSC_FULL_SCORE_STAVE2_BEATER_Y_MAX =OSC_FULL_SCORE_STAV_BEATER_Y_MAX;

    String OSC_ARG_CLOCK = "clock";
    String OSC_ARG_DATE = "date";
    String OSC_ARG_DY = "dy";
    String OSC_ARG_Y_POSITION = "y";
    String OSC_ARG_ALPHA = "alpha";
    String OSC_ARG_PEN_ALPHA = "penAlpha";
    String OSC_ARG_PEN_COLOR = "color";
    ///ITL/scene/javascript run "activate('/ITL/scene/stave2')"
    String RUN = "run";
    String HELLO = "hello";

    int OSC_COLOUR_GREEN = 1;
    int OSC_COLOUR_YELLOW = 2;
    int OSC_COLOUR_ORANGE = 3;
    int OSC_COLOUR_RED = 4;

    String REGEX_ALL = "*";

    String NAME_LISTENER_SERVER_LOG_OUT_PORT = "ServerLogOutPort";
    String NAME_LISTENER_SERVER_LOG_ERR_PORT = "ServerLogErrPort";
    String NAME_LISTENER_CLIENT_LOG_OUT_PORT = "ClientLogOutPort";
    String NAME_LISTENER_CLIENT_LOG_ERR_PORT = "ClientLogErrPort";
    String NAME_LISTENER_SERVER_HELLO = "ServerHello";
    String NAME_LISTENER_CLIENT_MAIN = "ClientMainListener";

    String NAME_EVENT_TIME = "time: ";
    String NAME_EVENT_HOST = "host: ";
    String NAME_EVENT_OSC_ADDR = "URL: ";
    String NAME_EVENT_OSC_ARGS = "arguments: ";
    String NAME_EVENT_OSC_IN = "Event type: OSC In, ";
    String NAME_NA = "N/A";
    String NAME_YES = "Y";
    String NAME_NO = "N";
    String NAME_WEB = "Web";
    String NAME_INSCORE = "InScore";

    String WEB_ROOT = "webRoot";
    int WEB_METADATA_CACHE_SIZE = 200;
    long WEB_MAX_FILE_SIZE = 1024 * 1024 * 5;
    int WEB_BUFFER_SLICE_SIZE = 1000;
    int WEB_SLICES_PER_PAGE = 10;
    int WEB_MAX_MEMORY_SIZE = 1024 * 1024 * 100;

    double WEB_TILE_PLAY_PAGE_DURATION_FACTOR = 0.8;

    String WEB_PATH_SSE = "/sse";
    String WEB_PATH_WEBSOCKETS = "/wsoc";
    String WEB_PATH_HTTP = "/htp";
    String WEB_PATH_STATIC = "/";

    String WEB_HTTP_HEADER_USER_AGENT = "User-Agent";
    String WEB_HTTP_HEADER_ACCEPT = "Accept";

    String WEB_EVENT_NAME = "ev";
    String WEB_EVENT_TIME_NAME = "evt";
    String WEB_EVENT_LAST_STATE_UPDATE_TIME = "lsut";
    String WEB_EVENT_SENT_TIME_NAME = "t";
    String WEB_EVENT_IS_POLL_NAME = "poll";
    String WEB_EVENT_ELEMENT_ID = "elementId";
    String WEB_EVENT_IS_SELECTED = "selected";
    String WEB_EVENT_PART = "part";
    String WEB_EVENT_SERVER_TIME = "serverTime";

    String WEB_RESPONSE_TYPE = "type";
    String WEB_RESPONSE_MESSAGE = "msg";
    String WEB_RESPONSE_STATE = "st";
    String WEB_RESPONSE_DATA = "d";
    String WEB_RESPONSE_TIME = "t";
    String WEB_RESPONSE_SUBMITTED = "Submitted";

    String WEB_TILE_PREFIX = "t";
    String WEB_ELEMENT_NAME_DELIMITER = "-";
    String WEB_ELEMENT_GRID = "grid";
    String WEB_ZOOM_DEFAULT = "default";

    String WEB_SCORE_SUFFIX = "_webscore";
    String WEB_TEXT_BACKGROUND_COLOUR = "rgba(225, 225, 225, 0.85)";

    String WEB_SCORE_ID = "webScore";
    String WEB_GRANULATOR = "granulator";
    String WEB_STAGE = "stage";
    String WEB_SPEECH_SYNTH = "speechSynth";
    String WEB_SPEECH_VOICE_RANDOM = "random";
    String WEB_SPEECH_VOICE_DEFAULT = "default";
    String WEB_TARGET_ALL = "all";
    String WEB_SELECTED_TILES = "selectedTiles";
    String WEB_OVERLAYS = "overlays";

    String WEB_OBJ_ELEMENT_STATE = "webElementState";
    String WEB_OBJ_TILE_TEXT = "tileText";
    String WEB_OBJ_WEB_TEXT = "webText";
    String WEB_OBJ_CENTRE_SHAPE = "centreShape";
    String WEB_OBJ_INNER_CIRCLE = "innerCircle";
    String WEB_OBJ_OUTER_CIRCLE = "outerCircle";
    String WEB_OBJ_INSTRUCTIONS = "instructions";
    String WEB_OBJ_TILES = "tiles";
    String WEB_OBJ_TILE = "tile";
    String WEB_OBJ_ACTIONS = "actions";
    String WEB_OBJ_STAGE_ALPHA = "stageAlpha";
    String WEB_OBJ_ZOOM_LEVEL = "zoomLevel";
    String WEB_OBJ_CONFIG_GRANULATOR = "granulatorConfig";
    String WEB_OBJ_CONFIG_SPEECH_SYNTH = "speechSynthConfig";
    String WEB_OBJ_STATE_SPEECH_SYNTH = "speechSynthState";
    String WEB_OBJ_CONFIG_GRAIN = "grain";
    String WEB_OBJ_CONFIG_GRAIN_ENVELOPE = "envelope";
    String WEB_OBJ_CONFIG_GRAIN_PANNER = "panner";

    String WEB_ACTION_ID_CONFIG = "config";
    String WEB_ACTION_ID_START = "start";
    String WEB_ACTION_ID_DISPLAY = "display";
    String WEB_ACTION_ID_ALL = "all";
    String WEB_ACTION_ID_RESET = "reset";
    String WEB_ACTION_ID_PLAY = "play";
    String WEB_ACTION_ID_STOP = "stop";
    String WEB_ACTION_ID_STATE = "state";
    String WEB_ACTION_ID_RAMP_LINEAR = "rampLinear";
    String WEB_ACTION_ID_RAMP_SIN = "rampSin";
    String WEB_ACTION_VOLUME = "volume";

    String WEB_ACTION_PARAM_LEVEL = "level";
    String WEB_ACTION_PARAM_TIME_MS = "timeMs";
    String WEB_ACTION_PARAM_SEND_TIME_MS = "sendTimeMs";

    int WEB_CONFIG_READY_PRESET = 0;
    int WEB_CONFIG_GO_PRESET = -1;
    int WEB_CONFIG_LOAD_PRESET = -2;

    String WEB_CONFIG_DURATION = "duration";
    String WEB_CONFIG_VALUE = "value";
    String WEB_CONFIG_GRAIN = "grain";
    String WEB_CONFIG_ENVELOPE = "envelope";
    String WEB_CONFIG_PANNER = "panner";
    String WEB_CONFIG_IS_USE_PANNER = "isUsePanner";
    String WEB_CONFIG_PANNING_MODEL = "panningModel";
    String WEB_CONFIG_DISTANCE_MODEL = "distanceModel";
    String WEB_CONFIG_MAX_PAN_ANGLE = "maxPanAngle";
    String WEB_CONFIG_SIZE_MS = "sizeMs";
    String WEB_CONFIG_PITCH_RATE = "pitchRate";
    String WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS = "maxPositionOffsetRangeMs";
    String WEB_CONFIG_MAX_PITCH_RATE_RANGE = "maxPitchRateRange";
    String WEB_CONFIG_TIME_OFFSET_STEPS_MS = "timeOffsetStepMs";
    String WEB_CONFIG_ATTACK_TIME = "attackTime";
    String WEB_CONFIG_DECAY_TIME = "decayTime";
    String WEB_CONFIG_SUSTAIN_TIME = "sustainTime";
    String WEB_CONFIG_RELEASE_TIME = "releaseTime";
    String WEB_CONFIG_SUSTAIN_LEVEL = "sustainLevel";
    String WEB_CONFIG_MASTER_GAIN_VAL = "masterGainVal";
    String WEB_CONFIG_PLAY_DURATION_SEC = "playDurationSec";
    String WEB_CONFIG_PLAY_START_OFFSET_SEC = "playStartOffsetSec";
    String WEB_CONFIG_MAX_GRAINS = "maxGrains";
    String WEB_CONFIG_BUFFER_POSITION_PLAY_RATE = "bufferPositionPlayRate";
    String WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS = "audioStopToleranceMs";
    String WEB_CONFIG_IS_USE_POSITION_OSCILLATOR = "isUsePositionOscillator";
    String WEB_CONFIG_IS_USE_SIZE_OSCILLATOR = "isUseSizeOscillator";
    String WEB_CONFIG_IS_USE_POSITION_FREQ_MOD = "isUsePositionFrequencyMod";
    String WEB_CONFIG_IS_USE_POSITION_RANGE_MOD = "isUsePositionRangeMod";
    String WEB_CONFIG_VOLUME = "volume";
    String WEB_CONFIG_PITCH = "pitch";
    String WEB_CONFIG_RATE = "rate";
    String WEB_CONFIG_LANG = "lang";
    String WEB_CONFIG_MAX_VOICE_LOAD_ATTEMPTS = "maxVoiceLoadAttempts";
    String WEB_CONFIG_MAX_UTTERANCES = "maxUtterances";
    String WEB_CONFIG_UTTERANCE_TIMEOUT_SEC = "utteranceTimeoutSec";
    String WEB_CONFIG_IS_INTERRUPT = "isInterrupt";
    String WEB_CONFIG_INTERRUPT_TIMEOUT_MS = "interruptTimeout";
    String WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK = "isPlaySpeechSynthOnClick";
    String WEB_CONFIG_SPEECH_TEXT = "speechText";
    String WEB_CONFIG_SPEECH_VOICE = "speechVoice";
    String WEB_CONFIG_SPEECH_IS_INTERRUPT = "speechIsInterrupt";
    String WEB_CONFIG_PARAM_NAME = "paramName";
    String WEB_CONFIG_END_VALUE = "endValue";
    String WEB_CONFIG_AMPLITUDE = "amplitude";
    String WEB_CONFIG_FREQUENCY = "frequency";
    String WEB_CONFIG_OSCILLATOR = "oscillator";
    String WEB_CONFIG_SIZE_OSCILLATOR = "sizeOscillator";
    String WEB_CONFIG_POSITION_OSCILLATOR = "positionOscillator";
    String WEB_CONFIG_FREQUENCY_LFO = "frequencyLFO";
    String WEB_CONFIG_START_LFO = "startLFO";
    String WEB_CONFIG_END_LFO = "endLFO";
    String WEB_CONFIG_MIN_VALUE = "minValue";
    String WEB_CONFIG_MAX_VALUE = "maxValue";
    String WEB_CONFIG_TYPE = "type";
    double WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD = 10E-3;

    String WEB_DATA_SCORE_INFO = "SCORE_INFO";
    String WEB_DATA_SCORE_STATE = "SCORE_STATE";
    String WEB_DATA_TARGET = "TARGET";
    String WEB_DATA_TARGET_TYPE = "TARGET_TYPE";
    String WEB_DATA_TARGET_ALL = "ALL";

    String WEB_SCORE_ROOT_DIR = "/score/";
    String WEB_SCORE_STAVE_TOP = "topStave";
    String WEB_SCORE_STAVE_BOTTOM = "bottomStave";

    String WEB_SCORE_PAGE_PREFIX = "p";
    String WEB_SCORE_PAGE_NO_TOKEN = "@PgNo@";

    String WEB_WS_CLOSE_REASON_NORMAL = "Closed by the server";

    String NAME_FULL_SCORE = "FullScore";

    int DEFAULT_THREAD_SLEEP_MILLIS = 10;

    Double[] TEMPO_MULTIPLIERS = {0.1, 0.2, 0.3, 0.4, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85,
            0.9, 0.91, 0.92, 0.93, 0.94, 0.95, 0.96, 0.97, 0.98, 0.99,
            1.0,
            1.01, 1.02, 1.03, 1.04, 1.05, 1.06, 1.07, 1.08, 1.09,
            1.1, 1.15, 1.2, 1.25, 1.3, 1.35, 1.4, 1.45, 1.5,
                                                        1.6, 1.7, 1.8, 1.9, 2.0};

    String RND_STRATEGY_1 = "1";
    String RND_STRATEGY_1_1 = "1,1";
    String RND_STRATEGY_1_1_1 = "1,1,1";
    String RND_STRATEGY_1_1_1_1 = "1,1,1,1";
    String RND_STRATEGY_2 = "2";
    String RND_STRATEGY_2_1 = "2,1";
    String RND_STRATEGY_2_1_1 = "2,1,1";
    String RND_STRATEGY_2_2 = "2,2";
    String RND_STRATEGY_3 = "3";
    String RND_STRATEGY_3_1 = "3,1";
    String RND_STRATEGY_4 = "4";
    String[] RANDOMISATION_STRATEGIES = {RND_STRATEGY_1, RND_STRATEGY_1_1, RND_STRATEGY_1_1_1, RND_STRATEGY_1_1_1_1,
            RND_STRATEGY_2, RND_STRATEGY_2_1, RND_STRATEGY_2_1_1, RND_STRATEGY_2_2, RND_STRATEGY_3, RND_STRATEGY_3_1, RND_STRATEGY_4};

    String PRESET_ALL_OFF = "All OFF";
    String PRESET_ALL_ON = "All ON Lines OFF";
    String PRESET_ALL_LINES_ON = "All ON Lines ON";
    String PRESET_ALL_OFF_CONTENT_ON = "All OFF Content ON";
    String PRESET_ALL_ON_CONTENT_OFF = "All ON Content OFF";
    String PRESET_ALL_ON_CONTENT_POSITION_OFF = "All ON Cnt/Pos OFF";
    String[] PRESETS = {PRESET_ALL_OFF, PRESET_ALL_ON, PRESET_ALL_LINES_ON, PRESET_ALL_OFF_CONTENT_ON, PRESET_ALL_ON_CONTENT_OFF, PRESET_ALL_ON_CONTENT_POSITION_OFF};

    String DISRUPTOR_THREAD_FACTORY = "SZCore_Dsrptr";
    String DISRUPTOR_OSC_OUT_THREAD_FACTORY = "SZCore_OSC_Out_Dsrptr";
    String DISRUPTOR_WEB_OUT_THREAD_FACTORY = "SZCore_Web_Out_Dsrptr";
    String DISRUPTOR_IN_THREAD_FACTORY = "SZCore_In_Dsrptr";
    String DISRUPTOR_CONTAINER_IN_THREAD_FACTORY = "SZCore_Container_In_Dsrptr";
    String SCHEDULER_THREAD_FACTORY = "SZCore_Schedlr";
    String DEFAULT_THREAD_SUFFIX = "-Thread-";

    double DYNAMICS_LINE_Y_MAX = 0.074;      //half  0.037
    double DYNAMICS_LINE_Y_MIN = 0.0;
    double DYNAMICS_LINE1_Y_MID_POSITION = -0.077;
    double DYNAMICS_LINE1_Y_MIN_POSITION = -0.04;
    double DYNAMICS_LINE1_Y_MAX_POSITION = -0.114;
    double DYNAMICS_LINE2_Y_MID_POSITION = 0.713;
    double DYNAMICS_LINE2_Y_MAX_POSITION = 0.676;
    double DYNAMICS_LINE2_Y_MIN_POSITION = 0.75;

    double PRESSURE_LINE_Y_MAX = 0.055;  //half 0.0275
    double PRESSURE_LINE_Y_MIN = 0.0;
    double PRESSURE_LINE1_Y_MID_POSITION = -0.4;
    double PRESSURE_LINE1_Y_MIN_POSITION = -0.3725;
    double PRESSURE_LINE1_Y_MAX_POSITION = -0.4275;
    double PRESSURE_LINE2_Y_MID_POSITION = 0.391;
    double PRESSURE_LINE2_Y_MAX_POSITION = 0.3635;
    double PRESSURE_LINE2_Y_MIN_POSITION = 0.4185;

    double SPEED_LINE_Y_MAX = 0.045;      //half  0.0275
    double SPEED_LINE_Y_MIN = 0.0;
    double SPEED_LINE1_Y_MID_POSITION = -0.457;
    double SPEED_LINE1_Y_MIN_POSITION = -0.435;
    double SPEED_LINE1_Y_MAX_POSITION = -0.48;
    double SPEED_LINE2_Y_MID_POSITION = 0.334;
    double SPEED_LINE2_Y_MAX_POSITION = 0.308;
    double SPEED_LINE2_Y_MIN_POSITION = 0.355;

    double POSITION_LINE_Y_MAX = 0.128;      //half 0.0675
    double POSITION_LINE_Y_MIN = 0.0;
    double POSITION_LINE1_Y_MID_POSITION = -0.555;
    double POSITION_LINE1_Y_MIN_POSITION = -0.492;
    double POSITION_LINE1_Y_MAX_POSITION = -0.62;
    double POSITION_LINE2_Y_MID_POSITION = 0.236;
    double POSITION_LINE2_Y_MAX_POSITION = 0.17;
    double POSITION_LINE2_Y_MIN_POSITION = 0.298;

    double CONTENT_LINE_Y_MAX = 0.244;      //half 0.125
    double CONTENT_LINE_Y_MIN = 0.0;
    double CONTENT_LINE1_Y_MID_POSITION = -0.245;
    double CONTENT_LINE1_Y_MIN_POSITION = -0.125;
    double CONTENT_LINE1_Y_MAX_POSITION = -0.365;
    double CONTENT_LINE2_Y_MID_POSITION = 0.545;
    double CONTENT_LINE2_Y_MAX_POSITION = 0.425;
    double CONTENT_LINE2_Y_MIN_POSITION = 0.665;

    String CONFIG_SCORE_NAME = "scoreName";
    String CONFIG_RND_STRATEGY = "rndStrategy";
    String CONFIG_ACTIVE_RANGES = "activePageRanges";
    String CONFIG_PAGE_RANGE_MAPPING = "pageRangeMapping";
    String CONFIG_PAGE_RANGE = "pageRange";
    String CONFIG_PAGE_RANGES = "pageRanges";
    String CONFIG_INSTRUMENTS = "instruments";
    String CONFIG_ACTIVE_RANGE = "activeRange";
    String CONFIG_RANGE = "range";
    String CONFIG_IS_RND_ACTIVE = "isRndActive";
    String CONFIG_SELECTION_RANGE = "selectionRange";
    String CONFIG_START = "start";
    String CONFIG_END = "end";
    String CONFIG_ALL = "all";
    String CONFIG_PRESETS = "presets";
    String CONFIG_SCRIPTS = "scripts";
    String CONFIG_WEB_CONFIG = "config";
    String CONFIG_ID = "id";
    String CONFIG_TILE_ROW = "tileRow";
    String CONFIG_TILE_COLS = "tileCols";
    String CONFIG_ASSIGNMENT_TYPE = "assignmentType";

    String MAXMSP_ID = "max";
    String MAXMSP_CMD_SET_FILE = "setFile";
    String MAXMSP_CMD_PLAY = "play";

    String MAXMSP_BAR_PREFIX = "b";

    String MAXMSP_BUFFER_NO_TOKEN = "$BUFNO";
    String MAXMSP_BUFFER_TARGET = "b" + MAXMSP_BUFFER_NO_TOKEN;

    String SCRIPTING_ENGINE_ID = "sce";
    String T_ACTION_TEMPO = "tempo";
}
