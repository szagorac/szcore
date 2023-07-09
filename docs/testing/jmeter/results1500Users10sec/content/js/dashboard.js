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

    var data = {"OkPercent": 99.85, "KoPercent": 0.15};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9169066666666666, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, ""], "isController": true}, {"data": [0.9956666666666667, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.994, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.989, 500, 1500, "\/htp-132"], "isController": false}, {"data": [0.9896666666666667, 500, 1500, "\/htp-133"], "isController": false}, {"data": [0.948, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.9906666666666667, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.9833333333333333, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.9833333333333333, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.981, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.139, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.9853333333333333, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.9916666666666667, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.9976666666666667, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.9916666666666667, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.9973333333333333, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.9963333333333333, 500, 1500, "\/htp-122"], "isController": false}, {"data": [0.9976666666666667, 500, 1500, "\/htp-123"], "isController": false}, {"data": [0.9983333333333333, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.9986666666666667, 500, 1500, "\/htp-125"], "isController": false}, {"data": [0.9996666666666667, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.9996666666666667, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.9996666666666667, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.988, 500, 1500, "\/htp-118"], "isController": false}, {"data": [0.9873333333333333, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 36000, 54, 0.15, 470.26349999999917, 1, 18070, 131.0, 208.0, 307.0, 13165.850000000024, 1416.1520003147004, 56692.23084045917, 539.6136099337162], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 1500, 43, 2.8666666666666667, 11286.323999999993, 3705, 20714, 11122.5, 17173.400000000005, 18638.350000000002, 19668.93, 58.802775491003175, 56496.656089823686, 537.7520709602493], "isController": true}, {"data": ["\/htp-130", 1500, 0, 0.0, 151.2593333333333, 3, 719, 143.0, 223.0, 258.0, 470.99, 67.18022214260122, 1153.8727998477248, 25.71742878896453], "isController": false}, {"data": ["\/htp-131", 1500, 0, 0.0, 156.76266666666677, 3, 721, 150.0, 227.0, 263.0, 562.9300000000001, 67.88866259334692, 1166.0408180583843, 25.988628649015617], "isController": false}, {"data": ["\/htp-132", 1500, 0, 0.0, 166.42333333333346, 2, 803, 155.0, 251.0, 306.95000000000005, 636.97, 68.18491749624982, 1171.129227464885, 26.10203872903314], "isController": false}, {"data": ["\/htp-133", 1500, 0, 0.0, 170.18000000000012, 2, 848, 158.0, 258.0, 328.95000000000005, 608.96, 68.49627836887528, 1176.4770937029089, 26.22123156308507], "isController": false}, {"data": ["\/htp-112", 1500, 11, 0.7333333333333333, 430.8859999999998, 3, 17031, 186.5, 361.7000000000003, 583.4500000000005, 8433.160000000003, 62.323416985208574, 1063.6757458684767, 22.95822643136114], "isController": false}, {"data": ["\/htp-134", 1500, 0, 0.0, 173.63533333333325, 2, 847, 162.0, 268.0, 342.9000000000001, 607.99, 68.98137502874224, 1184.809007817889, 26.406932628190386], "isController": false}, {"data": ["\/htp-113", 1500, 0, 0.0, 275.61466666666627, 14, 16491, 182.0, 307.0, 355.95000000000005, 1316.99, 62.160706145621816, 1067.658691102731, 23.79589532137085], "isController": false}, {"data": ["\/htp-114", 1500, 0, 0.0, 213.1639999999998, 4, 1697, 171.0, 323.0, 374.0, 1309.99, 61.99628022318661, 1064.8345474271543, 23.732951022938625], "isController": false}, {"data": ["\/htp-115", 1500, 0, 0.0, 213.5820000000001, 4, 1514, 165.0, 331.8000000000002, 397.0, 1307.99, 61.84036939313984, 1062.1566571157653, 23.673266408311346], "isController": false}, {"data": ["\/test.html-103", 1500, 43, 2.8666666666666667, 7226.266000000005, 1, 18070, 7418.0, 13529.800000000001, 16492.9, 17007.92, 62.11951795254069, 35150.199656013174, 22.037788726342818], "isController": false}, {"data": ["\/htp-116", 1500, 0, 0.0, 197.58066666666676, 3, 1711, 155.0, 302.0, 378.7000000000003, 1233.98, 61.99371796991238, 1064.790538725409, 23.731970160357083], "isController": false}, {"data": ["\/htp-117", 1500, 0, 0.0, 170.90133333333335, 4, 1534, 142.0, 266.9000000000001, 328.0, 573.95, 61.968107080889034, 1064.3506516979262, 23.722165991902834], "isController": false}, {"data": ["\/htp-129", 1500, 0, 0.0, 144.4880000000001, 3, 1234, 137.0, 217.0, 248.9000000000001, 367.97, 66.80919294494922, 1147.500083511491, 25.575394174238372], "isController": false}, {"data": ["\/htp-120", 1500, 0, 0.0, 153.70066666666654, 3, 1704, 127.0, 228.0, 282.0, 1219.98, 62.78777731268313, 1078.4291282963584, 24.035946002511512], "isController": false}, {"data": ["\/htp-121", 1500, 0, 0.0, 141.65733333333304, 3, 1315, 124.0, 219.9000000000001, 274.9000000000001, 407.9000000000001, 62.977579981526574, 1081.6891374170796, 24.10860483667814], "isController": false}, {"data": ["\/htp-122", 1500, 0, 0.0, 141.12466666666674, 3, 1322, 122.0, 211.9000000000001, 264.85000000000014, 453.3400000000006, 63.53778380210098, 1091.3110756946799, 24.32305786174178], "isController": false}, {"data": ["\/htp-123", 1500, 0, 0.0, 134.42000000000013, 3, 1518, 120.0, 201.0, 248.0, 339.95000000000005, 64.98288783953558, 1116.1318665251486, 24.87626175107222], "isController": false}, {"data": ["\/htp-124", 1500, 0, 0.0, 134.91666666666703, 4, 1309, 121.0, 201.0, 242.95000000000005, 369.97, 65.29688316211039, 1121.5249814992164, 24.996463085495385], "isController": false}, {"data": ["\/htp-125", 1500, 0, 0.0, 134.79333333333352, 4, 1307, 123.0, 197.0, 230.0, 396.82000000000016, 65.69151265656477, 1128.303051370763, 25.1475321888412], "isController": false}, {"data": ["\/htp-126", 1500, 0, 0.0, 134.91333333333318, 4, 1307, 124.0, 202.9000000000001, 236.95000000000005, 354.0, 66.01531555320834, 1133.8646190916293, 25.271487985212566], "isController": false}, {"data": ["\/htp-127", 1500, 0, 0.0, 135.33599999999979, 4, 554, 126.0, 202.9000000000001, 229.95000000000005, 309.0, 66.15506747816882, 1136.2649675840169, 25.324986768986506], "isController": false}, {"data": ["\/htp-128", 1500, 0, 0.0, 139.91333333333307, 5, 1221, 132.0, 213.0, 233.0, 322.98, 66.48346777767928, 1141.905499290843, 25.45070250864285], "isController": false}, {"data": ["\/htp-118", 1500, 0, 0.0, 172.5106666666665, 3, 1697, 138.0, 249.0, 325.95000000000005, 1232.99, 62.12466349140609, 1067.0396303582522, 23.782097742803895], "isController": false}, {"data": ["\/htp-119", 1500, 0, 0.0, 172.29466666666656, 4, 1710, 133.0, 251.80000000000018, 324.95000000000005, 1308.0, 62.54691018263697, 1074.2920471603702, 23.94373905429072], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 21, 38.888888888888886, 0.058333333333333334], "isController": false}, {"data": ["Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 33, 61.111111111111114, 0.09166666666666666], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 36000, 54, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 33, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 21, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["\/htp-112", 1500, 11, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 6, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 5, null, null, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["\/test.html-103", 1500, 43, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 27, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 16, null, null, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
