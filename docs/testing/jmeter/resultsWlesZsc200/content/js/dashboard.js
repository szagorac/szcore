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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.7952, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0025, 500, 1500, ""], "isController": true}, {"data": [0.8775, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.8825, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.9, 500, 1500, "\/htp-132"], "isController": false}, {"data": [0.905, 500, 1500, "\/htp-133"], "isController": false}, {"data": [0.8325, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.925, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.8325, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.8325, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.8525, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.095, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.8475, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.8375, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.875, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.8375, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.8475, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.8625, 500, 1500, "\/htp-122"], "isController": false}, {"data": [0.8325, 500, 1500, "\/htp-123"], "isController": false}, {"data": [0.865, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.86, 500, 1500, "\/htp-125"], "isController": false}, {"data": [0.8575, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.8675, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.8925, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.855, 500, 1500, "\/htp-118"], "isController": false}, {"data": [0.805, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 4800, 0, 0.0, 540.7308333333343, 4, 14238, 104.0, 1096.0, 1886.699999999999, 9433.57999999999, 218.14215597164153, 8884.943504590074, 83.24125727140519], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 200, 0, 0.0, 12977.539999999999, 1477, 19086, 13466.5, 16732.4, 17797.7, 19049.8, 9.062485839865875, 8858.774610313108, 82.99608613892791], "isController": true}, {"data": ["\/htp-130", 200, 0, 0.0, 272.28999999999996, 5, 3056, 94.5, 696.0000000000002, 1094.949999999999, 2174.5400000000022, 9.740417863926362, 167.29928651439147, 3.7287537135343105], "isController": false}, {"data": ["\/htp-131", 200, 0, 0.0, 265.18500000000006, 6, 3016, 84.0, 678.9000000000005, 1249.3999999999987, 2524.310000000003, 9.759430049285122, 167.62583565119797, 3.7360318157419607], "isController": false}, {"data": ["\/htp-132", 200, 0, 0.0, 243.71500000000015, 5, 3769, 86.5, 716.8000000000006, 1046.6499999999994, 2057.2100000000046, 9.785214540828807, 168.0687044375948, 3.745902441411028], "isController": false}, {"data": ["\/htp-133", 200, 0, 0.0, 247.41500000000042, 5, 2898, 89.5, 611.5, 1087.75, 2843.650000000004, 9.808248737187975, 168.46433475552942, 3.754720219704772], "isController": false}, {"data": ["\/htp-112", 200, 0, 0.0, 346.255, 9, 2225, 103.0, 1049.7, 1411.4499999999987, 2184.2200000000025, 9.976555095525514, 171.3551279493191, 3.7022372424801713], "isController": false}, {"data": ["\/htp-134", 200, 0, 0.0, 186.91000000000003, 4, 3111, 81.0, 557.8, 582.8, 1956.430000000005, 9.909820632246557, 170.20891140620355, 3.793603210781885], "isController": false}, {"data": ["\/htp-113", 200, 0, 0.0, 382.01499999999993, 15, 4290, 119.5, 965.9, 1479.3999999999999, 3460.3800000000015, 9.979541939024998, 171.40642931989422, 3.8202933985330074], "isController": false}, {"data": ["\/htp-114", 200, 0, 0.0, 378.8949999999999, 18, 2912, 116.5, 1098.5, 1614.9499999999991, 2704.520000000005, 9.964129135113591, 171.14170237146274, 3.8143931845356716], "isController": false}, {"data": ["\/htp-115", 200, 0, 0.0, 335.2399999999999, 19, 3958, 105.0, 984.0000000000003, 1353.1999999999998, 2619.3500000000004, 9.818360333824252, 168.63800932744232, 3.758591065292096], "isController": false}, {"data": ["\/test.html-103", 200, 0, 0.0, 5696.864999999999, 267, 14238, 4675.0, 11099.4, 12382.75, 13314.940000000004, 9.860474288813291, 5743.514427106444, 3.6013841640782918], "isController": false}, {"data": ["\/htp-116", 200, 0, 0.0, 327.52, 16, 2820, 105.5, 1001.5000000000001, 1275.5499999999997, 2384.9100000000053, 9.748488984207448, 167.43791431078185, 3.731843439266914], "isController": false}, {"data": ["\/htp-117", 200, 0, 0.0, 354.98000000000013, 15, 4894, 100.0, 1017.4000000000003, 1227.0499999999995, 2774.1200000000035, 9.739469198928658, 167.28299245191138, 3.728390552714877], "isController": false}, {"data": ["\/htp-129", 200, 0, 0.0, 299.41500000000013, 6, 3170, 100.5, 977.7000000000002, 1173.3999999999994, 2329.6700000000046, 9.766101860442404, 167.74042922017676, 3.738585868450608], "isController": false}, {"data": ["\/htp-120", 200, 0, 0.0, 352.19000000000005, 15, 3592, 106.5, 959.0000000000002, 1212.7999999999993, 2518.440000000004, 9.578544061302681, 164.51897749042146, 3.666786398467433], "isController": false}, {"data": ["\/htp-121", 200, 0, 0.0, 333.7649999999999, 19, 4177, 103.5, 963.8000000000002, 1202.85, 3108.2800000000025, 9.572125969177755, 164.40874174404138, 3.6643294725758593], "isController": false}, {"data": ["\/htp-122", 200, 0, 0.0, 308.24000000000007, 13, 3629, 114.0, 715.8000000000001, 1101.8, 3391.0600000000136, 9.479122233281197, 162.81132992084932, 3.6287264799279586], "isController": false}, {"data": ["\/htp-123", 200, 0, 0.0, 401.4199999999999, 9, 5894, 109.5, 1051.6, 1667.699999999998, 5000.550000000019, 9.4948727687049, 163.0818576718572, 3.6347559817698443], "isController": false}, {"data": ["\/htp-124", 200, 0, 0.0, 316.66499999999985, 8, 4482, 95.5, 793.6000000000007, 1366.6499999999974, 3611.4600000000078, 9.52154248988336, 163.53993096881695, 3.6449654844084742], "isController": false}, {"data": ["\/htp-125", 200, 0, 0.0, 288.80499999999984, 7, 2158, 98.0, 793.4000000000001, 1119.9499999999998, 1822.700000000004, 9.5333428666762, 163.74261165927834, 3.649482816149483], "isController": false}, {"data": ["\/htp-126", 200, 0, 0.0, 319.0049999999998, 6, 5891, 102.0, 878.2000000000003, 1123.2499999999998, 2247.850000000006, 9.578085340740385, 164.51109860638857, 3.666610794502179], "isController": false}, {"data": ["\/htp-127", 200, 0, 0.0, 290.3049999999999, 8, 2341, 92.0, 711.1000000000001, 1309.3999999999983, 2159.2500000000027, 9.757049468240805, 167.58494731193286, 3.7351204995609324], "isController": false}, {"data": ["\/htp-128", 200, 0, 0.0, 259.69500000000016, 9, 3941, 91.0, 591.5, 1098.2999999999997, 3381.0900000000156, 9.760382606998194, 167.6421965741057, 3.736396466741496], "isController": false}, {"data": ["\/htp-118", 200, 0, 0.0, 331.84999999999997, 22, 3192, 122.5, 938.1000000000016, 1244.6, 2116.890000000002, 9.633911368015413, 165.469954238921, 3.6879816955684004], "isController": false}, {"data": ["\/htp-119", 200, 0, 0.0, 438.8999999999999, 14, 4717, 110.0, 1178.1000000000004, 1751.5, 4664.630000000008, 9.579920486659962, 164.54261867126505, 3.6673133112995164], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 4800, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
