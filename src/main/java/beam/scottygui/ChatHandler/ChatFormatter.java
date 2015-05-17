/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.ChatHandler;

import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.ChatCache;
import java.util.ArrayList;
import java.util.List;
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
            } else {
                MSG = MSG + " " + obj.get("text");
            }
        }
        String html1 = "<html>";
        String html2 = "</html>";
        String newline = "<br>";
        ChatCache = ChatCache + newline + "<b>" + username + "</b>" + "<font color=\"white\" size=\"5\">: " + MSG + "</font>";
        CentralStore.cp.ChatOutput.setText(html1 + ChatCache + html2);
    }
}
