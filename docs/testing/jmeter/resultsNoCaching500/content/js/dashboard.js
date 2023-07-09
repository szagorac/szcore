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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.95738, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0785, 500, 1500, ""], "isController": true}, {"data": [0.9995, 500, 1500, "\/htp-130"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-131"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-132"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-133"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-112"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.8625, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-117"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-121"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-122"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-123"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-124"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-125"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-126"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-127"], "isController": false}, {"data": [1.0, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-118"], "isController": false}, {"data": [0.9995, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 24000, 0, 0.0, 129.61470833333308, 2, 2833, 124.0, 190.0, 262.0, 443.9900000000016, 1595.3203935123636, 66071.87925834219, 608.7611755517149], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 1000, 0, 0.0, 3110.753000000002, 499, 5488, 3441.5, 4720.799999999999, 4956.75, 5207.99, 66.11133148221606, 65713.6950656155, 605.461002578342], "isController": true}, {"data": ["\/htp-130", 1000, 0, 0.0, 111.90999999999984, 3, 512, 110.0, 178.0, 205.79999999999973, 364.7800000000002, 68.74742197167606, 1230.0014823662864, 26.317372473532245], "isController": false}, {"data": ["\/htp-131", 1000, 0, 0.0, 110.37799999999976, 2, 428, 109.5, 178.89999999999998, 214.94999999999993, 338.97, 68.78052135635187, 1230.59368336887, 26.330043331728454], "isController": false}, {"data": ["\/htp-132", 1000, 0, 0.0, 109.88299999999988, 2, 431, 109.0, 176.89999999999998, 211.0, 335.96000000000004, 68.81365262868152, 1231.1864548926508, 26.342726396917147], "isController": false}, {"data": ["\/htp-133", 1000, 0, 0.0, 111.99800000000003, 2, 489, 108.0, 181.89999999999998, 218.6999999999996, 351.9100000000001, 68.83259911894272, 1231.5254379474118, 26.349979350220263], "isController": false}, {"data": ["\/htp-112", 1000, 0, 0.0, 119.83300000000001, 6, 442, 118.0, 184.89999999999998, 229.89999999999986, 377.97, 68.72852233676977, 1229.663337628866, 25.504725085910653], "isController": false}, {"data": ["\/htp-134", 1000, 0, 0.0, 108.81400000000018, 2, 456, 105.5, 176.0, 206.94999999999993, 332.95000000000005, 68.84681583476764, 1231.7797977624784, 26.355421686746986], "isController": false}, {"data": ["\/htp-113", 1000, 0, 0.0, 119.3939999999999, 4, 554, 115.0, 182.0, 239.89999999999986, 379.8900000000001, 68.75687568756877, 1230.1706244843233, 26.320991474147412], "isController": false}, {"data": ["\/htp-114", 1000, 0, 0.0, 120.31600000000005, 6, 550, 117.5, 184.0, 229.8499999999998, 347.93000000000006, 68.07351940095303, 1217.9442860789654, 26.05939414567733], "isController": false}, {"data": ["\/htp-115", 1000, 0, 0.0, 115.24399999999994, 6, 567, 115.0, 178.0, 207.0, 330.0, 68.06425265450586, 1217.7784891437516, 26.055846719303023], "isController": false}, {"data": ["\/test.html-103", 1000, 0, 0.0, 466.0720000000005, 27, 2833, 160.0, 1916.8999999999996, 2565.0, 2716.99, 68.22213125938053, 39737.92574873789, 24.917067471687815], "isController": false}, {"data": ["\/htp-116", 1000, 0, 0.0, 117.7550000000001, 7, 539, 115.0, 182.0, 231.79999999999973, 370.95000000000005, 68.23609689525759, 1220.8530578300922, 26.1216308427158], "isController": false}, {"data": ["\/htp-117", 1000, 0, 0.0, 118.24999999999997, 4, 502, 116.0, 183.0, 239.94999999999993, 364.0, 68.30134553650707, 1222.0204605218223, 26.146608838194112], "isController": false}, {"data": ["\/htp-129", 1000, 0, 0.0, 112.51599999999999, 3, 433, 113.0, 180.89999999999998, 225.94999999999993, 327.99, 68.70963309055931, 1229.3253787618523, 26.30290641747973], "isController": false}, {"data": ["\/htp-120", 1000, 0, 0.0, 117.66800000000002, 5, 520, 114.0, 184.89999999999998, 233.8499999999998, 364.0, 68.29668078131402, 1221.9370005805217, 26.14482311159678], "isController": false}, {"data": ["\/htp-121", 1000, 0, 0.0, 113.40700000000014, 5, 508, 114.0, 176.0, 191.94999999999993, 312.98, 68.30134553650707, 1222.0204605218223, 26.146608838194112], "isController": false}, {"data": ["\/htp-122", 1000, 0, 0.0, 114.97800000000002, 6, 443, 114.0, 181.89999999999998, 222.89999999999986, 347.93000000000006, 68.30134553650707, 1222.0204605218223, 26.146608838194112], "isController": false}, {"data": ["\/htp-123", 1000, 0, 0.0, 116.15699999999994, 4, 457, 114.0, 182.0, 229.94999999999993, 340.9100000000001, 68.3246788740093, 1222.4379312995354, 26.155541131456683], "isController": false}, {"data": ["\/htp-124", 1000, 0, 0.0, 114.92900000000014, 3, 495, 112.5, 182.0, 234.94999999999993, 383.95000000000005, 68.35737234260715, 1223.0228698133844, 26.1680565999043], "isController": false}, {"data": ["\/htp-125", 1000, 0, 0.0, 113.86399999999989, 3, 402, 111.0, 181.89999999999998, 220.94999999999993, 335.8900000000001, 68.38074398249452, 1223.4410258821115, 26.177003555798684], "isController": false}, {"data": ["\/htp-126", 1000, 0, 0.0, 116.15699999999997, 3, 484, 115.0, 181.89999999999998, 229.94999999999993, 359.95000000000005, 68.53070175438597, 1226.1240105879933, 26.234409265350877], "isController": false}, {"data": ["\/htp-127", 1000, 0, 0.0, 112.32599999999996, 4, 430, 112.0, 178.0, 211.94999999999993, 335.0, 68.6012211017356, 1227.385714653221, 26.261404953008164], "isController": false}, {"data": ["\/htp-128", 1000, 0, 0.0, 113.81100000000018, 3, 469, 114.0, 180.0, 219.0, 373.7600000000002, 68.63889079552474, 1228.0596858054773, 26.275825382661814], "isController": false}, {"data": ["\/htp-118", 1000, 0, 0.0, 119.69100000000019, 6, 505, 115.0, 183.0, 255.0, 388.9100000000001, 68.27336655970507, 1221.5198718167542, 26.135898136137094], "isController": false}, {"data": ["\/htp-119", 1000, 0, 0.0, 115.40199999999997, 7, 567, 115.0, 181.0, 210.89999999999986, 330.8900000000001, 68.29201666325206, 1221.8535520385167, 26.14303762890118], "isController": false}]}, function(index, item){
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
