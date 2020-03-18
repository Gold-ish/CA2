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
            Person p = em.find(Person.class, (long) id);
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
//      Needs check for everything we cascade
//      Needs to check if the items allready exist, we don't want duplicates in the DB.
//        Address check = checkAddress(adr, em);
//        if (check != null) {
//            adr = check;
//        }
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
    /*
    public PersonDTO editPerson(PersonDTO p) {
        EntityManager em = getEntityManager();
        try {
            Person person = em.find(Person.class, p.getId());
            Address address = new Address(p.getStreet(), p.getZip(), p.getCity());
            Address check = checkAddress(address, em);
            if (check != null) {
                address = check;
            }
            em.getTransaction().begin();
            person.setFirstName(p.getFirstName());
            person.setLastName(p.getLastName());
            person.setPhone(p.getPhone());
            person.setLastEdited(new Date());
            person.setAddress(address);
            
            em.getTransaction().commit();
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }
     */
    //TODO Fejlhåndtering på getResultList.get(0)
    //TODO get person based on phone number
    public PersonDTO getPersonByPhone(String number) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Phone> q = em.createQuery("SELECT p FROM Phone p WHERE p.number = :number", Phone.class);
            q.setParameter("number", number);
            return new PersonDTO(q.getResultList().get(0).getPerson());
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
//            Query q = em.createNativeQuery("SELECT COUNT(*) "
//                    + "FROM (SELECT link_person_hobby.person_id, link_person_hobby.hobby_id, HOBBY.name "
//                    + "FROM link_person_hobby "
//                    + "JOIN HOBBY "
//                    + "ON link_person_hobby.hobby_id = HOBBY.id "
//                    + "WHERE HOBBY.name = :hobbyName) "
//                    + "AS returnValue;");
//            q.setParameter("hobbyName", hobby);

int rowCnt= Math.toIntExact((long)em.createNativeQuery("SELECT COUNT(*) "
                    + "FROM (SELECT link_person_hobby.person_id, link_person_hobby.hobby_id, HOBBY.name "
                    + "FROM link_person_hobby "
                    + "JOIN HOBBY "
                    + "ON link_person_hobby.hobby_id = HOBBY.id "
                    + "WHERE HOBBY.name = '" + hobby + "') "
                    + "AS returnValue;").getSingleResult());
            em.getTransaction().commit();
            //return ((Number) q.getSingleResult()).intValue();
            //return ((Number)q.getResultList().get(0)).intValue();
            return rowCnt;
        } finally {
            em.close();
        }
    }
    
    //Helping methods
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

}
