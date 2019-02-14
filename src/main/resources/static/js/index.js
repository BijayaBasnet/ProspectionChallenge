google.load("visualization", "1", {packages: ["corechart", "table", "timeline"]});

var DEFAULT_PURCHASE_CONDITION = {

    "VIOLATED": {
        "value": "Patients that violated by taking B and I together."
    },
    "Patients that violated by taking B and I together.": {
        "key": "Patients that violated by taking B and I together."
    },
    "VALID_NO_COMED": {
        "value": "Patients that did not violate, because they never took B and I together."
    },
    "Patients that did not violate, because they never took B and I together.": {
        "key": "Patients that did not violate, because they never took B and I together."
    },
    "VALID_BI_SWITCH": {

        "value": "Patients that did not violate, because they switched from B to I."
    },
    "Patients that did not violate, because they switched from B to I.": {
        "key": "Patients that did not violate, because they switched from B to I."
    },
    "VALID_IB_SWITCH": {

        "value": "Patients that did not violate, because they switched from I to B."
    },
    "Patients that did not violate, because they switched from I to B.": {
        "key": "Patients that did not violate, because they switched from I to B.",
    },
    "VALID_I_TRIAL": {

        "value": "Patients that did not violate, because they simply trialled I during B."
    },
    "Patients that did not violate, because they simply trialled I during B.": {
        "key": "Patients that did not violate, because they simply trialled I during B."
    },

    "VALID_B_TRIAL": {
        "value": "Patients that did not violate, because they simply trialled B during I."
    },
    "Patients that did not violate, because they simply trialled B during I.": {
        "key": "Patients that did not violate, because they simply trialled B during I."
    }
};

var categorizedRows = [];


google.setOnLoadCallback(function () {

    function drawChart(data) {
        var options = {
            title: 'B & I Analysis Result',
            pieHole: 0.4
        };

        var chart = new google.visualization.PieChart(document.getElementById('piechart'));


        function selectHandler() {
            var selected = chart.getSelection()[0];

            if (selected) {
                var topping = data.getValue(selected.row, 0);
                drawTimeLine(topping);
            }

        }

        google.visualization.events.addListener(chart, 'select', selectHandler);

        chart.draw(data, options);
    }


    function drawTable(data) {
        var table = new google.visualization.Table(document.getElementById('table_div'));
        table.draw(data, {width: '90%', height: '100%'});
    }





    function drawTimeLine(selectedOption = null) {

        var container = document.getElementById('timeline');
        var chart = new google.visualization.Timeline(container);
        var dataTable = new google.visualization.DataTable();



        dataTable.addColumn({type: 'string', id: 'TYpe'});
        dataTable.addColumn({type: 'string', id: 'Patient Id'});
        dataTable.addColumn({type: 'date', id: 'Start'});
        dataTable.addColumn({type: 'date', id: 'End'});

        if (selectedOption !== null) {
            var newArray = categorizedRows.filter(function (data) {

             //   console.log(categorizedRows);
                return DEFAULT_PURCHASE_CONDITION[selectedOption].key == data[0];
            });
            dataTable.addRows(newArray);
        }

        var options = {
            timeline: {
                showRowLabels: false ,
                groupByRowLabel:false,
                colorByRowLabel: true}
        };

        chart.draw(dataTable, options);
    }


    Analysis.getBIAnalysis(
        function success(result) {
            var rows = [];
            var patients = result.patients;
            for (var key in patients) {
                if (patients.hasOwnProperty(key)) {
                    rows.push([result.patientTypeNameMap[key], patients[key]])
                }
            }

            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Patient Type');
            data.addColumn('number', 'Count');
            data.addRows(rows);


            var categoriezedData = result.categorizedPurchaseRecords;
            console.log(categoriezedData);

            for (var k in categoriezedData) {
                if (categoriezedData.hasOwnProperty(k)) {
                    for (var innerKey in categoriezedData[k]) {


                        var hasStartDate = false;

                        for (var i = 1; i < categoriezedData[k][innerKey].length; i++) {

                            if (hasStartDate) {
                                var startDate = (categoriezedData[k][innerKey][i - 1].day);
                            } else {
                                startDate = today();
                            }

                            categorizedRows.push([
                                DEFAULT_PURCHASE_CONDITION[k].value,
                                categoriezedData[k][innerKey][i - 1].patientId.toString(),
                                startDate,
                                (categoriezedData[k][innerKey][i].day)
                            ]);
                            hasStartDate = true;
                        }
                    }
                }
            }
            drawChart(data);
            drawTable(data);
            drawTimeLine()
        },
        function failure() {
            toastr.error('Oops, failed to load analysis result.');
        }
    );

});
/*
function today() {
    return new Date(2018, 11);
}

function dateFromDay(day) {
    var date = new Date(2019, 0); // initialize a date in `year-01-01`
    return new Date(date.setDate(day)); // add the number of days
}
*/


