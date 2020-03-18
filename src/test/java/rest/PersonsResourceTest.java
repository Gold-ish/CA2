package rest;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
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
        Phone phone1 = new Phone("29384756", "Phone Number 1");
        Phone phone2 = new Phone("87654321", "Phone Number 2");
        hobby1 = new Hobby("Gaming", "Wasting time in front of computer or TV");
        hobby2 = new Hobby("Swimming", "Getting wet");
        hobby3 = new Hobby("Fishing", "Getting up early and doing nothing for 5 hours");
        hobby4 = new Hobby("D&D", "Very nerdy game");
        Address address1  = new Address("KagsåKollegiet", "Lejlighed");
        Address address2  = new Address("Fredensbovej", "Hus");
        Address address3  = new Address("Kattevej", "Lejlighed");
        CityInfo city1 = new CityInfo("Solrød Strand", "2680");
        CityInfo city2 = new CityInfo("Søborg", "2860");
        CityInfo city3 = new CityInfo("Albertslund", "2620");
        
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
            
            em.persist(hobby1);
            em.persist(hobby2);
            em.persist(hobby3);
            em.persist(hobby4);
            p2.addHobby(hobby1);
            p3.addHobby(hobby1);
            p3.addHobby(hobby2);
            p2.addHobby(hobby3);
            p1.addHobby(hobby4);
            
            em.persist(address1);
            em.persist(address2);
            em.persist(address3);
            p1.setAddress(address3);
            p2.setAddress(address2);
            p3.setAddress(address1);
            
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

    @Test
    public void testGetAllPersons() {
        given()
                .contentType("application/json")
                .get("/persons").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("personsList", hasSize(3));
    }

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
