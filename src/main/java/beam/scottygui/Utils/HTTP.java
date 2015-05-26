/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Utils;

import beam.scottygui.Login;
import beam.scottygui.Stores.CentralStore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class HTTP {

    JSONParser jsonParser = new JSONParser();
    List<String> EndPoints = new ArrayList();
    Long ChanID;
    String Username = "";
    String Password = "";

    public String Login(String Username, String Password, String Code) throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException, InterruptedException {
        //CookieManager customCookieManager = new CookieManager();
        //customCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        //CookieStore cookieStore = customCookieManager.getCookieStore();

        String dataIn = "";
        this.Username = Username;
        this.Password = Password;
        int attempt = 0;
        while (attempt < 6) {
            try {

                CookieManager manager = new CookieManager();
                manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(manager);
                URL url = new URL("https://beam.pro/api/v1/users/login");
                Map<String, Object> LoginParams = new LinkedHashMap<>();
                LoginParams.put("username", Username);
                LoginParams.put("password", Password);
                if (!"".equals(Code)) {
                    LoginParams.put("code", Code);
                }
                StringBuilder LoginPost = new StringBuilder();
                for (Map.Entry<String, Object> param : LoginParams.entrySet()) {
                    if (LoginPost.length() != 0) {
                        LoginPost.append('&');
                    }
                    LoginPost.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    LoginPost.append('=');
                    LoginPost.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] LoginBytes = LoginPost.toString().getBytes("UTF-8");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(LoginBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(LoginBytes);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        dataIn += inputLine;
                    }
                }
                CookieStore cookieStore = manager.getCookieStore();
                List<HttpCookie> cookieList = cookieStore.getCookies();
                for (HttpCookie t : cookieList) {
                    CentralStore.Cookie = t.getValue();
                    break;
                }
                break;
            } catch (Exception e) {
                attempt++;
                Thread.sleep(1000);
            }
        }
        ////System.out.println(dataIn);
        return dataIn;

    }

    public void CLogin(String Username, String Password) throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException, InterruptedException {
        //CookieManager customCookieManager = new CookieManager();
        //customCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        //CookieStore cookieStore = customCookieManager.getCookieStore();

        String dataIn = "";
        this.Username = Username;
        this.Password = Password;
        int attempt = 0;
        while (attempt < 6) {
            try {
                URL url = new URL("https://beam.pro/api/v1/users/login");
                Map<String, Object> LoginParams = new LinkedHashMap<>();
                LoginParams.put("username", Username);
                LoginParams.put("password", Password);
                StringBuilder LoginPost = new StringBuilder();
                for (Map.Entry<String, Object> param : LoginParams.entrySet()) {
                    if (LoginPost.length() != 0) {
                        LoginPost.append('&');
                    }
                    LoginPost.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    LoginPost.append('=');
                    LoginPost.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] LoginBytes = LoginPost.toString().getBytes("UTF-8");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(LoginBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(LoginBytes);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        dataIn += inputLine;
                    }
                }
                break;
            } catch (Exception e) {
                attempt++;
                Thread.sleep(1000);
            }
        }
        if ("".equalsIgnoreCase(dataIn)) {
            throw new IOException();
        }

    }
    JSONParser parser = new JSONParser();

    public String GetScotty(String urlString) {
        String dataIn = "";
        int TimesToTry = 0;
        while (TimesToTry < 10) {
            try {
                urlString = urlString.trim();
                ////System.out.println(urlString);
                URL url = new URL(urlString);
                // ////System.out.println("DEBUG: Getting data from " + url.toString());
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("User-Agent", "ScottyBot");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        dataIn += inputLine;
                    }
                }
                break;
            } catch (IOException | OutOfMemoryError ex) {
                TimesToTry++;
                ////System.out.println(ex.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(HTTP.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        ////System.out.println(dataIn);
        if (TimesToTry == 10) {
            JOptionPane.showMessageDialog(null, "Error communicating with server, logging out to prevent corruption.");
            CentralStore.cp.dispose();
            CentralStore.cp = null;
            Login login = new Login();
            login.setVisible(true);
        }

        JSONObject CheckForFailed = null;
        try {
            CheckForFailed = (JSONObject) parser.parse(dataIn);
        } catch (ParseException ex) {
            Logger.getLogger(HTTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (CheckForFailed.containsValue("Not Authed")) {
            ////System.out.println(CheckForFailed.toString());
            JOptionPane.showMessageDialog(null, "Issue talking with server. Did you log in elsewhere with this username? Closing program.");
            System.exit(0);
        }
        return dataIn;
    }

    public String BeamGet(String urlString) {
        String dataIn = "";
        int TimesToTry = 0;
        while (TimesToTry < 10) {
            try {
                urlString = urlString.trim();
                ////System.out.println(urlString);
                URL url = new URL(urlString);
                // ////System.out.println("DEBUG: Getting data from " + url.toString());
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Cookie", "sails.sid=" + CentralStore.Cookie);
                conn.setRequestProperty("User-Agent", "ScottyBot");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        dataIn += inputLine;
                    }
                }
                break;
            } catch (IOException | OutOfMemoryError ex) {
                TimesToTry++;
                ////System.out.println(ex.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(HTTP.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        if (TimesToTry == 10) {
            JOptionPane.showMessageDialog(null, "Error communicating with server, try again later.");
            return null;
        }
        return dataIn;
    }

    public String get(String urlString) {
        String dataIn = "";
        int TimesToTry = 0;
        while (TimesToTry < 10) {
            try {
                urlString = urlString.trim();
                ////System.out.println(urlString);
                URL url = new URL(urlString);
                // ////System.out.println("DEBUG: Getting data from " + url.toString());
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("User-Agent", "ScottyBot");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        dataIn += inputLine;
                    }
                }
                break;
            } catch (IOException | OutOfMemoryError ex) {
                TimesToTry++;
                ////System.out.println(ex.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(HTTP.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        if (TimesToTry == 10) {
            JOptionPane.showMessageDialog(null, "Error communicating with server, try again later.");
            return null;
        }
        return dataIn;
    }
}
