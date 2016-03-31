/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.OAuth;

import beam.scottygui.Stores.CS;
import beam.scottygui.Utils.HTTP;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class OAuthHandler {

    public static String GetAToken() {
        JSONObject OAuthStore = new JSONObject();
        OAuthStore.putAll((JSONObject) JSONValue.parse(CS.GUIGetSetting("oauth")));
        if (OAuthStore.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Unable to get OAuth token, information is missing. Please restart the GUI to reset.");
            delToken();
            System.exit(0);
        }
        try {
            String toSend = (String) OAuthStore.get("access_token");
            System.out.println("Providing access token " + toSend);
            return toSend;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                RenewToken();
            } catch (Exception ex) {
                Logger.getLogger(OAuthHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            String toSend = (String) OAuthStore.get("access_token");
            return toSend;
        }
    }

    public static Boolean hasToken() {
        JSONObject toCheck = new JSONObject();
        try {
            toCheck.putAll((JSONObject) JSONValue.parse(CS.GUIGetSetting("oauth")));
        } catch (Exception ignore) {
        }
        return !toCheck.isEmpty();
    }

    public static Long GetExpire() {
        JSONObject OAuthStore = new JSONObject();
        OAuthStore.putAll((JSONObject) JSONValue.parse(CS.GUIGetSetting("oauth")));
        if (OAuthStore.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Unable to renew OAuth token, information is missing. Please restart the GUI to reset.");
            delToken();
            System.exit(0);
        }

        if (!OAuthStore.containsKey("toexpire")) {
            Long toExpire = (((Long) OAuthStore.get("expires_in") - 30) * 1000) + System.currentTimeMillis();
            OAuthStore.put("toexpire", toExpire);
            return (Long) OAuthStore.get("toexpire");
        } else {
            return (Long) OAuthStore.get("toexpire");
        }
    }

    public static void RefreshToken() {
        JSONObject OAuthStore = new JSONObject();
        try {
            OAuthStore.putAll((JSONObject) JSONValue.parse(CS.GUIGetSetting("oauth")));
        } catch (Exception ignore) {
        }
        if (OAuthStore.isEmpty()) {
            return;
        }
        Long Expire = GetExpire();
        if (Expire < System.currentTimeMillis()) {
            try {
                RenewToken();
            } catch (Exception ex) {
                Logger.getLogger(OAuthHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void delToken() {
        JSONObject emptyObj = new JSONObject();
        CS.GUISaveSettings("oauth", emptyObj.toJSONString());
    }

    public static boolean GenToken(String Code) {
        try {
            Map<String, Object> pars = new HashMap();
            pars.put("grant_type", "authorization_code");
            pars.put("code", Code);
            pars.put("redirect_uri", "http://localhost:9090/oauthjoin");
            pars.put("client_id", CS.ClientIDOauth);
            String tokeninfo;
            try {
                tokeninfo = new HTTP().genToken(pars, CS.OAtokenAPI);
            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException ex) {
                Logger.getLogger(OAuthHandler.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Error generating token, please try again.");
                delToken();
                return false;
            }
            JSONObject obj = new JSONObject();
            String Token = null;
            try {
                obj.putAll((JSONObject) JSONValue.parse(tokeninfo));
                if (obj.containsKey("error")) {
                    JOptionPane.showMessageDialog(null, obj.get("error").toString());
                    return false;
                }
                //obj.put("code", Code);
                Long toExpire = (((Long) obj.get("expires_in") - 30) * 1000) + System.currentTimeMillis();
                obj.put("toexpire", toExpire);
                Token = (String) obj.get("access_token");
                CS.GUISaveSettings("oauth", obj.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(obj);
                JOptionPane.showMessageDialog(null, "Failed to Generate Code, Please Try Again Later.");
                return false;
            }
            JSONObject ChanInfo = new JSONObject();
            try {
                ChanInfo.putAll(HTTP.ChannelwhoAmI());
            } catch (IOException | InterruptedException | ClassNotFoundException ex) {
                Logger.getLogger(OAuthHandler.class
                .getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Failed to Generate Code, Please Try Again Later.");
            }
            Long UserID = (Long) ChanInfo.get("id");
            String UserName = (String) ChanInfo.get("username");
            CS.Username = UserName;
            CS.UserID = UserID;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String GetRToken() {
        JSONObject OAuthStore = new JSONObject();
        OAuthStore.putAll((JSONObject) JSONValue.parse(CS.GUIGetSetting("oauth")));
        if (OAuthStore.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Unable to renew OAuth token, information is missing. Please restart the GUI to reset.");
            delToken();
            System.exit(0);
        }
        return (String) OAuthStore.get("refresh_token");
    }

    public static void RenewToken() throws Exception {
        JSONObject obj = new JSONObject();
        obj.putAll((JSONObject) JSONValue.parse(CS.GUIGetSetting("oauth")));
        if (obj.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Unable to renew OAuth token, information is missing. Please restart the GUI to reset.");
            delToken();
            System.exit(0);
        }
        //String code = (Strin g) OAuthStore.get(ChanID).get("code");
        Map<String, Object> pars = new HashMap();
        pars.put("grant_type", "refresh_token");
        String Refresh = GetRToken();
        pars.put("refresh_token", Refresh);
        //pars.put("redirect_uri", "https://scottybot.net");
        pars.put("client_id", CS.ClientIDOauth);
        String tokeninfo = new HTTP().post(pars, CS.OAtokenAPI);
        //System.err.println(tokeninfo);
        obj.clear();
        try {
            obj.putAll((JSONObject) JSONValue.parse(tokeninfo));
            if (obj.containsKey("error")) {
                JOptionPane.showMessageDialog(null, "Unable to renew Oauth, closing GUI");
                delToken();
                System.exit(0);
            }
            Long toExpire = (((Long) obj.get("expires_in") - 30) * 1000) + System.currentTimeMillis();
            obj.put("toexpire", toExpire);
            CS.GUISaveSettings("oauth", obj.toJSONString());
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
}
