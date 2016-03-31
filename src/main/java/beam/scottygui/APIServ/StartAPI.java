/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.APIServ;

import beam.scottygui.OAuth.OAuthHandler;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Path("/")
public class StartAPI {

    @GET
    @Path("/oauthjoin")
    @Produces(MediaType.TEXT_PLAIN)
    public String OAuthJoin(@Context UriInfo info) {
        System.out.println(info.getQueryParameters());
        String Code = info.getQueryParameters().getFirst("code");
        System.out.println("Sending " + Code);
        boolean worked = OAuthHandler.GenToken(Code);
        if (worked) {
            return "Success, you may now close this page.";
        }
        return "Failed";
    }
}
