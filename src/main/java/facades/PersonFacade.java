package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public PersonsDTO getAllPersons() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Person> q = em.createNamedQuery("Person.getAll", Person.class);
            return new PersonsDTO(q.getResultList());
        } finally {
            em.close();
        }
    }

    public PersonDTO getPersonById(int id) {
        EntityManager em = getEntityManager();
        try {
            Person p = em.find(Person.class, id);
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }

    //TODO post person without id <- Add new person
    public PersonDTO addPerson(String fName, String lName, String email, String street,
            String city, String zip, String hobbies, String phones) {
        EntityManager em = getEntityManager();
        CityInfo cityInfo = new CityInfo(city, zip);
        Address adr = new Address(street, cityInfo);
        List<Hobby> hobbiesList = new ArrayList<>();
        if (hobbies != null) {
            hobbiesList = makeHobbyList(hobbies);
        }
        Set<Phone> phonesSet = new HashSet<>();
        if (phones != null) {
            phonesSet = makePhoneSet(phones);
        }
        
        Person p = new Person(email, fName, lName);
        phonesSet.forEach((phone) -> {
            phone.setPerson(p);
        });
        try {
            em.getTransaction().begin();
            em.persist(p);
            p.setAddress(adr);
            p.setHobbies(hobbiesList);
            p.setPhones(phonesSet);
            System.out.println(p.getHobbies());
            em.getTransaction().commit();
            System.out.println(p.getHobbies());
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }
    
    //TODO put person update person based on id
    public PersonDTO editPerson(PersonDTO p) {
        EntityManager em = getEntityManager();
        Person person = new Person(p);
        person.setPhones(makePhoneSet(p.getPhones()));
        person.setHobbies(makeHobbyList(p.getHobbies()));
        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }
     
    //TODO get person based on phone number
    public PersonDTO getPersonByPhone(String number) {
        EntityManager em = getEntityManager();
        try {
            Person p = em.find(Person.class, number);
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }

    //TODO get JSON array of who have a certain hobby

    //TODO get JSON array of persons who live in a certain city

    //TODO get person count based on hobby - Needs to return a number with how many people have this hobby
    public int getAmountOfPersonsWithHobby(String hobby) {// Dunno if this will work - temp
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Query q = em.createNativeQuery("SELECT COUNT(*) "
                    + "FROM (SELECT link_person_hobby.person_id, link_person_hobby.hobby_id, HOBBY.name "
                    + "FROM link_person_hobby "
                    + "JOIN HOBBY "
                    + "ON link_person_hobby.hobby_id = HOBBY.id "
                    + "WHERE HOBBY.name = :hobbyName) "
                    + "AS returnValue;");
            q.setParameter("hobbyName", hobby);
            em.getTransaction().commit();
            System.out.println(q);
            return 0;//temp
        } finally {
            em.close();
        }
    }

    
    //Needs check for everything we cascade
    private List<Hobby> makeHobbyList(String hobbiesStr) {
        List<Hobby> hobbies = new ArrayList<>();
        String[] values = hobbiesStr.split(",");
        List<String> strList = Arrays.asList(values);
        strList.stream().map((hobbyName) -> new Hobby(hobbyName.trim(), "")).forEachOrdered((hobby) -> {
            hobbies.add(hobby);
        });
        return hobbies;
    }

    private Set<Phone> makePhoneSet(String phonesStr) {
        Set<Phone> phones = new HashSet<>();
        String[] values = phonesStr.split(",");
        Set<String> strSet = new HashSet<>(Arrays.asList(values));
        strSet.stream().map((phoneNo) -> new Phone(phoneNo.trim(), "")).forEachOrdered((phone) -> {
            phones.add(phone);
        });
        return phones;
    }
    
    private Set<Phone> makePhoneSet(Set<String> strSet) {
        Set<Phone> phones = new HashSet<>();
        strSet.stream().map((phoneNo) -> new Phone(phoneNo.trim(), "")).forEachOrdered((phone) -> {
            phones.add(phone);
        });
        return phones;
    }

}
