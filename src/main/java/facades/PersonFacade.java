package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Person;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    
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
    /*
    public PersonDTO addPerson(String fName, String lName, String phone, String street, String city, Integer zip) {
        EntityManager em = getEntityManager();
        Address adr = new Address(street, zip, city);
        Address check = checkAddress(adr, em);
        if (check != null) {
            adr = check;
        }
        Person p = new Person(fName, lName, phone, adr);
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
            return new PersonDTO(p);
        } finally {
            em.close();
        }
    }
    */
    
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
    
    
    
    
    
    
    //TODO get person based on phone number

    //TODO get JSON array of who have a certain hobby
    
    //TODO get JSON array of persons who live in a certain city
    
    //TODO get person count based on hobby - Needs to return a number with how many people have this hobby

    
    
    
    
    
    
    
    
    /*
    Needs check for everything we cascade
    
    private Address checkAddress(Address adr, EntityManager em) {
        try {
            TypedQuery<Address> q = em.createQuery("SELECT a FROM Address a WHERE a.city = :city AND a.street = :street AND a.zip = :zip", Address.class);
            q.setParameter("street", adr.getStreet());
            q.setParameter("city", adr.getCity());
            q.setParameter("zip", adr.getZip());
            Address result = q.getSingleResult();
            return result;
        } catch (Exception e) {
            //System.out.println(e);
            return null;
        }
    }
    
    */
    
    
}
