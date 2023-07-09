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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.8098, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, ""], "isController": true}, {"data": [0.9325, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.8925, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.91, 500, 1500, "\/htp-132"], "isController": false}, {"data": [0.9125, 500, 1500, "\/htp-133"], "isController": false}, {"data": [0.845, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.9125, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.8625, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.8525, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.87, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.045, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.8475, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.875, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.9175, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.855, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.865, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.8575, 500, 1500, "\/htp-122"], "isController": false}, {"data": [0.8575, 500, 1500, "\/htp-123"], "isController": false}, {"data": [0.87, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.85, 500, 1500, "\/htp-125"], "isController": false}, {"data": [0.8925, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.9, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.9025, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.855, 500, 1500, "\/htp-118"], "isController": false}, {"data": [0.865, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 4800, 0, 0.0, 632.789583333334, 10, 18398, 169.5, 1005.9000000000005, 1739.9499999999998, 11417.97, 196.05440509741453, 7985.308045378426, 74.81275272638158], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 200, 0, 0.0, 15186.95, 3401, 23442, 15075.5, 18818.100000000002, 20057.55, 21634.950000000008, 8.141996417521577, 7958.976423831624, 74.5660570346849], "isController": true}, {"data": ["\/htp-130", 200, 0, 0.0, 239.68499999999995, 12, 1256, 141.5, 630.7, 909.9, 1089.9, 9.51655881233346, 163.45433241339933, 3.643057670346403], "isController": false}, {"data": ["\/htp-131", 200, 0, 0.0, 280.3950000000001, 12, 3121, 135.5, 603.8, 1022.9999999999993, 2139.7500000000027, 9.52471663967997, 163.59444947137823, 3.6461805886274883], "isController": false}, {"data": ["\/htp-132", 200, 0, 0.0, 247.68999999999997, 11, 2349, 124.0, 655.0000000000002, 848.1499999999994, 1850.740000000003, 9.566631589017508, 164.3143714723046, 3.6622261551707647], "isController": false}, {"data": ["\/htp-133", 200, 0, 0.0, 260.3500000000002, 11, 2066, 126.5, 618.9, 1158.2999999999988, 1802.99, 9.626955475330927, 165.35048134777378, 3.6853188929001206], "isController": false}, {"data": ["\/htp-112", 200, 0, 0.0, 366.58000000000004, 15, 2502, 193.0, 723.9000000000001, 1388.0499999999965, 2311.040000000001, 8.51861316977596, 146.313836357441, 3.1612041059715477], "isController": false}, {"data": ["\/htp-134", 200, 0, 0.0, 245.2349999999999, 13, 1681, 117.5, 627.5, 815.3499999999999, 1660.3900000000024, 9.70591089973794, 166.70660244588953, 3.71554401630593], "isController": false}, {"data": ["\/htp-113", 200, 0, 0.0, 393.0400000000003, 15, 4551, 176.0, 1008.1000000000004, 1282.0499999999997, 3128.6100000000024, 8.522243054371911, 146.3761824612238, 3.262421169251747], "isController": false}, {"data": ["\/htp-114", 200, 0, 0.0, 364.1150000000002, 18, 1900, 192.5, 932.0, 1122.5, 1880.1800000000007, 8.525149190110827, 146.42609761295822, 3.2635336743393006], "isController": false}, {"data": ["\/htp-115", 200, 0, 0.0, 349.76499999999993, 13, 1766, 203.5, 756.6, 1191.249999999999, 1669.010000000001, 8.528784648187633, 146.48853944562902, 3.2649253731343286], "isController": false}, {"data": ["\/test.html-103", 200, 0, 0.0, 7712.394999999999, 460, 18398, 8300.0, 13253.0, 14034.55, 16355.61, 8.30702774547267, 4838.665190438611, 3.0340120867253697], "isController": false}, {"data": ["\/htp-116", 200, 0, 0.0, 366.0150000000001, 13, 1873, 199.5, 836.8, 1171.3999999999999, 1823.5700000000004, 8.532423208191126, 146.551034556314, 3.2663182593856654], "isController": false}, {"data": ["\/htp-117", 200, 0, 0.0, 368.83000000000015, 14, 3254, 171.0, 951.4000000000007, 1301.4499999999998, 2351.1600000000008, 8.535700567624088, 146.60732576501215, 3.267572873543596], "isController": false}, {"data": ["\/htp-129", 200, 0, 0.0, 250.61499999999995, 13, 1787, 132.5, 625.3000000000001, 841.6499999999999, 1733.6700000000048, 9.49261948834781, 163.0431558213489, 3.633893397883146], "isController": false}, {"data": ["\/htp-120", 200, 0, 0.0, 377.49000000000024, 12, 2074, 208.5, 960.0, 1271.7499999999984, 1864.4200000000005, 8.680555555555555, 149.09532335069446, 3.323025173611111], "isController": false}, {"data": ["\/htp-121", 200, 0, 0.0, 362.605, 10, 3235, 196.5, 742.9000000000001, 1102.2999999999997, 2105.6900000000014, 8.713078330574191, 149.6539274200575, 3.3354752984229328], "isController": false}, {"data": ["\/htp-122", 200, 0, 0.0, 379.70500000000015, 11, 4271, 193.0, 878.1000000000001, 1102.6499999999996, 2955.250000000002, 8.726003490401396, 149.87592713787083, 3.340423211169284], "isController": false}, {"data": ["\/htp-123", 200, 0, 0.0, 359.35999999999996, 10, 2338, 171.0, 910.8000000000004, 1259.1, 2100.580000000003, 8.755417414525237, 150.38113426432605, 3.3516832289979424], "isController": false}, {"data": ["\/htp-124", 200, 0, 0.0, 316.93999999999994, 15, 1716, 159.5, 683.9000000000001, 1290.5499999999997, 1696.6600000000003, 8.942144326209425, 153.58831485290173, 3.4231646248770455], "isController": false}, {"data": ["\/htp-125", 200, 0, 0.0, 366.6349999999998, 11, 2406, 169.5, 973.1000000000004, 1204.2999999999997, 2255.5300000000025, 8.970621215519175, 154.07742767436645, 3.434065934065934], "isController": false}, {"data": ["\/htp-126", 200, 0, 0.0, 289.67999999999995, 13, 1879, 148.5, 646.3000000000002, 1011.4499999999989, 1646.4700000000023, 9.098767117055639, 156.27843364724077, 3.4831217869978617], "isController": false}, {"data": ["\/htp-127", 200, 0, 0.0, 283.9650000000001, 21, 1763, 148.5, 666.5, 916.9, 1221.2400000000007, 9.073175157646418, 155.83887175066914, 3.4733248650365196], "isController": false}, {"data": ["\/htp-128", 200, 0, 0.0, 277.49999999999994, 15, 1680, 147.5, 658.2, 866.9, 1510.5100000000004, 9.258401999814833, 159.0202874733821, 3.5442320155541154], "isController": false}, {"data": ["\/htp-118", 200, 0, 0.0, 377.86500000000007, 12, 2783, 172.5, 1019.8000000000001, 1304.5499999999984, 2761.0200000000063, 8.540438978563499, 146.68871167478008, 3.2693867964813395], "isController": false}, {"data": ["\/htp-119", 200, 0, 0.0, 350.4949999999999, 12, 2446, 174.0, 858.4000000000002, 1003.9499999999998, 2393.430000000001, 8.54664330584163, 146.7952758429127, 3.2717618905174994], "isController": false}]}, function(index, item){
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
