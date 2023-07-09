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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [1.0, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, ""], "isController": true}, {"data": [1.0, 500, 1500, "\/htp-130"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-131"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-132"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-133"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-112"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-134"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-113"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-114"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-115"], "isController": false}, {"data": [1.0, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-116"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-117"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-129"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-120"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-121"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-122"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-123"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-124"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-125"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-126"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-127"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-128"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-118"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2400, 0, 0.0, 3.4670833333333353, 1, 62, 3.0, 4.0, 5.0, 21.0, 242.00867197741252, 9857.028177624281, 92.3485239991933], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 100, 0, 0.0, 83.20999999999998, 62, 144, 81.0, 98.0, 103.89999999999998, 143.7999999999999, 10.01903616872057, 9793.823107654543, 91.75636834986474], "isController": true}, {"data": ["\/htp-130", 100, 0, 0.0, 2.5100000000000002, 1, 4, 2.0, 3.0, 3.9499999999999886, 4.0, 10.241704219582138, 175.9092713027448, 3.9206523965587876], "isController": false}, {"data": ["\/htp-131", 100, 0, 0.0, 2.4499999999999993, 2, 4, 2.0, 3.0, 3.0, 4.0, 10.241704219582138, 175.9092713027448, 3.9206523965587876], "isController": false}, {"data": ["\/htp-132", 100, 0, 0.0, 2.459999999999998, 2, 3, 2.0, 3.0, 3.0, 3.0, 10.242753252074158, 175.92728925535184, 3.9210539793096384], "isController": false}, {"data": ["\/htp-133", 100, 0, 0.0, 2.4800000000000004, 2, 6, 2.0, 3.0, 3.9499999999999886, 5.989999999999995, 10.24380249948781, 175.94531089940585, 3.921455644335177], "isController": false}, {"data": ["\/htp-112", 100, 0, 0.0, 3.46, 2, 6, 3.0, 4.0, 4.949999999999989, 5.989999999999995, 10.218679746576743, 175.51380799100758, 3.7920881872062133], "isController": false}, {"data": ["\/htp-134", 100, 0, 0.0, 2.4000000000000004, 2, 5, 2.0, 3.0, 3.0, 5.0, 10.24485196188915, 175.9633362360414, 3.9218573916606907], "isController": false}, {"data": ["\/htp-113", 100, 0, 0.0, 3.3600000000000003, 2, 5, 3.0, 4.0, 4.0, 5.0, 10.220768601798856, 175.54968571136547, 3.912637980376124], "isController": false}, {"data": ["\/htp-114", 100, 0, 0.0, 3.3200000000000007, 2, 5, 3.0, 4.0, 4.0, 4.989999999999995, 10.221813349688235, 175.56763007257487, 3.9130379229275274], "isController": false}, {"data": ["\/htp-115", 100, 0, 0.0, 3.2200000000000006, 2, 9, 3.0, 4.0, 4.0, 8.989999999999995, 10.22390348635109, 175.60352980267865, 3.9138380533687758], "isController": false}, {"data": ["\/test.html-103", 100, 0, 0.0, 19.519999999999996, 14, 62, 19.0, 23.0, 24.0, 61.72999999999986, 10.156408693885842, 5915.88986009547, 3.709469581555962], "isController": false}, {"data": ["\/htp-116", 100, 0, 0.0, 2.970000000000001, 2, 4, 3.0, 4.0, 4.0, 4.0, 10.224948875255624, 175.62148517382414, 3.9142382413087935], "isController": false}, {"data": ["\/htp-117", 100, 0, 0.0, 2.95, 2, 4, 3.0, 4.0, 4.0, 4.0, 10.225994477962981, 175.63944421720012, 3.914638511095204], "isController": false}, {"data": ["\/htp-129", 100, 0, 0.0, 2.45, 1, 7, 2.0, 3.0, 3.0, 6.969999999999985, 10.240655401945725, 175.89125704045057, 3.9202508960573472], "isController": false}, {"data": ["\/htp-120", 100, 0, 0.0, 2.7600000000000007, 2, 5, 3.0, 4.0, 4.0, 4.989999999999995, 10.227040294538762, 175.65740693393332, 3.915038862753119], "isController": false}, {"data": ["\/htp-121", 100, 0, 0.0, 2.910000000000001, 2, 27, 3.0, 3.0, 4.0, 26.769999999999882, 10.228086325048583, 175.67537332515087, 3.915439296307661], "isController": false}, {"data": ["\/htp-122", 100, 0, 0.0, 2.7099999999999995, 2, 8, 3.0, 3.0, 4.0, 7.9599999999999795, 10.230179028132993, 175.71131713554988, 3.9162404092071608], "isController": false}, {"data": ["\/htp-123", 100, 0, 0.0, 2.6899999999999995, 2, 4, 3.0, 3.0, 3.0, 4.0, 10.230179028132993, 175.71131713554988, 3.9162404092071608], "isController": false}, {"data": ["\/htp-124", 100, 0, 0.0, 2.63, 2, 4, 3.0, 3.0, 4.0, 4.0, 10.23122570083896, 175.72929455698795, 3.916641088602415], "isController": false}, {"data": ["\/htp-125", 100, 0, 0.0, 2.5400000000000005, 1, 5, 3.0, 3.0, 3.0, 4.989999999999995, 10.232272587741738, 175.74727565742353, 3.917041849994884], "isController": false}, {"data": ["\/htp-126", 100, 0, 0.0, 2.630000000000001, 1, 12, 2.5, 3.0, 3.9499999999999886, 11.929999999999964, 10.234367004400777, 175.78324889980553, 3.9178436188721726], "isController": false}, {"data": ["\/htp-127", 100, 0, 0.0, 2.51, 1, 7, 2.0, 3.0, 4.0, 6.97999999999999, 10.234367004400777, 175.78324889980553, 3.9178436188721726], "isController": false}, {"data": ["\/htp-128", 100, 0, 0.0, 2.540000000000001, 1, 6, 2.0, 3.0, 3.0, 6.0, 10.235414534288639, 175.8012410440123, 3.9182446264073696], "isController": false}, {"data": ["\/htp-118", 100, 0, 0.0, 2.9299999999999993, 2, 5, 3.0, 4.0, 4.0, 4.989999999999995, 10.227040294538762, 175.65740693393332, 3.915038862753119], "isController": false}, {"data": ["\/htp-119", 100, 0, 0.0, 2.810000000000001, 2, 5, 3.0, 3.0, 4.0, 5.0, 10.228086325048583, 175.67537332515087, 3.915439296307661], "isController": false}]}, function(index, item){
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
