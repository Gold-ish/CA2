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
        //Create Address
        /*
        Not working it should see if there allready is a address in the db that is the same and then reuse it.
        Not create a duplicate entry of it.
         */
        CityInfo cityInfo = new CityInfo(city, zip);
        Address adr = new Address(street, cityInfo);
        /*Address checkAdr = checkAddress(adr, em);
        if (checkAdr != null) {
            adr = checkAdr;
        }*/
        //Create Hobby
        /*
        Not working it should see if there allready is a Hobby in the db that is the same and then reuse it.
        Not create a duplicate entry of it.
         */
        List<Hobby> hobbiesList = new ArrayList<>();
        if (hobbies != null) {
            hobbiesList = makeHobbyList(hobbies);
            /*List<Hobby> checkHob = checkHobby(hobbiesList, em);
            if (checkHob != null) {
                hobbiesList = checkHob;
            }*/
        }
        //Create Phone
        /*
        If there allready is a phone with the same number then it shouldn't be able to add it again.
        2 different people can't have the same phone number.
         */
        Set<Phone> phonesSet = new HashSet<>();
        if (phones != null) {
            phonesSet = makePhoneSet(phones);
            /*Set<Phone> checkPhn = checkPhone(phonesSet, em);
            if (checkPhn != null) {
                phonesSet = checkPhn;
            }*/
        }

        //Create Person
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
    public PersonsDTO getAllPersonsByHobby(String hobby) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p "
                    + "INNER JOIN p.hobbies Hobby "
                    + "WHERE Hobby.name = :hobby", Person.class);
            q.setParameter("hobby", hobby);
            return new PersonsDTO(q.getResultList());
        } finally {
            em.close();
        }
    }
        
    //TODO get JSON array of persons who live in a certain city
    public PersonsDTO getPersonsFromCity(String city) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p "
                    + "JOIN p.address Address "
                    + "JOIN address.cityInfo CityInfo "
                    + "WHERE CityInfo.city = :city", Person.class);
            q.setParameter("city", city);
            return new PersonsDTO(q.getResultList());
        } finally {
            em.close();
        }
    }

    //TODO get person count based on hobby - Needs to return a number with how many people have this hobby  
    public int getAmountOfPersonsWithHobby(String hobby) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            int rowCnt = Math.toIntExact((long) em.createNativeQuery("SELECT COUNT(*) "
                    + "FROM (SELECT link_person_hobby.person_id, link_person_hobby.hobby_id, HOBBY.name "
                    + "FROM link_person_hobby "
                    + "JOIN HOBBY "
                    + "ON link_person_hobby.hobby_id = HOBBY.id "
                    + "WHERE HOBBY.name = '" + hobby + "') "
                    + "AS returnValue;").getSingleResult());
            em.getTransaction().commit();
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
    
    private Set<Phone> makePhoneSet(Set<String> strSet) {
        Set<Phone> phones = new HashSet<>();
        strSet.stream().map((phoneNo) -> new Phone(phoneNo.trim(), "")).forEachOrdered((phone) -> {
            phones.add(phone);
        });
        return phones;
    }

    //addCheck methods
    /*
    private Address checkAddress(Address adr, EntityManager em) {
        try {
            TypedQuery<Address> q = em.createQuery("SELECT a FROM Address a WHERE a.city = :city AND a.street = :street AND a.zip = :zip", Address.class);
            q.setParameter("street", adr.getStreet());
            q.setParameter("city", adr.getCityInfo().getCity());
            q.setParameter("zip", adr.getCityInfo().getZipCode());
            Address result = q.getSingleResult();
            return result;
        } catch (Exception e) {
            //System.out.println(e);
            return null;
        }
    }

    private List<Hobby> checkHobby(List<Hobby> hobbiesList, EntityManager em) {
        try {
            List<Hobby> result = null;
            for (Hobby hobby : hobbiesList) {
                TypedQuery<Hobby> q = em.createQuery("SELECT a FROM Hobby a WHERE a.name = :name", Hobby.class);
                q.setParameter("name", hobby.getName());
                Hobby addToResult = q.getSingleResult();
                result.add(addToResult);
            }
            return result;
        } catch (Exception e) {
            //System.out.println(e);
            return null;
        }
    }

    private Set<Phone> checkPhone(Set<Phone> phonesSet, EntityManager em) {
        try {
            Set<Phone> result = null;
            for (Phone phone : phonesSet) {
                TypedQuery<Phone> q = em.createQuery("SELECT a FROM Phone a WHERE a.number = :number", Phone.class);
                q.setParameter("number", phone.getNumber());
                Phone addToResult = q.getSingleResult();
                result.add(addToResult);
            }
            return result;
        } catch (Exception e) {
            //System.out.println(e);
            return null;
        }
    }*/
}
