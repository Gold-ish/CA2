package dto;

import entities.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author rando
 */
public class PersonsDTO {
    private List<PersonDTO> personsList = new ArrayList();

//Constructors
    public PersonsDTO(List<Person> listOfPersons) {
        listOfPersons.forEach((p) -> {
            personsList.add(new PersonDTO(p));
        });
    }
    
//Getters & Setters
    public List<PersonDTO> getPersonsList() {
        return personsList;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.personsList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersonsDTO other = (PersonsDTO) obj;
        if (!Objects.equals(this.personsList, other.personsList)) {
            return false;
        }
        return true;
    }
    
}