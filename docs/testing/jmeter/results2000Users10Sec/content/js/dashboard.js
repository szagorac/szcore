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

    var data = {"OkPercent": 99.06666666666666, "KoPercent": 0.9333333333333333};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.89651, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, ""], "isController": true}, {"data": [0.9805, 500, 1500, "\/htp-130"], "isController": false}, {"data": [0.97775, 500, 1500, "\/htp-131"], "isController": false}, {"data": [0.97275, 500, 1500, "\/htp-132"], "isController": false}, {"data": [0.97275, 500, 1500, "\/htp-133"], "isController": false}, {"data": [0.781, 500, 1500, "\/htp-112"], "isController": false}, {"data": [0.9725, 500, 1500, "\/htp-134"], "isController": false}, {"data": [0.91775, 500, 1500, "\/htp-113"], "isController": false}, {"data": [0.949, 500, 1500, "\/htp-114"], "isController": false}, {"data": [0.952, 500, 1500, "\/htp-115"], "isController": false}, {"data": [0.07, 500, 1500, "\/test.html-103"], "isController": false}, {"data": [0.96475, 500, 1500, "\/htp-116"], "isController": false}, {"data": [0.98, 500, 1500, "\/htp-117"], "isController": false}, {"data": [0.98675, 500, 1500, "\/htp-129"], "isController": false}, {"data": [0.99125, 500, 1500, "\/htp-120"], "isController": false}, {"data": [0.99275, 500, 1500, "\/htp-121"], "isController": false}, {"data": [0.99425, 500, 1500, "\/htp-122"], "isController": false}, {"data": [0.9965, 500, 1500, "\/htp-123"], "isController": false}, {"data": [0.99925, 500, 1500, "\/htp-124"], "isController": false}, {"data": [0.99925, 500, 1500, "\/htp-125"], "isController": false}, {"data": [0.999, 500, 1500, "\/htp-126"], "isController": false}, {"data": [0.99825, 500, 1500, "\/htp-127"], "isController": false}, {"data": [0.994, 500, 1500, "\/htp-128"], "isController": false}, {"data": [0.985, 500, 1500, "\/htp-118"], "isController": false}, {"data": [0.98575, 500, 1500, "\/htp-119"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 48000, 448, 0.9333333333333333, 789.3165208333292, 2, 27469, 126.0, 196.0, 312.0, 25194.93000000001, 1308.1870707511173, 48017.86522245142, 494.705611066445], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["", 2000, 330, 16.5, 18943.596500000003, 4407, 32526, 17610.5, 28503.100000000002, 29844.8, 32301.99, 54.380336070476915, 47905.58243357952, 493.548813998858], "isController": true}, {"data": ["\/htp-130", 2000, 0, 0.0, 178.8080000000002, 3, 2201, 136.0, 251.0, 343.0, 1833.0, 61.25949522175937, 1052.179689414359, 23.450900514579757], "isController": false}, {"data": ["\/htp-131", 2000, 0, 0.0, 187.00600000000037, 3, 1916, 141.0, 268.0, 345.9499999999998, 1833.0, 61.44770800049158, 1055.4123909303182, 23.522950718938183], "isController": false}, {"data": ["\/htp-132", 2000, 0, 0.0, 197.95599999999976, 3, 1979, 143.0, 309.8000000000002, 431.9499999999998, 1832.0, 61.72839506172839, 1060.2334104938273, 23.630401234567902], "isController": false}, {"data": ["\/htp-133", 2000, 0, 0.0, 200.68100000000027, 2, 1993, 147.0, 313.9000000000001, 449.9499999999998, 1832.0, 61.93868070610096, 1063.8452307215857, 23.710901207804273], "isController": false}, {"data": ["\/htp-112", 2000, 88, 4.4, 1713.1225, 5, 27379, 192.5, 2953.9, 12560.24999999985, 23724.86, 57.02717344814804, 942.2943758286762, 20.231280830315647], "isController": false}, {"data": ["\/htp-134", 2000, 0, 0.0, 200.39600000000002, 2, 1918, 149.0, 320.8000000000002, 459.0, 1830.98, 62.2568093385214, 1069.3093385214008, 23.832684824902724], "isController": false}, {"data": ["\/htp-113", 2000, 19, 0.95, 669.917500000001, 3, 25144, 159.0, 474.0, 1687.699999999988, 19526.800000000032, 56.96382796923953, 970.3825922101965, 21.599303973227002], "isController": false}, {"data": ["\/htp-114", 2000, 7, 0.35, 313.14650000000063, 3, 25156, 153.0, 335.8000000000002, 1102.0, 1860.7600000000002, 57.25409366769724, 980.4188483607437, 21.84087119260277], "isController": false}, {"data": ["\/htp-115", 2000, 4, 0.2, 239.79049999999987, 4, 6093, 145.0, 333.9000000000001, 1146.7999999999956, 1829.0, 57.80680964217585, 991.1665098054801, 22.08491097751315], "isController": false}, {"data": ["\/test.html-103", 2000, 330, 16.5, 12897.505500000003, 104, 27469, 11419.0, 25187.8, 26470.5, 27412.99, 57.33944954128441, 27910.95511060242, 17.486852243405963], "isController": false}, {"data": ["\/htp-116", 2000, 0, 0.0, 221.24949999999995, 3, 5957, 141.0, 304.9000000000001, 436.7999999999993, 1821.020000000001, 58.26487210860572, 1000.7446978966381, 22.30452135407563], "isController": false}, {"data": ["\/htp-117", 2000, 0, 0.0, 179.0875000000001, 3, 2008, 132.0, 241.9000000000001, 340.9499999999998, 1621.99, 59.194364696480896, 1016.7094592594785, 22.660342735371593], "isController": false}, {"data": ["\/htp-129", 2000, 0, 0.0, 164.60250000000005, 3, 1914, 134.0, 227.9000000000001, 307.89999999999964, 1562.98, 60.82540068732703, 1044.7237766491287, 23.284723700617377], "isController": false}, {"data": ["\/htp-120", 2000, 0, 0.0, 150.80799999999968, 4, 1850, 122.0, 202.0, 246.0, 1271.99, 59.12262031453234, 1015.4771934492136, 22.63287808915691], "isController": false}, {"data": ["\/htp-121", 2000, 0, 0.0, 144.22350000000014, 5, 1823, 118.0, 197.0, 237.84999999999945, 1124.0, 59.215396002960766, 1017.0706883789786, 22.668393782383422], "isController": false}, {"data": ["\/htp-122", 2000, 0, 0.0, 141.484, 3, 1726, 118.0, 196.0, 222.94999999999982, 499.0, 59.580552907530986, 1023.3425434938036, 22.808180409914204], "isController": false}, {"data": ["\/htp-123", 2000, 0, 0.0, 136.33349999999976, 3, 1427, 118.0, 193.0, 217.94999999999982, 390.98, 59.60718862694841, 1023.8000327839538, 22.81837689625369], "isController": false}, {"data": ["\/htp-124", 2000, 0, 0.0, 130.7294999999999, 3, 1272, 119.0, 185.9000000000001, 215.0, 375.99, 59.8712767549768, 1028.3359527016914, 22.919473132764555], "isController": false}, {"data": ["\/htp-125", 2000, 0, 0.0, 128.9549999999998, 3, 1276, 118.0, 180.9000000000001, 211.89999999999964, 310.97, 60.051043386878845, 1031.4235850472903, 22.988290046539557], "isController": false}, {"data": ["\/htp-126", 2000, 0, 0.0, 132.93399999999963, 4, 1278, 121.0, 189.0, 218.94999999999982, 355.99, 60.07449237053947, 1031.8263396611799, 22.99726661059714], "isController": false}, {"data": ["\/htp-127", 2000, 0, 0.0, 136.39150000000018, 3, 1841, 124.0, 191.0, 241.94999999999982, 350.98, 60.366424194862816, 1036.840496815671, 23.10902176209592], "isController": false}, {"data": ["\/htp-128", 2000, 0, 0.0, 149.03849999999986, 6, 2202, 129.0, 207.9000000000001, 270.0, 415.99, 60.69986949528058, 1042.5676803544873, 23.2366687911621], "isController": false}, {"data": ["\/htp-118", 2000, 0, 0.0, 169.09849999999977, 3, 1918, 129.0, 233.9000000000001, 311.84999999999945, 1451.88, 59.40888162780335, 1020.3939551462944, 22.742462498143475], "isController": false}, {"data": ["\/htp-119", 2000, 0, 0.0, 160.33149999999995, 3, 1903, 125.0, 210.0, 259.89999999999964, 1620.5200000000004, 59.11737755313174, 1015.3871449262509, 22.630871094558245], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset by peer (connect failed)", 2, 0.44642857142857145, 0.004166666666666667], "isController": false}, {"data": ["Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 207, 46.205357142857146, 0.43125], "isController": false}, {"data": ["Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 157, 35.044642857142854, 0.32708333333333334], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.conn.HttpHostConnectException\/Non HTTP response message: Connect to zscore:80 [zscore\\\/192.168.88.2] failed: Operation timed out (Connection timed out)", 82, 18.303571428571427, 0.17083333333333334], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 48000, 448, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 207, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 157, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException\/Non HTTP response message: Connect to zscore:80 [zscore\\\/192.168.88.2] failed: Operation timed out (Connection timed out)", 82, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset by peer (connect failed)", 2, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["\/htp-112", 2000, 88, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 46, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 41, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException\/Non HTTP response message: Connect to zscore:80 [zscore\\\/192.168.88.2] failed: Operation timed out (Connection timed out)", 1, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": ["\/htp-113", 2000, 19, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 14, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 5, null, null, null, null, null, null], "isController": false}, {"data": ["\/htp-114", 2000, 7, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 7, null, null, null, null, null, null, null, null], "isController": false}, {"data": ["\/htp-115", 2000, 4, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 4, null, null, null, null, null, null, null, null], "isController": false}, {"data": ["\/test.html-103", 2000, 330, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset", 136, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Broken pipe (Write failed)", 111, "Non HTTP response code: org.apache.http.conn.HttpHostConnectException\/Non HTTP response message: Connect to zscore:80 [zscore\\\/192.168.88.2] failed: Operation timed out (Connection timed out)", 81, "Non HTTP response code: java.net.SocketException\/Non HTTP response message: Connection reset by peer (connect failed)", 2, null, null], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
