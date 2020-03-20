package rest;

import dto.*;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonsResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1, p2, p3;
    private static Hobby hobby1, hobby2, hobby3, hobby4;
    private static CityInfo city1, city2, city3;
    private static Phone phone1, phone2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Person("carol@hoeg.iversen", "Caroline", "HoegIversen");
        p2 = new Person("tobias@anker.boldtJ", "Tobias", "AnkerB-J");
        p3 = new Person("allan@bo.simonsen", "Allan", "Simonsen");
        phone1 = new Phone("29384756", "Phone Number 1");
        phone2 = new Phone("87654321", "Phone Number 2");
        hobby1 = new Hobby("Gaming", "Wasting time in front of computer or TV");
        hobby2 = new Hobby("Swimming", "Getting wet");
        hobby3 = new Hobby("Fishing", "Getting up early and doing nothing for 5 hours");
        hobby4 = new Hobby("D&D", "Very nerdy game");
        Address address1 = new Address("KagsåKollegiet", "Lejlighed");
        Address address2 = new Address("Fredensbovej", "Hus");
        Address address3 = new Address("Kattevej", "Lejlighed");
        city1 = new CityInfo("Solrød Strand", "2680");
        city2 = new CityInfo("Søborg", "2860");
        city3 = new CityInfo("Albertslund", "2620");

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);

            //Phones
            em.persist(phone1);
            em.persist(phone2);
            p2.addPhone(phone1);
            p1.addPhone(phone2);

            //Hobby
            em.persist(hobby1);
            em.persist(hobby2);
            em.persist(hobby3);
            em.persist(hobby4);
            p2.addHobby(hobby1);
            p3.addHobby(hobby1);
            p3.addHobby(hobby2);
            p2.addHobby(hobby3);
            p1.addHobby(hobby4);

            //Address
            em.persist(address1);
            em.persist(address2);
            em.persist(address3);
            p1.setAddress(address3);
            p2.setAddress(address2);
            p3.setAddress(address1);

            //City
            em.persist(city1);
            em.persist(city2);
            em.persist(city3);
            city1.addAddress(address1);
            city2.addAddress(address2);
            city3.addAddress(address3);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/persons").then().statusCode(200);
    }

    //GET
    @Test
    public void testGetAllPersons() {
        given()
                .contentType("application/json")
                .get("/persons").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("personsList", hasSize(3));
    }

    //GET
    @Test
    public void testPersonsListContains() throws Exception {
        given()
                .contentType("application/json")
                .get("/persons").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("personsList.fName", containsInAnyOrder("Allan", "Tobias", "Caroline"))
                .body("personsList.lName", containsInAnyOrder("Simonsen", "AnkerB-J", "HoegIversen"));
    }

    //GET
    @Test
    public void testGetPersonById() {
        given()
                .contentType("application/json")
                .get("/persons/" + p3.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("fName", equalTo("Allan"))
                .body("lName", equalTo("Simonsen"));
    }

    //GET
    @Test
    public void testGetPersonByIdFail() {
        given()
                .contentType("application/json")
                .get("/persons/" + 0).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("code", equalTo(404))
                .body("message", equalTo("No content found for this request"));
    }

    //POST
    @Test
    public void testAddPerson() {
        CompletePersonDTO cpDTO = new CompletePersonDTO();
        cpDTO.setEmail("test-Email@mail.com");
        cpDTO.setfName("testFirstName");
        cpDTO.setlName("testLastName");
        cpDTO.setStreet("testStreet");
        cpDTO.setCity("testCity");
        cpDTO.setZip("852456");
        cpDTO.setadditionalAddressInfo("testHouse");
        cpDTO.setHobbyName("Programming, Fishing");
        cpDTO.setHobbyDescription("Hobby Description Test, asdf");
        cpDTO.setPhoneNumber("852134679");
        cpDTO.setPhoneDescription("Phone Description Test");
        given().contentType(ContentType.JSON)
                .body(cpDTO)
                .when()
                .post("/persons")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", notNullValue())
                .body("fName", equalTo("testFirstName"))
                .body("lName", equalTo("testLastName"))
                .body("street", equalTo("testStreet"))
                .body("city", equalTo("testCity"))
                .body("zip", equalTo("852456"))
                .body("hobbies", equalTo("Programming, Fishing"))
                .body("phones", containsInAnyOrder("852134679"));
    }

    //POST
    @Test
    public void testAddPersonMissingInput_fName() {
        CompletePersonDTO cpDTO = new CompletePersonDTO();
        cpDTO.setEmail("test-Email@mail.com");
        cpDTO.setlName("testLastName");
        cpDTO.setStreet("testStreet");
        cpDTO.setCity("testCity");
        cpDTO.setZip("852456");
        cpDTO.setadditionalAddressInfo("testHouse");
        cpDTO.setHobbyName("Programming, Fishing");
        cpDTO.setHobbyDescription("Hobby Description Test, asdf");
        cpDTO.setPhoneNumber("852134679");
        cpDTO.setPhoneDescription("Phone Description Test");
        given().contentType(ContentType.JSON)
                .body(cpDTO)
                .when()
                .post("/persons")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .body("code", equalTo(400))
                .body("message", equalTo("Field First name is required"));

    }

    @Test
    public void testAddPersonWrongHobbyInput() {
        CompletePersonDTO cpDTO = new CompletePersonDTO();
        cpDTO.setEmail("test-Email@mail.com");
        cpDTO.setfName("testFirstName");
        cpDTO.setlName("testLastName");
        cpDTO.setStreet("testStreet");
        cpDTO.setCity("testCity");
        cpDTO.setZip("852456");
        cpDTO.setadditionalAddressInfo("testHouse");
        cpDTO.setHobbyName("Programming, Fishing");
        cpDTO.setHobbyDescription("Hobby Description Test");
        cpDTO.setPhoneNumber("852134679");
        cpDTO.setPhoneDescription("Phone Description Test");
        given().contentType(ContentType.JSON)
                .body(cpDTO)
                .when()
                .post("/persons")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .body("code", equalTo(400))
                .body("message", equalTo("Hobbies and hobbie descriptions aren't the same length"));
    }

    @Test
    public void testAddPersonExistingPhone() {
        CompletePersonDTO cpDTO = new CompletePersonDTO();
        cpDTO.setEmail("test-Email@mail.com");
        cpDTO.setfName("testFirstName");
        cpDTO.setlName("testLastName");
        cpDTO.setStreet("testStreet");
        cpDTO.setCity("testCity");
        cpDTO.setZip("852456");
        cpDTO.setadditionalAddressInfo("testHouse");
        cpDTO.setHobbyName("Programming, Fishing");
        cpDTO.setHobbyDescription("Hobby Description Test, JAJAJAJAJA");
        cpDTO.setPhoneNumber("87654321");
        cpDTO.setPhoneDescription("Phone Description Test");
        given().contentType(ContentType.JSON)
                .body(cpDTO)
                .when()
                .post("/persons")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .body("code", equalTo(400))
                .body("message", equalTo("Phone number allready in use"));
    }

    //PUT
    //@Test
    public void testEditPerson() {

    }

    //PUT
    //@Test
    public void testEditPersonFail() {

    }

    //GET
    @Test
    public void testGetPersonByPhone() {
        given()
                .contentType("application/json")
                .get("/persons/phone/" + p2.getPhoneNumbers().iterator().next()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("fName", equalTo("Tobias"))
                .body("lName", equalTo("AnkerB-J"));
    }

    //GET
    @Test
    public void testGetPersonByPhoneFail() {
        given()
                .contentType("application/json")
                .get("/persons/phone/" + 00000000).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("code", equalTo(404))
                .body("message", equalTo("No content found for this request"));
    }

    //GET
    @Test
    public void testGetPersonsByHobby() {
        given()
                .contentType("application/json")
                .get("/persons/hobby/" + hobby1.getName()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("personsList.fName", containsInAnyOrder("Allan", "Tobias"))
                .body("personsList.lName", containsInAnyOrder("Simonsen", "AnkerB-J"));
    }

    //GET
    @Test
    public void testGetPersonsByHobbyFail() {
        given()
                .contentType("application/json")
                .get("/persons/hobby/HULLABULLA").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("code", equalTo(404))
                .body("message", equalTo("No content found for this request"));
    }

    //GET
    @Test
    public void testGetPersonsByCity() {
        given()
                .contentType("application/json")
                .get("/persons/city/" + city3.getCity()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("personsList.fName", containsInAnyOrder("Caroline"))
                .body("personsList.lName", containsInAnyOrder("HoegIversen"));
    }

    //GET
    @Test
    public void testGetPersonsByCityFail() {
        given()
                .contentType("application/json")
                .get("/persons/city/Langbortistan").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("code", equalTo(404))
                .body("message", equalTo("No content found for this request"));
    }

    @Test
    public void testGetAmountOfPersonsWithHobby() {
        given()
                .contentType("application/json")
                .get("/persons/count/" + hobby1.getName()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body(comparesEqualTo("2"));
    }

    @Test
    public void testGetAmountOfPersonsWithHobby_ZERO() {
        given()
                .contentType("application/json")
                .get("/persons/count/hulabula").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body(comparesEqualTo("0"));
    }

}
