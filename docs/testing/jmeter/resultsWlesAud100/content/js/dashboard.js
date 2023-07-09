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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9598, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.275, 500, 1500, ""], "isController": true}, {"data": [0.995, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.99, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-132"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-133"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-134"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-113"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.785, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-116"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-129"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.99, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-122"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-123"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-125"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-126"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-128"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-118"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2400, 0, 0.0, 67.48541666666674, 5, 3254, 21.0, 145.9000000000001, 307.59999999999854, 717.8699999999972, 212.5963327132607, 8659.061780272832, 81.12501660908849], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 100, 0, 0.0, 1619.6499999999999, 564, 6188, 1338.5, 2495.0, 3326.199999999998, 6175.3699999999935, 8.807468733485997, 8609.489909943633, 80.66058767835125], "isController": true}, {"data": ["\/htp-130", 100, 0, 0.0, 50.37999999999999, 8, 675, 23.0, 74.90000000000006, 213.94999999999976, 673.049999999999, 9.364172675344134, 160.8369814589381, 3.584722352280176], "isController": false}, {"data": ["\/htp-131", 100, 0, 0.0, 45.14000000000001, 8, 505, 20.0, 58.900000000000006, 191.3499999999994, 504.96, 9.36592675845275, 160.867109206706, 3.585393837220193], "isController": false}, {"data": ["\/htp-132", 100, 0, 0.0, 39.39000000000002, 7, 637, 20.0, 49.70000000000002, 88.79999999999973, 635.109999999999, 9.379103357719002, 161.09342759332208, 3.5904380041268054], "isController": false}, {"data": ["\/htp-133", 100, 0, 0.0, 33.98, 5, 333, 20.0, 56.60000000000002, 99.1499999999998, 331.41999999999916, 9.392317084624777, 161.32038367615291, 3.595496383957922], "isController": false}, {"data": ["\/htp-112", 100, 0, 0.0, 25.359999999999992, 6, 453, 13.0, 51.50000000000003, 64.69999999999993, 450.2999999999986, 9.28332714444857, 159.4483963052358, 3.4449846825102117], "isController": false}, {"data": ["\/htp-134", 100, 0, 0.0, 49.71000000000001, 7, 640, 19.5, 120.6000000000002, 246.2999999999996, 637.4799999999987, 9.402030838661151, 161.48722499059798, 3.599214930424972], "isController": false}, {"data": ["\/htp-113", 100, 0, 0.0, 53.17000000000002, 6, 445, 18.0, 154.70000000000002, 227.44999999999965, 444.91999999999996, 9.2781592132121, 159.35963304880312, 3.5517953238077564], "isController": false}, {"data": ["\/htp-114", 100, 0, 0.0, 53.60000000000001, 9, 474, 19.0, 129.50000000000014, 334.5499999999981, 473.7999999999999, 9.273856997125105, 159.2857391264027, 3.550148381711954], "isController": false}, {"data": ["\/htp-115", 100, 0, 0.0, 53.66999999999999, 11, 555, 19.0, 125.70000000000013, 285.24999999999915, 554.2899999999996, 9.272137227630969, 159.25620074177098, 3.5494900324524803], "isController": false}, {"data": ["\/test.html-103", 100, 0, 0.0, 573.0800000000002, 121, 3254, 330.5, 1156.6000000000004, 2059.1499999999946, 3251.589999999999, 9.167583425009166, 5339.920385267694, 3.3483166024935826], "isController": false}, {"data": ["\/htp-116", 100, 0, 0.0, 50.73000000000001, 8, 501, 20.0, 153.0, 212.5499999999999, 499.5499999999993, 9.27299703264095, 159.27096856454006, 3.5498191765578633], "isController": false}, {"data": ["\/htp-117", 100, 0, 0.0, 38.350000000000016, 7, 452, 20.0, 53.0, 82.74999999999994, 451.2099999999996, 9.2824654228163, 159.43359556298154, 3.553443794671865], "isController": false}, {"data": ["\/htp-129", 100, 0, 0.0, 50.17, 6, 517, 21.0, 88.90000000000006, 219.39999999999986, 516.5099999999998, 9.360666479453336, 160.77675980529813, 3.5833801366657307], "isController": false}, {"data": ["\/htp-120", 100, 0, 0.0, 53.60999999999999, 8, 446, 23.5, 144.50000000000003, 181.0, 445.37999999999965, 9.16254352208173, 157.37384322888033, 3.507536192046912], "isController": false}, {"data": ["\/htp-121", 100, 0, 0.0, 57.02, 9, 705, 21.5, 128.4000000000002, 304.99999999999886, 702.9599999999989, 9.160025648071816, 157.33059677567098, 3.5065723184024917], "isController": false}, {"data": ["\/htp-122", 100, 0, 0.0, 38.16000000000001, 8, 683, 22.0, 57.50000000000003, 101.59999999999968, 678.7099999999978, 9.266123054114159, 159.1529026130467, 3.5471877316530764], "isController": false}, {"data": ["\/htp-123", 100, 0, 0.0, 35.37000000000001, 8, 390, 21.0, 56.60000000000002, 114.0999999999998, 388.099999999999, 9.286775631500744, 159.50762676448736, 3.555093796433878], "isController": false}, {"data": ["\/htp-124", 100, 0, 0.0, 41.18000000000001, 9, 374, 21.0, 94.50000000000003, 147.5999999999999, 372.2199999999991, 9.306654257794323, 159.8490577012564, 3.5627035830618894], "isController": false}, {"data": ["\/htp-125", 100, 0, 0.0, 61.39999999999999, 9, 718, 25.0, 113.50000000000003, 355.49999999999966, 715.4199999999987, 9.314456035767511, 159.98305933308492, 3.56569020119225], "isController": false}, {"data": ["\/htp-126", 100, 0, 0.0, 42.33, 7, 372, 30.0, 67.9, 103.74999999999994, 371.25999999999965, 9.334453467749464, 160.32653085036873, 3.5733454681228416], "isController": false}, {"data": ["\/htp-127", 100, 0, 0.0, 44.040000000000006, 7, 459, 22.0, 60.60000000000002, 120.64999999999947, 458.7399999999999, 9.345794392523365, 160.52132009345794, 3.577686915887851], "isController": false}, {"data": ["\/htp-128", 100, 0, 0.0, 51.709999999999994, 9, 622, 20.0, 102.90000000000023, 236.2499999999996, 620.5899999999992, 9.3562874251497, 160.701546126497, 3.5817037799401197], "isController": false}, {"data": ["\/htp-118", 100, 0, 0.0, 33.38000000000002, 7, 207, 21.0, 68.00000000000006, 143.84999999999928, 207.0, 9.2824654228163, 159.43359556298154, 3.553443794671865], "isController": false}, {"data": ["\/htp-119", 100, 0, 0.0, 44.720000000000006, 6, 467, 20.0, 82.50000000000003, 193.79999999999995, 466.7899999999999, 9.277298450691159, 159.34484878003525, 3.5514658131552093], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 2400, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
