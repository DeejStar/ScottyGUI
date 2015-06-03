/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.ChanID;
import static beam.scottygui.Stores.CentralStore.ChatUserList;
import static beam.scottygui.Stores.CentralStore.Joined;
import static beam.scottygui.Stores.CentralStore.LastCount;
import static beam.scottygui.Stores.CentralStore.Left;
import static beam.scottygui.Stores.CentralStore.WorkerThreads;
import static beam.scottygui.Stores.CentralStore.cp;
import static beam.scottygui.Stores.CentralStore.endpoint;
import beam.scottygui.Utils.HTTP;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class WebSocket {

    public void connect(final Long ChanID) {
        CentralStore.TopViewers = Long.parseLong("0");
        ClientManager.ReconnectHandler reconnectHandler = new ClientManager.ReconnectHandler() {
            private int counter = 0;

            @Override
            public boolean onDisconnect(CloseReason closeReason) {
                if (counter < 4) {
                    try {
                        new HTTP().GetAuth();
                    } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return true;
                } else {
                    try {
                        new HTTP().GetAuth();
                    } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    new WebSocket().connect(ChanID);
                    return false;
                }
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
        while (true) {
            try {
                new HTTP().GetAuth();
                client.connectToServer(endpoint, cec, new URI(CentralStore.getEndPoint()));
                System.out.println("Logged in, do you see me naow?");
                break;
            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException | DeploymentException | URISyntaxException ex) {
                Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Runnable looper = new Looper();
        WorkerThreads.execute(looper);
    }

    public static class Looper implements Runnable {

        HTTP http = new HTTP();
        JSONParser parser = new JSONParser();

        @Override
        public void run() {
            while (true) {
                //System.out.println("Looper looped");
                JSONObject data = null;
                JSONArray UserList = null;
                try {
                    data = (JSONObject) parser.parse(http.BeamGet("https://beam.pro/api/v1/channels/" + ChanID));
                    UserList = (JSONArray) parser.parse(http.BeamGet("https://beam.pro/api/v1/chats/" + ChanID + "/users"));
                } catch (ParseException ex) {
                    Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
                ChatUserList.clear();
                for (Object t : UserList) {
                    JSONObject obj = (JSONObject) t;
                    ChatUserList.add(obj.get("userName"));
                }

                Integer viewers = 0;
                try {
                    viewers = Integer.parseInt(data.get("viewersCurrent").toString());
                } catch (Exception e) {
                }
                if (LastCount == null) {
                    LastCount = viewers;
                } else {
                    if (viewers < LastCount) {
                        int totick = LastCount - viewers;
                        while (totick > 0) {
                            CentralStore.Left++;
                            totick--;
                        }
                    }
                    if (viewers > LastCount) {
                        int totick = viewers - LastCount;
                        while (totick > 0) {
                            CentralStore.Joined++;
                            totick--;
                        }
                    }
                }
                if (viewers == null) {
                    cp.CurViewers.setText("Offline");
                } else {
                    cp.CurViewers.setText(viewers.toString() + " viewers");
                    if (CentralStore.TopViewers < viewers) {
                        CentralStore.TopViewers = Long.parseLong(viewers.toString());
                        cp.TopViewers.setText("Top Viewer Count: " + CentralStore.TopViewers.toString());
                        //System.out.println(CentralStore.TopViewers.toString());
                    }
                }
                try {
                    int Total = Joined + Left;
                    int Diff = Joined - Left;
                    float Retained = ((float) Diff / (float) Total) * (float) 100;
                    if (!"NaN".toLowerCase().equalsIgnoreCase(String.valueOf(Retained).toLowerCase())) {
                        cp.PercentRetainedViewers.setText(Math.round(Retained) + "% retained viewership");
                    }
                } catch (Exception e) {
                    //System.err.println(e);
                }
                try {
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
