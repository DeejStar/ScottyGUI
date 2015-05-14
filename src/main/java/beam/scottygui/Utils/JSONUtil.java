/*
 * Copyright 2012 Andrew Bashore
 * This file is part of GeoBot.
 *
 * GeoBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * GeoBot is distributed in the hope that it will be useful
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GeoBot.  If not, see <http://www.gnu.org/licenses/>.
 */
package beam.scottygui.Utils;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtil {

    // public  void krakenStreams() throws Exception{
    // JSONParser parser = new JSONParser();
    // Object obj =
    // parser.parse(BotManager.getRemoteContent("https://api.twitch.tv/kraken/streams/mlglol"));
    //
    // JSONObject jsonObject = (JSONObject) obj;
    //
    // JSONObject stream = (JSONObject)(jsonObject.get("stream"));
    // Long viewers = (Long)stream.get("viewers");
    // System.out.println("Viewers: " + viewers);
    // }
    public String ToReturn = null;
    HTTP http = new HTTP();

    public String GetUserName(String ChanID) throws InterruptedException {
        JSONParser parser = new JSONParser();
        JSONObject result = null;
        boolean Got = false;
        while (!Got) {
            try {
                result = (JSONObject) parser.parse(http.get("https://beam.pro/api/v1/users/" + ChanID));
                Got = true;
            } catch (ParseException ex) {
                sleep(1500);
            }
        }
        String ParseUserID = (String) result.get("username");
        //String UserID = "" + ParseUserID.get("username");
        return ParseUserID;
    }

    public String GetStatus(Long ChanID) throws InterruptedException {
        JSONParser parser = new JSONParser();
        JSONObject result = null;
        boolean Got = false;
        boolean Live = false;
        while (!Got) {

            try {
                result = (JSONObject) parser.parse(http.get("https://beam.pro/api/v1/channels/" + ChanID));
                Got = true;
            } catch (ParseException ex) {
                sleep(1500);
            }
        }
        String UserID = "" + result.get("name");
        return UserID;
    }

    public String GetUserID(String ChanName) {

        JSONParser parser = new JSONParser();
        JSONObject result = null;
        boolean Got = false;
        boolean Live = false;
        try {
            result = (JSONObject) parser.parse(http.get("https://beam.pro/api/v1/channels/" + ChanName));
        } catch (ParseException ex) {
            Logger.getLogger(JSONUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        Got = true;
        JSONObject ParseUserID = (JSONObject) result.get("user");
        String UserID = "" + ParseUserID.get("id");
        return UserID;
    }

    public boolean IsLive(Long ChanID) {
        JSONParser parser = new JSONParser();
        JSONObject result = null;
        boolean Got = false;
        boolean Live = false;
        while (!Got) {
            try {
                result = (JSONObject) parser.parse(http.get("https://beam.pro/api/v1/channels/" + ChanID));
                Got = true;
            } catch (ParseException ex) {
                try {
                    sleep(1500);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(JSONUtil.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        Boolean IsLive = Boolean.parseBoolean(result.get("online").toString());

        return IsLive;
    }
}