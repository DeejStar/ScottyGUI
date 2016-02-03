/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.ControlPanel;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

/**
 *
 * @author tjhasty
 */
public class ScottySocket {

    public void connect() {
        ClientManager.ReconnectHandler reconnectHandler = new ClientManager.ReconnectHandler() {
            private int counter = 0;

            @Override
            public boolean onDisconnect(CloseReason closeReason) {
                ControlPanel.ControlStatus.setBackground(Color.RED);
                ControlPanel.ControlStatus.setText("Reconnecting");
                return false;
            }

            @Override
            public boolean onConnectFailure(Exception exception) {
                ControlPanel.ControlStatus.setBackground(Color.RED);
                ControlPanel.ControlStatus.setText("Reconnecting");
                return false;
            }

            @Override
            public long getDelay() {
                return 1;
            }
        };

        ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
        ClientManager client = ClientManager.createClient();
        client.getProperties().put(ClientProperties.RECONNECT_HANDLER, reconnectHandler);
        try {
            //new HTTP().GetAuth();
            client.connectToServer(new ScottyEndPoint(), cec, new URI("ws://websocket.scottybot.net:8026/websocket/control"));
        } catch (URISyntaxException | DeploymentException | IOException ex) {
            Logger.getLogger(ScottySocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
