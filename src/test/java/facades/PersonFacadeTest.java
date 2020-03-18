package facades;

import dto.*;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1, p2, p3;
    private static Hobby hobby1, hobby2, hobby3, hobby4;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST,
                Strategy.DROP_AND_CREATE);
        facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

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
        Address address1 = new Address("KagsåKollegiet", "Lejlighed");
        Address address2 = new Address("Fredensbovej", "Hus");
        Address address3 = new Address("Kattevej", "Lejlighed");
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
    public void testGetAllPersons() {
        System.out.println("getAllPersons");
        PersonsDTO persons = facade.getAllPersons();
        assertThat(persons.getPersonsList(), everyItem(hasProperty("fName")));
        assertThat(persons.getPersonsList(), everyItem(hasProperty("lName")));
        assertThat(persons.getPersonsList(), everyItem(hasProperty("street")));
        assertThat(persons.getPersonsList(), everyItem(hasProperty("city")));
        assertThat(persons.getPersonsList(), everyItem(hasProperty("zip")));
        assertThat(persons.getPersonsList(), everyItem(hasProperty("hobbies")));
        assertThat(persons.getPersonsList(), everyItem(hasProperty("phones")));
    }

    //@Test
    public void testgetPerson(){
        assertEquals(new PersonDTO(p1), facade.getPersonById(Math.toIntExact(p1.getId())));
    }
    
    //assertfailure with exception
    public void testgetPersonFail(){
        assertEquals(new PersonDTO(p1), facade.getPersonById(-1));
    }


    /*
    @Test
    public void testAddPerson() {
        //Make Person
        Person p = new Person("jane@doe.com", "Jane", "Dow");
        
        //Make Address
        CityInfo cityInfo = new CityInfo("Copenhagen", "1700");
        Address adr = new Address("West Street", cityInfo);
        p.setAddress(adr);
        
        //Make Hobbies
        List<Hobby> hobbiesList = new ArrayList();
        //List<Person> emptyPersonList = new ArrayList();//Hacky hack method.. Almost works..
        hobbiesList.add(new Hobby("programming", ""));
        hobbiesList.add(new Hobby("dancing", ""));
        hobbiesList.get(0).setId(1L);
        hobbiesList.get(1).setId(2L);
        //hobbiesList.get(0).setPersons(emptyPersonList);
        //hobbiesList.get(1).setPersons(emptyPersonList);
        p.setHobbies(hobbiesList);
        
        //Make Phone
        Set<Phone> phoneNumber = new HashSet();
        phoneNumber.add(new Phone("45638213", "Phone Description"));
        p.setPhones(phoneNumber);
        PersonDTO expectedPersonResult = new PersonDTO(p);
        expectedPersonResult.setId(5L);
        PersonDTO actualAddPersonResult = facade.addPerson("Jane", "Doe", "jane@doe.com", "West Street", "Copenhagen", "1700", "programming, dancing", "45638213");
        
        //Der er noget underligt her i Hobby delen med Persons.. Kan ikke få testen til at mache outputtet.
        System.out.println("exp " + expectedPersonResult);
        System.out.println("act " + actualAddPersonResult);
        assertTrue(expectedPersonResult.equals(actualAddPersonResult));
    }
     */
    
    //@Test
    public void testAddPersonMissingInput() {
        
    }
    
    //@Test
    public void testAddPersonMissingInput2() {
        
    }
    
    
    @Test
    public void testEditPerson() {
        p1.setfName("John");
        p1.addHobby(new Hobby("eating", "stuffing food in your face"));
        PersonDTO pDTO = new PersonDTO(p1);
        PersonDTO editPerson = facade.editPerson(pDTO);
        System.out.println(editPerson);
    }
    
    //@Test
    public void testEditPersonWrongID() {
        
    }

//    @Test
//    public void testGetPersonByPhone() {
//        System.out.println("GetPersonByPhone");
//        PersonDTO person = facade.getPersonByPhone(p1.getPhoneNumbers().iterator().next());
//        assertEquals(new PersonDTO(p1), person);
//    }
    
    
    //@Test
    public void testGetPersonByPhoneFail() {
    }
            
    //@Test
    public void testGetPersonsByHobby() {
        System.out.println("getAllPersonsByHobby");
        PersonsDTO persons = facade.getAllPersonsByHobby("Programming");
        assertThat(persons.getPersonsList(), everyItem(hasProperty("fName")));
    }
    
    //@Test
    public void testGetPersonsByHobbyFail() {
    }

    //@Test
    public void testGetPersonsFromCity() {
        System.out.println("getPersonsFromCity");
        PersonsDTO persons = facade.getPersonsFromCity("CityName");
        assertThat(persons.getPersonsList(), everyItem(hasProperty("fName")));
//        assertTrue(persons.getPersonsList().contains(new PersonDTO(p1)));
    }
    
    //@Test
    public void testGetPersonsFromCityFail() {
    }
    
    //@Test
    public void testGetPersonCountWithHobby() {
        System.out.println("getAllPersonsByHobby");
        PersonsDTO persons = facade.getAllPersonsByHobby("Programming");
        assertThat(persons.getPersonsList(), everyItem(hasProperty("fName")));
    }
    
    //@Test
    public void testGetPersonCountWithHobbyFail() {
        System.out.println("getAllPersonsByHobby");
        PersonsDTO persons = facade.getAllPersonsByHobby("Programming");
        assertThat(persons.getPersonsList(), everyItem(hasProperty("fName")));
    }
}
