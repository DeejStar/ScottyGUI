/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Stores;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.ChatHandler.ChatPopOut;
import beam.scottygui.ControlPanel;
import beam.scottygui.PatchNotes;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.JSONUtil;
import beam.scottygui.Utils.SortedListModel;
import beam.scottygui.Utils.WritePropertiesFile;
import beam.scottygui.websocket.EndPoint;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javazoom.jl.player.Player;
import org.apache.http.client.CookieStore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class CS {

    public static Integer CurVer = 65;
    public static String apiLoc = "https://api.scottybot.net";
    public static Integer FolCount = 0;
    public static Integer SubCount = 0;
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
    public static String UserName = null;
    public static List<String> UniqueChatters = new ArrayList();
    public static Long TopViewers = 0L;
    public static Integer Joined = 0;
    public static Integer Left = 0;
    public static Session session = null;
    public static JSONObject GUISettings = new JSONObject();
    public static Properties prop = new Properties();
    public static Integer MsgCounter = 0;
    public static Font testfont = null;
    public static String ChatCache = "";
    public static ChatPopOut extchat = null;
    public static DefaultListModel BadWordsList = new DefaultListModel();
    public static SortedListModel ChatUserList = new SortedListModel();
    public static ComboBoxModel GameList = null;
    public static Integer LastCount = null;
    public static String Username = "";
    public static String Password = "";
    public static ExecutorService WorkerThreads = Executors.newFixedThreadPool(50);
    public static CookieStore cookie = null;
    public static String EndPoints = "";
    public static Map<String, String> isgdCache = new ConcurrentHashMap();
    public static long IsGdNextCheck = 0;
    public static boolean isLive = false;
    public static List<String> followerCache = new CopyOnWriteArrayList();
    public static AlertFrame af = null;
    public static String chanStatus = "";
    public static Runnable LiveLoad = null;
    public static Runnable FollowerQueue = null;
    public static Session llSocket = null;
    public static JSONObject chatObject = new JSONObject();
    public static JSONArray chatArray = new JSONArray();
    public static JSONArray lastFollowed = new JSONArray();
    public static Map<String, BufferedImage> EmoteMaps = new ConcurrentHashMap();
    public static Map<String, String> EmoticonPaths = new ConcurrentHashMap();
    public static String SubBadge = "";
    public static JSONObject GameListJSON = new JSONObject();
    public static List<String> GamesPreSorted = new ArrayList();
    public static Player playMP3 = null;
    public static boolean ModMode = false;
    public static Session controlSes = null;

    public static void AddModList(String Streamer, String uuid) {
        JSONArray chanList = new JSONArray();
        try {
            chanList.addAll((JSONArray) parser.parse(CS.GUISettings.get("ModList").toString()));
        } catch (ParseException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONObject streamer = new JSONObject();
        streamer.put("name", Streamer);
        streamer.put("uuid", uuid);
        chanList.add(streamer);
        CS.GUISaveSettings("ModList", chanList.toJSONString());
    }

    public static void DelModList(String Streamer) {
        JSONArray chanList = new JSONArray();
        JSONArray toDel = new JSONArray();
        try {
            chanList.addAll((JSONArray) parser.parse(CS.GUISettings.get("ModList").toString()));
        } catch (ParseException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Object t : chanList) {
            JSONObject streamer = (JSONObject) t;
            String name = (String) streamer.get("name");
            if (name.equalsIgnoreCase(Streamer)) {
                toDel.add(streamer);
            }
        }
        chanList.removeAll(toDel);
        CS.GUISaveSettings("ModList", chanList.toJSONString());
    }

    public static String getStreamerUUID(String Streamer) {
        JSONArray ChanList = null;
        try {
            ChanList = (JSONArray) parser.parse(CS.GUISettings.get("ModList").toString());
        } catch (ParseException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Object T : ChanList) {
            JSONObject chan = (JSONObject) T;
            String Name = chan.get("name").toString();
            if (Name.equalsIgnoreCase(Streamer)) {
                return chan.get("uuid").toString();
            }
        }
        return null;
    }

    public static ComboBoxModel getModList() {
        List<String> Names = new ArrayList();
        CS.GUILoadSettings();
        if (!GUISettings.containsKey("ModList")) {
            JSONArray blank = new JSONArray();
            CS.GUISaveSettings("ModList", blank.toJSONString());
        }
        JSONArray ChanList = null;
        try {
            ChanList = (JSONArray) parser.parse(CS.GUISettings.get("ModList").toString());
        } catch (ParseException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Object T : ChanList) {
            JSONObject chan = (JSONObject) T;
            String Name = chan.get("name").toString();
            Names.add(Name);
        }
        return new DefaultComboBoxModel(Names.toArray());
    }

    public static ComboBoxModel setgamelistmodel(String Query) {
        if (Query == null) {
            Collections.sort(GamesPreSorted);
            ControlPanel.StreamSet.setEnabled(true);
            ChatPopOut.StreamSet.setEnabled(true);
            return new DefaultComboBoxModel(GamesPreSorted.toArray(new String[GamesPreSorted.size()]));
        } else {
            List<String> ToNarrow = new ArrayList();
            for (Object T : GameListJSON.keySet()) {
                String Name = T.toString().toUpperCase();
                String QU = Query.toUpperCase();
                if (Name.contains(QU)) {
                    ToNarrow.add(T.toString());
                }
            }
            return new DefaultComboBoxModel(ToNarrow.toArray(new String[ToNarrow.size()]));
        }
    }

    public static void popGames() {
        String toParse = null;
        try {
            toParse = http.get(CS.apiLoc + "/getgamearray");
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            if (toParse.equalsIgnoreCase("Populating, may take some time")) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    JSONArray toLoop = (JSONArray) new JSONParser().parse(toParse);
                    for (Object t : toLoop) {
                        JSONObject Game = (JSONObject) t;
                        Long ID = (Long) Game.get("id");
                        String Name = (String) Game.get("name");
                        GamesPreSorted.add(Name);
                        GameListJSON.put(Name, ID);
                    }
                    setgamelistmodel(null);
                    break;
                } catch (ParseException ex) {
                    Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }
    }

    public static int getgameID(String game) {
        return (int) GameListJSON.get(game);
    }

    public static void addFollowerToArray(String Username) {
        if (lastFollowed.contains(Username)) {
            return;
        }
        while (lastFollowed.size() > 4) {
            lastFollowed.remove(0);
        }
        lastFollowed.add(0, Username);
        try {
            PrintWriter out = new PrintWriter("Last_5_Followers.txt");
            String toPrint = "";
            for (Object t : lastFollowed) {
                String user = t.toString();
                //System.err.println(user);
                if ("".equals(toPrint)) {
                    toPrint = user;
                } else {
                    toPrint = toPrint + ", " + user;
                }
            }
            //System.err.println(toPrint);
            out.print(toPrint);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            PrintWriter out = new PrintWriter("Last_Follower.txt");
            out.print(Username);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        CS.GUISaveSettings("followerJSON", lastFollowed.toJSONString());
    }

    public static AlertFrame getAlertFrame() {
        if (af == null) {
            af = new AlertFrame();
        }
        return af;
    }

    public static String getchanStatus(Long ChanID) throws InterruptedException {
        if ("".equalsIgnoreCase(chanStatus)) {
            chanStatus = new JSONUtil().GetStatus(ChanID);

        }
        return chanStatus;
    }

    public static String getEndPoint() {
        return EndPoints;
    }

    public static void setEndPoint(String endpoint) {

        EndPoints = endpoint;
    }

    public static String getAuthKey() {
        return AuthKey;
    }

    public static void setAuthKey(String authKey) {

        AuthKey = authKey;
    }

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
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (GUISettings.containsKey("oldVer")) {
            int oldVer = Integer.parseInt(GUISettings.get("oldVer").toString());
            if (oldVer < CS.CurVer) {
                CS.GUISaveSettings("oldVer", CS.CurVer.toString());
                new PatchNotes().setVisible(true);
            }
        } else {
            CS.GUISaveSettings("oldVer", CS.CurVer.toString());
            new PatchNotes().setVisible(true);
        }
        if (GUISettings.containsKey("followerJSON")) {
            try {
                lastFollowed.addAll((JSONArray) new JSONParser().parse(GUISettings.get("followerJSON").toString()));
            } catch (ParseException ex) {
                Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        File f = new File("Last_5_Followers.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        File t = new File("Last_Follower.txt");
        if (!t.exists()) {
            try {
                t.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void GUISaveSettings(String setting, String value) {
        if (GUISettings.containsKey(setting)) {
            GUISettings.remove(setting);
        }
        if (value != null) {
            GUISettings.put(setting, value);
            prop.setProperty("settings", GUISettings.toString());
        }
        prop.setProperty("settings", GUISettings.toString());
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            prop.store(output, null);
        } catch (IOException ex) {
            Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JSONObject GetSettings() {
        return ChanSettings;
    }

    public static void RefreshSettings() throws ParseException {
        if (ChanSettings.size() > 0) {
            ChanSettings.clear();
        }
        String toParse = http.GetScotty(CS.apiLoc + "/settings?authkey=" + AuthKey);
        //System.err.println(toParse);
        ChanSettings.putAll((JSONObject) parser.parse(toParse));
        //System.out.println(ChanSettings.toString());
    }

    public static String SendMSG(String message) {
        if (message.startsWith("/whisper".toLowerCase())) {
            String[] split = message.split(" ");
            String User = split[1].replace("@", "");
            message = message.replace("/whisper " + User + " ", "").trim();
            Iterator Users = ChatUserList.iterator();
            boolean Match = false;
            while (Users.hasNext()) {
                String toCheck = Users.next().toString();
                if (toCheck.equalsIgnoreCase(User)) {
                    Match = true;
                    break;
                }
            }
            if (Match) {
                JSONObject obj = new JSONObject();
                obj.put("type", "method");
                obj.put("method", "whisper");
                JSONArray args = new JSONArray();
                args.add(0, User);
                args.add(1, message);
                obj.put("arguments", args);
                obj.put("id", "2");
                return obj.toString();
            } else {

                JOptionPane.showMessageDialog(extchat, "Unable to find user, check spelling.");
                return "";
            }
        } else {
            JSONArray msg = new JSONArray();
            msg.add(message);
            JSONObject obj = new JSONObject();
            obj.put("type", "method");
            obj.put("method", "msg");
            obj.put("arguments", msg);
            obj.put("id", "2");
            return obj.toString();
        }
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
