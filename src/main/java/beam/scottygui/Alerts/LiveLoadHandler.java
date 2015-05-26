/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Alerts;

import static beam.scottygui.Stores.CentralStore.ChanID;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;

/**
 *
 * @author tjhasty
 */
public class LiveLoadHandler {

    private static final String CHANNEL = "/foo";
    private final ClientSessionChannel.MessageListener fooListener = new FooListener();

    public void attach() {
        HttpClient httpClient = new HttpClient();
        Map<String, Object> options = new HashMap();
        Map<String, Object> data = new HashMap();
        data.put("slug", "channel:" + ChanID + ":followed");
        try {
            httpClient.start();
        } catch (Exception ex) {
            Logger.getLogger(LiveLoadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        ClientTransport transport = new LongPollingTransport(options, httpClient);
        BayeuxClient client = new BayeuxClient("https://beam.pro", transport);
        client.getChannel("/api/v1/live").publish(data, new ClientSessionChannel.MessageListener() {
            @Override
            public void onMessage(ClientSessionChannel channel, Message message) {
                //System.out.println("RECEIVED THIS >>>> " + message);
                if (message.isSuccessful()) {
                    // The message reached the server
                }
            }
        });
    }

    private static class FooListener implements ClientSessionChannel.MessageListener {

        @Override
        public void onMessage(ClientSessionChannel channel, Message message) {
            //System.out.println("GOT MESSAGE >> " + message);
        }
    }
}
