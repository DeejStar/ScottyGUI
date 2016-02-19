/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.ChatHandler;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.ChatCache;
import static beam.scottygui.Stores.CS.UserName;
import static beam.scottygui.Stores.CS.chatArray;
import static beam.scottygui.Stores.CS.chatObject;
import static beam.scottygui.Stores.CS.cp;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author tjhasty
 */
public class ChatFormatter {

    public static void FormatChat(JSONObject msg) {
        boolean Whisper = false;
        //System.out.println(msg);
        JSONArray UserRoles = (JSONArray) msg.get("user_roles");
        List<String> Roles = new ArrayList();
        if (!CS.GUISettings.containsKey("showpurged")) {
            CS.GUISaveSettings("showpurged", "false");
        }
        for (Object t : UserRoles) {
            Roles.add(t.toString().toUpperCase());
        }

        String ranks = null;
        String username = null;
        ////System.err.println(msg);
        for (String t : Roles) {
            if (ranks == null) {
                ranks = t;
            } else {
                ranks = ranks + ":" + t;
            }
        }

        String fontcolor = "";
        String WhisperMSG = "";
        if (Roles.contains("OWNER")) {
            username = "<font color=\"white\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
            fontcolor = "white";
        } else if (Roles.contains("ADMIN")) {
            username = "<font color=\"red\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
            fontcolor = "red";
        } else if (Roles.contains("DEVELOPER")) {
            username = "<font color=\"yellow\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
            fontcolor = "yellow";
        } else if (Roles.contains("MOD")) {
            username = "<font color=\"green\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
            fontcolor = "green";
        } else if (Roles.contains("PREMIUM")) {
            username = "<font color=\"purple\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
            fontcolor = "purple";
        } else if (Roles.contains("PRO")) {
            username = "<font color=\"#D01DD3\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
            fontcolor = "#D01DD3";
        } else if (Roles.contains("USER")) {
            username = "<font color=\"#2E64FE\" size=\"5\">" + msg.get("user_name").toString() + "</font>";
            fontcolor = "#2E64FE";
        }
        if (Roles.contains("SUBSCRIBER")) {
            username = username + " <IMG SRC=" + CS.SubBadge + ">";
        }

        String MSG = "";
        JSONObject msgdatapre = (JSONObject) msg.get("message");
        JSONArray msgdata = (JSONArray) msgdatapre.get("message");
        //System.out.println(msgdatapre);
        if (msgdatapre.containsKey("meta")) {
            JSONObject meta = (JSONObject) msgdatapre.get("meta");
            if (meta.containsKey("whisper")) {
                Whisper = (boolean) meta.get("whisper");
            } else {
                Whisper = false;
            }
        }
        if (Whisper) {
            WhisperMSG = "<i> <em> <strong> <font color=\"" + fontcolor + "\" size=\"5\">Whisper from </font> </i> </em> </strong>".toUpperCase();
        }
        //System.out.println("WHISPER : " + Whisper);
        ////System.err.println(msgdata.toString());
        String msgID = msg.get("id").toString();
        for (Object t : msgdata) {
            JSONObject obj = (JSONObject) t;
            //System.out.println(obj.toString());
            String type = obj.get("type").toString();
            if ("TEXT".equals(type.toUpperCase())) {
                MSG = MSG + " " + StringEscapeUtils.escapeHtml(obj.get("data").toString());
            } else if ("EMOTICON".equalsIgnoreCase(type)) {
                String Pack = (String) obj.get("pack");
                UrlValidator urlValidator = new UrlValidator();
                String imgURL = "";
                if (urlValidator.isValid(Pack)) {
                    imgURL = Pack;
                } else {
                    imgURL = "https://beam.pro/_latest/emoticons/" + Pack + ".png";
                }
                JSONObject Coords = (JSONObject) obj.get("coords");
                String text = obj.get("text").toString();
                int X = Integer.parseInt(Coords.get("x").toString());
                int Y = Integer.parseInt(Coords.get("y").toString());
                BufferedImage Emote = null;
                try {
                    BufferedImage fullEmotes = null;
                    if (CS.EmoteMaps.containsKey(imgURL)) {
                        fullEmotes = CS.EmoteMaps.get(imgURL);
                    } else {
                        fullEmotes = ImageIO.read(new URL(imgURL).openStream());
                        CS.EmoteMaps.put(imgURL, fullEmotes);
                    }
                    Emote = fullEmotes.getSubimage(X, Y, 22, 22);
                } catch (IOException ex) {
                    Logger.getLogger(ChatFormatter.class.getName()).log(Level.SEVERE, null, ex);
                }
                File temp;
                String Path = null;
                if (Emote != null) {
                    if (!CS.EmoticonPaths.containsKey(text)) {
                        try {
                            temp = File.createTempFile("emote", ".png");
                            ImageIO.write(Emote, "PNG", new FileOutputStream(temp));
                            Path = temp.getAbsolutePath();
                            CS.EmoticonPaths.put(text, Path);
                            ////System.err.println(Path);
                        } catch (IOException ex) {
                            Logger.getLogger(ChatFormatter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        Path = CS.EmoticonPaths.get(text);
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
        String html1 = "<html> <body bgcolor=\"black\">";
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
                            try (InputStream fis = getClass().getResourceAsStream("assets/Woosh.mp3")) {
                                Player playMP3 = playMP3 = new Player(fis);
                                playMP3.play();
                                playMP3.close();
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
        chatPrep = WhisperMSG + chatPrep;

        String toRem = "";
        while (chatArray.size() > 100) {
            toRem = (String) chatArray.get(0);
            chatArray.remove(0);
            chatObject.remove(toRem);
        }
        chatArray.add(msgID);
        JSONObject msgPrep = new JSONObject();
        msgPrep.put("msg", chatPrep);
        msgPrep.put("purged", false);
        chatObject.put(msgID, msgPrep);
        ////System.err.println(chatObject.toJSONString());
        ChatCache = "";
        for (Object t : chatArray) {
            String ID = t.toString();
            JSONObject msgObj = (JSONObject) chatObject.get(ID);
            ////System.err.println(ID);
            String msgTXT = msgObj.get("msg").toString();

            if ((boolean) msgObj.get("purged")) {
                if (Boolean.parseBoolean(CS.GUIGetSetting("showpurged"))) {
                    try {
                        msgTXT = "<strike>" + msgTXT + "</strike>";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ChatCache = ChatCache + msgTXT + newline;
                    //System.err.println(msgObj.toJSONString());
                }
            } else {
                ChatCache = ChatCache + msgTXT + newline;
            }
            //ChatCache = ChatCache + msgTXT + newline;
            ////System.err.println(msgObj.toJSONString());
        }
        ////System.err.println(ChatCache);
        CS.cp.ChatOutput.setText(html1 + ChatCache + html2);
        CS.extchat.ExtChatOutput.setText(html1 + ChatCache + html2);

        CS.cp.ChatOutput.setCaretPosition(CS.cp.ChatOutput.getDocument().getLength());
        CS.extchat.ExtChatOutput.setCaretPosition(CS.extchat.ExtChatOutput.getDocument().getLength());
    }
}
