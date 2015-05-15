/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.GetEndPointAndAuth;
import static beam.scottygui.Stores.CentralStore.endpoint;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.ThreadPoolConfig;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer;
import org.glassfish.tyrus.container.grizzly.client.GrizzlyClientSocket;

/**
 *
 * @author tjhasty
 */
public class WebSocket {

    public void connect(Long ChanID) {
        CentralStore.TopViewers = Long.parseLong("0");
        ClientManager.ReconnectHandler reconnectHandler = new ClientManager.ReconnectHandler() {

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
                return 0;
            }
        };

        ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
        ClientManager client = ClientManager.createClient();
        client.getProperties().put(ClientProperties.RECONNECT_HANDLER, reconnectHandler);
        client.getProperties().put(GrizzlyClientContainer.SHARED_CONTAINER, true);
        client.getProperties().put(GrizzlyClientContainer.SHARED_CONTAINER_IDLE_TIMEOUT, 5);
        client.getProperties().put(GrizzlyClientSocket.SELECTOR_THREAD_POOL_CONFIG, ThreadPoolConfig.defaultConfig().setMaxPoolSize(50));
        client.getProperties().put(GrizzlyClientSocket.WORKER_THREAD_POOL_CONFIG, ThreadPoolConfig.defaultConfig().setMaxPoolSize(100));

        while (true) {
            try {
                Session session = client.connectToServer(endpoint, cec, new URI(GetEndPointAndAuth(ChanID)));
                break;
            } catch (DeploymentException | IOException | URISyntaxException ex) {
                Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
