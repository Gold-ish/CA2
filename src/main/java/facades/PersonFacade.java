package facades;

import dto.CompletePersonDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import entities.*;
import exception.NoContentFoundException;
import exception.WrongPersonFormatException;
import java.lang.reflect.Field;
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

    public PersonDTO getPersonById(int id) throws NoContentFoundException {
        EntityManager em = getEntityManager();
        try {
            Person p = em.find(Person.class, (long) id);
            if (p == null) {
                throw new NoContentFoundException("No content found for this request");
            }
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }

    //TODO post person without id <- Add new person
    public PersonDTO addPerson(CompletePersonDTO completePerson) throws WrongPersonFormatException, IllegalArgumentException, IllegalAccessException {
        EntityManager em = getEntityManager();
        checkIfComplete(completePerson);
        //Create Address
        CityInfo cityInfo = new CityInfo(completePerson.getCity(), completePerson.getZip());
        Address adr = new Address(completePerson.getStreet(), completePerson.getadditionalAddressInfo(), cityInfo);
        Address checkAdr = checkAddress(adr, em);
        if (checkAdr != null) {
            adr = checkAdr;
        }
        //Create Hobby
        List<Hobby> hobbiesList = new ArrayList<>();
        if (completePerson.getHobbyName() != null) {
            hobbiesList = makeHobbyList(completePerson.getHobbyName(), completePerson.getHobbyDescription());
            List<Hobby> checkHob = checkHobby(hobbiesList, em);
            hobbiesList = checkHob;
        }
        //Create Phone
        Set<Phone> phonesSet = new HashSet<>();
        if (completePerson.getPhoneNumber() != null) {
//TODO Addtest
            phonesSet = makePhoneSet(completePerson.getPhoneNumber());
            phonesSet.iterator().next().setDescription(completePerson.getPhoneDescription());
            Set<Phone> checkPhn = checkPhone(phonesSet, em);
            if (checkPhn.iterator().next().getId() != null) {
                throw new WrongPersonFormatException("Phone number allready in use");
            } else {
                phonesSet = checkPhn;
            }
        }
        //Create Person
        Person p = new Person(completePerson.getEmail(), completePerson.getfName(), completePerson.getlName());
        phonesSet.forEach((phone) -> {
            phone.setPerson(p);
        });
        try {
            em.getTransaction().begin();
            em.persist(p);
            p.setAddress(adr);
            p.setHobbies(hobbiesList);
            p.setPhones(phonesSet);
            em.getTransaction().commit();
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }

    //TODO put person update person based on id
    public PersonDTO editPerson(CompletePersonDTO cp) throws WrongPersonFormatException, NoContentFoundException, IllegalArgumentException, IllegalAccessException {
        EntityManager em = getEntityManager();
        //Create person
        Person person = new Person(cp.getEmail(), cp.getfName(), cp.getlName());
        person.setId(cp.getId());
        //Set phone
        Set<Phone> phones = makePhoneSet(cp.getPhoneNumber());
        phones.iterator().next().setDescription(cp.getPhoneDescription());
        person.setPhones(phones);
        //Set hobby
        person.setHobbies(makeHobbyList(cp.getHobbyName(), cp.getHobbyDescription()));
        //Set address
        CityInfo cityInfo = new CityInfo(cp.getCity(), cp.getZip());
        Address adr = new Address(cp.getStreet(), cp.getadditionalAddressInfo(), cityInfo);
        person.setAddress(adr);
        try {
            em.getTransaction().begin();
            Person p = em.find(Person.class, (long) cp.getId());
            if (p == null) {
                throw new NoContentFoundException("No content found for this request");
            }
            PersonFieldsToEditCheck(cp, p);
            em.merge(p);
            em.getTransaction().commit();
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }

    private void PersonFieldsToEditCheck(CompletePersonDTO cp, Person person) throws SecurityException, IllegalAccessException, WrongPersonFormatException, IllegalArgumentException {
        Field[] fields = cp.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                if (field.get(cp) != null) {
                    switch(field.getName()){
                        case "email":
                            person.setEmail(cp.getEmail());
                            break;
                        case "fName":
                            person.setfName(cp.getfName());
                            break;
                        case "lName":
                            person.setlName(cp.getlName());
                            break;
                        case "street":
                            person.getAddress().setStreet(cp.getStreet());
                            break;
                        case "additionalAddressInfo":
                            person.getAddress().setAdditionalInfo(cp.getadditionalAddressInfo());
                            break;
                        case "city":
                            person.getAddress().getCityInfo().setCity(cp.getCity());
                            break;
                        case "zip":
                            person.getAddress().getCityInfo().setZipCode(cp.getZip());
                            break;
                        case "hobbyName":
                            person.setHobbies(makeHobbyList(cp.getHobbyName(), cp.getHobbyDescription()));
                            break;
                        case "phoneNumber":
                            person.setPhones(makePhoneSet(cp.getPhoneNumber()));
                            if(cp.getPhoneDescription() == null) {
                                throw new WrongPersonFormatException("No phone description found.");
                            } else {
                                person.getPhones().iterator().next().setDescription(cp.getPhoneDescription());
                            }
                            break;
                    }
                }
            }
        }
    }

    public PersonDTO getPersonByPhone(String number) throws NoContentFoundException {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Phone> q = em.createQuery("SELECT p FROM Phone p WHERE p.number = :number", Phone.class);
            q.setParameter("number", number);
            if (q.getResultList().size() <= 0) {
                throw new NoContentFoundException("No content found for this request");
            }
            return new PersonDTO(q.getResultList().get(0).getPerson());
        } finally {
            em.close();
        }
    }

    //TODO get JSON array of who have a certain hobby
    public PersonsDTO getAllPersonsByHobby(String hobby) throws NoContentFoundException {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p "
                    + "INNER JOIN p.hobbies Hobby "
                    + "WHERE Hobby.name = :hobby", Person.class);
            q.setParameter("hobby", hobby);
            if (q.getResultList().size() <= 0) {
                throw new NoContentFoundException("No content found for this request");
            }
            return new PersonsDTO(q.getResultList());
        } finally {
            em.close();
        }
    }

    //TODO get JSON array of persons who live in a certain city
    public PersonsDTO getPersonsFromCity(String city) throws NoContentFoundException {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p "
                    + "JOIN p.address Address "
                    + "JOIN address.cityInfo CityInfo "
                    + "WHERE CityInfo.city = :city", Person.class);
            q.setParameter("city", city);
            if (q.getResultList().size() <= 0) {
                throw new NoContentFoundException("No content found for this request");
            }
            return new PersonsDTO(q.getResultList());
        } finally {
            em.close();
        }
    }

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
    private List<Hobby> makeHobbyList(String hobbiesNames, String hobbieDescriptions) throws WrongPersonFormatException {
        List<Hobby> hobbies = new ArrayList<>();
        String[] names = hobbiesNames.split(",");
        String[] descriptions = hobbieDescriptions.split(",");
        if (names.length != descriptions.length) {
            throw new WrongPersonFormatException("Hobbies and hobbie descriptions aren't the same length");
        }
        for (int i = 0; i < descriptions.length; i++) {
            hobbies.add(new Hobby(names[i].trim(), descriptions[i].trim()));
        }
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
    private Address checkAddress(Address adr, EntityManager em) {
        try {
            TypedQuery<Address> q = em.createQuery("SELECT a FROM Address a WHERE "
                    + "a.street = :street", Address.class);
            q.setParameter("street", adr.getStreet());
            Address result = q.getSingleResult();
            return result;
        } catch (Exception e) {
            //System.out.println(e);
            return null;
        }
    }

    private List<Hobby> checkHobby(List<Hobby> hobbiesList, EntityManager em) {
        try {
            List<Hobby> result = new ArrayList();
            for (Hobby hobby : hobbiesList) {
                TypedQuery<Hobby> q = em.createQuery("SELECT a FROM Hobby a WHERE a.name = :name", Hobby.class);
                q.setParameter("name", hobby.getName());
                Hobby addToResult = new Hobby();
                if (q.getResultList().isEmpty()) {
                    addToResult = hobby;
                } else {
                    addToResult = q.getSingleResult();
                }
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
            Set<Phone> result = new HashSet();
            for (Phone phone : phonesSet) {
                TypedQuery<Phone> q = em.createQuery("SELECT a FROM Phone a WHERE a.number = :number", Phone.class);
                q.setParameter("number", phone.getNumber());
                Phone addToResult = new Phone();
                if (q.getResultList().isEmpty()) {
                    addToResult = phone;
                } else {
                    addToResult = q.getSingleResult();
                }
                result.add(addToResult);
            }
            return result;
        } catch (Exception e) {
            //System.out.println(e);
            return null;
        }
    }

    private void checkIfComplete(CompletePersonDTO completePerson) throws WrongPersonFormatException, IllegalArgumentException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Field[] fields = completePerson.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                if (field.get(completePerson) == null) {
                    sb.append(field.getName() + ", ");
                }
                //System.out.println("Variable name: " + field.getName());
                //System.out.println("Varable value: " + field.get(completePerson));
            }
        }
        if (sb.length() > 0) {
            throw new WrongPersonFormatException("Field(s); " + sb.toString().substring(0, sb.length() - 2) + " is required");
        }
    }
}
