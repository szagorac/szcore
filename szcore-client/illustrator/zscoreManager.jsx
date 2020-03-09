#target illustrator

var szDoc;
var szApp = app;

if (szApp.documents.length > 0) {

    szDoc = szApp.activeDocument;

    if (!szDoc) {
        Window.alert("This script might modify your document. Please save it before running this script.");
    }

} else {
    Window.alert("You must open at least one document.");
}

var ZSCORE = function (Window) {

    var theWindow = Window;
    var availableFonts = szApp.textFonts;
    var theApp = szApp;

    var ERROR = 0;
    var INFO = 1;
    var ERROR_PREFIX = "ERROR: ";
    var INFO_PREFIX = "INFO: ";
    var LOG_PREFIX = "LOG: ";
    var SLASH = "/";
    var DASH = "-";
    var UNDERSCORE = "_";
    var PLUS = "+";
    var COMMA = ",";
    var PIPE = "|";
    var DOT = ".";
    var ESCAPE_DOT = "\.";
    var SPACE = " ";
    var EXT_XML = ".xml";
    var NEW_LINE = "\r\n";

    var ARTBOARD_SPACER = 50;

    var PROC_RENAME = 1;
    var PROC_FIND_LAYERS_BY_NAME = 2;
    var PROC_FIND_LAYERS_BY_REGEX = 3;
    var BUILD_LAYERS_XML = 4;

    var NAME_LAYER = "Layer";
    var NAME_DOC = "Document";
    var NAME_ERROR = "ERROR";
    var NAME_OK = "OK";
    var NAME_PAGE = "page";
    var NAME_START = "start";
    var NAME_END = "end";
    var NAME_END_LINE = "endline";
    var NAME_BARNUM = "barnum";
    var NAME_PAGENUM = "pageNo";
    var NAME_BEATLINE = "bl";
    var NAME_BEATLINES = "beatlines";
    var NAME_EVENTS = "events";
    var NAME_TIMESIG = "timesig";
    var NAME_STAVE = "stave";
    var NAME_TIMESIG_NUM = "num";
    var NAME_TIMESIG_DENOM = "denom";
    var NAME_TEMPO = "tempo";
    var NAME_BAR = "bar";
    var NAME_BARINFO = "barinfo";
    var NAME_COPY = "Copy";
    var NAME_ACTIVE_DOC = "Active Doc";
    var NAME_RIGHT = "right";
    var NAME_BELOW = "below";
    var NAME_NOTATION = "notation";
    var NAME_PRESTART = "prestart";
    var NAME_BPM = "bpm";
    var NAME_NOTE_VAL = "noteval";
    var NAME_PNG = "png";
    var NAME_SVG = "svg";
    var NAME_SCORE = "score";
    var NAME_FULL_SCORE = "FullScore";
    var NAME_RIM = "rim";
    var NAME_TEXT_TRAME_SCRIPT = "[TextFrame script]"
    var NAME_PAGENUM_PREFIX = "P";
    
    var NAME_INSCORE_MAP_FILE_SUFFIX = "_InScoreMap.txt";
    var NAME_BEAT_INFO_FILE_SUFFIX = "_BeatInfo.csv";
 
    var IS_USE_BEATLINE = true;
    var IS_SHOW_TIMESIG = true;

    var TYPE_STRING = "string";
    var TYPE_BOOLEAN = "boolean";

    var BT_CALL_GET_LAYER_XML = "btGetLayerXML";
    var BT_CALL_GET_OPEN_DOCS = "btGetOpenDocs";
    var BT_CALL_GET_ACTIVE_DOC_NAME = "btGetActiveDocName";
    var BT_CALL_EXPORT_LAYER_XML = "btExportLayerXML";
    var BT_CALL_LOAD_LAYER_XML = "btLoadLayerXML";
    var BT_CALL_DELETE_LAYER = "btDeleteLayer";
    var BT_CALL_COPY_LAYER = "btCopyLayer";
    var BT_CALL_CREATE_PAGE_FROM_PAGE = "btCreatePageFromPage";
    var BT_CALL_CREATE_BAR = "btCreateBar";
    var BT_CALL_COPY_BAR = "btCopyBar";
    var BT_CALL_DELETE_BAR = "btDeleteBar";
    var BT_CALL_DELETE_PAGE = "btDeletePage";
    var BT_CALL_UPDATE_PAGE = "btUpdatePage";
    var BT_CALL_INSERT_MODEL_XML = "btInsertModelXML";
    var BT_CALL_EXPORT_FILES = "btExportFiles";
    var BT_CALL_UNKNOWN = "btUnknown";

    var BT_PROP_ITEM_NAME = "propItemName";
    var BT_PROP_NEW_ITEM_NAME = "propNewItemName";
    var BT_PROP_OLD_ITEM_NAME = "propOldItemName";
    var BT_PROP_ITEM_NUMBER = "propItemNumber";
    var BT_PROP_DOC_NAME = "propDocName";
    var BT_PROP_DEST_DOC_NAME = "propDestDocName";
    var BT_PROP_ARTB_NAME = "propArtbName";
    var BT_PROP_NEW_ARTB_NAME = "propNewArtbName";
    var BT_PROP_XML_MODEL = "propXmlModel";
    var BT_PROP_FILE_PATH = "propFilePath";
    var BT_PROP_SCORE_NAME = "propScoreName";
    var BT_PROP_X_OFFSET = "propXOffset";
    var BT_PROP_Y_OFFSET = "propYOffset";
    var BT_PROP_NAME_FILTER = "propNameFilter";
    var BT_PROP_NUMERATOR = "propNumerator";
    var BT_PROP_DENOMINATOR = "propDenominator";
    var BT_PROP_TEMPO = "propTempo";
    var BT_PROP_POSITION = "propPosition";
    var BT_PROP_SHOW_TIMESIG = "propShowTimesig";
    var BT_PROP_CREATE_BEATLINES = "propCreateBeatlines";
    var BT_PROP_FORMAT = "propFormat";
    var BT_PROP_EXPORT_SCORE = "propExportScore";
    var BT_PROP_EXPORT_PARTS = "propExportParts";
    var BT_PROP_EXPORT_BEATLINES = "propExportBeatlines";

    var BT_RESP_ERROR = NAME_ERROR;
    var BT_RESP_OK = NAME_OK;

    var BT_HEADER_CALLER = "BTMH_CALLER";

    var START_PXL = 64;
    var WHOLE_PXL = 160;
    var MINIM_PXL = WHOLE_PXL / 2;
    var CROTCHET_PXL = WHOLE_PXL / 4;
    var QUAVER_PXL = WHOLE_PXL / 8;
    var SEMI_QUAVER_PXL = WHOLE_PXL / 16;
    var DEMI_SEMI_QUAVER_PXL = WHOLE_PXL / 32;
    var FIRST_BEAT_PXL = 5;
    
    var BASE_BEAT_UNIT = 8;
    var NO_BASE_UNITS_IN_BEAT = 2;
    var TEMPO_BEAT_UNIT = 4;
    var MILLIS_IN_MIN = 60000;

    var SPACER_PXL = 1;

    var BAR_NO_TXT_X_OFFSET = 5;
    var BAR_NO_TXT_Y_OFFSET = 5;
    var TIMESIG_NUM_X_OFFSET = 9;
    var TIMESIG_NUM_Y_OFFSET = 5;
    var TIMESIG_DENOM_X_OFFSET = 9;
    var TIMESIG_DENOM_Y_OFFSET = 15.5;
    var TEMPO_NOTEVAL_X_OFFSET = 5;
    var TEMPO_NOTEVAL_Y_OFFSET = 10;
    var TEMPO_BPM_X_OFFSET = 20;
    var TEMPO_BPM_Y_OFFSET = 6;
    var PAGE_NO_TXT_X_OFFSET = 28;
    var PAGE_NO_TXT_Y_OFFSET = 5;

    var BARS_REGEX = /^bar\d+/;
    var PAGES_REGEX = /^page\d+/;

    var DEFAULT_FONT = "Helvetica";
    var DEFAULT_TXT_STYLE = availableFonts.getByName(DEFAULT_FONT);
    var DEFAULT_TXT_SIZE = 10;
    var DEFAULT_TIMESIG_FONT = "Opus";
    var DEFAULT_TIMESIG_TXT_STYLE = availableFonts.getByName(DEFAULT_TIMESIG_FONT);
    var DEFAULT_TIMESIG_TXT_SIZE = 21;
    var DEFAULT_TIMESIG_NUM = 4;
    var DEFAULT_TIMESIG_DENOM = 4;
    var DEFAULT_BPM_FONT = "Opus";
    var DEFAULT_BPM_TXT_STYLE = availableFonts.getByName(DEFAULT_BPM_FONT);
    var DEFAULT_BPM_TXT_SIZE = 14;
    var DEFAULT_TEMPO_NOTE_VAL = "q=";
    var DEFAULT_PAGE_NO_FONT = "Verdana-Italic";
    var DEFAULT_PAGE_NO_STYLE = availableFonts.getByName(DEFAULT_PAGE_NO_FONT);
    var DEFAULT_PAGE_NO_SIZE = 10;
    var DEFAULT_Y_POSITION = 100;

    var defaultPath = "~/Desktop";
    var defaultName = "layerXml.xml";
    var pathSeparator = SLASH;
    var defaultSaveFilePath = defaultPath + pathSeparator + defaultName;
    
    var scoreFile;
    var scoreFileHeaderWritten = false;

    var btCallProps;

    var startLineName = NAME_START;
    var endLineName = NAME_END;
    var pageEndLineName = NAME_END_LINE;
    var barnumName = NAME_BARNUM;
    var pagenumName = NAME_PAGENUM;
    var pagenumPrefix = NAME_PAGENUM_PREFIX;
    var beatlineName = NAME_BEATLINE;
    var beatlineZeroName = NAME_BEATLINE + "0";
    var isUseBeatline = IS_USE_BEATLINE;
    var isShowTimesig = IS_SHOW_TIMESIG;
    var beatDistancePxl = CROTCHET_PXL;
    var firstBeatDistancePxl = FIRST_BEAT_PXL;
    var font = DEFAULT_FONT;
    var txtStyle = DEFAULT_TXT_STYLE;
    var txtSize = DEFAULT_TXT_SIZE;
    var timeSigFont = DEFAULT_TIMESIG_FONT;
    var timeSigTxtStyle = DEFAULT_TIMESIG_TXT_STYLE;
    var timeSigTxtSize = DEFAULT_TIMESIG_TXT_SIZE;
    var bpmTxtStyle = DEFAULT_BPM_TXT_STYLE;
    var bpmTxtSize = DEFAULT_BPM_TXT_SIZE;
    var pageNoTxtStyle = DEFAULT_PAGE_NO_STYLE;
    var pageNoTxtSize = DEFAULT_PAGE_NO_SIZE;
    var barnumXOffset = BAR_NO_TXT_X_OFFSET;
    var barnumYOffset = BAR_NO_TXT_Y_OFFSET;
    var pagenumXOffset = PAGE_NO_TXT_X_OFFSET;
    var pagenumYOffset = PAGE_NO_TXT_Y_OFFSET;
    var timesigNumXOffset = TIMESIG_NUM_X_OFFSET;
    var timesigNumYOffset = TIMESIG_NUM_Y_OFFSET;
    var timesigDenomXOffset = TIMESIG_DENOM_X_OFFSET;
    var timesigDenomYOffset = TIMESIG_DENOM_Y_OFFSET;
    var tempoNoteValXOffset = TEMPO_NOTEVAL_X_OFFSET;
    var tempoNoteValYOffset = TEMPO_NOTEVAL_Y_OFFSET;
    var bpmXOffset = TEMPO_BPM_X_OFFSET;
    var bpmYOffset = TEMPO_BPM_Y_OFFSET;
    var wholeNoteDistancePxl = WHOLE_PXL;
    var baseBeatUnit = BASE_BEAT_UNIT;
    var noBaseUnitsInBeat = NO_BASE_UNITS_IN_BEAT;
    var tempoBeatUnit = TEMPO_BEAT_UNIT;

    var pageNamePrefix = NAME_PAGE;
    var barNamePrefix = NAME_BAR;
    var timeSigNum = NAME_TIMESIG_NUM;
    var timeSigDenom = NAME_TIMESIG_DENOM;
    var bpm = NAME_BPM;
    var noteVal = NAME_NOTE_VAL;
    var btHeaders = [BT_HEADER_CALLER];
    var rim = NAME_RIM;

    var BtCallProperties = function () {
    };

    var BtHeader = function (name, value) {
        this.name = name;
        this.value = value;
    };

    var InstrumentMetricTrackerMap = {};
    
    var MetricTracker = function(){
        this.beatNo = 0;
        this.unitBeatNo = 0;
        this.timeSigNum = "";
        this.timeSigDenom = 0;
        this.tempoBpm = 0;
        this.tempoBeatValue = 0;
        this.barNo = 0;
        this.timeMillis = 0;
        
        this.reset = function(){
            this.beatNo = 0;
            this.unitBeatNo = 0;
            this.timeSigNum = "";
            this.timeSigDenom = 0;
            this.tempoBpm = 0;
            this.tempoBeatValue = 0;
            this.barNo = 0;
            this.timeMillis = 0;
        };
    };
    
    var BeatInfo = function(){
        this.scoreName = "";
        this.instrumentName = "";
        this.pageName = "";
        this.pageNo = 0;
        this.barName = "";
        this.barNo = 0;        
        this.timeSigNum = "";
        this.timeSigDenom = 0;
        this.tempoBpm = 0;
        this.tempoBeatValue = 0;
        this.beatNo = 0;
        this.startTimeMillis = 0;
        this.durationTimeMillis = 0;
        this.endTimeMillis = 0;
        this.startBaseBeatUnits = 0;
        this.durationBeatUnits = 0;
        this.endBaseBeatUnits = 0;
        this.xStartPxl = 0;
        this.xEndPxl = 0;
        this.yStartPxl = 0;
        this.yEndPxl = 0;
        this.isUpbeat = 0;
        this.resource = 0;
        
        this.reset = function(){
            this.scoreName = "";
            this.instrumentName = "";
            this.pageName = "";
            this.pageNo = 0;
            this.barName = "";
            this.barNo = 0;        
            this.timeSigNum = "";
            this.timeSigDenom = 0;
            this.tempoBpm = 0;
            this.tempoBeatValue = 0;
            this.beatNo = 0;
            this.startTimeMillis = 0;
            this.durationTimeMillis = 0;
            this.endTimeMillis = 0;
            this.startBaseBeatUnits = 0;
            this.durationBeatUnits = 0;
            this.endBaseBeatUnits = 0;
            this.xStartPxl = 0;
            this.xEndPxl = 0;
            this.yStartPxl = 0;
            this.yEndPxl = 0;
            this.isUpbeat = 0;
            this.resource = 0;
        };
        
    };    
    
    var createBeatInfoCsvStr = function (properties, beatinfo){
        var out = "";
        for (var i = 0; i < properties.length; i++) {
            var prop = properties[i];
            if(!prop){
                conrinue;
            }
            var value = beatinfo[prop];
            if(value || value === 0){
                out += value + COMMA;
            } else {
                out += COMMA;
            }
        }
        if(endsWith(out, COMMA)){
            out = out.substring(0, out.length -1);
        }
        return out;
    };
    
    var createBeatInfoHeaderCsvStr = function (properties, barInfo){
        var out = "";
        for (var i = 0; i < properties.length; i++) {
            var prop = properties[i];
            if(!prop){
                continue;
            }
            
            out += prop + COMMA;
        }
        if(endsWith(out, COMMA)){
            out = out.substring(0, out.length -1);
        }
        return out;
    };
    
    var getObjectProperties = function (obj){
        var out = [];
        for (var property in obj) {
            if (obj.hasOwnProperty(property)) {
                if(isFunction(obj[property])){
                    continue;
                }
                out.push(property);
            }
        }      
        return out;
    };
    
    var resetInstrumentMap = function (map){
        for (var property in map) {
            if (map.hasOwnProperty(property)) {
                var tracker = map[property];
                if(tracker){
                    tracker.reset();
                }
            }
        }
    };
    
    var getInstrumentMetricTracker = function (instrumentName){
        var metricTracker = InstrumentMetricTrackerMap[instrumentName];
        if(!metricTracker){
            metricTracker = new MetricTracker();
            InstrumentMetricTrackerMap[instrumentName] = metricTracker;
        }
        return metricTracker;
    };
    
    var showAlertInternal = function (content) {
        if (!theWindow) {
            return;
        }
        theWindow.alert(content);
    };

    var isArray = function (obj) {
        return Object.prototype.toString.call(obj) === '[object Array]';
    };
    
    var isFunction = function(functionToCheck) {
        var getType = {};
        return functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
    };

    var isNumber = function (n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    };

    var isErrResponse = function (outcome) {
        if (!outcome) {
            return true;
        }
        return BT_RESP_ERROR === outcome;
    };

    var isLayer = function (obj) {
        if (!obj || !obj.typename) {
            return false;
        }
        return obj.typename === NAME_LAYER;
    };

    var isString = function (value) {
        return (typeof value) === TYPE_STRING;
    };

    var isBoolean = function (value) {
        return (typeof value) === TYPE_BOOLEAN;
    };
    
    var isScriptEvent = function (eventItem) {
        if (!eventItem || eventItem.constructor.name !== "TextFrame"){
            return false;
        }
        if (eventItem == NAME_TEXT_TRAME_SCRIPT){  
            return true;
        }
    };

    var stringToBoolean = function (value) {
        if (isBoolean(value)) {
            return value;
        }
        if (isString(value)) {
            var isTrueSet = (value === "true");
            return isTrueSet;
        }

        return value;
    };

    var contains = function (strvalue, substr) {
        if (!strvalue || !isString(strvalue) || !isString(substr)) {
            return false;
        }

        return strvalue.indexOf(substr) > 0;
    };

    var startsWith = function (strvalue, substr) {
        if (!strvalue || !isString(strvalue) || !isString(substr)) {
            return false;
        }

        return strvalue.lastIndexOf(substr, 0) === 0;
    };

    var isDoc = function (obj) {
        if (!obj || !obj.typename) {
            return false;
        }
        return obj.typename === NAME_DOC;
    };

    var endsWith = function (str, suffix) {
        if (!str || !suffix || suffix.length > str.length) {
            return false;
        }

        return str.slice(-1 * suffix.length) === suffix;
    };
    
    var trimRegex = function(value) {
        return value.replace(/^\s+|\s+$/gm,'');
    };

    var convertCsvToArr = function (csvStr) {
        if (!csvStr) {
            return [];
        }

        return csvStr.split(COMMA);
    };
    
    var convertCsvToIntArr = function (csvStr) {
        var arr = convertCsvToArr(csvStr);
        var out = [];
        
        for(var i = 0; i < arr.length; i++){
            var val = arr[i];
            out.push(parseInt(val));
        }

        return out;
    };

    var createRangeFromString = function (rangeStr) {
        if (!rangeStr) {
            return [];
        }
        var min = 1;
        var max = 1;
        var out = [];
        var range = rangeStr.split(DASH);
        if (range.length > 0) {
            var val = range[0];
            min = parseInt(val);
        }
        if (range.length > 1) {
            var val = range[1];
            max = parseInt(val);;
        }
        for (var i = min; i <= max; i++) {
            out.push(i);
        }

        return out;
    };

    var forEach = function (collection, fn) {
        var n = collection.length;
        for (var i = 0; i < n; ++i) {
            fn(collection[i]);
        }
    };
    
    function hideLayers(layers) {  
        if(!layers){
            return;
        }
        forEach(layers, function(layer) {  
                layer.visible = false;  
            }
        );  
    }  

    function showLayers(layers)  { 
        if(!layers){
            return;
        }
        forEach(layers, function(layer) {  
                layer.visible = true;  
            }
        );   
    }

    var logErr = function (value) {
        log(value, ERROR);
    };

    var log = function (value, level) {
        var prefix = LOG_PREFIX;
        switch (level) {
            case ERROR:
                prefix = ERROR_PREFIX;
                break;
            case INFO:
                prefix = INFO_PREFIX;
                break;
        }

        $.writeln(prefix + value);
    };

    var makeXmlOpenTag = function (name) {
        return "<" + name + ">";
    };

    var makeXmlCloseTag = function (name) {
        return "</" + name + ">";
    };

    var isHeader = function (headerName) {
        if (!headerName) {
            return false;
        }
        for (var i = 0; i < btHeaders.length; i++) {
            if (btHeaders[i] === headerName) {
                return true;
            }
        }
        return false;
    };

    var isCompoundPath = function (path) {
        return startsWith(path, pathSeparator);
    };

    var getBtCallProp = function (propName) {
        if (!btCallProps) {
            return false;
        }

        if (!(propName in btCallProps)) {
            return false;
        }

        return btCallProps[propName];
    };

    var getBtDocument = function () {
        var propDocName = getBtCallProp(BT_PROP_DOC_NAME);
        if (!propDocName) {
            propDocName = NAME_ACTIVE_DOC;
        }
        return getDocument(propDocName);
    };

    var getBtDestDocument = function () {
        var propDocName = getBtCallProp(BT_PROP_DEST_DOC_NAME);
        if (!propDocName) {
            propDocName = NAME_ACTIVE_DOC;
        }
        return getDocument(propDocName);
    };

    var getDocument = function (docName) {
        if (!docName || NAME_ACTIVE_DOC === docName) {
            return getActiveDoc();
        }

        var docs = getOpenDocs();
        if (!docs) {
            return false;
        }

        for (var i = 0; i < docs.length; i++) {
            var doc = docs[i];
            if (doc.name === docName) {
                return doc;
            }
        }

        return false;
    };

    var appendBtMsgHeader = function (headerName, value, msg) {
        if (!headerName || !value || !isHeader(headerName)) {
            return msg;
        }

        var header = makeXmlOpenTag(headerName) + value + makeXmlCloseTag(headerName);
        return  header + msg;
    };

    var getBtMsgHeaders = function (content) {
        if (!content) {
            return BT_CALL_UNKNOWN;
        }

        var headers = [];

        for (var i = 0; i < btHeaders.length; i++) {
            var header = btHeaders[i];
            var prefix = makeXmlOpenTag(header);
            var suffix = makeXmlCloseTag(header);

            if (!content.lastIndexOf(prefix, 0) === 0 ||
                    !content.indexOf(suffix) > 0) {
                return BT_CALL_UNKNOWN;
            }

            var headerValue = content.substring(prefix.length, content.indexOf(suffix));
            headers.push(new BtHeader(header, headerValue));
        }

        return headers;
    };

    var naturalCompare = function (a, b) {
        var ax = [], bx = [];

        if (!a.name && !b.name) {
            return 0;
        }
        if (a.name && !b.name) {
            return 1;
        }
        if (!a.name && b.name) {
            return -1;
        }
        var aName = a.name;
        var bName = b.name;
        aName.replace(/(\d+)|(\D+)/g, function (_, $1, $2) {
            ax.push([$1 || Infinity, $2 || ""])
        });
        bName.replace(/(\d+)|(\D+)/g, function (_, $1, $2) {
            bx.push([$1 || Infinity, $2 || ""])
        });

        while (ax.length && bx.length) {
            var an = ax.shift();
            var bn = bx.shift();
            var nn = (an[0] - bn[0]) || an[1].localeCompare(bn[1]);
            if (nn)
                return nn;
        }

        return ax.length - bx.length;
    };
   
    var replaceAll = function (inString, replaceValue, replaceWith) {
        if(!inString || !replaceValue || !replaceWith) {
            return inString;
        }
        return inString.replace(new RegExp(replaceValue, 'g'), replaceWith);
    };

    var sortLayersByName = function (bars) {
        if (!isArray(bars)) {
            return bars;
        }

        bars.sort(naturalCompare);

        return bars;
    };

    var removeBtMsgHeaders = function (content) {
        if (!content) {
            return BT_CALL_UNKNOWN;
        }

        var ret = content;

        for (var i = 0; i < btHeaders.length; i++) {
            var header = btHeaders[i];
            var suffix = makeXmlCloseTag(header);
            var idx = ret.indexOf(suffix);
            if (idx > 0) {
                ret = ret.substr(idx + suffix.length);
            }
        }

        return ret;
    };

    var saveToFile = function (filePath, content) {
        if (!filePath || !content) {
            return;
        }
        try {
            log("Saving file: " + filePath);
            var destFile = new File(filePath);
            if(destFile.error){
                showAlertInternal("Failed to open file " + filePath + " error: " + destFile.error);
                return;
            }
            
            destFile.open('w');
            destFile.write(content);
            destFile.close();
        } catch (e) {
            logErr("Failed to save file " + filePath);
        }
    };
    
    var appendLineInFile = function (filePath, content) {
        if (!filePath || !content) {
            return;
        }
        try {
            log("Saving file: " + filePath);
            var destFile = new File(filePath);
            if(destFile.error){
                showAlertInternal("Failed to open file " + filePath + " error: " + destFile.error);
                return;
            }
            
            destFile.open('a');
            destFile.write(content);
            destFile.close();
        } catch (e) {
            logErr("Failed to save file " + filePath);
        }
    };
    
    var writeLinesInFile = function (filePath, lines) {
        if (!filePath || !lines || !isArray(lines)) {
            return;
        }
        try {
            log("Saving file: " + filePath);
            var destFile = new File(filePath);
            if(destFile.error){
                showAlertInternal("Failed to open file " + filePath + " error: " + destFile.error);
                return;
            }
            
            destFile.open('w');
            for(var i = 0; i < lines.length; i++){
                destFile.write(lines[i]);
                destFile.write(NEW_LINE);
            }
            destFile.close();
        } catch (e) {
            logErr("Failed to save file " + filePath);
        }
    };
    
    var writeLinesInOpenFile = function (destFile, lines) {
        if (!destFile || !destFile.exists || !lines || !isArray(lines)) {
            return;
        }
        try {
            for(var i = 0; i < lines.length; i++){
                var line = lines[i];
                if(i === 0 && scoreFileHeaderWritten){
                    continue;
                }

                destFile.write(line);
                destFile.write(NEW_LINE);
                
                if(i === 0){
                    scoreFileHeaderWritten = true;
                }
            }
        } catch (e) {
            logErr("Failed to save file " + destFile);
        }
    };
    
    var openFileForWrite = function (filePath) {
        if (!filePath) {
            return;
        }
        try {
            log("Opening file for write: " + filePath);
            var destFile = new File(filePath);
            if(destFile.error){
                showAlertInternal("Failed to open file " + filePath + " error: " + destFile.error);
                return;
            }
            
            destFile.open('w');
            return destFile;
        } catch (e) {
            logErr("Failed to open file " + filePath);
        }
    };
    
    var closeFile = function (destFile) {
        if (!destFile) {
            return;
        }
        try {
            log("Closing file: " + destFile);
            destFile.close();
        } catch (e) {
            logErr("Failed to close file " + destFile);
        }
    };

    var loadXmlFile = function (filePath) {
        if (!filePath) {
            return;
        }
        try {
            log("Opening file: " + filePath);
            var destFile = new File(filePath);
            destFile.open('r');
            var ret = destFile.read();
            destFile.close();
            if (ret) {
                return ret;
            }
        } catch (e) {
            logErr("Failed to save file " + filePath + " :" + e);
        }
        return NAME_ERROR;
    };

    var createFilePath = function (filePath) {
        if (!filePath) {
            filePath = defaultSaveFilePath;
        }

        if (!endsWith(filePath, EXT_XML)) {
            filePath = filePath + EXT_XML;
        }
        return filePath;
    };
    
    var getFileExportOptions = function(propFormat){
        var options;
        switch(propFormat){
            case NAME_SVG:
                options = new ExportOptionsSVG();
                options.embedRasterImages = true; 
                options.embedAllFonts = true; 
                options.compressed = false;
                options.fontSubsetting = SVGFontSubsetting.GLYPHSUSED;
                break;
            default:
                options = new ExportOptionsPNG24();  
                options.antiAliasing = true;  
                options.transparency = true;  
                options.artBoardClipping = false;
                break;
        }
        
        return options;
    };
    
    var getFileExportType = function(propFormat){
        var type;
        switch(propFormat){
            case NAME_SVG:
                type = ExportType.SVG;
                break;
            default:
                type = ExportType.PNG24;
                break;
        }
        
        return type;
    };

    var getPreviousBarName = function (barName, barNo) {
        if (!barName || !barNo) {
            return false;
        }

        try {
            var index = barName.indexOf(barNo.toString());
            var prefix = barName.substring(0, index);
            var prevBarNo = barNo - 1;
            return prefix + prevBarNo;
        } catch (e) {
            log(e, ERROR);
        }

        return false;
    };
    
    var createLayer = function (name, doc) {
        if (!doc) {
            return;
        }
        var newPageLayer = doc.layers.add();
        newPageLayer.name = name;
        return newPageLayer;
    };

    var removeObject = function (object) {
        if (!object) {
            return;
        }

        object.remove();
    };

    var removeLayer = function (layer) {
        if (!layer) {
            return;
        }
        var isItemLocked = isLocked(layer);
        if (isItemLocked) {
            unlock(layer);
        }

        removeObject(layer);
    };

    var removePageItem = function (item) {
        if (!item) {
            return;
        }
        var layer = item.layer;
        var isItemLocked = isLocked(layer);
        if (isItemLocked) {
            unlock(layer);
        }

        removeObject(item);

        if (isItemLocked) {
            lock(layer);
        }
    };

    var removeArtboard = function (artboard) {
        removeObject(artboard);
    };

    var getUnitMultiplier = function(beatUnit, baseBeatUnit){
        return Math.round(baseBeatUnit/beatUnit);
    };

    var removePageItems = function (layer) {
        if (!layer || !layer.pageItems) {
            return;
        }
        var len = layer.pageItems.length;
        var toRemove = [];
        for (var i = 0; i < len; i++) {
            var item = layer.pageItems[i];
            if (!item) {
                continue;
            }
            toRemove.push(item);
        }

        for (var i = 0; i < toRemove.length; i++) {
            var item = toRemove[i];
            removePageItem(item);
        }
    };

    var moveToLayer = function (item, targetLayer) {
        if (!item || !targetLayer) {
            log("moveLayer: Invalid layers", ERROR);
            return;
        }
        var isItemLocked = isLocked(targetLayer);
        if (isItemLocked) {
            unlock(targetLayer);
        }

        item.move(targetLayer, ElementPlacement.PLACEATBEGINNING);

        if (isItemLocked) {
            lock(targetLayer);
        }
    };

    var lock = function (layer) {
        if (!layer) {
            return;
        }
        layer.locked = true;
    };

    var unlock = function (layer) {
        if (!layer) {
            return;
        }
        layer.locked = false;
    };

    var isLocked = function (layer) {
        if (!layer) {
            return false;
        }

        return layer.locked;
    };

    var lockLayer = function (layer) {
        if (!layer) {
            return;
        }
        layer.locked = true;
    };

    var createLayerInParent = function (name, parent, doc) {
        if (!doc) {
            return false;
        }

        var layer = createLayer(name, doc);

        if (parent) {
            moveToLayer(layer, parent);
        }
        return layer;
    };

    var addXmlToLayer = function (layer, xmlElement, doc) {
        if (!xmlElement || !xmlElement.localName()) {
            return;
        }

        var newLayer = createLayerInParent(xmlElement.localName(), layer, doc);

        if (!xmlElement.hasComplexContent()) {
            return;
        }
        var kids = xmlElement.children();
        for (var i = 0; i < kids.length(); i++) {
            addXmlToLayer(newLayer, kids[i], doc);
        }
    };

    var addXmlModelToLayer = function (propXmlModel, layer) {
        if (!propXmlModel) {
            return;
        }

        var aDoc = getActiveDoc();
        if (!aDoc) {
            return;
        }

        addXmlToLayer(layer, propXmlModel, aDoc);
    };
    
    var createFileName = function(doc, page, instrumentName, scoreName){
        var name = "";
        
        if(scoreName){
            name += scoreName;
        } else {
            if(doc){
                name += doc.name;
                if(contains(name,DOT)){
                    var index = name.indexOf(DOT);
                    name = name.substring(0, index);
                }
            }
        }
        if(instrumentName){
            name += UNDERSCORE + instrumentName;
        }
        if(page){
            name += UNDERSCORE + page.name;
        }
        
        name = replaceAll(name, SPACE, UNDERSCORE);
        
        return name;
    };

    var getActiveDocName = function () {
        var adoc = getActiveDoc();
        if (!adoc) {
            return NAME_ERROR;
        }
        return adoc.name;
    };

    var getActiveDoc = function () {
        if (!theApp || !theApp.activeDocument) {
            return false;
        }
        return theApp.activeDocument;
    };

    var getOpenDocNames = function () {
        var documents = getOpenDocs();

        var ret = [];
        if (!documents) {
            return ret;
        }

        for (var i = 0; i < documents.length; i++) {
            ret.push(documents[i].name);
        }

        return ret;
    };

    var getOpenDocs = function () {
        if (!theApp) {
            return false;
        }

        var ret = [];

        var documents = theApp.documents;
        if (!documents) {
            return ret;
        }

        for (var i = 0; i < documents.length; i++) {
            ret.push(documents[i]);
        }

        return ret;
    };
    
    var getBarNoFromName = function (barName){
        if(!barName || !startsWith(barName, barNamePrefix)){
            return null;
        }
        
        var barNoStr = barName.substring(barNamePrefix.length);
        return parseInt(barNoStr);        
    };

    var createBarNameFromNo = function (barNo){
        return barNamePrefix + barNo;    
    };
    
    var getPageNoFromName = function (pageName){
        if(!pageName || !startsWith(pageName, pageNamePrefix)){
            return null;
        }
        
        var pageNoStr = pageName.substring(pageNamePrefix.length);
        return parseInt(pageNoStr);        
    };

    var createNewBarLayer = function (parent, barName, doc) {
        if (!parent || !barName) {
            log("createNewBar: invalid inputes");
            return false;
        }
        var barLayer = createLayer(barName, doc);
        moveToLayer(barLayer, parent);

        createLayerInParent(NAME_BARINFO, barLayer, doc);

        createLayerInParent(NAME_BEATLINES, barLayer, doc);

        createLayerInParent(NAME_TIMESIG, barLayer, doc);

        createLayerInParent(NAME_TEMPO, barLayer, doc);

        createLayerInParent(NAME_NOTATION, barLayer, doc);

        return barLayer;
    };

    var deepCopyLayer = function (layer, targetLayer, xOffset, yOffset, doc, filter) {
        if (!layer || !targetLayer) {
            log("invalid layers", ERROR);
            return;
        }

        if (layer.layers && layer.layers.length > 0) {
            var layerLen = layer.layers.length;

            var i = layerLen - 1;
            for (i; i > -1; i--) {
                var sublayer = layer.layers[i];
                var newSubLayer = createLayerInParent(sublayer.name, targetLayer, doc);
                deepCopyLayer(sublayer, newSubLayer, xOffset, yOffset, doc, filter);
            }
        }

        if (isNamedItemFiltered(layer, filter)) {
            return;
        }

        deepCopyPageItems(layer, targetLayer, xOffset, yOffset, filter);
        var isLayerLocked = isLocked(layer);
        if (isLayerLocked) {
            lock(targetLayer);
        }
    };

    var isNamedItemFiltered = function (item, filter) {
        if (!item || !item.name || !filter) {
            return false;
        }
        return isValueInList(filter, item.name);
    };

    var isValueInList = function (list, value) {
        if (!isArray(list) || !value) {
            return false;
        }

        for (var i = 0; i < list.length; i++) {
            if (list[i] === value) {
                return true;
            }
        }

        return false;
    };

    var isRootLayerName = function (layerName) {
        return SLASH === layerName;
    };

    var deepCopyPageItems = function (layer, targetLayer, xOffset, yOffset, filter) {
        var pageItemsLen = layer.pageItems.length;

        var i = pageItemsLen - 1;
        for (i; i > -1; i--) {
            var pageItem = layer.pageItems[i];
            copyPageItem(pageItem, targetLayer, xOffset, yOffset, filter);
        }
    };

    var copyPageItem = function (pageItem, targetLayer, xOffset, yOffset, filter) {
        if (!pageItem || !targetLayer) {
            log("invalid inputs", ERROR);
            return;
        }
        if (isNamedItemFiltered(pageItem, filter)) {
            return;
        }

        var parentLayer = pageItem.layer;
        var isLayerLocked = isLocked(parentLayer);
        if (isLayerLocked) {
            unlock(parentLayer);
        }

        var isTargetLayerLocked = isLocked(targetLayer);
        if (isTargetLayerLocked) {
            unlock(targetLayer);
        }

        var isItemLocked = isLocked(pageItem);
        if (isItemLocked) {
            unlock(pageItem);
        }

        var dupRef = pageItem.duplicate();
        dupRef.moveToBeginning(targetLayer);
        dupRef.translate(xOffset, yOffset);

        if (isItemLocked) {
            lock(pageItem);
        }
        if (isTargetLayerLocked) {
            lock(targetLayer);
        }
        if (isLayerLocked) {
            lock(parentLayer);
        }

        return dupRef;
    };

    var printUIProps = function (uiObj) {
        if (!uiObj) {
            return;
        }
        log(uiObj.reflect.name + " Properties: --------------");
        var props = uiObj.reflect.properties;
        var ary = [];
        for (var i = 0; i < props.length; i++) {
            try {
                ary.push(props[i].name + ": " + uiObj[props[i].name]);
            } catch (_) {
            }
        }
        ary.sort();
        for (var i = 0; i < ary.length; i++) {
            log(ary[i]);
        }
    };

    var printUIMethods = function (uiObj) {
        if (!uiObj) {
            return;
        }
        var methods = uiObj.reflect.methods.sort();
        log(uiObj.reflect.name + " Methods: --------------");
        for (var i = 0; i < methods.length; i++) {
            log(methods[i].name);
        }
    };

    var printFonts = function () {
        if(!availableFonts) {
            return;
        }
        var count = availableFonts.length;
        for(var i = 0; i< count; i++) {
            var font = availableFonts[i];
            log("Font: " + font.name + " style: " + font.style  + " family: " + font.family + " typename: " + font.typename);
        }

    };

    function NumbersFilter() {
        var propValues = false;
        var isAllValues = false;
        var values = [];
        this.init = function (propValues) {
            this.propValues = propValues;
            if (!propValues) {
                this.isAllValues = true;
            } else {
                this.parseProps();
            }
        };
        this.parseProps = function () {
            if (contains(this.propValues, DASH)) {
                this.values = createRangeFromString(this.propValues);
            } else if (contains(this.propValues, COMMA)) {
                this.values = convertCsvToIntArr(this.propValues);
            } else {
                var val = parseInt(this.propValues);
                var vals = [val];
                this.values = vals;
            }
        };
        this.isValid = function (value) {
            if (this.isAllValues) {
                return true;
            }

            return isValueInList(this.values, value);
        };
    };

    return {
        btHeaderCaller: BT_HEADER_CALLER,
        btStatic: {
            activeDoc: NAME_ACTIVE_DOC,
            right: NAME_RIGHT,
            below: NAME_BELOW
        },
        btModel: {
            startLineName: NAME_START,
            endLineName: NAME_END,
            barnumName: NAME_BARNUM,
            pagenumName: NAME_PAGENUM,
            barName: NAME_BAR,
            beatlineName: NAME_BEATLINE,
            beatlineZeroName: NAME_BEATLINE + "0",
            isUseBeatline: IS_USE_BEATLINE,
            beatDistancePxl: CROTCHET_PXL,
            firstBeatOffsetPxl: FIRST_BEAT_PXL,
            pageName: NAME_PAGE,
            pageOffset: ARTBOARD_SPACER,
            notationName: NAME_NOTATION,
            prestartName: NAME_PRESTART,
            fontName: DEFAULT_FONT,
            fontSize: DEFAULT_TXT_SIZE,
            barnumXOffset: BAR_NO_TXT_X_OFFSET,
            barnumYOffset: BAR_NO_TXT_Y_OFFSET,
            barNamePrefix: NAME_BAR,
            pngName: NAME_PNG,
            svgName: NAME_SVG
        },
        btCalls: {
            btGetOpenDocsCall: BT_CALL_GET_OPEN_DOCS,
            btGetLayerXmlCall: BT_CALL_GET_LAYER_XML,
            btGetActiveDocNameCall: BT_CALL_GET_ACTIVE_DOC_NAME,
            btExportLayerXmlCall: BT_CALL_EXPORT_LAYER_XML,
            btDeleteLayerCall: BT_CALL_DELETE_LAYER,
            btCopyLayerCall: BT_CALL_COPY_LAYER,
            btInsertModelXmlCall: BT_CALL_INSERT_MODEL_XML,
            btLoadLayerXmlCall: BT_CALL_LOAD_LAYER_XML,
            btCreatePageFromPageCall: BT_CALL_CREATE_PAGE_FROM_PAGE,
            btCreateBarCall: BT_CALL_CREATE_BAR,
            btCopyBarCall: BT_CALL_COPY_BAR,
            btDeleteBarCall: BT_CALL_DELETE_BAR,
            btDeletePageCall: BT_CALL_DELETE_PAGE,
            btUpdatePageCall: BT_CALL_UPDATE_PAGE,
            btExportFilesCall: BT_CALL_EXPORT_FILES
        },
        btProps: {
            btPropDocName: BT_PROP_DOC_NAME,
            btPropDestDocName: BT_PROP_DEST_DOC_NAME,
            btPropItemName: BT_PROP_ITEM_NAME,
            btPropNewItemName: BT_PROP_NEW_ITEM_NAME,
            btPropOldItemName: BT_PROP_OLD_ITEM_NAME,
            btPropItemNumber: BT_PROP_ITEM_NUMBER,
            btPropArtbName: BT_PROP_ARTB_NAME,
            btPropNewArtbName: BT_PROP_NEW_ARTB_NAME,
            btPropXMLModel: BT_PROP_XML_MODEL,
            btPropFilePath: BT_PROP_FILE_PATH,
            btPropScoreName: BT_PROP_SCORE_NAME,
            btPropXOffset: BT_PROP_X_OFFSET,
            btPropYOffset: BT_PROP_Y_OFFSET,
            btPropNameFilter: BT_PROP_NAME_FILTER,
            btPropNumerator: BT_PROP_NUMERATOR,
            btPropDenominator: BT_PROP_DENOMINATOR,
            btPropTempo: BT_PROP_TEMPO,
            btPropPosition: BT_PROP_POSITION,
            btPropShowTimesig: BT_PROP_SHOW_TIMESIG,
            btPropCreateBeatlines: BT_PROP_CREATE_BEATLINES,
            btPropFormat: BT_PROP_FORMAT,
            btPropExportScore: BT_PROP_EXPORT_SCORE,
            btPropExportParts: BT_PROP_EXPORT_PARTS,
            btPropExportBeatlines: BT_PROP_EXPORT_BEATLINES
        },
        btPrintUiInfo: function (uiObj) {
            printUIProps(uiObj);
            printUIMethods(uiObj);
        },
        getPathSeparator: function () {
            return pathSeparator;
        },
        setWindow: function (win) {
            theWindow = win;
        },
        getBtHeaders: function (content) {
            return getBtMsgHeaders(content);
        },
        getAvaileblBtHeaders: function (content) {
            return btHeaders;
        },
        removeBtHeaders: function (content) {
            return removeBtMsgHeaders(content);
        },
        addBtHeader: function (headerName, headerValue, msg) {
            appendBtMsgHeader(headerName, headerValue, msg);
        },
        setBtCallProperties: function (callProperties) {
            btCallProps = callProperties;
        },
        createBtCallProperties: function () {
            return new BtCallProperties();
        },
        logit: function (value) {
            log(value);
        },
        logFonts: function () {
            printFonts();
        },
        logError: function (value) {
            logErr(value);
        },
        isArr: function (value) {
            return isArray(value);
        },
        isErrorResponse: function (value) {
            return isErrResponse(value);
        },
        showAlert: function (content) {
            showAlertInternal(content);
        },
        getActiveDoc: function () {
            getActiveDoc();
        },
        createProcessor: function (procType) {
            if (!procType) {
                logErr("ProcessorFactory Invalid processor type");
                return false;
            }
            switch (procType) {
                case PROC_RENAME:
                    var RenameProcessor = function () {
                        this.name;
                        this.newName;
                    };
                    RenameProcessor.prototype.process = function (layer) {
                        if (!layer || !this.name || !this.newName) {
                            return;
                        }

                        var isItemLocked = isLocked(layer);
                        if (isItemLocked) {
                            unlock(layer);
                        }

                        if (this.name === layer.name) {
                            log("Renaming layer: " + layer.name + " to: " + this.newName);
                            layer.name = this.newName;
                        }

                        if (isItemLocked) {
                            lock(layer);
                        }
                    };

                    return new RenameProcessor();
                case PROC_FIND_LAYERS_BY_NAME:
                    var FindLayersProcessor = function () {
                        this.name;
                        this.result = new Array();
                    };
                    FindLayersProcessor.prototype.process = function (layer) {
                        if (!layer || !this.name) {
                            return;
                        }
                        if (this.name === layer.name) {
                            this.result.push(layer);
                        }
                    };

                    return new FindLayersProcessor();
                case PROC_FIND_LAYERS_BY_REGEX:
                    var FindLayersRegexProcessor = function () {
                        this.regex;
                        this.result = new Array();
                    };
                    FindLayersRegexProcessor.prototype.process = function (layer) {
                        if (!layer || !this.regex || !(this.regex instanceof RegExp)) {
                            return;
                        }
                        var layerName = layer.name;
                        if (!layerName) {
                            return false;
                        }

                        if (this.regex.test(layerName)) {
                            this.result.push(layer);
                        }
                    };

                    return new FindLayersRegexProcessor();
                case BUILD_LAYERS_XML:
                    var BuildLayerXMLProcessor = function () {
                        this.xml;
                        this.scorelib;
                    };
                    BuildLayerXMLProcessor.prototype.process = function (layer) {
                        if (!layer) {
                            logErr("Invalid xml element or layer: " + layer);
                            return;
                        }
                        var scorelib = this.scorelib;
                        if (!layer.parent || !this.xml) {
                            this.xml = scorelib.buildLayerXMLElement(layer);
                        } else {
                            var parent = scorelib.findLayerParentXMLElement(layer, this.xml);
                            parent.appendChild(scorelib.buildLayerXMLElement(layer));
                        }
                    };

                    return new BuildLayerXMLProcessor();
                default:
                    logErr("Could not find processor type: " + procType);
            }
            ;
        },
        processAllLayers: function (layer, processor) {
            if (!layer || !processor || (typeof processor.process !== 'function')) {
                logErr("processAllLayers invalid inputs");
                return;
            }

            if (!layer.layers) {
                return;
            }

            if (isLayer(layer) || isDoc(layer)) {
                processor.process(layer);
            }

            var layerLen = layer.layers.length;
            for (var i = 0; i < layerLen; i++) {
                var sublayer = layer.layers[i];
                this.processAllLayers(sublayer, processor);
            }
        },
        getOrCreateCompoundLayer: function (path, doc) {
            if (!doc || !path) {
                log("invalid inputs", ERROR);
                return;
            }

            if (startsWith(path, pathSeparator)) {
                path = path.substring(1);
            }
            var names = path.split(pathSeparator);
            var parent = doc;
            for (var i = 0; i < names.length; i++) {
                var name = names[i];
                var layer = this.findLayerByName(name, parent);
                if (!layer) {
                    layer = createLayerInParent(name, parent, doc);
                }
                parent = layer;
            }
            return parent;
        },
        findLayerByCompundPath: function (path, doc) {
            if (!doc || !path) {
                log("invalid inputs", ERROR);
                return;
            }

            if (startsWith(path, pathSeparator)) {
                path = path.substring(1);
            }

            var names = path.split(pathSeparator);
            var parent = doc;
            for (var i = 0; i < names.length; i++) {
                var name = names[i];
                parent = this.findLayerByName(name, parent);
            }
            return parent;
        },
        findLayersByName: function (name, doc) {
            if (!doc || !name) {
                log("invalid inputs", ERROR);
                return;
            }
            var processor = this.createProcessor(PROC_FIND_LAYERS_BY_NAME);
            if (!processor) {
                return;
            }

            processor.name = name;

            this.processAllLayers(doc, processor);
            return processor.result;
        },
        getOrCreateLayer: function (name, doc) {
            if (!name) {
                return false;
            }
            if (isCompoundPath(name)) {
                return this.getOrCreateCompoundLayer(name, doc);
            }
            var re = new RegExp(name);
            var layer = this.findLayerByName(re, doc);
            if (!layer) {
                layer = createLayer(name, doc);
            }
            return layer;
        },
        findArtboard: function (name, doc) {
            if (!name || !doc) {
                return false;
            }

            return doc.artboards.getByName(name);
        },
        findLayer: function (name, doc) {
            if (!name) {
                return false;
            }
            if (isCompoundPath(name)) {
                return this.findLayerByCompundPath(name, doc);
            }
            var re = new RegExp(name);
            return this.findLayerByRegex(re, doc);
        },
        findLayerByName: function (name, doc) {
            var layers = this.findLayersByName(name, doc);
            if (!layers || !isArray(layers) || !layers[0]) {
                log("Could not find Layer for page: " + name, ERROR);
                return false;
            }

            return layers[0];
        },
        findLayersByRegex: function (regex, layer) {
            if (!layer || !regex || !(regex instanceof RegExp)) {
                log("invalid inputs", ERROR);
                return;
            }
            var processor = this.createProcessor(PROC_FIND_LAYERS_BY_REGEX);
            if (!processor) {
                return;
            }

            processor.regex = regex;

            this.processAllLayers(layer, processor);
            return processor.result;
        },
        findLayerByRegex: function (regex, layer) {
            var layers = this.findLayersByRegex(regex, layer);
            if (!layers || !isArray(layers) || !layers[0]) {
                log("Could not find Layer for regex: " + regex, INFO);
                return false;
            }

            return layers[0];
        },
        renameLayers: function (name, newName, doc) {
            if (!doc || !name || !newName) {
                log("invalid inputs", ERROR);
                return;
            }
            var processor = this.createProcessor(PROC_RENAME);
            if (!processor) {
                return;
            }

            processor.name = name;
            processor.newName = newName;

            this.processAllLayers(doc, processor);
        },
        renameOnlyLayers: function (layers, newName) {
            if (!layers || !newName) {
                log("invalid inputs", ERROR);
                return;
            }
            for (var i = 0; i < layers.length; i++) {
                var layer = layers[i];
                if (!layer) {
                    continue;
                }
                var isItemLocked = isLocked(layer);
                if (isItemLocked) {
                    unlock(layer);
                }

                layer.name = newName;

                if (isItemLocked) {
                    lock(layer);
                }
            }

        },
        findLayerParentXMLElement: function (layer, xml) {
            if (!layer || !xml) {
                return false;
            }

            var sQuery = "";
            var parentLayer = layer.parent;
            while (parentLayer && parentLayer.name && (isLayer(parentLayer) || isDoc(parentLayer))) {
                sQuery = SLASH + parentLayer.name + sQuery;
                parentLayer = parentLayer.parent;
            }

            return xml.xpath(sQuery);
        },
        buildLayerXMLElement: function (layer) {
            if (!layer) {
                return false;
            }
            var elementName;
            if (layer.name) {
                elementName = layer.name;
            } else {
                elementName = NAME_LAYER;
            }

            elementName = elementName.replace(/\s/g, "_");

            var content = makeXmlOpenTag(elementName) + makeXmlCloseTag(elementName);
            return new XML(content);
        },
        exportLayerXML: function (propXmlFilePath, doc) {
            var processor = this.createProcessor(BUILD_LAYERS_XML);
            if (!processor) {
                logErr("Failed to build processor");
                return;
            }

            processor.scorelib = this;

            this.processAllLayers(doc, processor);

            var sXml = processor.xml.toXMLString();


            var filePath = createFilePath(propXmlFilePath);

            saveToFile(filePath, sXml);
        },
        loadLayerXML: function (propXmlFilePath) {
            var filePath = createFilePath(propXmlFilePath);
            return loadXmlFile(filePath);
        },
        insertLayerXML: function (propXmlModel, name, aDoc) {
            if (!propXmlModel) {
                logErr("Filed to insert, missing input data");
                return BT_RESP_ERROR;
            }

            var layer;
            if (name) {
                layer = this.findLayer(name, aDoc);
            }

            addXmlModelToLayer(propXmlModel, layer);

            return BT_RESP_OK;
        },
        getOpenDocsCsv: function () {
            var docs = getOpenDocNames();
            var out = "";

            if (!docs) {
                return out;
            }

            if (isArray(docs)) {
                if (docs.length > 1) {
                    out = docs.join(",");
                } else {
                    out = docs[0];
                }
            } else {
                out = "";
            }

            return out;
        },
        setItemContents: function (item, value) {
            var isItemLocked = isLocked(item);
            if (isItemLocked) {
                unlock(item);
            }

            item.contents = value;

            if (isItemLocked) {
                lock(item);
            }
        },
        setItemName: function (item, value) {
            var isItemLocked = isLocked(item);
            if (isItemLocked) {
                unlock(item);
            }

            item.name = value;

            if (isItemLocked) {
                lock(item);
            }
        },
        translateItem: function (item, xOffset, yOffset) {

            var layer = item.layer;
            var isLayerLocked = isLocked(layer);
            if (isLayerLocked) {
                unlock(layer);
            }

            var isItemLocked = isLocked(item);
            if (isItemLocked) {
                unlock(item);
            }

            item.translate(xOffset, yOffset);

            if (isItemLocked) {
                lock(item);
            }
            if (isLayerLocked) {
                lock(layer);
            }
        },
        deleteLayers: function (name, aDoc) {
            if (!name) {
                return BT_RESP_ERROR;
            }

            var layers = [];

            if (isCompoundPath(name)) {
                var layer = this.findLayerByCompundPath(name, aDoc);
                layers.push(layer);
            } else {
                var re = new RegExp(name);
                layers = this.findLayersByRegex(re, aDoc);
            }

            for (var i = 0; i < layers.length; i++) {
                removeLayer(layers[i]);
            }

            return BT_RESP_OK;
        },
        copyLayer: function (name, destinationLayerName, xOffset, yOffset, filter, sourceDoc, destDoc) {
            if (!name) {
                return BT_RESP_ERROR;
            }

            var source = this.findLayer(name, sourceDoc);
            if (!source) {
                return BT_RESP_ERROR;
            }

            var sourceCopy;
            if (isRootLayerName(destinationLayerName)) {
                sourceCopy = createLayer(source.name, destDoc);
            } else {
                var destination = this.getOrCreateLayer(destinationLayerName, destDoc);
                if (!destination) {
                    return BT_RESP_ERROR;
                }

                sourceCopy = createLayerInParent(source.name, destination, destDoc);
            }

            deepCopyLayer(source, sourceCopy, xOffset, yOffset, destDoc, filter);

            return BT_RESP_OK;
        },
        createTextInLayer: function (txt, layer, name, position, font, fontSize, doc) {
            if (!layer || !txt) {
                return false;
            }
            var textItem = this.createText(txt, position, font, fontSize, doc);
            moveToLayer(textItem, layer);

            this.setItemName(textItem, name);

            return textItem;
        },
        createText: function (txt, position, font, fontSize, doc) {
            if (!position || !isArray(position) || position.length !== 2) {
                position = [0, 0];
            }

            var textItem = doc.textFrames.pointText(position);
            this.setItemContents(textItem, txt);

            var fontStyle = textItem.textRange.characterAttributes;
            fontStyle.textFont = font;
            fontStyle.size = fontSize;

            return textItem;
        },
        findLayerPageItems: function (layer, pgItemName, itemList) {
            if (!layer || !pgItemName || !layer.pageItems) {
                return;
            }
            if (!itemList || !isArray(itemList)) {
                itemList = [];
            }

            for (var i = 0; i < layer.pageItems.length; i++) {
                var pageItem = layer.pageItems[i];
                if (!pageItem || pgItemName !== pageItem.name) {
                    continue;
                }
                itemList.push(pageItem);
            }

            return itemList;
        },
        findLayerPageItem: function (layer, pgItemName, itemList) {
            var pgItems = this.findLayerPageItems(layer, pgItemName, itemList);
            if (!pgItems || !isArray(pgItems)) {
                log("Could not find pageItem for layer: " + layer, ERROR);
                return false;
            }

            return pgItems[0];
        },
        findPageItems: function (layer, name, itemList) {
            this.findLayerPageItems(layer, name, itemList);

            if (!layer.layers) {
                return itemList;
            }

            for (var j = 0; j < layer.layers.length; j++) {
                this.findPageItems(layer.layers[j], name, itemList);
            }
        },
        findStartLine: function (barLayer) {
            return this.findNamedPathItem(barLayer, startLineName);
        },
        findEndLine: function (barLayer) {
            return this.findNamedPathItem(barLayer, endLineName);
        },
        findPageEndLine: function (staveLayer) {
            return this.findNamedPathItem(staveLayer, pageEndLineName);
        },
        findZeroBeatLine: function (barLayer) {
            return this.findNamedPathItem(barLayer, beatlineZeroName);
        },
        findBarnum: function (barLayer) {
            return this.findNamedPathItem(barLayer, barnumName);
        },
        findPagenum: function (staveLayer) {
            return this.findNamedPathItem(staveLayer, pagenumName);
        },
        findTimesigNum: function (barLayer) {
            return this.findNamedPathItem(barLayer, timeSigNum);
        },
        findBeatlineItems: function (barLayer, doc) {
            var itemList = [];
            var beatlinesLayer = this.getBeatlinesLayer(barLayer, doc);
            if(!beatlinesLayer){
                return itemList;
            }
            for (var i = 0; i < beatlinesLayer.pageItems.length; i++) {
                var pageItem = beatlinesLayer.pageItems[i];
                if (!pageItem || !startsWith(pageItem.name, beatlineName) ){
                    continue;
                }
                itemList.push(pageItem);
            }
            return itemList;
        },
        findEventItems: function (barLayer, doc) {
            var itemList = [];
            var eventsLayer = this.getEventsLayer(barLayer, doc);
            if(!eventsLayer){
                return itemList;
            }
            for (var i = 0; i < eventsLayer.pageItems.length; i++) {
                var pageItem = eventsLayer.pageItems[i];
                if (!pageItem){
                    continue;
                }
                itemList.push(pageItem);
            }
            return itemList;
        },
        findBpm: function (barLayer) {
            return this.findNamedPathItem(barLayer, bpm);
        },
        findTempoNoteValue: function (barLayer) {
            return this.findNamedPathItem(barLayer, noteVal);
        },
        findTimesigDenom: function (barLayer) {
            return this.findNamedPathItem(barLayer, timeSigDenom);
        },
        findBeatLine: function (barLayer, beatNo) {
            var name = this.getBeatlineName(beatNo);
            return this.findNamedPathItem(barLayer, name);
        },
        findNamedPathItem: function (layer, itemName) {
            var itemList = [];
            this.findPageItems(layer, itemName, itemList);
            if(itemList.length > 0){
                return itemList[0];
            }
            return null;
        },
        findAllBars: function (pageLayer) {
            return this.findLayersByRegex(BARS_REGEX, pageLayer);
        },
        findAllPages: function (doc) {
            return this.findLayersByRegex(PAGES_REGEX, doc);
        },
        findInstrumentBar: function (instrument, destBarName, doc) {
            var reBar = new RegExp(destBarName);
            var bar = this.findLayerByRegex(reBar, instrument);
            if (bar) {
                return bar;
            }

            var reInst = new RegExp(instrument.name);
            var instrumentLayers = this.findLayersByRegex(reInst, doc);
            if (!instrumentLayers) {
                return false;
            }

            for (var i = 0; i < instrumentLayers.length; i++) {
                var bar = this.findLayerByRegex(reBar, instrumentLayers[i]);
                if (bar) {
                    return bar;
                }
            }

            return false;
        },
        findStaveRim: function (instLayer, doc){
            var staveLayer = this.getStaveLayer(instLayer, doc);
            return this.findNamedPathItem(staveLayer, rim);
        },
        findStaveLayer: function (instLayer, doc){
            return this.getStaveLayer(instLayer, doc);
        },
        findStavePosition: function (barLayer, doc){
            var rim = this.findStaveRim(barLayer, doc);
            if(!rim){
                return null;
            }
            return rim.position;
        },
        findPreviousBars: function (pageLayer) {
            var bars = this.findAllBars(pageLayer);
            bars = sortLayersByName(bars);

            if (!bars) {
                return false;
            }
            var len = bars.length;
            if (len < 1) {
                return false;
            }
            var maxBar = bars[len - 1];
            if (!maxBar || !maxBar.name) {
                return false;
            }

            var name = maxBar.name;
            var last = [];
            for (var j = 0; j < len; j++) {
                if (name === bars[j].name) {
                    last.push(bars[j]);
                }
            }

            return last;
        },
        getNextBeatlinePosition: function (beatLineItem, blIndex, blItems, barIndex, bars, beatDistances) {
            var nextBlPosition = [];
            var nextBlIndex = blIndex + 1;
            var blPosition = beatLineItem.position;
            
            if(nextBlIndex < blItems.length){
                nextBlPosition = blItems[nextBlIndex].position;
                return nextBlPosition;
            }
            
            var nextBarIndex = barIndex + 1;
            if(nextBarIndex < bars.length){
                var nextBar = bars[nextBarIndex];
                var firstBeatItem = this.findBeatLine(nextBar, 1);
                if(firstBeatItem){
                    nextBlPosition = firstBeatItem.position;
                    return nextBlPosition;
                }
            }

            var bar = bars[barIndex];
            var endLine =  this.findEndLine(bar);
            if(endLine){
                nextBlPosition = endLine.position;
            } else {
                var lastDistance = beatDistances[(beatDistances.length -1)];
                nextBlPosition[0] = blPosition[0] + lastDistance;
                nextBlPosition[1] = blPosition[1];
            }
            
            return nextBlPosition;
        },
        getUnitsPerBeat: function (beatIndex, unitsPerBeat) {        
            if(!unitsPerBeat){
                return 1;
            }
            var unitPerBeat;
            if(beatIndex < 0 || beatIndex >= unitsPerBeat.length) {
                unitPerBeat = 1;
            } else {
                unitPerBeat = unitsPerBeat[beatIndex];
            }
            return unitPerBeat;
        },
        getLayerXML: function (doc) {
            var processor = this.createProcessor(BUILD_LAYERS_XML);
            if (!processor) {
                logErr("Failed to build processor");
                return;
            }

            processor.scorelib = this;

            this.processAllLayers(doc, processor);

            var sXml = processor.xml.toXMLString();

            return sXml;
        },
        deletePage: function (propPageName, artboardName, doc) {

            var layer = this.findLayer(propPageName, doc);
            removeLayer(layer);

            var artboard = this.findArtboard(artboardName, doc);
            removeArtboard(artboard);
        },
        updatePage: function (propPageName, artboardName, doc) {

            var pageLayer = this.findLayer(propPageName, doc);
            var instruments = pageLayer.layers;
            if (!instruments || instruments.length < 1) {
                this.showAlert("Could not find any staves on page");
                return;
            }
            for (var i = 0; i < instruments.length; i++) {
                var instrument = instruments[i];
                var staveLayer = this.findStaveLayer(instrument, doc);
                this.updatePagenum(staveLayer, propPageName, doc);
                
            }
            
        },
        setPageNum: function (pageName, staveLayer, doc) {
            if (!pageName || !staveLayer || !doc) {
                return;
            }
            var pageNo = getPageNoFromName(pageName);
            if(!pageNo) {
                pageno = "N/A";
            }

            var pageNoStr = pagenumPrefix + pageNo;

            var pagenum = this.findPagenum(staveLayer);
            if (!pagenum) {
                var endLine = this.findPageEndLine(staveLayer);
                if(!endLine) {
                    return;
                }
                this.createPagenum(pageNoStr, endLine, staveLayer, doc);
            } else {
                this.setItemContents(pagenum, pageNoStr);
            }
        },
        createBarFromPrevious: function (barName, previousBar, filter, doc) {
            if (!barName || !previousBar || !doc) {
                return;
            }

            var stave = previousBar.parent;

            var barLayer = createLayerInParent(barName, stave, doc);

            var startLine = this.findStartLine(previousBar);
            if (!startLine) {
                this.showAlert("Failed to find start line in bar " + previousBar.name);
                return;
            }

            var endLine = this.findEndLine(previousBar);
            if (!endLine) {
                this.showAlert("Failed to find end line in bar " + previousBar.name);
                return;
            }

            var startPosition = startLine.position;
            var endPosition = endLine.position;
            var xOffset = endPosition[0] - startPosition[0];

            deepCopyLayer(previousBar, barLayer, xOffset, 0, doc, filter);
            return barLayer;

        },
        deleteBar: function (barName, propPageName, doc) {
            log("Deleting bar " + barName + " on page: " + propPageName);
            var pageLayer = this.findLayer(propPageName, doc);
            if (!pageLayer) {
                return BT_RESP_ERROR;
            }

            this.deleteLayers(barName, pageLayer);
        },
        filterPages: function (pages, propPages) {
            if (!pages) {
                return pages;
            }

            var filter = new NumbersFilter();
            filter.init(propPages);
            if (filter.isAllValues) {
                return pages;
            }

            var out = [];

            for (var i = 0; i < pages.length; i++) {
                var page = pages[i];
                if (!page || !page.name) {
                    continue;
                }
                var pagePrefix = this.btModel.pageName;
                var name = page.name;
                if (!startsWith(name, pagePrefix)) {
                    continue;
                }

                var pageNoStr = name.substring(pagePrefix.length);
                if (!isNumber(pageNoStr)) {
                    continue;
                }

                var pageNo = parseInt(pageNoStr);
                if (filter.isValid(pageNo)) {
                    out.push(page);
                }
            }

            return out;
        },
        getScoreSaveDir: function(doc){
            if(!doc){
                return ".";
            }
            var afile = doc.fullName;  
            var folder = afile.parent;
            var path = folder.fsName + SLASH + NAME_SCORE + SLASH;
            
            return path;
        },
        exportLayerFile: function(path, propFormat, doc){
            var options = getFileExportOptions(propFormat);
            var type = getFileExportType(propFormat);

            var file = new File(path);
            doc.exportFile(file,type,options); 
        },
        getOrCreateBarLayerChild: function (name, barLayer, doc) {
            var childLayer = this.findLayer(name, barLayer);
            if (!childLayer) {
                childLayer = createLayerInParent(name, barLayer, doc);
            }
            return childLayer;
        },
        getBarLayerChild: function (name, barLayer, doc) {
            return this.findLayer(name, barLayer);
        },
        getBarInfoLayer: function (barLayer, doc) {
            return this.getOrCreateBarLayerChild(NAME_BARINFO, barLayer, doc);
        },
        getBeatlinesLayer: function (barLayer, doc) {
            return this.getOrCreateBarLayerChild(NAME_BEATLINES, barLayer, doc);
        },
        getEventsLayer: function (barLayer, doc) {
            return this.getBarLayerChild(NAME_EVENTS, barLayer, doc);
        },
        getTempoLayer: function (barLayer, doc) {
            return this.getOrCreateBarLayerChild(NAME_TEMPO, barLayer, doc);
        },
        getTimesigLayer: function (barLayer, doc) {
            return this.getOrCreateBarLayerChild(NAME_TIMESIG, barLayer, doc);
        },
        getStaveLayer: function (instLayer, doc) {
            return this.getOrCreateBarLayerChild(NAME_STAVE, instLayer, doc);
        },
        getBeatlineName: function (beatNo) {
            var beatStr = "";
            if (isNumber(beatNo)) {
                beatStr = beatNo.toString();
            }
            return beatlineName + beatStr;
        },
        createBeatline: function (barLayer, zeroBl, beat, doc) {
            var beatlinesLayer = this.getBeatlinesLayer(barLayer, doc);

            var beatline = copyPageItem(zeroBl, beatlinesLayer, 0, 0, false);

            this.setItemName(beatline, this.getBeatlineName(beat));

            return beatline;
        },
        createEndLine: function (barLayer, startLine, doc) {
            var barInfoLayer = this.getBarInfoLayer(barLayer, doc);

            var endline = copyPageItem(startLine, barInfoLayer, 0, 0, false);

            this.setItemName(endline, endLineName);

            return endline;
        },
        createBarnum: function (barNoStr, startLine, barInfoLayer, doc) {
            var startPosition = this.getPosition(startLine);
            var x = startPosition[0] + barnumXOffset;
            var y = startPosition[1] - startLine.height + barnumYOffset;
            var position = [x, y];

            this.createTextInLayer(barNoStr, barInfoLayer, barnumName, position, txtStyle, txtSize, doc);
        },
        createPagenum: function (pageNoStr, endLine, staveLayer, doc) {
            var startPosition = this.getPosition(endLine);
            var x = startPosition[0] - pagenumXOffset;
            var y = startPosition[1] + pagenumYOffset;
            var position = [x, y];

            this.createTextInLayer(pageNoStr, staveLayer, pagenumName, position, pageNoTxtStyle, pageNoTxtSize, doc);
        },
        adjustTimeSigPosition: function (startLine, textItem) {
            var startPosition = this.getPosition(startLine);
            var x = textItem.position[0];

            var width = textItem.width;
            var delta = startPosition[0] - (x + width) - SPACER_PXL;

            this.translateItem(textItem, delta, 0);

        },
        createTimeSigNum: function (numStr, startLine, timesigLayer, doc) {
            var startPosition = this.getPosition(startLine);
            var x = startPosition[0] - timesigNumXOffset;
            var y = startPosition[1] - timesigNumYOffset;
            var position = [x, y];

            var textItem = this.createTextInLayer(numStr, timesigLayer, timeSigNum, position, timeSigTxtStyle, timeSigTxtSize, doc);
            var width = textItem.width;

            if ((x + width) > startPosition[0]) {
                var delta = startPosition[0] - (x + width) + 1;
                this.translateItem(textItem, -delta, 0);
            }
        },
        createTimeSigDenom: function (denomStr, startLine, timesigLayer, doc) {
            var startPosition = this.getPosition(startLine);
            var x = startPosition[0] - timesigDenomXOffset;
            var y = startPosition[1] - timesigDenomYOffset;
            var position = [x, y];

            var textItem = this.createTextInLayer(denomStr, timesigLayer, timeSigDenom, position, timeSigTxtStyle, timeSigTxtSize, doc);
            var width = textItem.width;

            if ((x + width) > startPosition[0]) {
                var delta = startPosition[0] - (x + width) + 1;
                this.translateItem(textItem, -delta, 0);
            }
        },
        createBpm: function (tempoStr, startLine, tempoLayer, doc) {
            var startPosition = this.getPosition(startLine);
            var x = startPosition[0] + bpmXOffset;
            var y = startPosition[1] - bpmYOffset;
            var position = [x, y];

            this.createTextInLayer(tempoStr, tempoLayer, bpm, position, bpmTxtStyle, bpmTxtSize, doc);
        },
        createTempoNoteVal: function (noteValue, startLine, tempoLayer, doc) {
            var startPosition = this.getPosition(startLine);
            var x = startPosition[0] + tempoNoteValXOffset;
            var y = startPosition[1] - tempoNoteValYOffset;
            var position = [x, y];

            this.createTextInLayer(noteValue, tempoLayer, noteVal, position, bpmTxtStyle, bpmTxtSize, doc);
        },
        addStartLine: function (barLayer, startLine, doc) {

            var barInfoLayer = this.getBarInfoLayer(barLayer, doc);
            var isBarInfoLocked = isLocked(barInfoLayer);
            if (isBarInfoLocked) {
                unlock(barInfoLayer);
            }
            var isStartLineLocked = isLocked(startLine);
            if (isStartLineLocked) {
                unlock(startLine);
            }

            var dupRef = startLine.duplicate();
            dupRef.moveToBeginning(barInfoLayer);

            if (isBarInfoLocked) {
                lock(barInfoLayer);
            }
            if (isStartLineLocked) {
                lock(startLine);
            }
        },
        setBeatlinePosition: function (beatline, startLine, beatNo, beatDistances) {
            var beatlinePos = beatline.position;
            var startPos = startLine.position;

            var distance = firstBeatDistancePxl;
            for (var i = 0; i < (beatNo - 1); i++) {
                if (i >= beatDistances.length) {
                    log("Invalid beat number ", ERROR);
                    continue;
                }
                distance = distance + beatDistances[i];
            }

            var xOffset = (startPos[0] + distance) - beatlinePos[0];

            this.translateItem(beatline, xOffset, 0);
        },
        setEndLinePosition: function (endline, startLine, beatNo, beatDistances) {
            var endlinePos = endline.position;
            var startPos = startLine.position;

            var distance = firstBeatDistancePxl;
            for (var i = 0; i < beatDistances.length; i++) {
                distance = distance + beatDistances[i];
            }

            var xOffset = (startPos[0] + distance) - endlinePos[0];

            this.translateItem(endline, xOffset, 0);
        },
        getPosition: function (obj) {
            if (!obj) {
                log("getObjPosition: Invalid object", ERROR);
                return false;
            }

            return obj.position;
        },
        removeBeatlines: function (barLayer, doc) {
            var bl1 = this.getBeatlineName(1);
            var beatlinesLayer = this.getBeatlinesLayer(barLayer, doc);
            var pageItemsLen = beatlinesLayer.pageItems.length;
            var toRemove = [];

            for (var i = 0; i < pageItemsLen; i++) {
                var bl = beatlinesLayer.pageItems[i];
                if (!bl || !bl.name || bl1 === bl.name) {
                    continue;
                }
                toRemove.push(bl);
            }
            for (var i = 0; i < toRemove.length; i++) {
                removePageItem(toRemove[i]);
            }
        },
        isComplexMeter: function (timeSigNum) {
            return !isNumber(timeSigNum);
        },
        getBeatDistancePxl: function (beatUnit) {
            var distance = CROTCHET_PXL;
            if(!beatUnit || !isNumber(beatUnit)){
                return distance;
            }
            
            distance = wholeNoteDistancePxl/beatUnit;
            distance = Math.round(distance);
            return distance;
        },
        calculateNoUnitsPerBeat: function (timeSigNum) {
            var beatNo = 0;
            var beatUnits = [];
            if (this.isComplexMeter(timeSigNum)) {
                var beatUnitsStrs = timeSigNum.split(PLUS);
                for(var i = 0; i < beatUnitsStrs.length; i++){
                    var beatUnitStr = beatUnitsStrs[i];
                    beatUnits[i] = parseInt(beatUnitStr);
                }
                beatNo = beatUnits.length;
            } else {
                beatNo = parseInt(timeSigNum);
                beatUnits = [beatNo];
                for (var i = 0; i < beatNo; i++) {
                    beatUnits[i] = 1;
                }
            }
            return beatUnits;
        },
        parseBeatUnit: function (timeSigDenom) {
            return parseInt(timeSigDenom);
        },
        calculateBeatPositions: function (timeSigNum, timeSigDenom) {
            var beatUnits = this.calculateNoUnitsPerBeat(timeSigNum);
            var beatNo = beatUnits.length;

            var beatDistances = [beatNo];
            var beatUnit = this.parseBeatUnit(timeSigDenom);
            var unitDistance = this.getBeatDistancePxl(beatUnit);

            for (var i = 0; i < beatUnits.length; i++) {
                var units = beatUnits[i];
                beatDistances[i] = units * unitDistance;
            }

            return beatDistances;

        },
        processBeatlines: function (barLayer, startLine, beatDistances, doc) {

            this.removeBeatlines(barLayer);

            if (!isUseBeatline) {
                return;
            }

            var zeroBl = this.findZeroBeatLine(barLayer.parent);
            if (!zeroBl) {
                this.showAlert("Failed to find stave beatline template in " + barLayer.name);
                return;
            }

            for (var i = 0; i < beatDistances.length; i++) {
                var beat = i + 1;
                var beatline = this.findBeatLine(barLayer, beat);
                if (!beatline) {
                    beatline = this.createBeatline(barLayer, zeroBl, beat, doc);
                }
                this.setBeatlinePosition(beatline, startLine, beat, beatDistances);
            }

        },
        processEndLine: function (barLayer, startLine, beatNo, beatDistances, doc) {
            var endLine = this.findEndLine(barLayer);
            if (!endLine) {
                endLine = this.createEndLine(barLayer, startLine, doc);
            }
            if (!endLine) {
                this.showAlert("Failed to create endline in " + barLayer.name);
                return;
            }

            this.setEndLinePosition(endLine, startLine, beatNo, beatDistances);
        },
        getPageNumber: function (pageLayer, doc){
            if(!pageLayer || ! doc){
                return null;
            }
            var pageName = pageLayer.name;
            return getPageNoFromName(pageName);
        },      
        getBarNumber: function (barLayer, doc){
            if(!barLayer || !doc){
                return null;
            }
            var barInfoLayer = this.getBarInfoLayer(barLayer, doc);
            var barnum = this.findBarnum(barInfoLayer);
            var displayNo;
            if (barnum) {
                displayNo = parseInt(barnum.contents);
            } 
            var barName = barLayer.name;
            var nameNo = getBarNoFromName(barName);
            if(displayNo !== nameNo){
                log("Inconsistent bar Number in bar name: " + nameNo + " and view: " + displayNo);
            }
            return displayNo;
        },
        processBarnum: function (barLayer, barNo, startLine, doc) {
            var barNoStr = "N/A";
            if (isNumber(barNo)) {
                barNoStr = barNo.toString();
            }

            var barInfoLayer = this.getBarInfoLayer(barLayer, doc);

            var barnum = this.findBarnum(barInfoLayer);
            if (!barnum) {
                this.createBarnum(barNoStr, startLine, barInfoLayer, doc);
            } else {
                this.setItemContents(barnum, barNoStr);
            }
        },
        updatePagenum: function (staveLayer, pageName, doc) {            
            this.setPageNum(pageName, staveLayer, doc);
        },
        removeTimeSig: function (barLayer, doc) {
            var timesigLayer = this.getTimesigLayer(barLayer, doc);

            var timeSigNum = this.findTimesigNum(timesigLayer);
            removePageItem(timeSigNum);

            var timeSigDenom = this.findTimesigDenom(timesigLayer);
            removePageItem(timeSigDenom);
        },
        processTimeSig: function (barLayer, num, denom, startLine, doc) {
            if (!num || !denom || !isShowTimesig) {
                this.removeTimeSig(barLayer, doc);
                return;
            }
            var numStr = DEFAULT_TIMESIG_NUM;
            if (isNumber(num)) {
                numStr = num.toString();
            } else {
                numStr = num;
            }

            var denomStr = DEFAULT_TIMESIG_DENOM;
            if (isNumber(denom)) {
                denomStr = denom.toString();
            }

            var timesigLayer = this.getTimesigLayer(barLayer, doc);

            var timeSigNum = this.findTimesigNum(timesigLayer);
            if (!timeSigNum) {
                this.createTimeSigNum(numStr, startLine, timesigLayer, doc);
            } else {
                this.setItemContents(timeSigNum, numStr);
                this.adjustTimeSigPosition(startLine, timeSigNum);
            }

            var timeSigDenom = this.findTimesigDenom(timesigLayer);
            if (!timeSigDenom) {
                this.createTimeSigDenom(denomStr, startLine, timesigLayer, doc);
            } else {
                var isItemLocked = isLocked(timeSigDenom);
                if (isItemLocked) {
                    unlock(timeSigDenom);
                }
                this.setItemContents(timeSigDenom, denomStr);
                this.adjustTimeSigPosition(startLine, timeSigDenom);

                if (isItemLocked) {
                    lock(timeSigDenom);
                }
            }
        },
        removeTempo: function (barLayer, doc) {
            var tempoLayer = this.getTempoLayer(barLayer, doc);
            removePageItems(tempoLayer);
        },
        processTempo: function (barLayer, tempo, noteValue, startLine, doc) {
            if (!tempo) {
                this.removeTempo(barLayer, doc);
                return;
            }
            var tempoStr = "";
            if (isNumber(tempo)) {
                tempoStr = tempo.toString();
            }

            var tempoLayer = this.getTempoLayer(barLayer, doc);

            var noteval = this.findTempoNoteValue(tempoLayer);
            if (!noteval) {
                this.createTempoNoteVal(noteValue, startLine, tempoLayer, doc);
            } else {
                this.setItemContents(noteval, noteValue);
            }

            var bpm = this.findBpm(tempoLayer);
            if (!bpm) {
                this.createBpm(tempoStr, startLine, tempoLayer, doc);
            } else {
                this.setItemContents(bpm, tempoStr);
            }

        },
        processBar: function (barLayer, barNo, timeSigNum, timeSigDenom, tempo, doc) {
            var startLine = this.findStartLine(barLayer);
            if (!startLine) {
                startLine = this.findStartLine(barLayer.parent);
                if (startLine) {
                    this.addStartLine(barLayer, startLine, doc);
                }
            }
            if (!startLine) {
                this.showAlert("Failed to find start line in bar " + barLayer.name);
                return;
            }

            var beatDistances = this.calculateBeatPositions(timeSigNum, timeSigDenom);

            this.processBeatlines(barLayer, startLine, beatDistances, doc);
            this.processEndLine(barLayer, startLine, timeSigNum, beatDistances, doc);
            this.processBarnum(barLayer, barNo, startLine, doc);
            this.processTimeSig(barLayer, timeSigNum, timeSigDenom, startLine, doc);

            var noteValue = DEFAULT_TEMPO_NOTE_VAL;
            this.processTempo(barLayer, tempo, noteValue, startLine, doc);

        },
        copyBar: function (srcBarName, destBarName, barNo, xOffset, yOffset, doc) {
            log("Copying bar " + srcBarName + " to Bar: " + destBarName);

            var srcBarLayers = this.findLayersByName(srcBarName, doc);

            if (!srcBarLayers || !isArray(srcBarLayers)) {
                log("No source bars found", ERROR);
                return;
            }

            for (var i = 0; i < srcBarLayers.length; i++) {
                var srcBar = srcBarLayers[i];
                var instrument = srcBar.parent;

                var destBar = this.findInstrumentBar(instrument, destBarName, doc);

                if (!destBar) {
                    //createBar();
                    log("No destination bar found", ERROR);
                    return;
                }

                if (xOffset === 0 && yOffset === 0) {
                    var srcStartLine = this.findStartLine(srcBar);
                    var destStartLine = this.findStartLine(destBar);
                    if (srcStartLine && destStartLine) {
                        var srcStartPosition = srcStartLine.position;
                        var destStartPosition = destStartLine.position;
                        xOffset = destStartPosition[0] - srcStartPosition[0];
                        yOffset = destStartPosition[1] - srcStartPosition[1];
                    }
                }

                var srcNotation = this.findLayerByName(NAME_NOTATION, srcBar);
                var destNotation = this.findLayerByName(NAME_NOTATION, destBar);
                removePageItems(destNotation);
                deepCopyLayer(srcNotation, destNotation, xOffset, yOffset, doc, false);
            }


        },
        createBar: function (barName, barNo, propPageName, timeSigNum, timeSigDenom, tempo, overwriteBarName, filter, doc) {
            log("Creating bar " + barName + " on page: " + propPageName);

            var pageLayer = this.findLayer(propPageName, doc);
            if (!pageLayer) {
                return BT_RESP_ERROR;
            }
 
            var barLayers;
            if (overwriteBarName) {
                barLayers = this.findLayersByName(overwriteBarName, pageLayer);
                this.renameOnlyLayers(barLayers, barName);
            } else {
                barLayers = this.findLayersByName(barName, pageLayer);
            }
            if (!barLayers || !isArray(barLayers) || barLayers.length < 1) {
                barLayers = [];

                var previousBars = this.findPreviousBars(pageLayer);
                if (previousBars && isArray(previousBars) && previousBars.length > 0) {
                    for (var i = 0; i < previousBars.length; i++) {
                        var newBarLayer = this.createBarFromPrevious(barName, previousBars[i], filter, doc);
                        if (!newBarLayer) {
                            continue;
                        }
                        barLayers.push(newBarLayer);
                    }
                } else {
                    var instruments = pageLayer.layers;
                    if (!instruments || instruments.length < 1) {
                        this.showAlert("Could not find any staves on page");
                        return;
                    }
                    for (var i = 0; i < instruments.length; i++) {
                        var instrument = instruments[i];
                        var barLayer = createNewBarLayer(instrument, barName, doc);
                        if (barLayer) {
                            barLayers.push(barLayer);
                        }
                    }
                }

            }

            for (var i = 0; i < barLayers.length; i++) {
                this.processBar(barLayers[i], barNo, timeSigNum, timeSigDenom, tempo, doc);
            }
        },
        createNewPageFromPage: function (existingPageName, newPageName, existingArtboardName, newArtboardName, spacer, newPagePosition, filter, doc) {

            log("Duplicating page " + existingPageName + " to page: " + newPageName);

            if (!existingPageName) {
                return;
            }

            try {

                var pageArtboard = this.findArtboard(existingArtboardName, doc);

                var newPageArtboard;
                var xOffset = 0;
                var yOffset = 0;

                var pageLayer = this.findLayer(existingPageName, doc);
                if (!pageLayer) {
                    log("Could not find Layer for page: " + existingPageName, ERROR);
                    return;
                }

                if (!pageArtboard) {
                    log("Could not find artboard for page: " + existingPageName, ERROR);
                    newPageArtboard = doc.artboards.add([0, 0, 750, -830]);
                } else {
                    var rect = pageArtboard.artboardRect;
                    var xstart = rect[0];
                    var xend = rect[2];
                    var ystart = rect[1];
                    var yend = rect[3];
                    if (NAME_BELOW === newPagePosition) {
                        yOffset = (yend - ystart) - spacer;
                    } else {
                        xOffset = (xend - xstart) + spacer;
                    }
                    newPageArtboard = doc.artboards.add([rect[0] + xOffset, rect[1] + yOffset, rect[2] + xOffset, rect[3] + yOffset]);
                }
                newPageArtboard.name = newArtboardName;

                var newPageLayer = createLayerInParent(newPageName, pageLayer.parent, doc);

                deepCopyLayer(pageLayer, newPageLayer, xOffset, yOffset, doc, filter);

                var instruments = newPageLayer.layers;
                if (!instruments || instruments.length < 1) {
                    return;
                }
                for (var i = 0; i < instruments.length; i++) {
                    var instrument = instruments[i];
                    var staveLayer = this.findStaveLayer(instrument, doc);
                    this.updatePagenum(staveLayer, newPageName, doc);
                    
                }

            } catch (e) {
                this.log(e, ERROR);
            }
        },
        initIntrumentMetric: function(bar, metricTracker, doc){
            var timesigLayer = this.getTimesigLayer(bar, doc);
            var timeSigNumItem = this.findTimesigNum(timesigLayer);
            if(timeSigNumItem){
                metricTracker.timeSigNum = timeSigNumItem.contents;
            }

            var timeSigDenomItem = this.findTimesigDenom(timesigLayer);
            if(timeSigDenomItem){
                metricTracker.timeSigDenom = timeSigDenomItem.contents;
            }
            
            var tempoLayer = this.getTempoLayer(bar, doc);
            var bpmItem = this.findBpm(tempoLayer);
            if(bpmItem){
                metricTracker.tempoBpm = parseInt(bpmItem.contents);
            }  
            
            metricTracker.tempoBeatValue = tempoBeatUnit;
            
        },
        calculateBeatDurationMillis: function(numberOfBaseUnits, tempoBpm, tempoBeatValue){
            var millisPerBeat = Math.round(MILLIS_IN_MIN/tempoBpm);
            var multiplier = baseBeatUnit/tempoBeatValue;
            var duration = millisPerBeat*(1/multiplier);
            duration = Math.round(numberOfBaseUnits*duration);
            return duration;
        },
        caluclateNumberOfBaseUnits: function(beatUnit, unitPerBeat, baseBeatUnit){
            var unitMultiplier = getUnitMultiplier(beatUnit, baseBeatUnit);        
            return unitPerBeat * unitMultiplier;
        },        
        createInScoreMapString: function(beatInfo){     
            var mapString = "( [" + beatInfo.xStartPxl + ", " +  beatInfo.xEndPxl + "[ [" 
                    + beatInfo.yStartPxl + ", " + beatInfo.yEndPxl + "[ ) ( [" 
                    + beatInfo.startBaseBeatUnits + "/"+ baseBeatUnit +", " + beatInfo.endBaseBeatUnits + "/"+ baseBeatUnit +"[ )";
            return mapString;
        },      
        createBeatInfoString: function(infoProps, beatInfo){    
            var infoStr = createBeatInfoCsvStr(infoProps, beatInfo);
            return infoStr;
        },  
        createBeatInfoHeaderString: function(infoProps, beatInfo){    
            var infoStr = createBeatInfoHeaderCsvStr(infoProps, beatInfo);
            return infoStr;
        },
        exportInscoreMapStrings: function(inscoreMapStrings, fileName){    
            fileName += NAME_INSCORE_MAP_FILE_SUFFIX;
            writeLinesInFile(fileName, inscoreMapStrings);
        },
        exportInfoStrings: function(beatInfoStrings, fileName){    
            fileName += NAME_BEAT_INFO_FILE_SUFFIX;
            writeLinesInFile(fileName, beatInfoStrings);
            if(scoreFile){
                writeLinesInOpenFile(scoreFile, beatInfoStrings);
            }
        },
        exportBeatInfosAsFullScore: function(beatInfos, eventBeatInfos, instrument, fileName){ 
            if(!beatInfos || !instrument || !fileName){
                 return;
            } 
            
            var fileNameFullScore = fileName.replace(instrument.name, NAME_FULL_SCORE);
            
            var fullScoreBeatInfos = [];
            for(var i = 0; i < beatInfos.length; i++){
                var beatInfo = beatInfos[i];
                var fullcoreBeatInfo = new BeatInfo();
                
                if(!beatInfo.scoreName || beatInfo.scoreName ==="") {
                    logError("Invalid score name");
                    continue;
                }
                
                fullcoreBeatInfo.scoreName = beatInfo.scoreName;
                fullcoreBeatInfo.instrumentName = NAME_FULL_SCORE;
                fullcoreBeatInfo.pageName = beatInfo.pageName;
                fullcoreBeatInfo.pageNo = beatInfo.pageNo;
                fullcoreBeatInfo.barName = beatInfo.barName;
                fullcoreBeatInfo.barNo = beatInfo.barNo;        
                fullcoreBeatInfo.timeSigNum = beatInfo.timeSigNum;
                fullcoreBeatInfo.timeSigDenom = beatInfo.timeSigDenom;
                fullcoreBeatInfo.tempoBpm = beatInfo.tempoBpm;
                fullcoreBeatInfo.tempoBeatValue = beatInfo.tempoBeatValue;     
                fullcoreBeatInfo.beatNo = beatInfo.beatNo;
                fullcoreBeatInfo.unitBeatNo = beatInfo.unitBeatNo;
                fullcoreBeatInfo.startTimeMillis = beatInfo.startTimeMillis;
                fullcoreBeatInfo.durationTimeMillis = beatInfo.durationTimeMillis;
                fullcoreBeatInfo.endTimeMillis = beatInfo.endTimeMillis;
                fullcoreBeatInfo.startBaseBeatUnits = beatInfo.startBaseBeatUnits;
                fullcoreBeatInfo.durationBeatUnits = beatInfo.durationBeatUnits;
                fullcoreBeatInfo.endBaseBeatUnits = beatInfo.endBaseBeatUnits;
                fullcoreBeatInfo.xStartPxl = beatInfo.xStartPxl;
                fullcoreBeatInfo.xEndPxl = beatInfo.xEndPxl;
                fullcoreBeatInfo.yStartPxl = beatInfo.yStartPxl;
                fullcoreBeatInfo.yEndPxl = beatInfo.yEndPxl;
                fullcoreBeatInfo.isUpbeat = beatInfo.isUpbeat;
                fullcoreBeatInfo.resource = fileNameFullScore;      
                
                fullScoreBeatInfos.push(fullcoreBeatInfo);
            }
            
            if(eventBeatInfos) {
                for(var i = 0; i < eventBeatInfos.length; i++){
                    var beatInfo = eventBeatInfos[i];
                    var fullcoreBeatInfo = new BeatInfo();

                    fullcoreBeatInfo.scoreName = beatInfo.scoreName;
                    fullcoreBeatInfo.instrumentName = NAME_FULL_SCORE;
                    fullcoreBeatInfo.pageName = beatInfo.pageName;
                    fullcoreBeatInfo.pageNo = beatInfo.pageNo;
                    fullcoreBeatInfo.barName = beatInfo.barName;
                    fullcoreBeatInfo.barNo = beatInfo.barNo;        
                    fullcoreBeatInfo.timeSigNum = beatInfo.timeSigNum;
                    fullcoreBeatInfo.timeSigDenom = beatInfo.timeSigDenom;
                    fullcoreBeatInfo.tempoBpm = beatInfo.tempoBpm;
                    fullcoreBeatInfo.tempoBeatValue = beatInfo.tempoBeatValue;     
                    fullcoreBeatInfo.beatNo = beatInfo.beatNo;
                    fullcoreBeatInfo.unitBeatNo = beatInfo.unitBeatNo;
                    fullcoreBeatInfo.startTimeMillis = beatInfo.startTimeMillis;
                    fullcoreBeatInfo.durationTimeMillis = beatInfo.durationTimeMillis;
                    fullcoreBeatInfo.endTimeMillis = beatInfo.endTimeMillis;
                    fullcoreBeatInfo.startBaseBeatUnits = beatInfo.startBaseBeatUnits;
                    fullcoreBeatInfo.durationBeatUnits = beatInfo.durationBeatUnits;
                    fullcoreBeatInfo.endBaseBeatUnits = beatInfo.endBaseBeatUnits;
                    fullcoreBeatInfo.xStartPxl = beatInfo.xStartPxl;
                    fullcoreBeatInfo.xEndPxl = beatInfo.xEndPxl;
                    fullcoreBeatInfo.yStartPxl = beatInfo.yStartPxl;
                    fullcoreBeatInfo.yEndPxl = beatInfo.yEndPxl;
                    fullcoreBeatInfo.isUpbeat = beatInfo.isUpbeat;
                    fullcoreBeatInfo.resource = fileNameFullScore;      

                    fullScoreBeatInfos.push(fullcoreBeatInfo);
                }
            }
            
            this.exportBeatInfos(fullScoreBeatInfos, null, fileNameFullScore);
        },
        exportBeatInfos: function(beatInfos, eventBeatInfos, fileName){                        
            if(!beatInfos){
                return;
            }
            
            var inscoreMapStrings = [];
            var beatInfoStrings = [];
            var infoProps = [];
            for(var i = 0; i < beatInfos.length; i++){
                var beatInfo = beatInfos[i];
                
                var inScoreString = this.createInScoreMapString(beatInfo);
                inscoreMapStrings.push(inScoreString);  
                
                if(i === 0){
                    infoProps = getObjectProperties(beatInfo);
                    var beatInfoHeader = this.createBeatInfoHeaderString(infoProps, beatInfo);
                    beatInfoStrings.push(beatInfoHeader);
                }
                var beatInfoString = this.createBeatInfoString(infoProps, beatInfo);
                beatInfoStrings.push(beatInfoString);
            }     
            
            if(eventBeatInfos) {
                for(var i = 0; i < eventBeatInfos.length; i++){
                    var beatInfo = eventBeatInfos[i];
                    var beatInfoString = this.createBeatInfoString(infoProps, beatInfo);
                    beatInfoStrings.push(beatInfoString);
                }     
            }
            
            this.exportInscoreMapStrings(inscoreMapStrings, fileName);
            this.exportInfoStrings(beatInfoStrings, fileName);
        },
        createBeatInfo: function(beatLineItem, beatLineItems, blIndex, bars, barIndex, beatDistances, stavePosition, 
                                    metricTracker, beatUnit, untsPerBeat, beatInfos, docName, instrumentName, pageName, pageNo, 
                                    barName, barNo, fileName, scoreName){
            var isUpbeat = (blIndex < 0);
            var blPosition = beatLineItem.position;
            var nextBlPosition = this.getNextBeatlinePosition(beatLineItem, blIndex, beatLineItems, barIndex, bars, beatDistances);

            var xStartPxl = Math.round(blPosition[0] - stavePosition[0]);
            var xEndPxl = Math.round(nextBlPosition[0] - stavePosition[0]);
            var yStartPxl = DEFAULT_Y_POSITION; //Math.round(stavePosition[1] - blPosition[1]);
            var yEndPxl = DEFAULT_Y_POSITION;  //Math.round(yStartPxl + beatLineItem.height);

            var unitsPerBeat = this.getUnitsPerBeat(blIndex, untsPerBeat);                    
            var numberOfBaseUnits = this.caluclateNumberOfBaseUnits(beatUnit, unitsPerBeat, baseBeatUnit);

            var beatNo = metricTracker.beatNo;
            var startBaseBeatUnits = metricTracker.unitBeatNo;
            var endBaseBeatUnits = startBaseBeatUnits + numberOfBaseUnits;

            var startMillis = metricTracker.timeMillis;      
            var tempoBpm = metricTracker.tempoBpm;
            var tempoBeatValue = metricTracker.tempoBeatValue;
            var beatDurationMillis = this.calculateBeatDurationMillis(numberOfBaseUnits, tempoBpm, tempoBeatValue);
            
            if(isUpbeat && startMillis !== 0){
                var upBeatDurationMillis = this.calculateBeatDurationMillis(noBaseUnitsInBeat, tempoBpm, tempoBeatValue);
                startMillis -= upBeatDurationMillis;
            }
            var endMillis = startMillis + beatDurationMillis;

            var beatInfo = new BeatInfo();
            if(scoreName){
                beatInfo.scoreName = scoreName;
            } else {
                beatInfo.scoreName = docName;
            }
            beatInfo.instrumentName = instrumentName;
            beatInfo.pageName = pageName;
            beatInfo.pageNo = pageNo;
            beatInfo.barName = barName;
            beatInfo.barNo = barNo;        
            beatInfo.timeSigNum = metricTracker.timeSigNum;
            beatInfo.timeSigDenom = metricTracker.timeSigDenom;
            beatInfo.tempoBpm = metricTracker.tempoBpm;
            beatInfo.tempoBeatValue = metricTracker.tempoBeatValue;     
            beatInfo.beatNo = beatNo;
            beatInfo.unitBeatNo = startBaseBeatUnits;
            beatInfo.startTimeMillis = startMillis;
            beatInfo.durationTimeMillis = beatDurationMillis;
            beatInfo.endTimeMillis = endMillis;
            beatInfo.startBaseBeatUnits = startBaseBeatUnits;
            beatInfo.durationBeatUnits = numberOfBaseUnits;
            beatInfo.endBaseBeatUnits = endBaseBeatUnits;
            beatInfo.xStartPxl = xStartPxl;
            beatInfo.xEndPxl = xEndPxl;
            beatInfo.yStartPxl = yStartPxl;
            beatInfo.yEndPxl = yEndPxl;
            beatInfo.isUpbeat = (isUpbeat?1:0);
            beatInfo.resource = fileName;

            beatInfos.push(beatInfo);

            metricTracker.unitBeatNo = endBaseBeatUnits;
            metricTracker.beatNo = ++beatNo;
//            if(!isUpbeat){
            metricTracker.timeMillis = endMillis;   
//            }
        },
        createEventBeatInfo: function(matchingBeatinfo, eventBeatInfos, resource){
           
            if(!matchingBeatinfo) {
                logError("Invalid Matching Beat Info, can not add event Beat Info");
                return;
            }
           
            var beatInfo = new BeatInfo();
            beatInfo.scoreName = matchingBeatinfo.scoreName;
            beatInfo.instrumentName = matchingBeatinfo.instrumentName;
            beatInfo.pageName = matchingBeatinfo.pageName;
            beatInfo.pageNo = matchingBeatinfo.pageNo;
            beatInfo.barName = matchingBeatinfo.barName;
            beatInfo.barNo = matchingBeatinfo.barNo;        
            beatInfo.timeSigNum = matchingBeatinfo.timeSigNum;
            beatInfo.timeSigDenom = matchingBeatinfo.timeSigDenom;
            beatInfo.tempoBpm = matchingBeatinfo.tempoBpm;
            beatInfo.tempoBeatValue = matchingBeatinfo.tempoBeatValue;     
            beatInfo.beatNo = matchingBeatinfo.beatNo;
            beatInfo.unitBeatNo = matchingBeatinfo.unitBeatNo;
            beatInfo.startTimeMillis = matchingBeatinfo.startTimeMillis;
            beatInfo.durationTimeMillis = matchingBeatinfo.durationTimeMillis;
            beatInfo.endTimeMillis = matchingBeatinfo.endTimeMillis;
            beatInfo.startBaseBeatUnits = matchingBeatinfo.startBaseBeatUnits;
            beatInfo.durationBeatUnits = matchingBeatinfo.durationBeatUnits;
            beatInfo.endBaseBeatUnits = matchingBeatinfo.endBaseBeatUnits;
            beatInfo.xStartPxl = matchingBeatinfo.xStartPxl;
            beatInfo.xEndPxl = matchingBeatinfo.xEndPxl;
            beatInfo.yStartPxl = matchingBeatinfo.yStartPxl;
            beatInfo.yEndPxl = matchingBeatinfo.yEndPxl;
            beatInfo.isUpbeat = matchingBeatinfo.isUpbeat;
            beatInfo.resource = resource;

            eventBeatInfos.push(beatInfo);
        },
        exportBeatlines: function(page, instrument, fileName, doc, scoreName, isExportDataAsFullScore){
            var docName = doc.name;
            var pageName = page.name;
            var pageNo = this.getPageNumber(page, doc);
            
            var stavePosition = this.findStavePosition(instrument, doc);
            if(!stavePosition){
                stavePosition = [0,0];
            }
            
            var bars = this.findAllBars(instrument);
            bars = sortLayersByName(bars);
            if (!bars) {
                return false;
            }
            var instrumentName = instrument.name;
            
            log("Doing Instrument: " + instrumentName);
            var metricTracker = getInstrumentMetricTracker(instrumentName);
            
            //Leave space for upbeat
            metricTracker.unitBeatNo = metricTracker.unitBeatNo - noBaseUnitsInBeat;
            metricTracker.beatNo = metricTracker.beatNo - 1;
                                 
            var beatInfos = [];
            var eventBeatInfos = [];
            var firstBarBeatIndex = 0;
                
            for(var i = 0; i < bars.length; i++){
                var bar = bars[i];
                if(!bar){
                    continue;
                }
                var barName = bar.name;
                var barNo = getBarNoFromName(barName);
                
                this.initIntrumentMetric(bar, metricTracker, doc);
                
                var unitsPerBeat = this.calculateNoUnitsPerBeat(metricTracker.timeSigNum);
                var beatUnit = this.parseBeatUnit(metricTracker.timeSigDenom);
                var beatDistances = this.calculateBeatPositions(metricTracker.timeSigNum, metricTracker.timeSigDenom);
                
                var beatLineItems = this.findBeatlineItems(bar, doc);
                beatLineItems = sortLayersByName(beatLineItems);
                if(!beatLineItems){
                    continue;
                }
                
                //Add upbeat info for first bar
                if(i === 0){
                    var blZero = this.findBeatLine(instrument, 0);
                    if(blZero){
                        var barZero = barNo - 1;
                        var barZeroName = createBarNameFromNo(barZero);
                        this.createBeatInfo(blZero, beatLineItems, -1, bars, i, beatDistances, stavePosition, 
                                                metricTracker, tempoBeatUnit, null, beatInfos, docName, instrumentName, 
                                                pageName, pageNo, barZeroName, barZero, fileName, scoreName);
                    }
                }
                                    
                firstBarBeatIndex = beatInfos.length;
                for(var j = 0; j < beatLineItems.length; j++){
                    this.createBeatInfo(beatLineItems[j], beatLineItems, j, bars, i, beatDistances, stavePosition, 
                                                metricTracker, beatUnit, unitsPerBeat, beatInfos, docName, instrumentName, pageName, 
                                                pageNo, barName, barNo, fileName, scoreName);                    
                }
                
                // Add event objects (javascript), currently only first beat of bar        
                var eventItems = this.findEventItems(bar, doc);
                if(eventItems && firstBarBeatIndex >= 0){
                    for(var j = 0; j < eventItems.length; j++){
                        var eventItem = eventItems[j];
                        log("Have eventItem " + eventItem);
                        if (isScriptEvent(eventItem)){  
                            var scriptContent = eventItem.contents;
                            scriptContent = trimRegex(scriptContent);
                            scriptContent = replaceAll(scriptContent, COMMA, PIPE);
//                            scriptContent = scriptContent.replace(new RegExp(COMMA, 'g'), PIPE);
                            var matchingBeatInfo = beatInfos[firstBarBeatIndex];
                            this.createEventBeatInfo(matchingBeatInfo, eventBeatInfos, scriptContent);
                        }      
                    }
                }
            }
            
            if(isExportDataAsFullScore) {
               this.exportBeatInfosAsFullScore(beatInfos, eventBeatInfos, instrument, fileName); 
            }
            
            this.exportBeatInfos(beatInfos, eventBeatInfos, fileName);
            var barNo  = this.getBarNumber(bar, doc);
            metricTracker.barNo = barNo;
        },
        initMetricTrackers: function (pagesToDo){
            if(!pagesToDo){
                return;
            }
            var page0 = pagesToDo[0];
            var instruments = page0.layers;
            if(instruments){
                for(var i = 0; i < instruments.length; i++){
                    var instrument = instruments[i];
                    var instrumentName = instrument.name;
                    var metricTracker = getInstrumentMetricTracker(instrumentName);
                    metricTracker.unitBeatNo = noBaseUnitsInBeat + 1;
                    metricTracker.beatNo = 1;
                }
            }
        },
        exportFiles: function (propPages, propFormat, isExportScore, isExportParts, isExportBeatlines, dir, scoreName, doc) {
            log("Exporting files ");
            var pages = this.findAllPages(doc);
            if (!pages) {
                return;
            }

            var pagesToDo = this.filterPages(pages, propPages);
            if (!pagesToDo) {
                return;
            }
            pagesToDo = sortLayersByName(pagesToDo);
            
            if(!dir){
                dir = this.getScoreSaveDir(doc);
            }

            var path = dir;
            if(!endsWith(path,SLASH)){
                path += SLASH;
            }
            
            resetInstrumentMap(InstrumentMetricTrackerMap);
            this.initMetricTrackers(pagesToDo);
            
            scoreFile = null;
            scoreFileHeaderWritten = false;
            
            if(contains(scoreName, DOT)) {
                scoreName = scoreName.replace(/\./g,UNDERSCORE)
            }
            
            if(isExportBeatlines){
                var name = createFileName(doc, false, false, scoreName);
                name = path + name + NAME_BEAT_INFO_FILE_SUFFIX;
                var sf = openFileForWrite(name);
                if(sf){
                    scoreFile = sf;
                }
            }
            
            hideLayers(pages);         
            
            for(var i = 0; i < pagesToDo.length; i++){
                var page = pagesToDo[i];
                if(!page){
                    continue;
                }
                page.visible = true;
                
                if(isExportScore){
                    var iname = NAME_FULL_SCORE;
                    var name = createFileName(doc, page, iname, scoreName);
                    name = path + name;
                    this.exportLayerFile(name, propFormat, doc);
                }
                
                if(isExportParts){
                    var instruments = page.layers;
                    if(!instruments){
                        continue;
                    }
                    hideLayers(instruments);
                    
                    for(var j = 0; j < instruments.length; j++){
                        var instrument = instruments[j];
                        if(!instrument){
                            continue;
                        }
                        instrument.visible = true;
                        var name = createFileName(doc, page, instrument.name, scoreName);
                        name = path + name;
                        this.exportLayerFile(name, propFormat, doc);
                        
                        instrument.visible = false;
                    }
                    
                    showLayers(instruments);
                }
                
                if(isExportBeatlines){
                    var instruments = page.layers;
                    if(!instruments){
                        continue;
                    }
                    log("Doing page: " + page.name);
                    for(var j = 0; j < instruments.length; j++){
                        var instrument = instruments[j];
                        if(!instrument){
                            continue;
                        }
                        var name = createFileName(doc, page, instrument.name, scoreName);
                        name = path + name;
                        
                        var isExportDataAsFullScore = false;
                        if(j === 0) {
                            isExportDataAsFullScore = true;
                        }
                        
                        this.exportBeatlines(page, instrument, name, doc, scoreName, isExportDataAsFullScore);
                    }
                }
                
                page.visible = false;
            }
            
            if(scoreFile){
                closeFile(scoreFile);
            }
            
            showLayers(pages);
        },
//################# BridgeTalk functions ####################################################
        btExportLayerXML: function () {
            var propXmlFilePath = getBtCallProp(BT_PROP_FILE_PATH);
            if (!propXmlFilePath) {
                return BT_RESP_ERROR;
            }

            var doc = getBtDocument();

            var ret = this.exportLayerXML(propXmlFilePath, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_EXPORT_LAYER_XML, ret);
            return ret;
        },
        btGetLayerXML: function () {
            var doc = getBtDocument();
            var ret = this.getLayerXML(doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_GET_LAYER_XML, ret);
            return ret;
        },
        btGetActiveDocName: function () {
            var ret = getActiveDocName();
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_GET_ACTIVE_DOC_NAME, ret);
            return ret;
        },
        btLoadLayerXML: function () {
            var propXmlFilePath = getBtCallProp(BT_PROP_FILE_PATH);
            if (!propXmlFilePath) {
                return BT_RESP_ERROR;
            }
            var ret = this.loadLayerXML(propXmlFilePath);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_LOAD_LAYER_XML, ret);
            return ret;
        },
        btInsertModelXML: function () {
            var propXmlModel = getBtCallProp(BT_PROP_XML_MODEL);
            if (!propXmlModel) {
                return BT_RESP_ERROR;
            }

            var propRootLayerName = getBtCallProp(BT_PROP_ITEM_NAME);
            var doc = getBtDocument();

            var ret = this.insertLayerXML(propXmlModel, propRootLayerName, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_INSERT_MODEL_XML, ret);
            return ret;
        },
        btDeleteLayer: function () {
            var propLayerName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!propLayerName) {
                return BT_RESP_ERROR;
            }

            var doc = getBtDocument();
            var ret = this.deleteLayers(propLayerName, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_DELETE_LAYER, ret);
            return ret;
        },
        btGetOpenDocs: function () {
            var ret = this.getOpenDocsCsv();
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_GET_OPEN_DOCS, ret);
            return ret;
        },
        btCopyLayer: function () {
            var propLayerName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!propLayerName) {
                return BT_RESP_ERROR;
            }

            var propNewLayerName = getBtCallProp(BT_PROP_NEW_ITEM_NAME);
            if (!propNewLayerName) {
                propNewLayerName = pathSeparator;
            }

            var propXOffset = getBtCallProp(BT_PROP_X_OFFSET);
            if (!propXOffset) {
                propXOffset = 0;
            }

            var propYOffset = getBtCallProp(BT_PROP_Y_OFFSET);
            if (!propYOffset) {
                propYOffset = 0;
            }

            var propFilter = getBtCallProp(BT_PROP_NAME_FILTER);
            if (!propFilter) {
                propFilter = [];
            } else {
                propFilter = convertCsvToArr(propFilter);
            }

            var sourceDoc = getBtDocument();
            var destinationDoc = getBtDestDocument();

            var ret = this.copyLayer(propLayerName, propNewLayerName, propXOffset, propYOffset, propFilter, sourceDoc, destinationDoc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_COPY_LAYER, ret);
            return ret;
        },
        btCreatePageFromPage: function () {
            var propPageName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!propPageName) {
                return BT_RESP_ERROR;
            }

            var propNewPageName = getBtCallProp(BT_PROP_NEW_ITEM_NAME);
            if (!propNewPageName) {
                return BT_RESP_ERROR;
            }

            var newArtboardName = getBtCallProp(BT_PROP_NEW_ARTB_NAME);
            if (!newArtboardName) {
                newArtboardName = propNewPageName;
            }

            var existingArtboardName = getBtCallProp(BT_PROP_ARTB_NAME);
            if (!existingArtboardName) {
                existingArtboardName = propPageName;
            }

            var filter = getBtCallProp(BT_PROP_NAME_FILTER);
            if (!filter) {
                filter = [];
            } else {
                filter = convertCsvToArr(filter);
            }

            var spacer = this.btModel.pageOffset;
            if (!spacer) {
                spacer = ARTBOARD_SPACER;
            }

            var newPagePosition = getBtCallProp(BT_PROP_POSITION);
            if (!newPagePosition) {
                newPagePosition = NAME_RIGHT;
            }

            var doc = getBtDocument();

            var ret = this.createNewPageFromPage(propPageName, propNewPageName, existingArtboardName, newArtboardName, spacer, newPagePosition, filter, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_CREATE_PAGE_FROM_PAGE, ret);
            return ret;
        },
        btDeletePage: function () {
            var propPageName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!propPageName) {
                return BT_RESP_ERROR;
            }

            var artboardName = getBtCallProp(BT_PROP_ARTB_NAME);
            if (!artboardName) {
                artboardName = propPageName;
            }

            var doc = getBtDocument();

            var ret = this.deletePage(propPageName, artboardName, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_CREATE_PAGE_FROM_PAGE, ret);
            return ret;
        },
        btUpdatePage: function () {
            var propPageName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!propPageName) {
                return BT_RESP_ERROR;
            }

            var artboardName = getBtCallProp(BT_PROP_ARTB_NAME);
            if (!artboardName) {
                artboardName = propPageName;
            }

            var doc = getBtDocument();

            var ret = this.updatePage(propPageName, artboardName, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_CREATE_PAGE_FROM_PAGE, ret);
            return ret;
        },
        btCreateBar: function () {
            var propPageName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!propPageName) {
                return BT_RESP_ERROR;
            }

            var barName = getBtCallProp(BT_PROP_NEW_ITEM_NAME);
            if (!barName) {
                return BT_RESP_ERROR;
            }

            var barNo = parseInt(getBtCallProp(BT_PROP_ITEM_NUMBER));
            if (!barNo) {
                return BT_RESP_ERROR;
            }

            var timeSigNum = getBtCallProp(BT_PROP_NUMERATOR);
            var timeSigDenom = getBtCallProp(BT_PROP_DENOMINATOR);
            var tempo = getBtCallProp(BT_PROP_TEMPO);
            var overwriteBar = getBtCallProp(BT_PROP_OLD_ITEM_NAME);
            var showTimesig = getBtCallProp(BT_PROP_SHOW_TIMESIG);
            var isCreateBeatlines = getBtCallProp(BT_PROP_CREATE_BEATLINES);

            var start = this.btModel.startLineName;
            var end = this.btModel.endLineName;
            var barnum = this.btModel.barnumName;
            var beatline = this.btModel.beatlineName;
            var font = this.btModel.fontName;
            var fontSize = this.btModel.fontSize;
            var barNoXOffset = this.btModel.barnumXOffset;
            var barNoYOffset = this.btModel.barnumYOffset;
            var barNamePre = this.btModel.barNamePrefix;

            var doc = getBtDocument();

            var filter;
            var filter = getBtCallProp(BT_PROP_NAME_FILTER);
            if (!filter) {
                filter = [];
            } else {
                filter = convertCsvToArr(filter);
            }

            if (start) {
                startLineName = start;
            }
            if (end) {
                endLineName = end;
            }
            if (barnum) {
                barnumName = barnum;
            }
            if (beatline) {
                beatlineName = beatline;
            }
            if (font) {
                var sysFont = availableFonts.getByName(font);
                if (sysFont) {
                    txtStyle = sysFont;
                }
            }
            if (fontSize) {
                txtSize = fontSize;
            }
            if (barNoXOffset) {
                barnumXOffset = barNoXOffset;
            }
            if (barNoYOffset) {
                barnumYOffset = barNoYOffset;
            }
            if (barNamePre) {
                barNamePrefix = barNamePre;
            }
            isUseBeatline = isCreateBeatlines;
            isShowTimesig = showTimesig;

            var ret = this.createBar(barName, barNo, propPageName, timeSigNum, timeSigDenom, tempo, overwriteBar, filter, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_CREATE_BAR, ret);
            return ret;
        },
        btCopyBar: function () {
            var srcBarName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!srcBarName) {
                return BT_RESP_ERROR;
            }

            var destBarName = getBtCallProp(BT_PROP_NEW_ITEM_NAME);
            if (!destBarName) {
                return BT_RESP_ERROR;
            }

            var xOffset = getBtCallProp(BT_PROP_X_OFFSET);
            var yOffset = getBtCallProp(BT_PROP_Y_OFFSET);
            if (xOffset) {
                xOffset = parseInt(xOffset);
            }
            if (yOffset) {
                yOffset = parseInt(yOffset);
            }

            var barNo = parseInt(getBtCallProp(BT_PROP_ITEM_NUMBER));
            if (!barNo) {
                return BT_RESP_ERROR;
            }
            var doc = getBtDocument();

            var ret = this.copyBar(srcBarName, destBarName, barNo, xOffset, yOffset, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_COPY_BAR, ret);
            return ret;
        },
        btDeleteBar: function () {
            var propPageName = getBtCallProp(BT_PROP_ITEM_NAME);
            if (!propPageName) {
                return BT_RESP_ERROR;
            }

            var barName = getBtCallProp(BT_PROP_NEW_ITEM_NAME);
            if (!barName) {
                return BT_RESP_ERROR;
            }

            var doc = getBtDocument();

            var ret = this.deleteBar(barName, propPageName, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_DELETE_BAR, ret);
            return ret;
        },
        btExportFiles: function () {
            var propPages = getBtCallProp(BT_PROP_ITEM_NAME);
            var propFormat = getBtCallProp(BT_PROP_FORMAT);
            var dir = getBtCallProp(BT_PROP_FILE_PATH);
            var propExportScore = getBtCallProp(BT_PROP_EXPORT_SCORE);
            var propExportParts = getBtCallProp(BT_PROP_EXPORT_PARTS);
            var propExportBeatlines = getBtCallProp(BT_PROP_EXPORT_BEATLINES);
            var scoreName = getBtCallProp(BT_PROP_SCORE_NAME);
            
            var isExportScore = stringToBoolean(propExportScore);
            var isExportParts = stringToBoolean(propExportParts);
            var isExportBeatlines = stringToBoolean(propExportBeatlines);

            var doc = getBtDocument();

            var ret = this.exportFiles(propPages, propFormat, isExportScore, isExportParts, isExportBeatlines, dir, scoreName, doc);
            ret = appendBtMsgHeader(BT_HEADER_CALLER, BT_CALL_EXPORT_FILES, ret);
            return ret;
        }
    };
}(Window);

var ZSVIEW = function (zscorelib) {

    var scorelib = zscorelib;
    var theApp = szApp;

    var scoreLibName = "ZSCORE";

    var COMMA = ",";
    var DOT = ".";
    var SLASH = "/";
    var NAME_NODE = "node";
    var NAME_ITEM = "item";
    var NAME_SCORE = "Score";
    var NAME_EMPTY = "";

    var NAME_ACTIVE_DOC = scorelib.btStatic.activeDoc;
    var NAME_RIGHT = scorelib.btStatic.right;
    var NAME_BELOW = scorelib.btStatic.below;
    var NAME_PAGE = scorelib.btModel.pageName;
    var NAME_START = scorelib.btModel.startLineName;
    var NAME_END = scorelib.btModel.endLineName;
    var NAME_BARNUM = scorelib.btModel.barnumName;
    var NAME_BEATLINE = scorelib.btModel.beatlineName;
    var NAME_BAR = scorelib.btModel.barName;
    var NAME_NOTATION = scorelib.btModel.notationName;
    var NAME_PRESTART = scorelib.btModel.prestartName;
    var PAGE_OFFSET = scorelib.btModel.pageOffset;
    var TXT_FONT = scorelib.btModel.fontName;
    var TXT_SIZE = scorelib.btModel.fontSize;
    var BARNUM_X_OFFSET = scorelib.btModel.barnumXOffset;
    var BARNUM_Y_OFFSET = scorelib.btModel.barnumYOffset;
    var NAME_PNG = scorelib.btModel.pngName;
    var NAME_SVG = scorelib.btModel.svgName;

    var BTalk = new BridgeTalk();

    var btHeaderCaller = scorelib.btHeaderCaller;
    var btCalls = scorelib.btCalls;
    var btProps = scorelib.btProps;

    var pathSeparator = scorelib.getPathSeparator();

    var layersTreeView;
    var modelTreeView;
    var activeLayerTxt;
    var copyToLyrNameTxt;
    var copyDocTxt;
    var activeDocTxt;
    var xOffsetTxt;
    var yOffsetTxt;
    var barXOffsetTxt;
    var barYOffsetTxt;
    var filterTxt;
    var barFilterTxt;
    var openDocsSel;
    var docTxt;
    var layerTxt;
    var pagePrefixTxt;
    var barPagePrefixTxt;
    var artboardPrefixTxt;
    var artboardOffsetTxt;
    var artboardPosSel;
    var pageLayerfilterTxt;
    var pageNoTxt;
    var barPageNoTxt;
    var startTxt;
    var endTxt;
    var barnumTxt;
    var beatlineTxt;
    var beatlineCb;
    var showTimesigCb;
    var pageNameTxt;
    var srcPageNoTxt;
    var delPageNoTxt;
    var barPrefixTxt;
    var barNoTxt;
    var overwriteBarNoTxt;
    var srcBarNoTxt;
    var destBarNoTxt;
    var timeSigNumTxt;
    var timeSigDenomTxt;
    var tempoTxt;
    var fontNameTxt;
    var fontSizeTxt;
    var barnumXOffsetTxt;
    var barnumYOffsetTxt;
    var exportFormatSel;
    var exportPagesTxt;
    var exportScoreCb;
    var exportPartsCb;
    var exportDirTxt;
    var exportScoreNameTxt;
    var exportBeatlinesCb;

    var winRefLayerTools;
    var winRefPageTools;
    var winRefScoreTools;
    var winRefLayerBrowser;
    var winRefBarTools;
    var winRefBarModel;
    var winRefExportTools;
    var activeDocName;
    var selectedLayerName;

    var makeBtCallString = function (callName) {
        return scoreLibName + '.' + callName + '()';
    };

    var log = function (value) {
        scorelib.logit(value);
    };

    var logError = function (value) {
        scorelib.logError(value);
    };

    var printUiInfo = function (uiObj) {
        if (!uiObj) {
            return;
        }
        scorelib.btPrintUiInfo(uiObj);
    };

    var getValueFromTxtBox = function (txtBox, defaultVal) {
        if (txtBox && txtBox.text) {
            return txtBox.text;
        }

        if (defaultVal !== undefined) {
            return defaultVal;
        }

        return null;
    };

    var bridgeTalkExecute = function ($script) {
        if (!BTalk) {
            logError("BridgeTalk is not initialised");
            return;
        }
        try {
            BTalk.target = "Illustrator";
            BTalk.body = $script;
            var out = BTalk.send();
            return out;
        } catch (e) {
            logError("Failed to execute script over BridgeTalk: " + e);
            return;
        }
    };

    var executeCall = function (name) {
        var callName = makeBtCallString(name);
        bridgeTalkExecute(callName);
    };

    var getActiveDoc = function () {
        if (!theApp) {
            scorelib.showAlert("Can not find Adobe main APP");
            return false;
        }
        return theApp.activeDocument;
    };

    BTalk.onResult = function (msg) {
        if (!msg) {
            return;
        }

        var body = msg.body;
        var headers = scorelib.getBtHeaders(body);
        if (!scorelib.isArr(headers)) {
            log("Invalid headers");
            return;
        }

        var content = scorelib.removeBtHeaders(body);

        for (var i = 0; i < headers.length; i++) {
            var header = headers[i];
            if (!header || !header.name || !header.value) {
                continue;
            }
            switch (header.name) {
                case btHeaderCaller:
                    processBtMessage(content, header.value);
                    break;
            }
        }
    };

    var processBtMessage = function (content, caller) {
        if (!content || !caller) {
            return;
        }

        if (scorelib.isErrorResponse(content)) {
            scorelib.showAlert("Failed to process request: " + content);
            return;
        }

        switch (caller) {
            case btCalls.btGetLayerXmlCall:
                processLayerXML(content);
                break;
            case btCalls.btLoadLayerXmlCall:
                processModelXML(content);
                break;
            case btCalls.btInsertModelXmlCall:
                refreshLayerTreeViewOnResponse(content);
                break;
            case btCalls.btGetActiveDocNameCall:
                processActiveDocName(content);
                break;
            case btCalls.btDeleteLayerCall:
                refreshLayerTreeViewOnResponse(content);
                break;
            case btCalls.btCopyLayerCall:
                refreshLayerTreeViewOnResponse(content);
                break;
            case btCalls.btGetOpenDocsCall:
                processActiveDocs(content);
                break;
            case btCalls.btCreatePageFromPageCall:
                processCreatePage(content);
            case btCalls.btDeletePageCall:
                processDeletePageCall(content);
            case btCalls.btCreateBarCall:
                processCreateBarCall(content);
            case btCalls.btCreateBarCall:
                processDeleteBarCall(content);
            case btCalls.btExportFilesCall:
                processExportFilesCall(content);
        }

    };

    var processLayerXML = function (content) {
        if (!content) {
            return;
        }
        try {
            var layerXml = new XML(content);
            populateLayerXml(layerXml, layersTreeView);
        } catch (e) {
            logError("Failed to process XML: " + content);
        }
    };
    
    var getActiveDocDir = function(){
        var activeDoc = getActiveDoc();
        if(!activeDoc){
            return DOT;
        }
        
        var file = activeDoc.fullName;
        if(!file){
            return DOT;
        }
    
        var fs = file.fsName;
        var full = file.fullName;
        
        var dir = full.substring(0, full.lastIndexOf(SLASH));
        return dir;
    };

    var getItemType = function (layerXml) {
        var name = NAME_ITEM;
        var kids = layerXml.children();
        if (kids && kids.length() > 0) {
            name = NAME_NODE;
        }
        ;
        return name;
    };

    var processModelXML = function (content) {
        if (!content || !modelTreeView) {
            return;
        }
        try {
            var modelXml = new XML(content);
            var root = modelTreeView.add(getItemType(modelXml), modelXml.localName());
            root.xmlElement = modelXml;
            populateLayerXml(modelXml, root);

        } catch (e) {
            logError("Failed to process XML: " + e);
        }
    };

    var refreshLayerTreeViewOnResponse = function (content) {
        if (scorelib.isErrorResponse(content)) {
            scorelib.showAlert("Failed to insert model");
            return;
        }
        populateLayerTreeView();
    };

    var populateLayerXml = function iterateXml(theXml, viewParent) {

        if (!theXml.hasComplexContent()) {
            return;
        }
        var kids = theXml.children();
        for (var i = 0; i < kids.length(); i++) {
            var layerXml = kids[i];
            if (!layerXml) {
                continue;
            }
            var kidView = viewParent.add(getItemType(layerXml), layerXml.localName());
            kidView.xmlElement = layerXml;
            populateLayerXml(layerXml, kidView);
        }
    };

    var populateModelTreeView = function () {
        var fname = File.openDialog();
        if (!fname) {
            return;
        }

        if (!modelTreeView) {
            scorelib.showAlert("Can not find tree view to populate");
            return;
        }

        modelTreeView.removeAll();

        var path = fname.absoluteURI;
        log(path);

        var callProps = scorelib.createBtCallProperties();
        callProps[btProps.btPropFilePath] = path;
        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btLoadLayerXmlCall);
    };

    var insertModel = function () {
        var roots = modelTreeView.items;
        if (!roots) {
            scorelib.showAlert("Can not find data to insert");
            return;
        }
        for (var i = 0; i < roots.length; i++) {
            var item = roots[i];
            var xml = item.xmlElement;
            if (!xml) {
                continue;
            }

            var layerName;
            if (activeLayerTxt && activeLayerTxt.text) {
                layerName = activeLayerTxt.text;
            }

            var callProps = scorelib.createBtCallProperties();
            callProps[btProps.btPropItemName] = layerName;
            callProps[btProps.btPropXMLModel] = xml;
            populateBtDocProp(callProps);
            scorelib.setBtCallProperties(callProps);

            executeCall(btCalls.btInsertModelXmlCall);
        }
    };

    var deleteLayers = function () {
        var layerName = getValueFromTxtBox(activeLayerTxt);

        var callProps = scorelib.createBtCallProperties();
        callProps[btProps.btPropItemName] = layerName;

        populateBtDocProp(callProps);

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btDeleteLayerCall);
    };

    var exportFiles = function () {
        var callProps = scorelib.createBtCallProperties();

        var exportPages = getValueFromTxtBox(exportPagesTxt);
        if (exportPages) {
            callProps[btProps.btPropItemName] = exportPages;
        }

        var format = getSelected(exportFormatSel, NAME_PNG);
        if (format) {
            callProps[btProps.btPropFormat] = format;
        }
        
        var path = getValueFromTxtBox(exportDirTxt);
        if (path) {
            callProps[btProps.btPropFilePath] = path;
        }
        
        var scoreName = getValueFromTxtBox(exportScoreNameTxt);
        if (scoreName) {
            callProps[btProps.btPropScoreName] = scoreName;
        }

        callProps[btProps.btPropExportScore] = exportScoreCb.value;
        callProps[btProps.btPropExportParts] = exportPartsCb.value;
        callProps[btProps.btPropExportBeatlines] = exportBeatlinesCb.value;

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btExportFilesCall);
    };
    
    var selectExportDir = function () {
        var folder = Folder.selectDialog("Select Export Directory");
        if(folder){
            exportDirTxt.text = folder.fsName;
        }
    };

    var exportLayers = function (docName) {

        var fname = File.saveDialog();
        if (!fname) {
            return;
        }
        var path = fname.absoluteURI;
        log(path);

        if (!path) {
            return;
        }

        var callProps = scorelib.createBtCallProperties();
        callProps[btProps.btPropFilePath] = path;

        populateBtDocProp(callProps, docName);

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btExportLayerXmlCall);
    };

    var populateBrowserBtDocProp = function (callProps) {
        if (!callProps) {
            return;
        }

        if (openDocsSel || openDocsSel.selection || openDocsSel.selection.text) {
            var docName = openDocsSel.selection.text;
            callProps[btProps.btPropDocName] = docName;
        }
    };

    var populateBtDocProp = function (callProps, docName) {
        if (!callProps || !docName) {
            return;
        }

        callProps[btProps.btPropDocName] = docName;
    };

    var populateBtDestDocProp = function (callProps, docName) {
        if (!callProps || !docName) {
            return;
        }

        callProps[btProps.btPropDestDocName] = docName;
    };

    var populateBtDocProps = function (callProps) {
        if (!callProps) {
            return;
        }

        if (activeDocTxt) {
            populateBtDocProp(callProps, activeDocTxt.text);
        }

        if (copyDocTxt) {
            populateBtDestDocProp(callProps, copyDocTxt.text);
        }
    };

    var copyLayers = function () {
        var layerName = getValueFromTxtBox(activeLayerTxt);

        if (!layerName) {
            scorelib.showAlert("Invalid Active Layer Name");
            return;
        }

        var toLayerName = getValueFromTxtBox(copyToLyrNameTxt);
        var xOffset = getValueFromTxtBox(xOffsetTxt);
        var yOffset = getValueFromTxtBox(yOffsetTxt);
        var filter = getValueFromTxtBox(filterTxt);

        var callProps = scorelib.createBtCallProperties();
        callProps[btProps.btPropItemName] = layerName;
        callProps[btProps.btPropNewItemName] = toLayerName;
        if (xOffset) {
            callProps[btProps.btPropXOffset] = xOffset;
        }
        if (yOffset) {
            callProps[btProps.btPropYOffset] = yOffset;
        }
        if (filter) {
            callProps[btProps.btPropNameFilter] = filter;
        }

        populateBtDocProps(callProps);

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btCopyLayerCall);
    };

    var deletePage = function () {
        var callProps = scorelib.createBtCallProperties();

        var pagePrefix = getValueFromTxtBox(pagePrefixTxt, NAME_PAGE);
        var pageNo = parseInt(getValueFromTxtBox(delPageNoTxt, ""));

        if (!pagePrefix || !pageNo) {
            scorelib.showAlert("Invalid page name");
            return;
        }

        var pageName = pagePrefix + pageNo;
        callProps[btProps.btPropItemName] = pageName;

        var artbPrefix = getValueFromTxtBox(artboardPrefixTxt, NAME_PAGE);

        if (!artbPrefix || !pageNo) {
            scorelib.showAlert("Invalid artboard name");
            return;
        }
        var artbName = artbPrefix + pageNo;
        callProps[btProps.btPropArtbName] = artbName;

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btDeletePageCall);
    };

    var updatePage = function () {
        var callProps = scorelib.createBtCallProperties();

        var pagePrefix = getValueFromTxtBox(pagePrefixTxt, NAME_PAGE);
        var pageNo = parseInt(getValueFromTxtBox(delPageNoTxt, ""));

        if (!pagePrefix || !pageNo) {
            scorelib.showAlert("Invalid page name");
            return;
        }

        var pageName = pagePrefix + pageNo;
        callProps[btProps.btPropItemName] = pageName;

        var artbPrefix = getValueFromTxtBox(artboardPrefixTxt, NAME_PAGE);

        if (!artbPrefix || !pageNo) {
            scorelib.showAlert("Invalid artboard name");
            return;
        }
        var artbName = artbPrefix + pageNo;
        callProps[btProps.btPropArtbName] = artbName;

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btUpdatePageCall);
    };


    var setBarModelData = function () {

        var start = getValueFromTxtBox(startTxt, NAME_START);
        if (start) {
            scorelib.btModel.startLineName = start;
        }

        var end = getValueFromTxtBox(endTxt, NAME_END);
        if (end) {
            scorelib.btModel.endLineName = end;
        }

        var barnum = getValueFromTxtBox(barnumTxt, NAME_BARNUM);
        if (barnum) {
            scorelib.btModel.barnumName = barnum;
        }

        var beatline = getValueFromTxtBox(beatlineTxt, NAME_BEATLINE);
        if (beatline) {
            scorelib.btModel.beatlineName = beatline;
            scorelib.btModel.beatlineZeroName = beatline + "0";
        }

        var font = getValueFromTxtBox(fontNameTxt, TXT_FONT);
        if (font) {
            scorelib.btModel.fontName = font;
        }

        var fontSize = getValueFromTxtBox(fontSizeTxt, TXT_FONT);
        if (fontSize) {
            scorelib.btModel.fontSize = fontSize;
        }

        var barnoXOffset = getValueFromTxtBox(barnumXOffsetTxt, BARNUM_X_OFFSET);
        if (barnoXOffset) {
            scorelib.btModel.barnumXOffset = barnoXOffset;
        }

        var barnoYOffset = getValueFromTxtBox(barnumXOffsetTxt, BARNUM_Y_OFFSET);
        if (barnoYOffset) {
            scorelib.btModel.barnumYOffset = barnoYOffset;
        }

    };

    var createBar = function () {
        var callProps = scorelib.createBtCallProperties();

        var barPrefix = getValueFromTxtBox(barPrefixTxt, NAME_BAR);
        var barNo = parseInt(getValueFromTxtBox(barNoTxt, ""));
        if (!barPrefix || !barNo) {
            scorelib.showAlert("Invalid bar name");
            return;
        }

        var barName = barPrefix + barNo;
        callProps[btProps.btPropNewItemName] = barName;
        callProps[btProps.btPropItemNumber] = barNo;
        scorelib.btModel.barNamePrefix = barPrefix;

        var pagePrefix = getValueFromTxtBox(barPagePrefixTxt, NAME_PAGE);
        var pageNo = parseInt(getValueFromTxtBox(barPageNoTxt, ""));
        if (!pagePrefix || !pageNo) {
            scorelib.showAlert("Invalid page name");
            return;
        }

        var pageName = pagePrefix + pageNo;
        callProps[btProps.btPropItemName] = pageName;

        var timeSigNum = getValueFromTxtBox(timeSigNumTxt);
        var timeSigDenom = getValueFromTxtBox(timeSigDenomTxt);
        if (timeSigNum) {
            callProps[btProps.btPropNumerator] = timeSigNum;
        }
        if (timeSigDenom) {
            callProps[btProps.btPropDenominator] = timeSigDenom;
        }

        var tempo = getValueFromTxtBox(tempoTxt);
        if (tempo) {
            callProps[btProps.btPropTempo] = tempo;
        }

        var filter = getValueFromTxtBox(barFilterTxt, NAME_NOTATION);
        ;
        if (filter) {
            callProps[btProps.btPropNameFilter] = filter;
        }

        var overWriteBarNo = parseInt(getValueFromTxtBox(overwriteBarNoTxt, ""));
        if (overWriteBarNo) {
            callProps[btProps.btPropOldItemName] = barPrefix + overWriteBarNo;
        }

        var isShowTimesig = showTimesigCb.value;
        if (isShowTimesig) {
            callProps[btProps.btPropShowTimesig] = isShowTimesig;
        }

        var isCreateBeatlines = beatlineCb.value;
        if (isCreateBeatlines) {
            callProps[btProps.btPropCreateBeatlines] = isCreateBeatlines;
        }

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btCreateBarCall);
    };

    var copyBar = function () {
        var callProps = scorelib.createBtCallProperties();

        var barPrefix = getValueFromTxtBox(barPrefixTxt, NAME_BAR);
        var srcBarNo = parseInt(getValueFromTxtBox(srcBarNoTxt));
        var destBarNo = parseInt(getValueFromTxtBox(destBarNoTxt));
        if (!barPrefix || !srcBarNo || !destBarNo) {
            scorelib.showAlert("Invalid bar name");
            return;
        }

        var srcBarName = barPrefix + srcBarNo;
        var destBarName = barPrefix + destBarNo;
        callProps[btProps.btPropItemName] = srcBarName;
        callProps[btProps.btPropNewItemName] = destBarName;
        callProps[btProps.btPropItemNumber] = srcBarNo;
        scorelib.btModel.barNamePrefix = barPrefix;

        var xOffset = getValueFromTxtBox(barXOffsetTxt);
        if (xOffset) {
            callProps[btProps.btPropXOffset] = xOffset;
        }
        var yOffset = getValueFromTxtBox(barYOffsetTxt);
        if (yOffset) {
            callProps[btProps.btPropYOffset] = yOffset;
        }

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btCopyBarCall);
    };

    var deleteBar = function () {

        var callProps = scorelib.createBtCallProperties();

        var barPrefix = getValueFromTxtBox(barPrefixTxt, NAME_BAR);
        var barNo = parseInt(getValueFromTxtBox(barNoTxt, ""));
        if (!barPrefix || !barNo) {
            scorelib.showAlert("Invalid bar name");
            return;
        }

        var barName = barPrefix + barNo;
        callProps[btProps.btPropNewItemName] = barName;
        callProps[btProps.btPropItemNumber] = barNo;

        var pagePrefix = getValueFromTxtBox(barPagePrefixTxt, NAME_PAGE);
        var pageNo = parseInt(getValueFromTxtBox(barPageNoTxt, ""));
        if (!pagePrefix || !pageNo) {
            scorelib.showAlert("Invalid page name");
            return;
        }

        var pageName = pagePrefix + pageNo;
        callProps[btProps.btPropItemName] = pageName;

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btDeleteBarCall);
    };

    var createPageFromPage = function () {

        var callProps = scorelib.createBtCallProperties();

        var pagePrefix = NAME_PAGE;
        if (pagePrefixTxt && pagePrefixTxt.text) {
            pagePrefix = pagePrefixTxt.text;
        }

        var pageNo = "";
        if (pageNoTxt && pageNoTxt.text) {
            pageNo = parseInt(pageNoTxt.text);
        }

        var pageName = pagePrefix + pageNo;

        var srcPageNo = "";
        if (srcPageNoTxt && srcPageNoTxt.text) {
            srcPageNo = parseInt(srcPageNoTxt.text);
        }

        var srcPageName = pagePrefix + srcPageNo;

        callProps[btProps.btPropItemName] = srcPageName;
        callProps[btProps.btPropNewItemName] = pageName;

        var artbPrefix = NAME_PAGE;
        if (artboardPrefixTxt && artboardPrefixTxt.text) {
            artbPrefix = artboardPrefixTxt.text;
        }

        var artbName = artbPrefix + pageNo;
        var srcArtbName = artbPrefix + srcPageNo;

        callProps[btProps.btPropArtbName] = srcArtbName;
        callProps[btProps.btPropNewArtbName] = artbName;

        var artbOffset;
        if (artboardOffsetTxt && artboardOffsetTxt.text) {
            artbOffset = parseInt(artboardOffsetTxt.text);
        }
        if (artbOffset) {
            scorelib.btModel.pageOffset = artbOffset;
        }

        if (artboardPosSel || artboardPosSel.selection || artboardPosSel.selection.text) {
            var artbPosition = artboardPosSel.selection.text;
            callProps[btProps.btPropPosition] = artbPosition;
        }

        var filter;
        if (pageLayerfilterTxt && pageLayerfilterTxt.text) {
            filter = pageLayerfilterTxt.text;
        }
        if (filter) {
            callProps[btProps.btPropNameFilter] = filter;
        }

        scorelib.setBtCallProperties(callProps);

        executeCall(btCalls.btCreatePageFromPageCall);
    };

    var processActiveDocName = function (name) {
        activeDocName = name;
    };

    var processCreatePage = function (content) {
        if (scorelib.isErrorResponse(content)) {
            scorelib.showAlert("Failed to create page");
            return;
        }
    };

    var processDeletePageCall = function (content) {
        if (scorelib.isErrorResponse(content)) {
            scorelib.showAlert("Failed to create page");
            return;
        }
    };

    var processCreateBarCall = function (content) {
        if (scorelib.isErrorResponse(content)) {
            scorelib.showAlert("Failed to create page");
            return;
        }
    };

    var processDeleteBarCall = function (content) {
        if (scorelib.isErrorResponse(content)) {
            scorelib.showAlert("Failed to create page");
            return;
        }
    };

    var processExportFilesCall = function (content) {
        if (scorelib.isErrorResponse(content)) {
            scorelib.showAlert("Failed to create page");
            return;
        }
    };

    var processActiveDocs = function (docNames) {
        if (!docNames || !openDocsSel) {
            return;
        }

        var docs = docNames.split(',');

        populateDocSel(openDocsSel, docs);
    };

    var populateDocSel = function (docSel, docs) {
        docSel.removeAll();
        var activeDoc = docSel.add(NAME_ITEM, NAME_ACTIVE_DOC);
        for (var i = 0; i < docs.length; i++) {
            docSel.add(NAME_ITEM, docs[i]);
        }
        docSel.selection = activeDoc;
    };

    var populateLayerTreeView = function () {
        if (!layersTreeView) {
            return;
        }

        var callProps = scorelib.createBtCallProperties();
        populateBrowserBtDocProp(callProps);
        scorelib.setBtCallProperties(callProps);

        layersTreeView.removeAll();

        executeCall(btCalls.btGetLayerXmlCall);
    };

    var populateOpenDocs = function () {
        if (!openDocsSel) {
            return;
        }

        executeCall(btCalls.btGetOpenDocsCall);
    };

    var processLayerSelection = function (sel) {
        if (!sel) {
            return;
        }

        var selStr = sel.text;
        var hierarchy = sel;
        while (hierarchy.parent && hierarchy.parent.type === NAME_NODE) {
            selStr = hierarchy.parent.text + pathSeparator + selStr;
            hierarchy = hierarchy.parent;
        }

        var selStr = pathSeparator + selStr;

        selectedLayerName = selStr;
    };

    var populateSeletedLayer = function () {
        if (layerTxt) {
            layerTxt.text = selectedLayerName;
        }

        if (docTxt) {
            docTxt.text = getSelectedDocName();
        }

    };

    var getSelectedDocName = function () {
        if (openDocsSel && openDocsSel.selection && openDocsSel.selection.text) {
            return openDocsSel.selection.text;
        }
        return NAME_EMPTY;
    };

    var getSelected = function (control, defaultValue) {
        if (control && control.selection && control.selection.text) {
            return control.selection.text;
        }
        return defaultValue;
    };

    var shutDown = function () {
        if (winRefLayerTools) {
            winRefLayerTools.close(2);
        }
        if (winRefScoreTools) {
            winRefScoreTools.close(2);
        }
        if (winRefLayerBrowser) {
            winRefLayerBrowser.close(2);
        }
        if (winRefPageTools) {
            winRefPageTools.close(2);
        }
        if (winRefBarTools) {
            winRefBarTools.close(2);
        }
        if (winRefBarModel) {
            winRefBarModel.close(2);
        }
        if (winRefExportTools) {
            winRefExportTools.close(2);
        }

    };

    var browseLayers = function (activeDocTxt, activeLayerTxt) {
        docTxt = activeDocTxt;
        layerTxt = activeLayerTxt;

        if (!winRefLayerBrowser) {
            createLayerBrowserView();
        }
        populateOpenDocs();

        winRefLayerBrowser.show();

    };

    // Dialog window  resources;
    var layersViewRes =
            "palette { \
            properties:{ closeButton:true, maximizeButton:false,  minimizeButton:false, resizeable:true}, \
            orientation:'column', spacing:2, margins:5,\
            alignChildren:['fill','top'], \
            text: 'Layer Tools', frameLocation:[100,100], \
            pnl: Group { orientation:'row', alignChildren:['fill', 'fill'], \
                layers: Group { orientation:'column', alignChildren:['fill', 'fill'], \
                    active: Panel { orientation:'column', alignChildren:['fill', 'top'], text:'Active Layer', \
                        doc: Group { orientation:'row', alignChildren:['right', 'center'], \
                            docTxt: StaticText { text: 'Document: ' }, \
                            docNameTxt: EditText {characters:30, justify:'left'}, \
                        }, \
                        lyr: Group { orientation:'row', alignChildren:['right', 'center'], \
                            layerTxt: StaticText { text: 'Layer: ' }, \
                            layerNameTxt: EditText {characters:30, justify:'left'}, \
                        }, \
                        btns: Group { orientation:'row', alignChildren:['right', 'center'], \
                            browseBtn: Button { text:'Browse', properties:{name:'populate'} }, \
                            exportBtn: Button { text:'Save Doc XML', properties:{name:'export'} }, \
                            deleteBtn: Button { text:'Delete Layer', properties:{name:'delete'} }, \
                        }, \
                    }, \
                    copy: Panel { orientation:'column', alignChildren:['fill', 'top'], text: 'Copy to: ', \
                        doc: Group { orientation:'row', alignChildren:['right', 'center'], \
                            docTxt: StaticText { text: 'Document: ' }, \
                            docNameTxt: EditText {characters:30, justify:'left'}, \
                        }, \
                        layer: Group { orientation:'row', alignChildren:['right', 'center'], \
                            copyLayerToTxt: StaticText { text: 'Layer: ' }, \
                            copyToLayerNameTxt: EditText { characters:30, justify:'left'}, \
                        }, \
                        filter: Group { orientation:'row', alignChildren:['right', 'center'], \
                            filterNameTxt: StaticText { text: 'Name Filter: ' }, \
                            filterEdtTxt: EditText { characters:30, justify:'right'}, \
                        }, \
                        offset: Group { orientation:'row', alignChildren:['right', 'center'], \
                            xOffsetNameTxt: StaticText { text: 'X offset: ' }, \
                            xOffsetEdtTxt: EditText { characters:6, text:'0', justify:'right'}, \
                            yOffsetNameTxt: StaticText { text: 'Y offset: ' }, \
                            yOffsetEdtTxt: EditText { characters:6, text:'0', justify:'right'}, \
                        }, \
                        btns: Group { orientation:'row', alignChildren:['right', 'center'], \
                            browseBtn: Button { text:'Browse', properties:{name:'populate'} }, \
                            copyBtn: Button { text:'Copy', properties:{name:'copy'} }, \
                        }, \
                    }, \
                }, \
                model: Panel { orientation:'column', alignChildren:['fill', 'top'], text:' Layer Model', \
                    btns: Group { orientation:'row', \
                        loadBtn: Button { text:'Load XML', properties:{name:'load'} }, \
                    }, \
                    modelTreeView: TreeView { preferredSize: [150, 200]  }, \
                    btnsBtm: Group { orientation:'row', alignChildren:['right', 'center'], \
                        insertBtn: Button { text:'Insert into Active', properties:{name:'insert'} }, \
                    }, \
                }, \
            }, \
            ctrl: Group { orientation:'row', alignChildren:['right', 'bottom'], \
                cancelBtn: Button { text:'Cancel', properties:{name:'cancel'} } \
            } \
        }";

    var layerBrowserViewRes =
            "palette { \
            properties:{ closeButton:true, maximizeButton:false,  minimizeButton:false, resizeable:true}, \
            orientation:'column', spacing:2, margins:5,\
            alignChildren:['fill','top'], \
            text: 'Layer Browser', frameLocation:[100,100], \
            browser: Panel { orientation:'column', alignChildren:['fill', 'top'], text:'Current Layers', \
                doc: Group { orientation:'row', alignChildren:['right', 'center'], \
                    openDocsSel: DropDownList { title: 'Document: ', preferredSize:[250,40], alignment:’right’ }, \
                }, \
                btns: Group { orientation:'row', \
                    loadDocsBtn: Button { text:'Refresh', properties:{name:'loadDocs'} }, \
                    exportBtn: Button { text:'Save Doc XML', properties:{name:'export'} }, \
                }, \
                layersTreeView: TreeView { preferredSize: [100, 150]  }, \
                btnsBottom: Group { orientation:'row', alignChildren:['right', 'center'], \
                    okBtn: Button { text:'OK', properties:{name:'ok'} }, \
                    cancelBtn: Button { text:'Cancel', properties:{name:'cancel'} }, \
                }, \
            }, \
        }";

    var pageToolsViewRes =
            "palette { \
            properties:{ closeButton:true, maximizeButton:false,  minimizeButton:false, resizeable:true}, \
            orientation:'column', spacing:2, margins:5,\
            alignChildren:['fill','top'], \
            text: 'Page Tools', frameLocation:[100,100], \
            pt: Panel { orientation:'column', alignChildren:['fill', 'top'], text:'Page Tools', \
                pageInfo: Group { orientation:'row', alignChildren:['right', 'center'], \
                    pagePrefixTxt: StaticText { text: 'Page Layer Prefix: ' }, \
                    pagePrefixEdtTxt: EditText { characters:10, justify:'right'}, \
                }, \
                artboardInfo: Group { orientation:'row', alignChildren:['right', 'center'], \
                    artbPrefixTxt: StaticText { text: 'Artboard Prefix: ' }, \
                    artbPrefixEdtTxt: EditText { characters:10, justify:'right'}, \
                }, \
                artboardOffset: Group { orientation:'row', alignChildren:['right', 'center'], \
                    artbOffsetTxt: StaticText { text: 'Page Offset (pt): ' }, \
                    artbOffsetEdtTxt: EditText { characters:10, justify:'right'}, \
                }, \
                artboardPosition: Group { orientation:'row', alignChildren:['right', 'center'], \
                    artbPosSel: DropDownList { title: 'New page position: ', preferredSize:[210,40]}, \
                }, \
                filter: Group { orientation:'row', alignChildren:['right', 'center'], \
                    filterNameTxt: StaticText { text: 'Filter Out Layers: ' }, \
                    filterEdtTxt: EditText { characters:10, justify:'right'}, \
                }, \
                create: Group { orientation:'row', alignChildren:['right', 'center'], \
                    pageNoTxt: StaticText { text: 'Create page No: ' }, \
                    pageNoEdtTxt: EditText { characters:3, justify:'right'}, \
                    srcPageNoTxt: StaticText { text: 'From page No: ' }, \
                    srcPageNoEdtTxt: EditText { characters:3, justify:'right'}, \
                }, \
                delete: Group { orientation:'row', alignChildren:['right', 'center'], \
                    pageNoTxt: StaticText { text: 'Delete/Update page No: ' }, \
                    pageNoEdtTxt: EditText { characters:3, justify:'right'}, \
                }, \
                btns: Group { orientation:'row', alignChildren:['right', 'center'], \
                    createBtn: Button { text:'Create', properties:{name:'createPage'} }, \
                    deleteBtn: Button { text:'Delete', properties:{name:'deletePage'} }, \
                    updateBtn: Button { text:'Update', properties:{name:'updatePage'} }, \
                    cancelBtn: Button { text:'Cancel', properties:{name:'cancel'} }, \
                }, \
            }, \
        }";

    var barToolsViewRes =
            "palette { \
            properties:{ closeButton:true, maximizeButton:false,  minimizeButton:false, resizeable:true}, \
            orientation:'column', spacing:2, margins:2,\
            alignChildren:['fill','top'], \
            text: 'Bar Tools', frameLocation:[100,100], \
            bt: Panel { orientation:'column', alignChildren:['fill', 'top'], text:'Bar Tools', \
                model: Group { orientation:'row', alignChildren:['right', 'center'], \
                    c1: Group { orientation:'column', alignChildren:['right', 'center'], \
                        r1: Group { orientation:'row', alignChildren:['right', 'center'], \
                            barNoTxt: StaticText { text: 'Bar No: ' }, \
                            barNoEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r2: Group { orientation:'row', alignChildren:['right', 'center'], \
                            timeSigTxt: StaticText { text: 'Time Signature: ' }, \
                            numeratorEdtTxt: EditText { characters:2, justify:'right'}, \
                            timeSigDelimTxt: StaticText { text: '/' }, \
                            denominatorEdtTxt: EditText { characters:2, justify:'right'}, \
                        }, \
                        r3: Group { orientation:'row', alignChildren:['right', 'center'], \
                            overwriteBarNoTxt: StaticText { text: 'Overwrite Bar No: ' }, \
                            overwriteBarNoEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r4: Group { orientation:'row', alignChildren:['right', 'center'], \
                            srcBarNoTxt: StaticText { text: 'Copy Notation From Bar No: ' }, \
                            srcBarNoEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r5: Group { orientation:'row', alignChildren:['right', 'center'], \
                            xOffsetNameTxt: StaticText { text: 'Override Copy X offset: ' }, \
                            xOffsetEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r6: Group { orientation:'row', alignChildren:['right', 'center'], \
                            showTimesigTxt: StaticText { text: 'Show Time Sig: ' }, \
                            showTimesigCb: Checkbox {preferredSize:[20,20]}, \
                        }, \
                    }, \
                    c2: Group { orientation:'column', alignChildren:['right', 'center'], \
                        r1: Group { orientation:'row', alignChildren:['right', 'center'], \
                            pageNoTxt: StaticText { text: 'On Page No: ' }, \
                            pageNoEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r2: Group { orientation:'row', alignChildren:['right', 'center'], \
                            tempoTxt: StaticText { text: 'Tempo: ' }, \
                            tempoEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r3: Group { orientation:'row', alignChildren:['right', 'center'], \
                            filterNameTxt: StaticText { text: 'Filter Layers: ' }, \
                            filterEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r4: Group { orientation:'row', alignChildren:['right', 'center'], \
                            destBarNoTxt: StaticText { text: 'To Bar No: ' }, \
                            destBarNoEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r5: Group { orientation:'row', alignChildren:['right', 'center'], \
                            yOffsetNameTxt: StaticText { text: 'Copy Y offset: ' }, \
                            yOffsetEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r6: Group { orientation:'row', alignChildren:['right', 'center'], \
                            isBeatlinesTxt: StaticText { text: 'Create Beatlines: ' }, \
                            isBeatlinesCb: Checkbox {preferredSize:[20,20]}, \
                        }, \
                    }, \
                }, \
                btns: Group { orientation:'row', alignChildren:['right', 'center'], \
                    modelBtn: Button { text:'Edit Model', properties:{name:'editModel'} }, \
                    createBtn: Button { text:'Create', properties:{name:'createBar'} }, \
                    deleteBtn: Button { text:'Delete', properties:{name:'deleteBar'} }, \
                    copyBtn: Button { text:'Copy', properties:{name:'copyBar'} }, \
                    cancelBtn: Button { text:'Cancel', properties:{name:'cancel'} }, \
                }, \
            }, \
        }";

    var barToolsModelRes =
            "palette { \
            properties:{ closeButton:true, maximizeButton:false,  minimizeButton:false, resizeable:true}, \
            orientation:'column', spacing:2, margins:2,\
            alignChildren:['fill','top'], \
            text: 'Bar Tools', frameLocation:[100,100], \
            bt: Panel { orientation:'column', alignChildren:['fill', 'top'], text:'Bar Tools', \
                model: Group { orientation:'row', alignChildren:['right', 'center'], \
                    c1: Group { orientation:'column', alignChildren:['right', 'center'], \
                        r1: Group { orientation:'row', alignChildren:['right', 'center'], \
                            barPrefixTxt: StaticText { text: 'Bar Prefix: ' }, \
                            barPrefixEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r2: Group { orientation:'row', alignChildren:['right', 'center'], \
                            startLineTxt: StaticText { text: 'Start Line Name: ' }, \
                            startLineEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r3: Group { orientation:'row', alignChildren:['right', 'center'], \
                            beatLinePrefixTxt: StaticText { text: 'Beat Line Prefix: ' }, \
                            beatLinePrefixEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r4: Group { orientation:'row', alignChildren:['right', 'center'], \
                            fontNameTxt: StaticText { text: 'Default Font: ' }, \
                            fontNameEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r5: Group { orientation:'row', alignChildren:['right', 'center'], \
                            barnumXOffsetTxt: StaticText { text: 'BarNo X Offset: ' }, \
                            barnumXOffsetEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                    }, \
                    c2: Group { orientation:'column', alignChildren:['right', 'center'], \
                        r1: Group { orientation:'row', alignChildren:['right', 'center'], \
                            pagePrefixTxt: StaticText { text: 'Page Prefix: ' }, \
                            pagePrefixEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r2: Group { orientation:'row', alignChildren:['right', 'center'], \
                            endLineTxt: StaticText { text: 'End Line Name: ' }, \
                            endLineEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r3: Group { orientation:'row', alignChildren:['right', 'center'], \
                            barnumPrefixTxt: StaticText { text: 'Bar Number Name: ' }, \
                            barnumPrefixEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r4: Group { orientation:'row', alignChildren:['right', 'center'], \
                            fontSizeTxt: StaticText { text: 'Default Font Size: ' }, \
                            fontSizeEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r5: Group { orientation:'row', alignChildren:['right', 'center'], \
                            barnumYOffsetTxt: StaticText { text: 'BarNo Y Offset: ' }, \
                            barnumYOffsetEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                    }, \
                }, \
                btns: Group { orientation:'row', alignChildren:['right', 'center'], \
                    okBtn: Button { text:'OK', properties:{name:'ok'} }, \
                    cancelBtn: Button { text:'Cancel', properties:{name:'cancel'} }, \
                }, \
            }, \
        }";

    var exportToolsRes =
            "palette { \
            properties:{ closeButton:true, maximizeButton:false,  minimizeButton:false, resizeable:true}, \
            orientation:'column', spacing:2, margins:2,\
            alignChildren:['fill','top'], \
            text: 'Export Tools', frameLocation:[100,100], \
            et: Panel { orientation:'column', alignChildren:['fill', 'top'], text:'Export Tools', \
                model: Group { orientation:'row', alignChildren:['right', 'center'], \
                    c1: Group { orientation:'column', alignChildren:['right', 'center'], \
                        r1: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportPagesTxt: StaticText { text: 'Export Pages: ' }, \
                            exportPagesEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r2: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportDirTxt: StaticText { text: 'Save to Dir: ' }, \
                            exportDirEdtTxt: EditText { characters:15, justify:'right'}, \
                        }, \
                        r3: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportScoreTxt: StaticText { text: 'Export Score: ' }, \
                            exportScoreCb: Checkbox {preferredSize:[20,20]}, \
                        }, \
                        r4: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportScoreTxt: StaticText { text: 'Export: ' }, \
                        }, \
                    }, \
                    c2: Group { orientation:'column', alignChildren:['right', 'center'], \
                        r1: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportFormatTxt: StaticText { text: 'Export Format: ' }, \
                            exportFormatSel: DropDownList { preferredSize:[60,25], alignment:’right’ }, \
                        }, \
                        r2: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportPartsTxt: StaticText { text: 'Score Name: ' }, \
                            exportScoreNameEdtTxt: EditText { characters:7, justify:'right'}, \
                        }, \
                        r3: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportPartsTxt: StaticText { text: 'Export Parts: ' }, \
                            exportPartsCb: Checkbox {preferredSize:[20,20]}, \
                        }, \
                        r4: Group { orientation:'row', alignChildren:['right', 'center'], \
                            exportBeatlinesPTxt: StaticText { text: 'Export Beatlines: ' }, \
                            exportBeatlinesCb: Checkbox {preferredSize:[20,20]}, \
                        }, \
                    }, \
                }, \
                btns: Group { orientation:'row', alignChildren:['right', 'center'], \
                    chooseDirBtn: Button { text:'Choose Dir', properties:{name:'chooseDir'} }, \
                    exportBtn: Button { text:'Export', properties:{name:'export'} }, \
                    cancelBtn: Button { text:'Cancel', properties:{name:'cancel'} }, \
                }, \
            }, \
        }";

    var topViewRes =
            "palette { \
            properties:{ closeButton:true, maximizeButton:false,  minimizeButton:false, resizeable:true}, \
            orientation:'column', spacing:2, margins:2,\
            alignChildren:['fill','top'], \
            text: 'ZScore Tools', frameLocation:[100,100], \
            tools: Panel { orientation:'column', alignChildren:['fill', 'fill'], text:'ZScore Tools', \
                btns: Group { orientation:'column', alignChildren:['fill', 'fill'], \
                    layerToolsBtn: Button { text:'Layer', properties:{name:'manageLayers'} } \
                    pageToolsBtn: Button { text:'Page', properties:{name:'managePages'} } \
                    barToolsBtn: Button { text:'Bar', properties:{name:'manageBars'} } \
                    exportToolsBtn: Button { text:'Export', properties:{name:'exportFile'} } \
                    closeBtn: Button { text:'Close', properties:{name:'close'} } \
                } \
            } \
        }";

    var createTopView = function () {
        // Create the dialog with the components
        if (winRefScoreTools) {
            shutDown();
        }

        winRefScoreTools = new Window(topViewRes);

        var btns = winRefScoreTools.tools.btns;

        btns.layerToolsBtn.onClick = function () {
            if (!winRefLayerTools) {
                createLayersView();
            }

            winRefLayerTools.show();
        };

        btns.pageToolsBtn.onClick = function () {
            if (!winRefPageTools) {
                createPagesView();
            }

            winRefPageTools.show();
        };

        btns.barToolsBtn.onClick = function () {
            if (!winRefBarTools) {
                createBarsView();
            }

            winRefBarTools.show();
        };

        btns.exportToolsBtn.onClick = function () {
            if (!winRefExportTools) {
                createExportView();
            }

            winRefExportTools.show();
        };

        btns.closeBtn.onClick = function () {
            shutDown();
        };

        winRefScoreTools.center();

        return true;
    };

    var createPagesView = function () {
        // Create the dialog with the components
        if (!winRefPageTools) {
            winRefPageTools = new Window(pageToolsViewRes);
        }

        var pt = winRefPageTools.pt;
        var btns = pt.btns;

        pagePrefixTxt = pt.pageInfo.pagePrefixEdtTxt;
        artboardPrefixTxt = pt.artboardInfo.artbPrefixEdtTxt;
        artboardOffsetTxt = pt.artboardOffset.artbOffsetEdtTxt;
        pageLayerfilterTxt = pt.filter.filterEdtTxt;
        artboardPosSel = pt.artboardPosition.artbPosSel;
        pageNoTxt = pt.create.pageNoEdtTxt;
        srcPageNoTxt = pt.create.srcPageNoEdtTxt;
        delPageNoTxt = pt.delete.pageNoEdtTxt;

        pagePrefixTxt.text = NAME_PAGE;
        artboardPrefixTxt.text = NAME_PAGE;
        artboardOffsetTxt.text = PAGE_OFFSET;
        pageLayerfilterTxt.text = NAME_NOTATION;
        pageNoTxt.text = 0;
        srcPageNoTxt.text = 0;
        delPageNoTxt.text = 0;

        var right = artboardPosSel.add(NAME_ITEM, NAME_RIGHT);
        artboardPosSel.add(NAME_ITEM, NAME_BELOW);
        artboardPosSel.selection = right;

        btns.cancelBtn.onClick = function () {
            winRefPageTools.hide();
        };

        btns.deleteBtn.onClick = function () {
            deletePage();
        };

        btns.createBtn.onClick = function () {
            createPageFromPage();
        };

        btns.updateBtn.onClick = function () {
            updatePage();
        };

        winRefPageTools.center();

        return true;
    };

    var createBarsView = function () {
        // Create the dialog with the components
        if (!winRefBarTools) {
            winRefBarTools = new Window(barToolsViewRes);
        }

        var bt = winRefBarTools.bt;
        var btns = bt.btns;

        barNoTxt = bt.model.c1.r1.barNoEdtTxt;
        barPageNoTxt = bt.model.c2.r1.pageNoEdtTxt;

        timeSigNumTxt = bt.model.c1.r2.numeratorEdtTxt;
        timeSigDenomTxt = bt.model.c1.r2.denominatorEdtTxt;
        tempoTxt = bt.model.c2.r2.tempoEdtTxt;

        overwriteBarNoTxt = bt.model.c1.r3.overwriteBarNoEdtTxt;
        barFilterTxt = bt.model.c2.r3.filterEdtTxt;

        srcBarNoTxt = bt.model.c1.r4.srcBarNoEdtTxt;
        destBarNoTxt = bt.model.c2.r4.destBarNoEdtTxt;

        barXOffsetTxt = bt.model.c1.r5.xOffsetEdtTxt;
        barYOffsetTxt = bt.model.c2.r5.yOffsetEdtTxt;

        showTimesigCb = bt.model.c1.r6.showTimesigCb;
        beatlineCb = bt.model.c2.r6.isBeatlinesCb;

        barFilterTxt.text = NAME_NOTATION + COMMA + NAME_PRESTART;
        barNoTxt.text = 0;
        barPageNoTxt.text = 0;
        timeSigNumTxt.text = 4;
        timeSigDenomTxt.text = 4;
        barXOffsetTxt.text = 0;
        barYOffsetTxt.text = 0;
        beatlineCb.value = true;
        showTimesigCb.value = true;

        btns.cancelBtn.onClick = function () {
            winRefBarTools.hide();
        };

        btns.deleteBtn.onClick = function () {
            deleteBar();
        };

        btns.createBtn.onClick = function () {
            createBar();
        };

        btns.copyBtn.onClick = function () {
            copyBar();
        };

        btns.modelBtn.onClick = function () {
            if (!winRefBarModel) {
                createBarModelView();
            }

            winRefBarModel.show();
        };

        winRefBarTools.center();

        return true;
    };

    var createBarModelView = function () {
        // Create the dialog with the components
        if (!winRefBarModel) {
            winRefBarModel = new Window(barToolsModelRes);
        }

        var bt = winRefBarModel.bt;
        var btns = bt.btns;

        barPrefixTxt = bt.model.c1.r1.barPrefixEdtTxt;
        barPagePrefixTxt = bt.model.c2.r1.pagePrefixEdtTxt;

        startTxt = bt.model.c1.r2.startLineEdtTxt;
        endTxt = bt.model.c2.r2.endLineEdtTxt;

        beatlineTxt = bt.model.c1.r3.beatLinePrefixEdtTxt;
        barnumTxt = bt.model.c2.r3.barnumPrefixEdtTxt;

        fontNameTxt = bt.model.c1.r4.fontNameEdtTxt;
        fontSizeTxt = bt.model.c2.r4.fontSizeEdtTxt;

        barnumXOffsetTxt = bt.model.c1.r5.barnumXOffsetEdtTxt;
        barnumYOffsetTxt = bt.model.c2.r5.barnumYOffsetEdtTxt;

        barPrefixTxt.text = NAME_BAR;
        barPagePrefixTxt.text = NAME_PAGE;
        startTxt.text = NAME_START;
        endTxt.text = NAME_END;
        barnumTxt.text = NAME_BARNUM;
        beatlineTxt.text = NAME_BEATLINE;
        fontNameTxt.text = TXT_FONT;
        fontSizeTxt.text = TXT_SIZE;
        barnumXOffsetTxt.text = BARNUM_X_OFFSET;
        barnumYOffsetTxt.text = BARNUM_Y_OFFSET;

        btns.cancelBtn.onClick = function () {
            winRefBarModel.hide();
        };

        btns.okBtn.onClick = function () {
            setBarModelData();
            winRefBarModel.hide();
        };

        winRefBarModel.center();

        return true;
    };

    var createLayerBrowserView = function () {
        // Create the dialog with the components
        if (!winRefLayerBrowser) {
            winRefLayerBrowser = new Window(layerBrowserViewRes);
        }

        var browser = winRefLayerBrowser.browser;
        openDocsSel = browser.doc.openDocsSel;
        layersTreeView = browser.layersTreeView;

        openDocsSel.onChange = function () {
            if (!this.selection) {
                return;
            }

            populateLayerTreeView();
        };

        layersTreeView.onChange = function () {
            if (!this.selection) {
                return;
            }

            processLayerSelection(this.selection);
        };

        browser.btns.loadDocsBtn.onClick = function () {
            populateOpenDocs();
        };

        browser.btnsBottom.okBtn.onClick = function () {
            populateSeletedLayer();
            winRefLayerBrowser.hide();
        };

        browser.btnsBottom.cancelBtn.onClick = function () {
            winRefLayerBrowser.hide();
        };

        browser.btns.exportBtn.onClick = function () {
            var docName = getSelectedDocName();
            if (docName.length < 1) {
                docName = NAME_ACTIVE_DOC;
            }
            exportLayers(docName);
        };

        winRefLayerBrowser.center();

        return true;
    };


    var createLayersView = function () {

        if (!winRefLayerTools) {
            winRefLayerTools = new Window(layersViewRes);
        }

        var lyrs = winRefLayerTools.pnl.layers;
        var active = lyrs.active;
        var copy = lyrs.copy;
        var model = winRefLayerTools.pnl.model;
        var ctrl = winRefLayerTools.ctrl;

        modelTreeView = model.modelTreeView;
        copyToLyrNameTxt = copy.layer.copyToLayerNameTxt;
        copyDocTxt = copy.doc.docNameTxt;
        activeLayerTxt = active.lyr.layerNameTxt;
        activeDocTxt = active.doc.docNameTxt;
        xOffsetTxt = copy.offset.xOffsetEdtTxt;
        yOffsetTxt = copy.offset.yOffsetEdtTxt;
        filterTxt = copy.filter.filterEdtTxt;

        active.btns.browseBtn.onClick = function () {
            browseLayers(activeDocTxt, activeLayerTxt);
        };

        copy.btns.browseBtn.onClick = function () {
            browseLayers(copyDocTxt, copyToLyrNameTxt);
        };

        copy.btns.copyBtn.onClick = function () {
            copyLayers();
        };

        active.btns.deleteBtn.onClick = function () {
            deleteLayers();
        };

        ctrl.cancelBtn.onClick = function () {
            winRefLayerTools.hide();
        };

        active.btns.exportBtn.onClick = function () {
            var docName = NAME_ACTIVE_DOC;
            if (activeDocTxt && activeDocTxt.text) {
                docName = activeDocTxt.text;
            }
            exportLayers(docName);
        };

        model.btns.loadBtn.onClick = function () {
            populateModelTreeView();
        };

        model.btnsBtm.insertBtn.onClick = function () {
            insertModel();
        };

        populateOpenDocs();

        winRefLayerTools.center();

        return true;
    };
    
    var createExportView = function () {
        // Create the dialog with the components
        if (!winRefExportTools) {
            winRefExportTools = new Window(exportToolsRes);
        }
        
        var et = winRefExportTools.et;
        var btns = et.btns;

        exportPagesTxt = et.model.c1.r1.exportPagesEdtTxt;
        exportFormatSel = et.model.c2.r1.exportFormatSel;

        exportDirTxt = et.model.c1.r2.exportDirEdtTxt;
        exportScoreNameTxt = et.model.c2.r2.exportScoreNameEdtTxt;
        
        exportScoreCb = et.model.c1.r3.exportScoreCb;
        exportPartsCb = et.model.c2.r3.exportPartsCb;
        
        exportBeatlinesCb = et.model.c2.r4.exportBeatlinesCb;
        
        exportPartsCb.value = true;
        exportScoreCb.value = true;
        exportBeatlinesCb.value=true;
        exportFormatSel.removeAll();
        var png = exportFormatSel.add(NAME_ITEM, NAME_PNG);
        var svg = exportFormatSel.add(NAME_ITEM, NAME_SVG);
        exportFormatSel.selection = png;
        exportDirTxt.text = getActiveDocDir();
        var activeDoc = getActiveDoc();
        if(activeDoc){
            exportScoreNameTxt.text = activeDoc.name;
        }

        btns.cancelBtn.onClick = function () {
            winRefExportTools.hide();
        };
        
        btns.chooseDirBtn.onClick = function () {
            selectExportDir();
        };

        btns.exportBtn.onClick = function () {
            exportFiles();
        };

        winRefExportTools.center();

        return true;
    };

    return {
        prop: "",
        run: function () {
            log("About to run ZSVIEW");
            if (!scorelib || !szApp) {
                Window.alert("Could not find required libraries.");
                return;
            }
            getActiveDoc();
            // scorelib.logFonts();

            if (!winRefLayerTools) {
                createLayersView();
            }

            if (!winRefScoreTools) {
                createTopView();
            }

            if (!winRefLayerBrowser) {
                createLayerBrowserView();
            }
            
            if (!winRefExportTools) {
                createExportView();
            }

            if (winRefScoreTools) {
                winRefScoreTools.show();
            } else {
                scorelib.showAlert("Failed to create Tools Window");
            }
            log("Ran ZSCORE");
            return true;
        }
    };

}(ZSCORE);

ZSVIEW.run();
