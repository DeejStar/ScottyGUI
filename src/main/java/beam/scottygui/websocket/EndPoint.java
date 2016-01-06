/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.ChatHandler.ChatFormatter;
import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.BeamAuthKey;
import static beam.scottygui.Stores.CentralStore.ChanID;
import static beam.scottygui.Stores.CentralStore.ChatCache;
import static beam.scottygui.Stores.CentralStore.ChatUserList;
import static beam.scottygui.Stores.CentralStore.MsgCounter;
import static beam.scottygui.Stores.CentralStore.UniqueChatters;
import static beam.scottygui.Stores.CentralStore.chatArray;
import static beam.scottygui.Stores.CentralStore.chatObject;
import static beam.scottygui.Stores.CentralStore.cp;
import beam.scottygui.Utils.JSONUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
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
    String html1 = "<html>";
    String html2 = "</html>";
    String newline = "<br>";

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        while (true) {
            try {
                String ToAuthWith = CentralStore.Auth(BeamAuthKey).toString();
                //System.out.println(ToAuthWith);
                session.getBasicRemote().sendText(ToAuthWith);
                CentralStore.session = session;
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
        }

        List<String> userList = new ArrayList();
        try {
            userList.addAll(new JSONUtil().GetUserList(ChanID));
        } catch (InterruptedException ex) {
            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        ChatUserList.clear();
        ChatUserList.addAll(userList.toArray());

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {

                //System.out.println(message);
                JSONObject msg = null;
                try {
                    msg = (JSONObject) parser.parse(message);
                } catch (ParseException ex) {
                    Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject data = null;
                String EventType = msg.get("event").toString().toUpperCase();
                //System.err.println(EventType);
                switch (EventType) {
                    case "STATS":
                        break;
                    case "CHATMESSAGE":
                        MsgCounter++;
                        JSONObject ChatMessage = (JSONObject) msg.get("data");
                        cp.SessionMsgCount.setText(MsgCounter.toString() + " messages this session.");
                        String userid = ChatMessage.get("user_id").toString();
                        String username = ChatMessage.get("user_name").toString();

                        if (!ChatUserList.contains(username)) {
                            ChatUserList.add(username);

                        }
                        if (!UniqueChatters.contains(userid)) {
                            UniqueChatters.add(userid);
                            cp.UChatters.setText(UniqueChatters.size() + " Unique Chatters This Session.");
                        }

                        ChatFormatter.FormatChat(ChatMessage);
                        break;
                    case "USERJOIN":
                        data = (JSONObject) msg.get("data");
                        username = data.get("username").toString();
                        //System.out.println(username + " joined the channel.");
                        if (!ChatUserList.contains(username)) {
                            ChatUserList.add(username);
                        }
                        break;
                    case "USERLEAVE":
                        data = (JSONObject) msg.get("data");
                        username = data.get("username").toString();
                        //System.out.println(username + " left the channel.");
                        if (ChatUserList.contains(username)) {
                            ChatUserList.removeElement(username);
                        }
                        break;
                    case "DELETEMESSAGE":
                        data = (JSONObject) msg.get("data");
                        String ID = data.get("id").toString();
                        JSONObject toPurge = (JSONObject) chatObject.get(ID);
                        toPurge.put("purged", true);
                        chatObject.put(ID, toPurge);
                        ChatCache = "";
                        for (Object t : chatArray) {
                            String msgID = t.toString();
                            JSONObject msgObj = (JSONObject) chatObject.get(msgID);
                            ////System.err.println(ID);
                            String msgTXT = msgObj.get("msg").toString();
                            if ((boolean) msgObj.get("purged")) {
                                if (Boolean.parseBoolean(CentralStore.GUIGetSetting("showpurged"))) {
                                    msgTXT = "<strike>" + msgTXT + "</strike>";
                                    ChatCache = ChatCache + msgTXT + newline;
                                    //System.err.println(msgObj.toJSONString());
                                }
                            } else {
                                ChatCache = ChatCache + msgTXT + newline;
                            }
                            //System.err.println(msgObj.toJSONString());
                        }
                        CentralStore.cp.ChatOutput.setText(html1 + ChatCache + html2);
                        CentralStore.extchat.ExtChatOutput.setText(html1 + ChatCache + html2);
                        //CentralStore.cp.ChatOutput.setCaretPosition(CentralStore.cp.ChatOutput.getDocument().getLength());
                        //CentralStore.extchat.ExtChatOutput.setCaretPosition(CentralStore.extchat.ExtChatOutput.getDocument().getLength());
                        //System.err.println(chatObject.toJSONString());
                        break;
                }

            }
        }
        );
    }

}
