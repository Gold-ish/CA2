document.getElementById("addBtn").addEventListener("click", postPerson);
document.getElementById("editBtn").addEventListener("click", editPerson);
document.getElementById("getAllBtn").addEventListener('click', (event) => {
    fetchFunction("api/persons", insertAllPersonsInTable);
});

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
    console.log(firstN);
    console.log(lastN);
    console.log(email);
    console.log(street);
    console.log(aInfo);
    console.log(city);
    console.log(zip);
    console.log(hobby);
    console.log(hDesc);
    console.log(phoneNo);
    console.log(phoneDesc);

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
            additionalInfo: aInfo,
            city: city,
            zip: zip,
            hobbyName: hobby,
            hobbyDescription: hDesc,
            phoneNumber: phoneNo,
            phoneDescription: phoneDesc

        })
    };
    fetch("api/persons", options);
}


function editPerson() {
    console.log("Hi from function");
    let inputId = document.getElementById("inputIdPUT").value;
    let inputfName = document.getElementById("inputFNamePUT").value;
    let inputlName = document.getElementById("inputLNamePUT").value;
    let inputStreet = document.getElementById("inputStreetPUT").value;
    let inputCity = document.getElementById("inputCityPUT").value;
    let inputZip = document.getElementById("inputZipPUT").value;
    let inputHobbies = document.getElementById("inputHobbyPUT").value;
    let inputPhones = document.getElementById("inputPhoneNumberPUT").value;
    let options = {
        method: "PUT",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            fName: inputfName,
            lName: inputlName,
            street: inputStreet,
            city: inputCity,
            zip: inputZip,
            hobbies: inputHobbies,
            phone: inputPhones
        })
    };

    fetch("api/persons/" + inputId, options);
}


function fetchFunction(fetchUrl, callback) {
    fetch(fetchUrl)
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            callback(data);
        });
};

function insertAllPersonsInTable(dataArray) {
    let printString = createTableFromArray(dataArray);
    document.getElementById("output").innerHTML = printString;
};

function createTableFromArray(array) {
    console.log("Ello All");
    let tableHead = "<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Street</th><th>City</th><th>Zip</th><th>Hobbies</th><th>Phones</th><th></th>";
    let htmlRows = "";
    console.log(array);


    array.forEach(element => {
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

    return "<table border='1'>" + tableHead + htmlRows + "</table>";
};



