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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 6000, 0, 0.0, 3.127666666666673, 1, 127, 2.0, 4.0, 13.0, 16.0, 604.2904622822036, 24612.787903489778, 230.59228333669049], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 250, 0, 0.0, 75.06400000000001, 53, 375, 64.0, 85.9, 120.09999999999957, 358.3900000000001, 24.990003998400642, 24428.26580305378, 228.86353271191524], "isController": true}, {"data": ["\/htp-130", 250, 0, 0.0, 2.4600000000000004, 1, 20, 2.0, 3.0, 5.0, 15.940000000000055, 26.082420448617633, 447.98594809598325, 9.984676577986436], "isController": false}, {"data": ["\/htp-131", 250, 0, 0.0, 2.3439999999999994, 1, 17, 2.0, 3.0, 4.0, 10.470000000000027, 26.09058651638489, 448.1262066896264, 9.987802650803589], "isController": false}, {"data": ["\/htp-132", 250, 0, 0.0, 2.328000000000001, 1, 12, 2.0, 3.0, 4.0, 9.980000000000018, 26.09875769913352, 448.2665531370707, 9.99093068169955], "isController": false}, {"data": ["\/htp-133", 250, 0, 0.0, 2.276, 1, 10, 2.0, 3.0, 4.0, 8.980000000000018, 26.106934001670844, 448.40698752088554, 9.99406067251462], "isController": false}, {"data": ["\/htp-112", 250, 0, 0.0, 2.9319999999999995, 1, 32, 2.0, 3.0, 7.0, 30.0, 25.512807429329524, 438.20239947953877, 9.467643381977753], "isController": false}, {"data": ["\/htp-134", 250, 0, 0.0, 2.2199999999999984, 1, 11, 2.0, 3.0, 4.0, 6.470000000000027, 26.12330198537095, 448.68812042842217, 10.000326541274816], "isController": false}, {"data": ["\/htp-113", 250, 0, 0.0, 2.6159999999999988, 1, 17, 2.0, 3.0, 4.899999999999977, 16.0, 25.5885363357216, 439.50310261003074, 9.795611566018424], "isController": false}, {"data": ["\/htp-114", 250, 0, 0.0, 2.6, 1, 33, 2.0, 3.0, 4.449999999999989, 15.490000000000009, 25.625256252562526, 440.13379586920865, 9.809668409184091], "isController": false}, {"data": ["\/htp-115", 250, 0, 0.0, 2.5999999999999996, 1, 21, 2.0, 3.0, 5.0, 14.470000000000027, 25.643655759565082, 440.44982177659244, 9.816711970458508], "isController": false}, {"data": ["\/test.html-103", 250, 0, 0.0, 17.1, 13, 127, 15.0, 18.0, 29.44999999999999, 86.49000000000001, 25.28828646570908, 14729.883563246005, 9.236151502124217], "isController": false}, {"data": ["\/htp-116", 250, 0, 0.0, 2.5999999999999988, 1, 43, 2.0, 3.0, 5.449999999999989, 14.960000000000036, 25.672622715136576, 440.94735186896696, 9.827800883138222], "isController": false}, {"data": ["\/htp-117", 250, 0, 0.0, 2.587999999999998, 1, 47, 2.0, 3.0, 5.0, 13.490000000000009, 25.699013157894736, 441.4006283408717, 9.837903474506579], "isController": false}, {"data": ["\/htp-129", 250, 0, 0.0, 2.2999999999999994, 1, 23, 2.0, 3.0, 4.0, 9.980000000000018, 26.071540306601314, 447.79907315674205, 9.980511523620816], "isController": false}, {"data": ["\/htp-120", 250, 0, 0.0, 2.3640000000000003, 1, 13, 2.0, 3.0, 4.449999999999989, 12.0, 25.845135945415073, 443.9104013749612, 9.893841104104208], "isController": false}, {"data": ["\/htp-121", 250, 0, 0.0, 2.408000000000001, 1, 17, 2.0, 3.0, 4.449999999999989, 14.490000000000009, 25.877238381119966, 444.4617857882207, 9.906130317772488], "isController": false}, {"data": ["\/htp-122", 250, 0, 0.0, 2.451999999999998, 1, 15, 2.0, 3.0, 5.0, 13.490000000000009, 25.92016588906169, 445.1990992742354, 9.922563504406428], "isController": false}, {"data": ["\/htp-123", 250, 0, 0.0, 2.551999999999999, 1, 25, 2.0, 3.0, 4.449999999999989, 12.980000000000018, 25.941683096399295, 445.56867412057693, 9.930800560340355], "isController": false}, {"data": ["\/htp-124", 250, 0, 0.0, 2.651999999999999, 1, 12, 2.0, 4.0, 4.449999999999989, 11.490000000000009, 25.968629895086735, 446.0315064402202, 9.94111613171289], "isController": false}, {"data": ["\/htp-125", 250, 0, 0.0, 2.792000000000002, 1, 12, 2.0, 4.0, 4.0, 11.490000000000009, 25.998336106489187, 446.54173382903497, 9.952488040765392], "isController": false}, {"data": ["\/htp-126", 250, 0, 0.0, 2.823999999999999, 1, 15, 2.0, 4.0, 5.0, 8.490000000000009, 26.025400791172185, 447.0065909327504, 9.962848740370601], "isController": false}, {"data": ["\/htp-127", 250, 0, 0.0, 2.6520000000000006, 1, 11, 2.0, 4.0, 4.0, 8.980000000000018, 26.038954275596293, 447.2393826163941, 9.968037183626704], "isController": false}, {"data": ["\/htp-128", 250, 0, 0.0, 2.356000000000001, 1, 8, 2.0, 3.0, 4.0, 7.490000000000009, 26.060669237986033, 447.61235406025224, 9.976349942666527], "isController": false}, {"data": ["\/htp-118", 250, 0, 0.0, 2.475999999999999, 1, 30, 2.0, 3.0, 4.449999999999989, 21.3900000000001, 25.725457913150855, 441.8548376723606, 9.848026857378061], "isController": false}, {"data": ["\/htp-119", 250, 0, 0.0, 2.5720000000000014, 1, 37, 2.0, 3.0, 4.0, 18.49000000000001, 25.746652935118437, 442.21887873326466, 9.856140576725025], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 6000, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
