package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CompletePersonDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import exception.NoContentFoundException;
import exception.WrongPersonFormatException;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

@Path("persons")
public class PersonsResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPersons() {
        PersonsDTO psDTO = FACADE.getAllPersons();
        return GSON.toJson(psDTO);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonById(@PathParam("id") int id) throws NoContentFoundException{
        PersonDTO pDTO = FACADE.getPersonById(id);
        return GSON.toJson(pDTO);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addPerson(String person) throws WrongPersonFormatException {
        PersonDTO pCon = FACADE.addPerson(GSON.fromJson(person, CompletePersonDTO.class));
        return GSON.toJson(pCon);
    }
    
    
    
    
//    @PUT
//    @Path("/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public String editPersonOnId(String personInfo, @PathParam("id") Long id) {
//        PersonDTO pCon = GSON.fromJson(personInfo, CompletePersonDTO.class);
//        pCon.setId(id);
//        return GSON.toJson(FACADE.editPerson(pCon));
//    }

    @GET
    @Path("phone/{phoneNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonByPhoneNo(@PathParam("phoneNo") String phoneNo) throws NoContentFoundException{
        PersonDTO pDTO = FACADE.getPersonByPhone(phoneNo);
        return GSON.toJson(pDTO);
    }

    @GET
    @Path("hobby/{hobby}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonsByHobby(@PathParam("hobby") String hobby) throws NoContentFoundException{
        PersonsDTO psDTO = FACADE.getAllPersonsByHobby(hobby);
        return GSON.toJson(psDTO);
    }

    @GET
    @Path("city/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonsByCity(@PathParam("city") String city) throws NoContentFoundException{
        PersonsDTO psDTO = FACADE.getPersonsFromCity(city);
        return GSON.toJson(psDTO);
    }

    @GET
    @Path("count/{hobby}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCountByHobby(@PathParam("hobby") String hobby) {
        return GSON.toJson(FACADE.getAmountOfPersonsWithHobby(hobby));
    }

}
