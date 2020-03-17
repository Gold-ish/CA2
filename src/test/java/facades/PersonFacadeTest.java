package facades;


import dto.*;
import entities.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1, p2;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, 
                Strategy.DROP_AND_CREATE);
        facade = PersonFacade.getPersonFacade(emf);
    }

//    @AfterAll
//    public static void tearDownClass() {
//        EntityManager em = emf.createEntityManager();
//        try {
//            em.getTransaction().begin();
//            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        //Address a1 = new Address("AdresseTest", "AddresseDescription");
        p1 = new Person("Allan@HotMail.com", "ALLAH'N", "SIMONSEN");
        p2 = new Person("Alfred@Mail.com", "Alfred", "Johansen");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetAllPersons() {
        System.out.println("getAllPersons");
        PersonsDTO persons = facade.getAllPersons();
        assertThat(persons.getPersonsList(), everyItem(hasProperty("fName")));
//        assertTrue(persons.getPersonsList().contains(new PersonDTO(p1)));
    }
    
    @Test
    public void testAddPerson() {
        PersonDTO addPerson = facade.addPerson("Jane", "Doe", "jane@doe.com", "West Street", 
                "Copenhagen", "1700", "programming, dancing", "45638213");
        
    }

}
