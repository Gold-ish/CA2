package dto;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rando
 */
public class PersonDTO {

    private int id;
    private String fName;
    private String lName;
    private String street;
    private String city;
    private String zip;
    private String hobbies;
    private Set<String> phones = new HashSet();

    //Constructors
    public PersonDTO(int id, String fName, String lName, String street, String city, String zip, String hobbies, Set<String> phones) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.hobbies = hobbies;
        this.phones = phones;
    }

    //Getters & Setters
    public void setId(int id) {
        this.id = id;
    }
    
}
