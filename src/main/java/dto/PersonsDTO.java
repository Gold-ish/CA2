package dto;

import entities.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rando
 */
public class PersonsDTO {
    private List<PersonDTO> PersonList = new ArrayList();

//Constructors
    public PersonsDTO(List<Person> listOfPersons) {
        listOfPersons.forEach((p) -> {
            PersonList.add(new PersonDTO(p));
        });
    }
    
//Getters & Setters

    public List<PersonDTO> getPersonList() {
        return PersonList;
    }
}