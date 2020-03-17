package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
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
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPersons() /*throws PersonNotFoundException */ {
        // PersonDTO psDTO = FACADE.getAllPersons();
        // return GSON.toJson(psDTO);
        return GSON.toJson("TestingAll");

    }

    @GET
    @Path("phoneNumber/{phoneNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonOnId(@PathParam("phoneNumber") String phoneNumber) /*throws PersonNotFoundException */ {
        // PersonDTO pDTO = FACADE.getPerson(phoneNumber);
//        if (pDTO == null) {
//            throw new PersonNotFoundException("No person with provided phone number found");
//        }
        // return GSON.toJson(pDTO);
        return GSON.toJson("Testing");
    }

}
