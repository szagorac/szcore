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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9493333333333334, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.009333333333333334, 500, 1500, ""], "isController": true}, {"data": [1.0, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.9973333333333333, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.9953333333333333, 500, 1500, "\/htp-132"], "isController": false}, {"data": [0.9993333333333333, 500, 1500, "\/htp-133"], "isController": false}, {"data": [0.9953333333333333, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.9993333333333333, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.9993333333333333, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.9926666666666667, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.796, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.9973333333333333, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.9966666666666667, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.994, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.9953333333333333, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.994, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.996, 500, 1500, "\/htp-122"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-123"], "isController": false}, {"data": [0.9973333333333333, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-125"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.9953333333333333, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.994, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.9973333333333333, 500, 1500, "\/htp-118"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 18000, 0, 0.0, 151.1474444444447, 2, 1803, 135.0, 206.0, 301.9500000000007, 1006.850000000024, 1472.3926380368098, 59970.64321319019, 561.8529524539878], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 750, 0, 0.0, 3627.5399999999977, 1107, 5314, 3765.5, 4547.7, 4647.799999999999, 4869.92, 60.92112744699862, 59551.71093178864, 557.928059763626], "isController": true}, {"data": ["\/htp-130", 750, 0, 0.0, 127.95333333333326, 2, 493, 131.0, 194.0, 224.3499999999998, 423.96000000000004, 65.83567415730337, 1130.7791377721207, 25.202719013342698], "isController": false}, {"data": ["\/htp-131", 750, 0, 0.0, 131.12933333333342, 2, 564, 132.0, 198.89999999999998, 265.7999999999997, 480.98, 66.09676566493346, 1135.263588393408, 25.302668106107344], "isController": false}, {"data": ["\/htp-132", 750, 0, 0.0, 127.07999999999996, 2, 573, 129.0, 197.89999999999998, 251.3499999999998, 502.94000000000005, 66.62521097983478, 1144.3400495247402, 25.504963578218], "isController": false}, {"data": ["\/htp-133", 750, 0, 0.0, 120.4546666666666, 2, 541, 126.0, 189.89999999999998, 216.89999999999986, 414.4100000000001, 66.9882100750268, 1150.5748425777062, 25.643924169346196], "isController": false}, {"data": ["\/htp-112", 750, 0, 0.0, 141.8839999999999, 4, 575, 135.5, 204.0, 286.4999999999993, 499.43000000000006, 66.35993629446116, 1139.7837495576005, 24.625757609272696], "isController": false}, {"data": ["\/htp-134", 750, 0, 0.0, 122.09600000000009, 2, 519, 127.5, 192.0, 207.0, 434.7800000000002, 67.23442402510085, 1154.8037595248768, 25.73817794710892], "isController": false}, {"data": ["\/htp-113", 750, 0, 0.0, 136.60799999999995, 5, 574, 134.0, 198.0, 233.8499999999991, 387.33000000000015, 66.17257808364215, 1136.5657259131817, 25.33169004764426], "isController": false}, {"data": ["\/htp-114", 750, 0, 0.0, 137.6480000000001, 3, 534, 133.0, 197.0, 247.89999999999986, 406.96000000000004, 65.88772731265922, 1131.6731913818853, 25.22264561187736], "isController": false}, {"data": ["\/htp-115", 750, 0, 0.0, 144.2773333333334, 7, 653, 137.0, 201.0, 257.89999999999986, 535.45, 66.02112676056338, 1133.9644311179577, 25.27371258802817], "isController": false}, {"data": ["\/test.html-103", 750, 0, 0.0, 528.3346666666665, 24, 1803, 306.5, 1301.8, 1390.0, 1570.49, 65.92827004219409, 38401.800871901374, 24.079270503691983], "isController": false}, {"data": ["\/htp-116", 750, 0, 0.0, 141.1613333333333, 4, 576, 138.0, 198.0, 249.3499999999998, 424.96000000000004, 65.53652569031806, 1125.6410291419083, 25.088201240824883], "isController": false}, {"data": ["\/htp-117", 750, 0, 0.0, 139.34666666666672, 4, 561, 136.0, 194.0, 232.89999999999986, 460.74000000000024, 65.302568567697, 1121.6226327818895, 24.998639529821506], "isController": false}, {"data": ["\/htp-129", 750, 0, 0.0, 132.9213333333334, 3, 636, 129.5, 195.89999999999998, 259.24999999999966, 545.0, 65.67425569176882, 1128.0066495183887, 25.140926007005255], "isController": false}, {"data": ["\/htp-120", 750, 0, 0.0, 140.53599999999997, 6, 575, 136.0, 196.0, 235.44999999999993, 489.74000000000024, 65.00260010400416, 1116.4704400676026, 24.883807852314092], "isController": false}, {"data": ["\/htp-121", 750, 0, 0.0, 144.8706666666669, 4, 600, 138.0, 201.89999999999998, 270.7999999999997, 541.49, 65.10981856063894, 1118.3120008247245, 24.924852417744596], "isController": false}, {"data": ["\/htp-122", 750, 0, 0.0, 137.46666666666667, 3, 557, 135.0, 197.0, 234.44999999999993, 466.2500000000002, 64.58835687220117, 1109.3554889338616, 24.72523036513951], "isController": false}, {"data": ["\/htp-123", 750, 0, 0.0, 133.35466666666645, 4, 575, 134.5, 191.79999999999995, 217.6999999999996, 446.6400000000003, 64.92944333823911, 1115.2139154618649, 24.85580252791966], "isController": false}, {"data": ["\/htp-124", 750, 0, 0.0, 135.7720000000002, 5, 564, 136.0, 194.0, 227.44999999999993, 462.9200000000001, 64.71654154801968, 1111.5571608853222, 24.77430106135128], "isController": false}, {"data": ["\/htp-125", 750, 0, 0.0, 134.61599999999993, 4, 570, 134.5, 197.0, 245.44999999999993, 445.8800000000001, 64.90134994807892, 1114.7313895379025, 24.845048026998963], "isController": false}, {"data": ["\/htp-126", 750, 0, 0.0, 130.5746666666668, 3, 768, 132.0, 190.89999999999998, 209.44999999999993, 395.72000000000025, 65.04770164787512, 1117.2450943191675, 24.901073287077192], "isController": false}, {"data": ["\/htp-127", 750, 0, 0.0, 135.0773333333333, 2, 635, 130.0, 200.89999999999998, 258.0, 499.96000000000004, 65.30825496342737, 1121.7203010710555, 25.000816353187044], "isController": false}, {"data": ["\/htp-128", 750, 0, 0.0, 132.07599999999994, 3, 556, 132.0, 192.0, 249.6999999999996, 515.47, 65.38796861377506, 1123.089445292066, 25.031331734960766], "isController": false}, {"data": ["\/htp-118", 750, 0, 0.0, 136.38933333333347, 8, 533, 133.0, 194.0, 219.24999999999966, 443.94000000000005, 65.10981856063894, 1118.3120008247245, 24.924852417744596], "isController": false}, {"data": ["\/htp-119", 750, 0, 0.0, 135.91066666666654, 4, 499, 136.0, 194.89999999999998, 219.44999999999993, 389.1700000000003, 64.93506493506493, 1115.3104707792206, 24.857954545454543], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 18000, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
