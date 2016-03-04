/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class LiveLoad {

    public static void llSocket(Long ChanID) throws URISyntaxException, InterruptedException, IOException, ParseException {

        try {
            ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
            ClientManager Connectable = new ClientManager();
            ClientManager.ReconnectHandler reconnectHandler;
            reconnectHandler = new ClientManager.ReconnectHandler() {
                @Override
                public boolean onDisconnect(CloseReason closeReason) {
                    return true;
                }

                @Override
                public boolean onConnectFailure(Exception exception) {

                    return true;
                }

                @Override
                public long getDelay() {
                    return 1;

                }

            };

            Connectable.getProperties().put(ClientProperties.RECONNECT_HANDLER, reconnectHandler);

            //CentralStore.setReconnectCount(chanid, 0);
            URI EndPoint = new URI("wss://beam.pro/socket.io/?__sails_io_sdk_version=0.11.0&__sails_io_sdk_platform=node&__sails_io_sdk_language=javascript&EIO=3&transport=websocket");
            llEndPoint endpoint = new llEndPoint();
            endpoint.ChanID = ChanID;
            Connectable.asyncConnectToServer(endpoint, cec, EndPoint);
        } catch (Exception e) {
            //System.err.println("Interuppted");
        }
    }
}
