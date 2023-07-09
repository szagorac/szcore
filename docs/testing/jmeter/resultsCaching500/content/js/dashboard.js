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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.95798, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.077, 500, 1500, ""], "isController": true}, {"data": [0.9995, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-131"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-132"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-133"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-113"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.882, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-129"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-120"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.998, 500, 1500, "\/htp-122"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-123"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-125"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-127"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-128"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-118"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 24000, 0, 0.0, 119.82754166666679, 2, 2364, 118.0, 182.0, 225.0, 436.9900000000016, 1651.1867905056758, 68385.64510233919, 630.079334365325], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 1000, 0, 0.0, 2875.860999999996, 413, 5239, 3206.0, 4248.7, 4433.65, 4702.6900000000005, 68.40413160954922, 67992.7048062453, 626.4589318694849], "isController": true}, {"data": ["\/htp-130", 1000, 0, 0.0, 105.07500000000006, 3, 506, 102.0, 162.89999999999998, 199.94999999999993, 326.98, 71.92174913693901, 1286.795279236191, 27.532544591484466], "isController": false}, {"data": ["\/htp-131", 1000, 0, 0.0, 106.71700000000001, 2, 696, 103.0, 171.89999999999998, 196.89999999999986, 329.8900000000001, 71.96833393306945, 1287.628755847427, 27.55037783375315], "isController": false}, {"data": ["\/htp-132", 1000, 0, 0.0, 106.38, 2, 430, 103.0, 169.0, 201.94999999999993, 329.98, 72.02016564638099, 1288.5561082102988, 27.57021966150522], "isController": false}, {"data": ["\/htp-133", 1000, 0, 0.0, 104.03300000000009, 3, 432, 102.0, 164.0, 193.89999999999986, 329.9100000000001, 72.05649228995533, 1289.206050043234, 27.584125954748522], "isController": false}, {"data": ["\/htp-112", 1000, 0, 0.0, 111.02300000000001, 6, 483, 109.0, 166.0, 195.94999999999993, 396.8600000000001, 70.50694493407602, 1261.4821661496158, 26.164686596629767], "isController": false}, {"data": ["\/htp-134", 1000, 0, 0.0, 103.03800000000001, 2, 538, 100.0, 162.89999999999998, 195.89999999999986, 340.93000000000006, 72.35366471311771, 1294.522940633818, 27.69788727299038], "isController": false}, {"data": ["\/htp-113", 1000, 0, 0.0, 114.54399999999994, 7, 508, 111.0, 173.89999999999998, 210.89999999999986, 379.97, 70.57661091114404, 1262.7286020537792, 27.01760886442233], "isController": false}, {"data": ["\/htp-114", 1000, 0, 0.0, 113.35199999999986, 4, 441, 111.0, 175.0, 218.64999999999952, 383.96000000000004, 70.70635650144948, 1265.0499584600154, 27.06727709821113], "isController": false}, {"data": ["\/htp-115", 1000, 0, 0.0, 114.06500000000005, 9, 573, 108.0, 173.0, 217.94999999999993, 391.98, 70.73136228603764, 1265.4973519946245, 27.07684962512378], "isController": false}, {"data": ["\/test.html-103", 1000, 0, 0.0, 357.14800000000037, 25, 2364, 152.0, 1249.1, 2002.3999999999992, 2206.99, 69.93496048674733, 40735.61197461361, 25.542651584026856], "isController": false}, {"data": ["\/htp-116", 1000, 0, 0.0, 112.693, 4, 520, 109.0, 165.89999999999998, 199.0, 392.98, 70.71135624381274, 1265.1394118582946, 27.06919106208457], "isController": false}, {"data": ["\/htp-117", 1000, 0, 0.0, 111.33899999999997, 3, 901, 109.0, 164.0, 198.0, 388.97, 70.83156254426973, 1267.2900950913727, 27.115207536478255], "isController": false}, {"data": ["\/htp-129", 1000, 0, 0.0, 107.12899999999998, 3, 905, 103.0, 165.0, 195.94999999999993, 368.7000000000003, 71.85456635769204, 1285.5932717180426, 27.50682618380398], "isController": false}, {"data": ["\/htp-120", 1000, 0, 0.0, 109.78800000000008, 3, 436, 109.0, 169.0, 197.0, 337.96000000000004, 71.23521869212139, 1274.512150056988, 27.269732155577717], "isController": false}, {"data": ["\/htp-121", 1000, 0, 0.0, 108.61600000000003, 4, 474, 108.0, 166.0, 195.0, 313.7900000000002, 71.33176403452457, 1276.239500855981, 27.306690919466437], "isController": false}, {"data": ["\/htp-122", 1000, 0, 0.0, 112.09899999999995, 5, 659, 107.0, 173.89999999999998, 214.8499999999998, 385.9200000000001, 71.52052639107424, 1279.6167617293663, 27.37895150908311], "isController": false}, {"data": ["\/htp-123", 1000, 0, 0.0, 107.99299999999991, 4, 422, 107.5, 166.0, 190.0, 331.97, 71.55635062611806, 1280.257714669052, 27.392665474060824], "isController": false}, {"data": ["\/htp-124", 1000, 0, 0.0, 111.24099999999999, 5, 449, 109.0, 172.89999999999998, 203.89999999999986, 388.98, 71.64350193437456, 1281.8169911520274, 27.426028084252756], "isController": false}, {"data": ["\/htp-125", 1000, 0, 0.0, 107.34099999999995, 3, 574, 105.5, 162.0, 192.94999999999993, 379.63000000000034, 71.68458781362007, 1282.5520833333335, 27.441756272401435], "isController": false}, {"data": ["\/htp-126", 1000, 0, 0.0, 106.65300000000006, 3, 431, 104.0, 166.0, 194.94999999999993, 363.94000000000005, 71.7154331612163, 1283.1039560025818, 27.45356425702811], "isController": false}, {"data": ["\/htp-127", 1000, 0, 0.0, 109.74299999999995, 3, 900, 106.0, 172.89999999999998, 197.0, 383.98, 71.76690110521028, 1284.0247999497633, 27.47326682933831], "isController": false}, {"data": ["\/htp-128", 1000, 0, 0.0, 111.2810000000002, 3, 442, 107.0, 180.89999999999998, 217.94999999999993, 389.99, 71.80812868016659, 1284.7624272942696, 27.489049260376273], "isController": false}, {"data": ["\/htp-118", 1000, 0, 0.0, 111.05800000000004, 9, 488, 108.0, 170.0, 195.0, 338.8600000000001, 70.98743522396536, 1270.0789069709663, 27.17487754667424], "isController": false}, {"data": ["\/htp-119", 1000, 0, 0.0, 113.51199999999997, 2, 500, 107.0, 175.0, 221.0, 412.0, 71.07320540156361, 1271.613472814499, 27.20771144278607], "isController": false}]}, function(index, item){
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
