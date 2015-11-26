/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.ChatHandler;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.ChatCache;
import static beam.scottygui.Stores.CentralStore.UserName;
import static beam.scottygui.Stores.CentralStore.chatArray;
import static beam.scottygui.Stores.CentralStore.chatObject;
import static beam.scottygui.Stores.CentralStore.cp;
import beam.scottygui.Utils.HTTP;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author tjhasty
 */
public class ChatFormatter {

    public static void FormatChat(JSONObject msg) {
        JSONArray UserRoles = (JSONArray) msg.get("user_roles");
        List<String> Roles = new ArrayList();
        if (!CentralStore.GUISettings.containsKey("showpurged")) {
            CentralStore.GUISaveSettings("showpurged", "false");
        }
        for (Object t : UserRoles) {
            Roles.add(t.toString().toUpperCase());
        }

        String ranks = null;
        String username = null;
        System.err.println(msg);
        for (String t : Roles) {
            if (ranks == null) {
                ranks = t;
            } else {
                ranks = ranks + ":" + t;
            }
        }

        if (Roles.contains("OWNER")) {
            username = "<font color=\"white\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
        } else if (Roles.contains("ADMIN")) {
            username = "<font color=\"red\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
        } else if (Roles.contains("DEVELOPER")) {
            username = "<font color=\"yellow\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
        } else if (Roles.contains("MOD")) {
            username = "<font color=\"green\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
        } else if (Roles.contains("PREMIUM")) {
            username = "<font color=\"purple\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
        } else if (Roles.contains("USER")) {
            username = "<font color=\"#2E64FE\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
        }

        String MSG = "";
        JSONObject msgdatapre = (JSONObject) msg.get("message");
        JSONArray msgdata = (JSONArray) msgdatapre.get("message");
        System.err.println(msgdata.toString());
        String msgID = msg.get("id").toString();

        for (Object t : msgdata) {
            JSONObject obj = (JSONObject) t;
            //System.out.println(obj.toString());
            String type = obj.get("type").toString();
            if ("TEXT".equals(type.toUpperCase())) {
                MSG = MSG + " " + obj.get("data").toString();
            } else if ("EMOTICON".equalsIgnoreCase(type)) {
                String Pack = (String) obj.get("pack");
                String imgURL = "https://beam.pro/_latest/emoticons/" + Pack + ".png";
                JSONObject Coords = (JSONObject) obj.get("coords");
                String text = obj.get("text").toString();
                int X = Integer.parseInt(Coords.get("x").toString());
                int Y = Integer.parseInt(Coords.get("y").toString());
                BufferedImage Emote = null;
                try {
                    BufferedImage fullEmotes = null;
                    if (CentralStore.EmoteMaps.containsKey(imgURL)) {
                        fullEmotes = CentralStore.EmoteMaps.get(imgURL);
                    } else {
                        fullEmotes = ImageIO.read(new URL(imgURL));
                        CentralStore.EmoteMaps.put(imgURL, fullEmotes);
                    }
                    Emote = fullEmotes.getSubimage(X, Y, 22, 22);
                } catch (IOException ex) {
                    Logger.getLogger(ChatFormatter.class.getName()).log(Level.SEVERE, null, ex);
                }
                File temp;
                String Path = null;
                if (Emote != null) {
                    if (!CentralStore.EmoticonPaths.containsKey(text)) {
                        try {
                            temp = File.createTempFile("emote", ".png");
                            ImageIO.write(Emote, "PNG", new FileOutputStream(temp));
                            Path = temp.getAbsolutePath();
                            CentralStore.EmoticonPaths.put(text, Path);
                            //System.err.println(Path);
                        } catch (IOException ex) {
                            Logger.getLogger(ChatFormatter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        Path = CentralStore.EmoticonPaths.get(text);
                    }
                }
                String ShowEmote = "<IMG SRC=file:///" + Path.replaceAll("\"", "/") + " width=\"20\" height=\"20\" >";
                //System.out.println(ShowEmote);
                MSG = MSG + ShowEmote;
            } else {
                String urltoshorten = obj.get("text").toString();
                String url = new HTTP().shortenUrl(urltoshorten);
                String URLLink = "<a href=\"" + url + "\">" + url + "</a>";
                MSG = MSG + " " + URLLink;
            }
        }
        String html1 = "<html>";
        String html2 = "</html>";
        String newline = "<br>";
        String[] SplitMSG = MSG.split(" ");
        boolean WooshMe = false;
        for (String t : SplitMSG) {
            if (t.toUpperCase().contains("@" + UserName.toUpperCase())) {
                WooshMe = true;
            }
        }
        String chatPrep = "";
        if (WooshMe) {
            if (cp.WooshMeEnabled.isSelected()) {
                new Thread("Sound Alert!") {
                    @Override
                    public void run() {
                        try {
                            try (InputStream fis = new URL("http://scottybot.x10host.com/files/ScottyGUI-Woosh.mp3").openStream()) {
                                Player playMP3 = playMP3 = new Player(fis);
                                playMP3.play();
                            }
                        } catch (JavaLayerException ex) {
                            Logger.getLogger(AlertFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(ChatFormatter.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ChatFormatter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            }

            //TestChat = "<b>" + username + "</b>" + "<font color=\"red\" size=\"5\">: " + MSG + "</font>";
            //ChatCache = ChatCache + newline + "<b>" + username + "</b>" + "<font color=\"red\" size=\"5\">: " + MSG + "</font>";
            chatPrep = "<b>" + username + "</b>" + "<font color=\"red\" size=\"5\">: " + MSG + "</font>";
        } else {
            //TestChat = "<b>" + username + "</b>" + "<font color=\"white\" size=\"5\">: " + MSG + "</font>";
            //ChatCache = ChatCache + newline + "<b>" + username + "</b>" + "<font color=\"white\" size=\"5\">: " + MSG + "</font>";
            chatPrep = "<b>" + username + "</b>" + "<font color=\"white\" size=\"5\">: " + MSG + "</font>";
        }
        chatArray.add(msgID);
        JSONObject msgPrep = new JSONObject();
        msgPrep.put("msg", chatPrep);
        msgPrep.put("purged", false);
        chatObject.put(msgID, msgPrep);
        //System.err.println(chatObject.toJSONString());
        ChatCache = "";
        for (Object t : chatArray) {
            String ID = t.toString();
            JSONObject msgObj = (JSONObject) chatObject.get(ID);
            //System.err.println(ID);
            String msgTXT = msgObj.get("msg").toString();
            if ((boolean) msgObj.get("purged")) {
                if (Boolean.parseBoolean(CentralStore.GUIGetSetting("showpurged"))) {
                    msgTXT = "<strike>" + msgTXT + "</strike>";
                    ChatCache = ChatCache + msgTXT + newline;
                    System.err.println(msgObj.toJSONString());
                }
            } else {
                ChatCache = ChatCache + msgTXT + newline;
            }
            //ChatCache = ChatCache + msgTXT + newline;
            System.err.println(msgObj.toJSONString());
        }
        //System.err.println(ChatCache);
        CentralStore.cp.ChatOutput.setText(html1 + ChatCache + html2);
        CentralStore.extchat.ExtChatOutput.setText(html1 + ChatCache + html2);

        CentralStore.cp.ChatOutput.setCaretPosition(CentralStore.cp.ChatOutput.getDocument().getLength());
        CentralStore.extchat.ExtChatOutput.setCaretPosition(CentralStore.extchat.ExtChatOutput.getDocument().getLength());
    }
}
