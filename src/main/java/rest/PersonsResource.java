package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
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
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getRenameMeCount() {
        long count = FACADE.getRenameMeCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPersons() {
        PersonsDTO psDTO = FACADE.getAllPersons();
        return GSON.toJson(psDTO);
    }

    @GET
    @Path("id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonById(@PathParam("id") Long id) /*throws PersonNotFoundException*/ {
        PersonDTO pDTO = FACADE.getPersonById(id);
//        if (pDTO == null) {
//            throw new PersonNotFoundException("No person with provided id found");
//        }
        return GSON.toJson(pDTO);
    }

    //@Path("add")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addPerson(String person) {
        PersonDTO pCon = GSON.fromJson(person, PersonDTO.class);
        pCon = FACADE.addPerson(pCon.getEmail(), pCon.getfName(), pCon.getlName());
        return GSON.toJson(pCon);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String editPersonOnId(String personInfo, @PathParam("id") Long id) {
        PersonDTO pCon = GSON.fromJson(personInfo, PersonDTO.class);
        pCon.setId(id);
        pCon = FACADE.editPerson(pCon);
        return GSON.toJson(pCon);
    }

    @GET
    @Path("/{phoneNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonByPhoneNo(@PathParam("phoneNo") String phoneNo) {
        PersonDTO pDTO = FACADE.getPersonByPhone(phoneNo);
        return GSON.toJson(pDTO);
        //return GSON.toJson("Testing");
    }

    @GET
    @Path("/{hobby}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonsByHobby(@PathParam("hobby") String hobby) {
        PersonDTO pDTO = FACADE.getPersonsByHobby(hobby);
        return GSON.toJson(pDTO);
        //return GSON.toJson("Testing");
    }

    @GET
    @Path("/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonsByCity(@PathParam("city") String city) {
        PersonDTO pDTO = FACADE.getPersonsByCity(city);
        return GSON.toJson(pDTO);
        //return GSON.toJson("Testing");
    }

    @GET
    @Path("count/{hobby}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCountByHobby(@PathParam("city") String city) {
        PersonDTO pDTO = FACADE.getPersonCountOnHobbies(city);
        return GSON.toJson(pDTO);
        //return GSON.toJson("Testing");
    }

}
