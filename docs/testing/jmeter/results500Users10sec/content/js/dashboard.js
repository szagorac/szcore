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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.99776, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.944, 500, 1500, ""], "isController": true}, {"data": [1.0, 500, 1500, "\/htp-130"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-131"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-132"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-133"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-112"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-134"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-113"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-114"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-115"], "isController": false}, {"data": [1.0, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-116"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-117"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-129"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-120"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-121"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-122"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-123"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-124"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-125"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-126"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-127"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-128"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-118"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 12000, 0, 0.0, 7.29149999999999, 1, 421, 3.0, 16.0, 23.0, 65.98999999999978, 1202.1638950110198, 48964.2097963835, 458.73588083550396], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 500, 0, 0.0, 174.99599999999998, 64, 1219, 92.0, 520.0, 625.75, 828.8900000000001, 49.67709885742673, 48560.431414555394, 454.9529619970194], "isController": true}, {"data": ["\/htp-130", 500, 0, 0.0, 5.922000000000006, 1, 87, 3.0, 12.0, 21.94999999999999, 65.96000000000004, 52.67038870746866, 904.655074791952, 20.162883177077845], "isController": false}, {"data": ["\/htp-131", 500, 0, 0.0, 6.292, 1, 136, 3.0, 11.0, 21.0, 62.99000000000001, 52.71481286241434, 905.4180943595151, 20.17988929889299], "isController": false}, {"data": ["\/htp-132", 500, 0, 0.0, 6.506000000000001, 1, 194, 3.0, 10.900000000000034, 20.94999999999999, 72.94000000000005, 52.748180187783525, 905.9912042409537, 20.192662728135883], "isController": false}, {"data": ["\/htp-133", 500, 0, 0.0, 6.578, 1, 302, 3.0, 10.0, 21.0, 51.98000000000002, 52.770448548812666, 906.3736807387863, 20.20118733509235], "isController": false}, {"data": ["\/htp-112", 500, 0, 0.0, 6.848000000000004, 1, 133, 3.0, 14.0, 28.899999999999977, 66.97000000000003, 51.03082261686059, 876.4942462747499, 18.937219330475607], "isController": false}, {"data": ["\/htp-134", 500, 0, 0.0, 7.289999999999998, 2, 204, 3.0, 11.0, 23.94999999999999, 104.96000000000004, 52.93806246691371, 909.2525807305452, 20.265352038115406], "isController": false}, {"data": ["\/htp-113", 500, 0, 0.0, 6.640000000000004, 1, 142, 3.0, 13.0, 24.94999999999999, 69.90000000000009, 51.14043162524292, 878.3768666257544, 19.577196481538305], "isController": false}, {"data": ["\/htp-114", 500, 0, 0.0, 6.164000000000003, 1, 78, 4.0, 12.0, 19.94999999999999, 59.99000000000001, 51.34524543027315, 881.8947037379339, 19.655601766276444], "isController": false}, {"data": ["\/htp-115", 500, 0, 0.0, 6.4080000000000075, 2, 211, 3.0, 12.0, 17.0, 52.950000000000045, 51.4668039114771, 883.982565620175, 19.702135872362327], "isController": false}, {"data": ["\/test.html-103", 500, 0, 0.0, 30.400000000000006, 14, 421, 18.0, 49.900000000000034, 87.84999999999997, 268.4700000000005, 50.38291011688835, 29346.962697752922, 18.401570687222893], "isController": false}, {"data": ["\/htp-116", 500, 0, 0.0, 5.913999999999995, 1, 148, 3.0, 11.0, 15.949999999999989, 59.8900000000001, 51.6422226812642, 886.9955200371824, 19.769288370171452], "isController": false}, {"data": ["\/htp-117", 500, 0, 0.0, 5.871999999999998, 1, 156, 3.0, 11.0, 19.94999999999999, 49.960000000000036, 51.77055290950507, 889.1996919652103, 19.818414785669912], "isController": false}, {"data": ["\/htp-129", 500, 0, 0.0, 5.581999999999993, 1, 108, 3.0, 11.0, 18.0, 44.0, 52.61496369567505, 903.7031069136062, 20.141665789750604], "isController": false}, {"data": ["\/htp-120", 500, 0, 0.0, 6.548000000000002, 1, 272, 3.0, 11.0, 19.94999999999999, 65.86000000000013, 51.93725979017347, 892.0630128804404, 19.88223226342578], "isController": false}, {"data": ["\/htp-121", 500, 0, 0.0, 7.392, 1, 316, 3.0, 13.0, 26.899999999999977, 68.8900000000001, 51.985859846121855, 892.8977568101476, 19.900836972343523], "isController": false}, {"data": ["\/htp-122", 500, 0, 0.0, 6.882000000000006, 1, 273, 3.0, 12.0, 19.94999999999999, 67.94000000000005, 52.0074890784273, 893.2692557728312, 19.909116912835447], "isController": false}, {"data": ["\/htp-123", 500, 0, 0.0, 6.798000000000011, 1, 321, 3.0, 12.0, 22.0, 61.840000000000146, 52.04538357447694, 893.9201233475592, 19.923623399604455], "isController": false}, {"data": ["\/htp-124", 500, 0, 0.0, 6.228000000000011, 1, 263, 3.0, 11.900000000000034, 20.94999999999999, 55.99000000000001, 52.10504376823676, 894.9448337849103, 19.946462067528138], "isController": false}, {"data": ["\/htp-125", 500, 0, 0.0, 5.54, 1, 79, 3.0, 13.0, 18.0, 41.99000000000001, 52.14851898206091, 895.691554547351, 19.963104922820193], "isController": false}, {"data": ["\/htp-126", 500, 0, 0.0, 5.705999999999994, 1, 83, 3.0, 13.0, 21.0, 50.850000000000136, 52.345058626465665, 899.0672764865997, 20.038342755443885], "isController": false}, {"data": ["\/htp-127", 500, 0, 0.0, 5.642000000000003, 1, 139, 3.0, 12.0, 17.0, 56.97000000000003, 52.41639584862145, 900.2925490093302, 20.0656515358004], "isController": false}, {"data": ["\/htp-128", 500, 0, 0.0, 5.587999999999998, 1, 202, 3.0, 10.0, 15.0, 45.98000000000002, 52.515492070160704, 901.9946040331897, 20.10358680810839], "isController": false}, {"data": ["\/htp-118", 500, 0, 0.0, 6.395999999999995, 2, 272, 3.0, 11.0, 19.0, 68.86000000000013, 51.845707175445874, 890.4905251970137, 19.847184778100374], "isController": false}, {"data": ["\/htp-119", 500, 0, 0.0, 5.86, 1, 167, 3.0, 12.0, 17.0, 48.0, 51.89952252439278, 891.4148458584181, 19.86778596636911], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 12000, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
