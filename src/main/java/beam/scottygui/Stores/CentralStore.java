/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Stores;

import beam.scottygui.ControlPanel;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.WritePropertiesFile;
import beam.scottygui.websocket.EndPoint;
import beam.scottygui.websocket.WebSocket;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class CentralStore {

    public static HTTP http = new HTTP();
    public static Long ChanID = null;
    public static String AuthKey = null;
    public static String BeamAuthKey = null;
    public static String newline = System.lineSeparator();
    public static Boolean AddingCommand = false;
    public static ControlPanel cp = null;
    public static JSONObject ChanSettings = new JSONObject();
    public static JSONParser parser = new JSONParser();
    public static String Cookie = null;
    public static Endpoint endpoint = new EndPoint();
    public static Long UserID = null;
    public static List<String> UniqueChatters = new ArrayList();
    public static Long TopViewers = null;
    public static Integer Joined = 0;
    public static Integer Left = 0;
    public static Session session = null;
    public static JSONObject GUISettings = new JSONObject();
    public static Properties prop = new Properties();
    public static Integer MsgCounter = 0;
    public static Font testfont = null;

    public static String GUIGetSetting(String Setting) {
        return GUISettings.get(Setting).toString();
    }

    public static void GUILoadSettings() {
        File propertiesFile = new File("config.properties");
        if (!propertiesFile.exists()) {
            WritePropertiesFile.Write();
        }

        try {
            prop.load(new FileInputStream("config.properties"));
        } catch (IOException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            GUISettings.putAll((JSONObject) parser.parse(prop.getProperty("settings")));
        } catch (ParseException ex) {
            Logger.getLogger(CentralStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void GUISaveSettings(String setting, String value) {
        if (GUISettings.containsKey(setting)) {
            GUISettings.remove(setting);
        }
        GUISettings.put(setting, value);
        prop.setProperty("settings", GUISettings.toString());
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CentralStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            prop.store(output, null);
        } catch (IOException ex) {
            Logger.getLogger(CentralStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JSONObject GetSettings() {
        return ChanSettings;
    }

    public static void RefreshSettings() throws ParseException {
        if (ChanSettings.size() > 0) {
            ChanSettings.clear();
        }
        ChanSettings.putAll((JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/api/settings?authkey=" + AuthKey)));
        System.out.println(ChanSettings.toString());
    }

    public static String SendMSG(String message) {
        JSONArray msg = new JSONArray();
        msg.add(message);
        JSONObject obj = new JSONObject();
        obj.put("type", "method");
        obj.put("method", "msg");
        obj.put("arguments", msg);
        obj.put("id", "2");
        return obj.toString();
    }

    public static String GetEndPointAndAuth(Long ChanID) {
        JSONParser parser = new JSONParser();
        HTTP http = new HTTP();
        JSONObject obj = null;
        while (true) {
            try {
                obj = (JSONObject) parser.parse(http.BeamGet("https://beam.pro/api/v1/chats/" + ChanID));
                break;
            } catch (ParseException ex) {
                Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }

        JSONArray EndPoints = (JSONArray) obj.get("endpoints");
        List<String> EPToRandom = new ArrayList();
        for (Object t : EndPoints) {
            EPToRandom.add(t.toString());
        }
        Random myRandomizer = new Random();
        String EndPoint = EPToRandom.get(myRandomizer.nextInt(EPToRandom.size()));
        BeamAuthKey = obj.get("authkey").toString();
        return EndPoint;
    }

    public static JSONObject Auth(String Auth) throws InterruptedException {
        JSONArray auth = new JSONArray();
        JSONObject body = new JSONObject();
        auth.add(ChanID);
        auth.add(UserID);
        auth.add(Auth);
        body.put("type", "method");
        body.put("method", "auth");
        body.put("arguments", auth);
        body.put("id", "1");
        return body;
    }
}
