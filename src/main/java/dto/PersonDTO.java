package dto;

import entities.Person;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rando
 */
public class PersonDTO {

    private Long id;
    private String fName;
    private String lName;
    private String street;
    private String city;
    private String zip;
    private String hobbies;
    private Set<String> phones = new HashSet();

    //Constructors
    public PersonDTO(Long id, String fName, String lName, String street, String city, String zip, String hobbies, Set<String> phones) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.hobbies = hobbies;
        this.phones = phones;
    }

    public PersonDTO(Person p) {
        this.id = p.getId();
        this.fName = p.getfName();
        this.lName = p.getlName();
        if (p.getAddress() != null) {
            this.street = p.getAddress().getStreet();
            this.city = p.getAddress().getCityinfo().getCity();
            this.zip = p.getAddress().getCityinfo().getZipCode();
        }
        this.hobbies = p.getHobbies().toString();
        this.phones = p.getPhoneNumbers();

    }

    //Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public Set<String> getPhones() {
        return phones;
    }

    public void setPhones(Set<String> phones) {
        this.phones = phones;
    }

}
