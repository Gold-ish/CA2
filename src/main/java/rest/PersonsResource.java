package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CompletePersonDTO;
import exception.NoContentFoundException;
import exception.WrongPersonFormatException;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPersons() {
        return GSON.toJson(FACADE.getAllPersons());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonById(@PathParam("id") int id) throws NoContentFoundException{
        return GSON.toJson(FACADE.getPersonById(id));
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addPerson(String person) throws WrongPersonFormatException, IllegalArgumentException, IllegalAccessException {
        return GSON.toJson(FACADE.addPerson(GSON.fromJson(person, CompletePersonDTO.class)));
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String editPersonOnId(String personInfo, @PathParam("id") Long id) throws WrongPersonFormatException, NoContentFoundException, IllegalArgumentException, IllegalAccessException {
        CompletePersonDTO cpDto = GSON.fromJson(personInfo, CompletePersonDTO.class);
        cpDto.setId(id);
        return GSON.toJson(FACADE.editPerson(cpDto));
    }

    @GET
    @Path("phone/{phoneNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonByPhoneNo(@PathParam("phoneNo") String phoneNo) throws NoContentFoundException{
        return GSON.toJson(FACADE.getPersonByPhone(phoneNo));
    }

    @GET
    @Path("hobby/{hobby}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonsByHobby(@PathParam("hobby") String hobby) throws NoContentFoundException{
        return GSON.toJson(FACADE.getAllPersonsByHobby(hobby));
    }

    @GET
    @Path("city/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonsByCity(@PathParam("city") String city) throws NoContentFoundException{
        return GSON.toJson(FACADE.getPersonsFromCity(city));
    }

    @GET
    @Path("count/{hobby}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCountByHobby(@PathParam("hobby") String hobby) {
        return GSON.toJson(FACADE.getAmountOfPersonsWithHobby(hobby));
    }

}
