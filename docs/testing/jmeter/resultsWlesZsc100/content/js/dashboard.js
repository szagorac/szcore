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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9238, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.065, 500, 1500, ""], "isController": true}, {"data": [0.975, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.965, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.98, 500, 1500, "\/htp-132"], "isController": false}, {"data": [0.99, 500, 1500, "\/htp-133"], "isController": false}, {"data": [0.99, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.98, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.975, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.98, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.975, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.555, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.98, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.985, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.98, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.965, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.985, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.975, 500, 1500, "\/htp-122"], "isController": false}, {"data": [0.97, 500, 1500, "\/htp-123"], "isController": false}, {"data": [0.975, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.96, 500, 1500, "\/htp-125"], "isController": false}, {"data": [0.995, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.98, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.985, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.955, 500, 1500, "\/htp-118"], "isController": false}, {"data": [0.975, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2400, 0, 0.0, 103.97291666666662, 4, 2196, 34.0, 261.0, 565.6999999999989, 1182.3799999999865, 217.47009786154402, 8857.57053619971, 82.9848054095687], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 100, 0, 0.0, 2495.35, 923, 5998, 2342.0, 4018.9, 4300.599999999999, 5993.939999999998, 9.010632546404757, 8808.086901919263, 82.52120314471075], "isController": true}, {"data": ["\/htp-130", 100, 0, 0.0, 79.95000000000002, 4, 1535, 33.0, 147.40000000000026, 443.9499999999977, 1530.3399999999976, 10.650761529449357, 182.9351501757376, 4.077244647992332], "isController": false}, {"data": ["\/htp-131", 100, 0, 0.0, 92.05999999999999, 5, 967, 33.0, 190.20000000000016, 531.75, 966.9399999999999, 10.667804565820354, 183.22787764028163, 4.083768935353104], "isController": false}, {"data": ["\/htp-132", 100, 0, 0.0, 66.5, 4, 636, 34.0, 119.70000000000002, 380.1499999999998, 635.0799999999995, 10.683760683760683, 183.50193643162393, 4.089877136752137], "isController": false}, {"data": ["\/htp-133", 100, 0, 0.0, 57.78999999999999, 5, 541, 33.0, 87.40000000000009, 368.74999999999903, 540.6499999999999, 10.718113612004288, 184.09197481243302, 4.103027867095391], "isController": false}, {"data": ["\/htp-112", 100, 0, 0.0, 48.959999999999994, 8, 549, 22.0, 122.00000000000045, 231.5999999999999, 548.6099999999998, 9.601536245799329, 164.9138862217955, 3.5630700912145947], "isController": false}, {"data": ["\/htp-134", 100, 0, 0.0, 65.34999999999997, 5, 571, 32.5, 131.40000000000026, 424.84999999999997, 570.4699999999997, 10.736525660296328, 184.40821612626155, 4.110076229332188], "isController": false}, {"data": ["\/htp-113", 100, 0, 0.0, 91.41, 8, 1536, 29.0, 215.20000000000005, 397.3999999999994, 1528.319999999996, 9.612611746611554, 165.1041166009805, 3.6798279342497353], "isController": false}, {"data": ["\/htp-114", 100, 0, 0.0, 92.35000000000005, 7, 1329, 36.0, 224.70000000000002, 474.4999999999992, 1321.8499999999963, 9.629272989889264, 165.39028647087144, 3.686206066441984], "isController": false}, {"data": ["\/htp-115", 100, 0, 0.0, 80.62999999999998, 7, 959, 38.0, 203.80000000000024, 518.9499999999991, 955.2899999999981, 9.493070058857034, 163.05089472185304, 3.634065881906208], "isController": false}, {"data": ["\/test.html-103", 100, 0, 0.0, 806.77, 240, 2196, 661.0, 1459.5, 1590.0999999999995, 2195.5199999999995, 9.38174312787316, 5464.663811098601, 3.426535087719298], "isController": false}, {"data": ["\/htp-116", 100, 0, 0.0, 64.07999999999998, 7, 699, 32.0, 91.50000000000009, 429.0499999999968, 697.8299999999994, 9.502090459901178, 163.20582715697455, 3.6375190041809202], "isController": false}, {"data": ["\/htp-117", 100, 0, 0.0, 64.47, 7, 871, 33.0, 113.20000000000016, 246.5499999999999, 869.019999999999, 9.52653138992093, 163.62561922454034, 3.646875297704106], "isController": false}, {"data": ["\/htp-129", 100, 0, 0.0, 56.99000000000003, 5, 559, 33.0, 71.20000000000005, 220.0999999999991, 558.9499999999999, 10.602205258693807, 182.1011582909245, 4.0586567005937235], "isController": false}, {"data": ["\/htp-120", 100, 0, 0.0, 81.23999999999997, 7, 968, 33.5, 192.1000000000001, 527.6999999999999, 965.6899999999988, 9.70591089973794, 166.70660244588953, 3.71554401630593], "isController": false}, {"data": ["\/htp-121", 100, 0, 0.0, 60.69999999999999, 6, 544, 32.5, 75.0, 374.4499999999994, 543.8199999999999, 9.932459276916964, 170.59774781485896, 3.8022695669447755], "isController": false}, {"data": ["\/htp-122", 100, 0, 0.0, 75.32999999999996, 7, 1050, 34.5, 127.00000000000006, 502.8499999999968, 1046.6599999999983, 9.979044007584074, 171.39787695838737, 3.8201027841532778], "isController": false}, {"data": ["\/htp-123", 100, 0, 0.0, 80.60999999999999, 5, 984, 34.0, 188.70000000000041, 524.55, 979.949999999998, 10.027073097362878, 172.22281409806476, 3.838488920084227], "isController": false}, {"data": ["\/htp-124", 100, 0, 0.0, 74.83, 4, 729, 33.0, 168.00000000000006, 505.69999999999993, 727.8399999999995, 10.05631536604988, 172.7250729082864, 3.8496832260659692], "isController": false}, {"data": ["\/htp-125", 100, 0, 0.0, 94.36, 4, 681, 36.0, 371.40000000000026, 551.8999999999997, 680.1599999999996, 10.224948875255624, 175.62148517382414, 3.9142382413087935], "isController": false}, {"data": ["\/htp-126", 100, 0, 0.0, 52.98000000000001, 4, 534, 34.5, 89.20000000000005, 185.54999999999967, 532.8999999999994, 10.255358424776945, 176.14379294431342, 3.925879396984925], "isController": false}, {"data": ["\/htp-127", 100, 0, 0.0, 76.03999999999998, 4, 967, 36.0, 145.10000000000022, 366.7999999999993, 965.3899999999992, 10.273268954181221, 176.4514202794329, 3.9327357715224984], "isController": false}, {"data": ["\/htp-128", 100, 0, 0.0, 59.860000000000035, 4, 968, 35.0, 83.70000000000002, 148.84999999999997, 963.8699999999978, 10.536297545042672, 180.96914181856496, 4.033426403961648], "isController": false}, {"data": ["\/htp-118", 100, 0, 0.0, 100.63, 7, 1196, 36.0, 409.8000000000015, 568.3999999999999, 1192.9399999999985, 9.575792396820836, 164.4717155032079, 3.6657330269079766], "isController": false}, {"data": ["\/htp-119", 100, 0, 0.0, 71.45999999999998, 7, 1045, 32.0, 99.20000000000005, 497.3999999999969, 1042.6499999999987, 9.607993850883934, 165.02480063412762, 3.6780601460415068], "isController": false}]}, function(index, item){
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
