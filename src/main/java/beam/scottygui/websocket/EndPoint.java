/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.ChatHandler.ChatFormatter;
import beam.scottygui.ChatHandler.ChatPopOut;
import beam.scottygui.ControlPanel;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.BeamAuthKey;
import static beam.scottygui.Stores.CS.ChanID;
import static beam.scottygui.Stores.CS.ChatCache;
import static beam.scottygui.Stores.CS.MsgCounter;
import static beam.scottygui.Stores.CS.UniqueChatters;
import static beam.scottygui.Stores.CS.chatArray;
import static beam.scottygui.Stores.CS.chatObject;
import static beam.scottygui.Stores.CS.cp;
import beam.scottygui.Utils.JSONUtil;
import beam.scottygui.Utils.SortedListModel;
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
                String ToAuthWith = CS.Auth(BeamAuthKey).toString();
                //System.out.println(ToAuthWith);
                session.getBasicRemote().sendText(ToAuthWith);
                CS.session = session;
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
        }

        SortedListModel InitList = (SortedListModel) ControlPanel.Viewers.getModel();
        SortedListModel InitList2 = (SortedListModel) ChatPopOut.Viewers.getModel();
        List<String> LeavingList = new ArrayList();
        try {
            LeavingList.addAll(new JSONUtil().GetUserList(ChanID));
        } catch (InterruptedException ex) {
            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        InitList.clear();
        InitList.addAll(LeavingList.toArray());
        InitList2.clear();
        InitList2.addAll(LeavingList.toArray());
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
                        System.err.println(message);
                        JSONObject ChatMessage = (JSONObject) msg.get("data");
                        cp.SessionMsgCount.setText(MsgCounter.toString() + " messages this session.");
                        String userid = ChatMessage.get("user_id").toString();
                        if (!UniqueChatters.contains(userid)) {
                            UniqueChatters.add(userid);
                            cp.UChatters.setText(UniqueChatters.size() + " Unique Chatters This Session.");
                        }
                        ChatFormatter.FormatChat(ChatMessage);
                        break;
                    case "USERJOIN":
                        data = (JSONObject) msg.get("data");
                        String JoiningName = data.get("username").toString();
                        System.out.println(JoiningName + " joined the channel.");
                        SortedListModel Joining = (SortedListModel) ControlPanel.Viewers.getModel();
                        SortedListModel Joining2 = (SortedListModel) ChatPopOut.Viewers.getModel();
                        List<String> userList = new ArrayList();
                        try {
                            userList.addAll(new JSONUtil().GetUserList(ChanID));
                        } catch (InterruptedException ex) {
                            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Joining.clear();
                        Joining.addAll(userList.toArray());
                        Joining2.clear();
                        Joining2.addAll(userList.toArray());
                    case "USERLEAVE":
                        data = (JSONObject) msg.get("data");
                        String LeavingName = data.get("username").toString();
                        System.out.println(LeavingName + " left the channel.");
                        SortedListModel Leaving = (SortedListModel) ControlPanel.Viewers.getModel();
                        SortedListModel Leaving2 = (SortedListModel) ChatPopOut.Viewers.getModel();
                        List<String> LeavingList = new ArrayList();
                        try {
                            LeavingList.addAll(new JSONUtil().GetUserList(ChanID));
                        } catch (InterruptedException ex) {
                            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(EndPoint.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Leaving.clear();
                        Leaving.addAll(LeavingList.toArray());
                        Leaving2.clear();
                        Leaving2.addAll(LeavingList.toArray());
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
                                if (Boolean.parseBoolean(CS.GUIGetSetting("showpurged"))) {
                                    msgTXT = "<strike>" + msgTXT + "</strike>";
                                    ChatCache = ChatCache + msgTXT + newline;
                                    //System.err.println(msgObj.toJSONString());
                                }
                            } else {
                                ChatCache = ChatCache + msgTXT + newline;
                            }
                            //System.err.println(msgObj.toJSONString());
                        }
                        CS.cp.ChatOutput.setText(html1 + ChatCache + html2);
                        CS.extchat.ExtChatOutput.setText(html1 + ChatCache + html2);
                        //CentralStore.cp.ChatOutput.setCaretPosition(CS.cp.ChatOutput.getDocument().getLength());
                        //CentralStore.extchat.ExtChatOutput.setCaretPosition(CS.extchat.ExtChatOutput.getDocument().getLength());
                        //System.err.println(chatObject.toJSONString());
                        break;
                }

            }
        }
        );
    }

}
