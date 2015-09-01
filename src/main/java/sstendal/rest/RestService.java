package sstendal.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * The REST backend
 */
@Path("/")
public class RestService {

    /**
     * curl http://localhost:8090/rest/username
     */
    @GET
    @Produces("text/plain")
    @Path("/username")
    public Response getUsername(@Context HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            return Response.status(401).build();
        } else {
            return Response.status(200).entity("You are logged in as " + username).build();
        }
    }

    /**
     * curl http://localhost:8090/rest/logout
     */
    @GET
    @Produces("text/plain")
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        request.getSession().removeAttribute("username");
        return Response.status(200).entity("You are logged out").build();
    }

}
