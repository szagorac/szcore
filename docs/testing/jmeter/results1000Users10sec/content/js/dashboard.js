/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "KO",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "OK",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.92972, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, ""], "isController": true}, {"data": [1.0, 500, 1500, "\/htp-130"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-132"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-133"], "isController": false}, {"data": [0.9985, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.997, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.2735, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.9985, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.9985, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.9975, 500, 1500, "\/htp-122"], "isController": false}, {"data": [0.9985, 500, 1500, "\/htp-123"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.9985, 500, 1500, "\/htp-125"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.9975, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-118"], "isController": false}, {"data": [0.9985, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 24000, 0, 0.0, 251.92241666666496, 2, 5338, 147.0, 236.0, 375.0, 4566.970000000005, 1473.0252255569876, 59996.40854201191, 562.0943426624931], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 1000, 0, 0.0, 6046.137999999993, 3015, 7928, 6459.0, 7468.9, 7551.849999999999, 7707.92, 61.061244428161444, 59688.67829120108, 559.2112795383771], "isController": true}, {"data": ["\/htp-130", 1000, 0, 0.0, 138.31999999999982, 3, 438, 141.0, 207.79999999999995, 236.8499999999998, 351.9200000000001, 70.16066792955868, 1205.0642847119905, 26.858380691784184], "isController": false}, {"data": ["\/htp-131", 1000, 0, 0.0, 135.41899999999993, 2, 488, 141.0, 205.89999999999998, 222.94999999999993, 314.99, 71.36739937196688, 1225.7908399942905, 27.32033257208107], "isController": false}, {"data": ["\/htp-132", 1000, 0, 0.0, 140.72599999999994, 2, 512, 144.5, 214.89999999999998, 249.89999999999986, 402.98, 73.23861139592793, 1257.9303683902153, 28.03665592500366], "isController": false}, {"data": ["\/htp-133", 1000, 0, 0.0, 141.59999999999982, 3, 586, 144.0, 219.89999999999998, 261.94999999999993, 417.94000000000005, 74.62686567164178, 1281.7747201492537, 28.56809701492537], "isController": false}, {"data": ["\/htp-112", 1000, 0, 0.0, 173.0969999999998, 11, 557, 161.0, 264.4999999999999, 335.89999999999986, 452.96000000000004, 66.67555674089878, 1145.2047773036406, 24.74288238431791], "isController": false}, {"data": ["\/htp-134", 1000, 0, 0.0, 141.186, 2, 605, 144.5, 218.89999999999998, 253.89999999999986, 400.99, 75.17101405697963, 1291.1208937833571, 28.776403818687513], "isController": false}, {"data": ["\/htp-113", 1000, 0, 0.0, 163.6919999999999, 4, 625, 154.0, 239.0, 283.94999999999993, 436.8600000000001, 67.0690811535882, 1151.9638665325285, 25.67488262910798], "isController": false}, {"data": ["\/htp-114", 1000, 0, 0.0, 161.5600000000003, 20, 848, 150.0, 233.0, 286.7999999999997, 411.94000000000005, 66.19886137958426, 1137.0171620548126, 25.341751621872103], "isController": false}, {"data": ["\/htp-115", 1000, 0, 0.0, 157.856, 18, 580, 148.0, 228.0, 274.8499999999998, 420.93000000000006, 66.56460094521734, 1143.2990248285962, 25.48176129934101], "isController": false}, {"data": ["\/test.html-103", 1000, 0, 0.0, 2584.3579999999997, 76, 5338, 2381.5, 4982.0, 5040.95, 5245.87, 66.30860022544924, 38623.33503249122, 24.21818016046681], "isController": false}, {"data": ["\/htp-116", 1000, 0, 0.0, 155.35599999999994, 37, 575, 144.0, 229.89999999999998, 275.89999999999986, 403.97, 66.2778366914104, 1138.3736247348886, 25.37198435843054], "isController": false}, {"data": ["\/htp-117", 1000, 0, 0.0, 152.34700000000012, 26, 574, 145.0, 223.0, 250.94999999999993, 405.99, 65.98917777484492, 1133.4156823280982, 25.26148211693282], "isController": false}, {"data": ["\/htp-129", 1000, 0, 0.0, 139.1950000000001, 3, 598, 139.0, 208.0, 236.0, 378.84000000000015, 69.29526713325481, 1190.2003499410991, 26.527094449449105], "isController": false}, {"data": ["\/htp-120", 1000, 0, 0.0, 154.8199999999999, 5, 621, 147.0, 227.89999999999998, 289.4999999999993, 440.8800000000001, 66.02839220864972, 1134.089220864972, 25.276493892373722], "isController": false}, {"data": ["\/htp-121", 1000, 0, 0.0, 150.87899999999993, 6, 576, 146.0, 223.0, 255.89999999999986, 388.97, 66.19886137958426, 1137.0171620548126, 25.341751621872103], "isController": false}, {"data": ["\/htp-122", 1000, 0, 0.0, 153.0449999999998, 5, 576, 148.0, 220.0, 265.74999999999966, 410.96000000000004, 66.20324395895399, 1137.0924362793776, 25.34342932803707], "isController": false}, {"data": ["\/htp-123", 1000, 0, 0.0, 152.8389999999997, 29, 554, 148.0, 221.89999999999998, 253.89999999999986, 408.94000000000005, 66.41429235571495, 1140.717357375307, 25.42422129242213], "isController": false}, {"data": ["\/htp-124", 1000, 0, 0.0, 151.97699999999992, 14, 633, 147.0, 220.0, 270.94999999999993, 418.8000000000002, 67.00167504187604, 1150.8061139028475, 25.649078726968174], "isController": false}, {"data": ["\/htp-125", 1000, 0, 0.0, 153.16100000000006, 17, 519, 151.0, 217.0, 260.94999999999993, 410.9100000000001, 66.58232904987015, 1143.6035188760902, 25.488547839403424], "isController": false}, {"data": ["\/htp-126", 1000, 0, 0.0, 147.34599999999998, 3, 598, 146.0, 211.89999999999998, 245.94999999999993, 395.95000000000005, 67.26306585054147, 1155.2957052532454, 25.749142395910404], "isController": false}, {"data": ["\/htp-127", 1000, 0, 0.0, 148.73799999999983, 20, 737, 143.5, 223.0, 259.74999999999966, 447.94000000000005, 67.49460043196544, 1159.2724925755938, 25.83777672786177], "isController": false}, {"data": ["\/htp-128", 1000, 0, 0.0, 141.6300000000001, 5, 555, 141.0, 209.0, 241.89999999999986, 384.99, 68.18956699624958, 1171.2090862598022, 26.10381861575179], "isController": false}, {"data": ["\/htp-118", 1000, 0, 0.0, 154.80199999999977, 34, 611, 145.0, 227.89999999999998, 267.94999999999993, 409.97, 65.8457891617831, 1130.9528708764076, 25.206591163495094], "isController": false}, {"data": ["\/htp-119", 1000, 0, 0.0, 152.18899999999988, 4, 542, 145.0, 223.0, 268.8499999999998, 399.99, 65.8457891617831, 1130.9528708764076, 25.206591163495094], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 24000, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
