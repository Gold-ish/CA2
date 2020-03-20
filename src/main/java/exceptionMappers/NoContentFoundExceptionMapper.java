/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptionMappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ExceptionDTO;
import exception.NoContentFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author allan
 */
@Provider
public class NoContentFoundExceptionMapper implements ExceptionMapper<NoContentFoundException> 
{
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();   
    @Override
    public Response toResponse(NoContentFoundException ex) {
       Logger.getLogger(NoContentFoundExceptionMapper.class.getName())
           .log(Level.SEVERE, null, ex);
       ExceptionDTO err = new ExceptionDTO(404,ex.getMessage());
       return Response
               .status(404)
               .entity(gson.toJson(err))
               .type(MediaType.APPLICATION_JSON)
               .build();
	}
}

