/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.ChatHandler.ChatFormatter;
import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.BeamAuthKey;
import static beam.scottygui.Stores.CentralStore.Joined;
import static beam.scottygui.Stores.CentralStore.Left;
import static beam.scottygui.Stores.CentralStore.MsgCounter;
import static beam.scottygui.Stores.CentralStore.UniqueChatters;
import static beam.scottygui.Stores.CentralStore.cp;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class EndPoint extends Endpoint {
    
    JSONParser parser = new JSONParser();
    String cusername = null;
    String channame = null;
    
    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        while (true) {
            try {
                session.getBasicRemote().sendText(CentralStore.Auth(BeamAuthKey).toString());
                CentralStore.session = session;
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
        }
        
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                try {
                    int Total = Joined + Left;
                    int Diff = Joined - Left;
                    float Retained = ((float) Diff / (float) Total) * (float) 100;
                    if (!"NaN".toLowerCase().equalsIgnoreCase(String.valueOf(Retained).toLowerCase())) {
                        cp.PercentRetainedViewers.setText(Math.round(Retained) + "% retained viewership");
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
                
                System.out.println(message);
                JSONObject msg = null;
                try {
                    msg = (JSONObject) parser.parse(message);
                } catch (ParseException ex) {
                    Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject data = null;
                String EventType = msg.get("event").toString();
                switch (EventType.toUpperCase()) {
                    case "STATS":
                        data = (JSONObject) msg.get("data");
                        Integer viewers = 0;
                        try {
                            viewers = Integer.parseInt(data.get("viewers").toString());
                        } catch (Exception e) {
                        }
                        if (viewers == null) {
                            cp.CurViewers.setText("Offline");
                        } else {
                            cp.CurViewers.setText(viewers.toString() + " viewers");
                            if (CentralStore.TopViewers < viewers) {
                                CentralStore.TopViewers = Long.parseLong(viewers.toString());
                                cp.TopViewers.setText("Top Viewer Count: " + CentralStore.TopViewers.toString());
                                System.out.println(CentralStore.TopViewers.toString());
                            }
                        }
                        break;
                    case "CHATMESSAGE":
                        MsgCounter++;
                        cp.SessionMsgCount.setText(MsgCounter.toString() + " messages this session.");
                        data = (JSONObject) msg.get("data");
                        String userid = data.get("user_id").toString();
                        String username = data.get("user_name").toString();
                        JSONArray msgdata = (JSONArray) data.get("message");
                        
                        if (!cp.ChatUserList.contains(username)) {
                            cp.ChatUserList.add(username);
                            
                        }
                        if (!UniqueChatters.contains(userid)) {
                            UniqueChatters.add(userid);
                            cp.UChatters.setText(UniqueChatters.size() + " Unique Chatters This Session.");
                        }
                        
                        ChatFormatter.FormatChat(data);
                        break;
                    case "USERJOIN":
                        Joined++;
                        data = (JSONObject) msg.get("data");
                        username = data.get("username").toString();
                        if (!cp.ChatUserList.contains(username)) {
                            cp.ChatUserList.add(username);
                            
                        }
                        break;
                    case "USERLEAVE":
                        Left++;
                        data = (JSONObject) msg.get("data");
                        username = data.get("username").toString();
                        if (cp.ChatUserList.contains(username)) {
                            cp.ChatUserList.removeElement(username);
                            
                        }
                        break;
                }
                
            }
        }
        );
    }
    
}
