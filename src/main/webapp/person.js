document.getElementById("addBtn").addEventListener("click", postPerson);
document.getElementById("editBtn").addEventListener("click", editPerson);
document.getElementById("getAllBtn").addEventListener("click", (event) => {
    fetchFunction("api/persons/all", insertAllPersonsInTable);
});
document.getElementById("getBtn").addEventListener("click", getPerson);

function postPerson() {
    let firstN = document.getElementById("inputFName").value;
    let lastN = document.getElementById("inputLName").value;
    let email = document.getElementById("inputEmail").value;
    let street = document.getElementById("inputStreet").value;
    let aInfo = document.getElementById("inputAdditionalInfo").value;
    let city = document.getElementById("inputCity").value;
    let zip = document.getElementById("inputZip").value;
    let hobby = document.getElementById("inputHobby").value;
    let hDesc = document.getElementById("inputHobbyDescription").value;
    let phoneNo = document.getElementById("inputPhoneNumber").value;
    let phoneDesc = document.getElementById("inputPhoneDescription").value;

    let options = {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            fName: firstN,
            lName: lastN,
            email: email,
            street: street,
            additionalAddressInfo: aInfo,
            city: city,
            zip: zip,
            hobbyName: hobby,
            hobbyDescription: hDesc,
            phoneNumber: phoneNo,
            phoneDescription: phoneDesc

        })
    };
    fetch("api/persons", options).then(function (response) {
        return response.json();
    })
            .then(function (data) {
                if (!data.hasOwnProperty("message")) {
                    insertPersonInTable(data);
                } else {
                    document.getElementById("output").innerHTML = data.message;
                    document.getElementById("output").style = "color: red";
                }
            });

}


function editPerson() {
    let inputId = document.getElementById("inputIdPUT").value;
    let inputfName = document.getElementById("inputFNamePUT").value;
    let inputlName = document.getElementById("inputLNamePUT").value;
    let inputStreet = document.getElementById("inputStreetPUT").value;
    let inputCity = document.getElementById("inputCityPUT").value;
    let inputZip = document.getElementById("inputZipPUT").value;
    let inputHobbies = document.getElementById("inputHobbyPUT").value;
    let inputPhones = document.getElementById("inputPhoneNumberPUT").value;
    let inputEmail = document.getElementById("inputEmailPUT").value;
    let inputaInfo = document.getElementById("inputaInfoPUT").value;
    let inputHobbyDesc = document.getElementById("inputHobbyDescPUT").value;
    let inputPhoneDesc = document.getElementById("inputPhoneDescPUT").value;
    let options = {
        method: "PUT",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            fName: inputfName,
            lName: inputlName,
            email: inputEmail,
            street: inputStreet,
            additionalAddressInfo: inputaInfo,
            city: inputCity,
            zip: inputZip,
            hobbyName: inputHobbies,
            hobbyDescription: inputHobbyDesc,
            phoneNumber: inputPhones,
            phoneDescription: inputPhoneDesc
        })
    };

    fetch("api/persons/" + inputId, options).then(function (response) {
        return response.json();
    })
            .then(function (data) {
                if (!data.hasOwnProperty("message")) {
                    insertPersonInTable(data);
                } else {
                    document.getElementById("output").innerHTML = data.message;
                    document.getElementById("output").style = "color: red";
                }
            });
}

function getPerson() {
    var radios = document.getElementsByName('radiobtn');
    let inputInfo = document.getElementById('inputInfoGET').value;

    for (var i = 0, length = radios.length; i < length; i++) {
        if (radios[i].checked) {
            if (radios[i].value === "id") {
                fetchFunction("api/persons/" + inputInfo, insertPersonInTable);
            } else if (radios[i].value === "phone") {
                fetchFunction("api/persons/" + radios[i].value + '/' + inputInfo, insertPersonInTable);
            } else {
                fetchFunction("api/persons/" + radios[i].value + '/' + inputInfo, insertAllPersonsInTable);
            }
        }
    }
}


function fetchFunction(fetchUrl, callback) {
    fetch(fetchUrl)
            .then(function (response) {
                return response.json();
            })
            .then(function (data) {
                if (!data.hasOwnProperty("message")) {
                    document.getElementById("output").style = "color: black";
                    callback(data);
                } else {
                    document.getElementById("output").innerHTML = data.message;
                    document.getElementById("output").style = "color: red";
                }
            });
}

function insertAllPersonsInTable(dataArray) {
    let printString = createTableFromArray(dataArray);
    document.getElementById("output").innerHTML = printString;
}

function insertPersonInTable(data) {
    let printString = createTableFromData(data);
    document.getElementById("output").innerHTML = printString;
}

function createTableFromArray(array) {
    let tableHead = "<thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Street</th><th>City</th><th>Zip</th><th>Hobbies</th><th>Phones</th></tr></thead>";
    let htmlRows = "";

    array.personsList.forEach(element => {
        let temp = "<tr>" +
                "<td>" + element.id + "</td>" +
                "<td>" + element.fName + "</td>" +
                "<td>" + element.lName + "</td>" +
                "<td>" + element.street + "</td>" +
                "<td>" + element.city + "</td>" +
                "<td>" + element.zip + "</td>" +
                "<td>" + element.hobbies + "</td>" +
                "<td>" + element.phones + "</td>" +
                "</tr>";
        htmlRows += temp;
    });

    return "<table class='table table-hover'>" + tableHead + htmlRows + "</table>";
}


function createTableFromData(element) {
    let tableHead = "<thead><tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Street</th><th>City</th><th>Zip</th><th>Hobbies</th><th>Phones</th></tr></thead>";
    let htmlRows = "";

    htmlRows = "<tr>" +
            "<td>" + element.id + "</td>" +
            "<td>" + element.fName + "</td>" +
            "<td>" + element.lName + "</td>" +
            "<td>" + element.street + "</td>" +
            "<td>" + element.city + "</td>" +
            "<td>" + element.zip + "</td>" +
            "<td>" + element.hobbies + "</td>" +
            "<td>" + element.phones + "</td>" +
            "</tr>";

    return "<table class='table table-hover'>" + tableHead + htmlRows + "</table>";
}


document.getElementById("editBtn").onclick = checkIdInput;

function checkIdInput() {
    document.getElementById("output").style = "color: black";
    let id = document.getElementById("inputIdPUT").value;
    if (id === "") {
        document.getElementById("putError").innerHTML = "Id must be provided!!";
        document.getElementById("putError").style = "color: red";
    }
}

