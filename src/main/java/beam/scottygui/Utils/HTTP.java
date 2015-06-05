/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Utils;

import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.ChanID;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
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
    String Username = "";
    String Password = "";

    public String Login(String Username, String Password, String Code) throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException, InterruptedException {

        String url = "https://beam.pro/api/v1/users/login";

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpClientContext context = HttpClientContext.create();
        List<NameValuePair> urlParameters = new ArrayList();
        urlParameters.add(new BasicNameValuePair("username", Username));
        urlParameters.add(new BasicNameValuePair("password", Password));
        urlParameters.add(new BasicNameValuePair("code", Code));
        HttpPost request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(request, context);
        String dataIn = null;
        try {
            CookieStore cookieStore = context.getCookieStore();
            CentralStore.cookie = cookieStore;
//}
//	System.out.println("Response Code : "
//                + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            dataIn = result.toString();
            //System.err.println(dataIn);
        } finally {
            client.close();
        }

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

    public void GetAuth() throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException, SQLException {

        String AuthKey = "";
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(CentralStore.cookie);
            String url = "https://beam.pro/api/v1/chats/" + ChanID;
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request, context);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            String dataIn = result.toString();

            //Console(ChanID + " >>>>>>  " + dataIn);
            JSONObject obj = (JSONObject) jsonParser.parse(dataIn);
            AuthKey = obj.get("authkey").toString();
            CentralStore.BeamAuthKey = AuthKey;
            JSONArray endpointsArray = (JSONArray) obj.get("endpoints");

            for (Object t : endpointsArray) {
                EndPoints.add(t.toString());
            }

            Random myRandomizer = new Random();
            String EndPoint = EndPoints.get(myRandomizer.nextInt(EndPoints.size()));
            CentralStore.setEndPoint(EndPoint);
            //System.out.println(dataIn);
        }
    }
    JSONParser parser = new JSONParser();

    Map<String, String> getscottyReturnOnCrash = new HashMap();

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
            if (this.getscottyReturnOnCrash.containsKey(urlString)) {
                return this.getscottyReturnOnCrash.get(urlString);
            } else {
                return "{}";
            }
        }

        JSONObject CheckForFailed = null;
        try {
            CheckForFailed = (JSONObject) parser.parse(dataIn);
        } catch (ParseException ex) {
            Logger.getLogger(HTTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (CheckForFailed.containsValue("Not Authed")) {
            JOptionPane.showMessageDialog(null, "Issues AuthKey is invalid, did you log in elsewhere with this username? Closing program.");
            System.exit(0);
        }
        this.getscottyReturnOnCrash.put(urlString, dataIn);
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
