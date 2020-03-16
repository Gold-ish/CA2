package facades;

import dto.PersonsDTO;
import entities.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasProperty.hasProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

public class FacadeExampleTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1, p2;
    
    public FacadeExampleTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST,Strategy.DROP_AND_CREATE);
       facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
        //..
    }
    
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        //Address a1 = new Address("AdresseTest", "AddresseDescription");
        p1 = new Person("Allan@HotMail.com", "ALLAH'N", "SIMONSEN");
        p2 = new Person("Alfred@Mail.com", "Alfred", "Johansen");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        //..
    }

    @Test
    public void testGetAllPersons() {
        System.out.println("getAllPersons");
        PersonsDTO persons = facade.getAllPersons();
        assertThat(persons.getPersonList(), everyItem(hasProperty("fName")));
    }

}
