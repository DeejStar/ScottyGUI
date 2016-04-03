/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.APIServ;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author tjhasty
 */
public class RestServ {

    public static HttpServer createHttpServer() throws IOException, URISyntaxException {
        ResourceConfig Config = new PackagesResourceConfig("beam.scottygui.APIServ");
        HttpServer toSend = HttpServerFactory.create(getCrunchifyURI(), Config);

        return toSend;
    }

    private static URI getCrunchifyURI() throws URISyntaxException {
        return new URI("http://0.0.0.0:9090/").normalize();
    }
}
