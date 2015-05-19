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
import static beam.scottygui.Stores.CentralStore.cp;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        for (Object t : UserRoles) {
            Roles.add(t.toString().toUpperCase());
        }

        String ranks = null;
        String username = null;

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
            username = "<font color=\"blue\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
        }

        String MSG = "";
        JSONArray msgdata = (JSONArray) msg.get("message");
        for (Object t : msgdata) {
            JSONObject obj = (JSONObject) t;
            System.out.println(obj.toString());
            String type = obj.get("type").toString();
            if ("TEXT".equals(type.toUpperCase())) {
                MSG = MSG + " " + obj.get("data").toString();
            } else if ("EMOTICON".equalsIgnoreCase(type)) {
                String imglink = "https://beam.pro/emoticons/" + obj.get("path") + ".png";
                String ShowEmote = "<IMG SRC=\"" + imglink + "\" width=\"20\" height=\"20\" >";
                System.out.println(ShowEmote);
                MSG = MSG + ShowEmote;
            } else {
                String url = obj.get("text").toString();
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
            ChatCache = ChatCache + newline + "<b>" + username + "</b>" + "<font color=\"red\" size=\"5\">: " + MSG + "</font>";

        } else {
            ChatCache = ChatCache + newline + "<b>" + username + "</b>" + "<font color=\"white\" size=\"5\">: " + MSG + "</font>";
        }
        CentralStore.cp.ChatOutput.setText(html1 + ChatCache + html2);
        CentralStore.extchat.ExtChatOutput.setText(html1 + ChatCache + html2);

    }
}
