package exceptionMappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ExceptionDTO;
import exception.WrongPersonFormatException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author rando
 */
@Provider
public class WrongPersonFormatExceptionMapper implements ExceptionMapper<WrongPersonFormatException> 
{
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();   
    @Override
    public Response toResponse(WrongPersonFormatException ex) {
       Logger.getLogger(WrongPersonFormatExceptionMapper.class.getName())
           .log(Level.SEVERE, null, ex);
       ExceptionDTO err = new ExceptionDTO(400,ex.getMessage());
       return Response
               .status(400)
               .entity(gson.toJson(err))
               .type(MediaType.APPLICATION_JSON)
               .build();
	}
}

