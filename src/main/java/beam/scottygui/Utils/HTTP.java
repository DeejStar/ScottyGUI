/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Utils;

import beam.scottygui.OAuth.OAuthHandler;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.ChanID;
import static beam.scottygui.Stores.CS.UserID;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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

    public String shortenUrl(final String longUrl) {

        if (CS.isgdCache.containsKey(longUrl)) {
            return CS.isgdCache.get(longUrl);
        } else if (CS.IsGdNextCheck < System.currentTimeMillis()) {
            String URL_SHORTENER_URL = "http://is.gd/create.php?format=simple&url=";
            try {
                URL obj = new URL(URL_SHORTENER_URL + longUrl);
                //System.out.println("URL = >> " + obj);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", "ScottyBot");
                BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                CS.isgdCache.put(longUrl, response.toString());
                return response.toString();
            } catch (IOException e) {
                CS.IsGdNextCheck = System.currentTimeMillis() + 120000;
                return longUrl;
            }
        } else {
            return longUrl;
        }
    }

    public static JSONObject ChannelwhoAmI() throws IOException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException {
        String URL = "https://beam.pro/api/v1/users/current";
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpClientContext context = HttpClientContext.create();
//context.setCookieStore(CS.GetCookie(ChanID, cusername));
            //String url = "https://beam.pro/api/v1/chats/" + ChanID;
            HttpGet request = new HttpGet(URL);
            request.addHeader("Authorization", "Bearer " + OAuthHandler.GetAToken());
            HttpResponse response = client.execute(request, context);
            BufferedReader rd = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            if (response.getStatusLine().getStatusCode() == 401) {
                if (result.toString().equalsIgnoreCase("{\"statusCode\":401,\"error\":\"Unauthorized\",\"message\":\"Missing authentication\"}")) {
                    return null;
                }
            }
            String dataIn = result.toString();
            //System.err.println(dataIn);
            JSONObject user = new JSONObject();
            try {
                user.putAll((JSONObject) JSONValue.parseWithException(dataIn));
                return user;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public String Beamput(Map<String, Object> object, String url) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException, SQLException {
        int tried = 0;
        String toSend = "";
        while (tried < 5) {
            tried++;
            try {
                System.out.println(url + " : " + object);
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    JSONObject toPut = new JSONObject(object);
                    HttpPut request = new HttpPut(url);
                    request.addHeader("Authorization", "Bearer " + OAuthHandler.GetAToken());
                    HttpEntity ent = new ByteArrayEntity(toPut.toJSONString().getBytes("UTF-8"));
                    request.setEntity(ent);
                    HttpResponse response = client.execute(request);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    //System.err.println(result.toString());
                    toSend = result.toString();
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public String put(Map<String, String> object, String url) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException, SQLException {
        int tried = 0;
        String toSend = "";
        while (tried < 5) {
            tried++;
            try {
                System.err.println(url);
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    JSONObject toPut = new JSONObject(object);
                    HttpPut request = new HttpPut(url);
                    List<NameValuePair> postpair = new ArrayList();
                    for (Map.Entry<String, String> T : object.entrySet()) {
                        postpair.add((new BasicNameValuePair(T.getKey(), T.getValue())));
                    }
                    request.setEntity(new UrlEncodedFormEntity(postpair, "UTF-8"));
                    HttpResponse response = client.execute(request);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    //System.err.println(result.toString());
                    toSend = result.toString();
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public void deleteCustRanks() throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException, SQLException {
        int tried = 0;
        while (tried < 5) {
            tried++;
            try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

                HttpDelete request = new HttpDelete(CS.apiLoc + "/rankscheme/reset?authkey=" + URLEncoder.encode(CS.AuthKey, "UTF-8"));
                request.addHeader("Authorization", "Bearer " + OAuthHandler.GetAToken());
                HttpResponse response = client.execute(request);
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }

    }

    public String genToken(Map<String, Object> object, String url) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException {
        int tried = 0;
        String toSend = "";
        while (tried < 5) {
            tried++;
            try {

                System.err.println(url);
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    JSONObject toPost = new JSONObject(object);
                    HttpPost request = new HttpPost(url);
                    HttpEntity ent = new ByteArrayEntity(toPost.toJSONString().getBytes("UTF-8"));
                    request.setEntity(ent);
                    //request.addHeader("Authorization", "Bearer " + OAuthHandler.GetAToken());
                    HttpResponse response = client.execute(request);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    //System.err.println(result.toString());
                    toSend = result.toString();
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public static String OAuthVerifyScotty(String Token) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException {
        int tried = 0;
        String toSend = "";
        while (tried < 5) {
            tried++;
            try {
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    HttpPost request = new HttpPost("https://api.scottybot.net/api/oauthverify?token=" + URLEncoder.encode(Token, "UTF-8"));
                    HttpResponse response = client.execute(request);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    //System.err.println(result.toString());
                    toSend = result.toString();
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public String BeamPost(Map<String, Object> object, String url) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException {
        int tried = 0;
        String toSend = "";
        while (tried < 5) {
            tried++;
            try {

                System.err.println(url);
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    JSONObject toPost = new JSONObject(object);
                    HttpPost request = new HttpPost(url);
                    HttpEntity ent = new ByteArrayEntity(toPost.toJSONString().getBytes("UTF-8"));
                    request.setEntity(ent);
                    request.addHeader("Authorization", "Bearer " + OAuthHandler.GetAToken());
                    HttpResponse response = client.execute(request);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    //System.err.println(result.toString());
                    toSend = result.toString();
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public String post(Map<String, String> object, String url) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException {
        int tried = 0;
        String toSend = "";
        while (tried < 5) {
            tried++;
            try {

                System.err.println(url);
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    JSONObject toPost = new JSONObject(object);
                    HttpPost request = new HttpPost(url);
                    List<NameValuePair> postpair = new ArrayList();
                    for (Map.Entry<String, String> T : object.entrySet()) {
                        postpair.add((new BasicNameValuePair(T.getKey(), T.getValue())));
                    }
                    request.setEntity(new UrlEncodedFormEntity(postpair, "UTF-8"));
                    HttpResponse response = client.execute(request);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    //System.err.println(result.toString());
                    toSend = result.toString();
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public String getCUser(String URL, CookieStore cookie) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException, SQLException {
        int tried = 0;
        String toSend = "";

        while (tried < 5) {
            tried++;
            try {
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    HttpClientContext context = HttpClientContext.create();
                    context.setCookieStore(cookie);
                    HttpGet request = new HttpGet(URL);
                    HttpResponse response = client.execute(request, context);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    toSend = result.toString();
                    //System.err.println(result.toString());
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public String get(String URL) throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException, SQLException {
        int tried = 0;
        String toSend = "";
        while (tried < 5) {
            tried++;
            try {
                try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
                    HttpClientContext context = HttpClientContext.create();
                    HttpGet request = new HttpGet(URL);
                    request.addHeader("Authorization", "Bearer " + OAuthHandler.GetAToken());
                    HttpResponse response = client.execute(request, context);
                    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    toSend = result.toString();
                    //System.err.println(result.toString());
                }
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //this.Login(ChanID);
                Thread.sleep(1000);
            }

        }
        return toSend;
    }

    public CookieStore CUserLogin(String Username, String Password, String Code) throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException, InterruptedException {
        String url = "https://beam.pro/api/v1/users/login";
        JSONObject Error = new JSONObject();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpClientContext context = HttpClientContext.create();
        List<NameValuePair> urlParameters = new ArrayList();
        urlParameters.add(new BasicNameValuePair("username", Username));
        urlParameters.add(new BasicNameValuePair("password", Password));
        urlParameters.add(new BasicNameValuePair("code", Code));
        HttpPost request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
        HttpResponse response = client.execute(request, context);
        String dataIn = null;
        CookieStore cookieStore = null;
        try {
            cookieStore = context.getCookieStore();
            BufferedReader rd = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            dataIn = result.toString();
            //System.err.println(dataIn);
            //System.err.println(response.getStatusLine().getStatusCode() + ":" + response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() != 200) {

                try {
                    Error.putAll((JSONObject) new JSONParser().parse(dataIn));
                } catch (Exception blah) {
                }
            }
////System.err.println(dataIn);
        } finally {
            client.close();
        }
        JSONObject obj = null;
        JSONObject ChanObj = new JSONObject();
        try {
            obj = (JSONObject) parser.parse(dataIn);
            UserID = Long.parseLong(obj.get("id").toString());
            ChanObj.putAll((JSONObject) parser.parse(new HTTP().get("https://beam.pro/api/v1/channels/" + Username)));

        } catch (Exception ex) {
            //Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            if (!obj.isEmpty() && obj.containsKey("message")) {
                String error = (String) obj.get("message");
                JOptionPane.showMessageDialog(null, "Error: " + error);
            } else {
                JOptionPane.showMessageDialog(null, "Login failed.");
            }
            return null;
        }

        return cookieStore;

    }

//    public String Login(String Username, String Password, String Code) throws MalformedURLException, UnsupportedEncodingException, ProtocolException, IOException, InterruptedException {
//
//        String url = "https://beam.pro/api/v1/users/login";
//        JSONObject Error = new JSONObject();
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpClientContext context = HttpClientContext.create();
//        List<NameValuePair> urlParameters = new ArrayList();
//        urlParameters.add(new BasicNameValuePair("username", Username));
//        urlParameters.add(new BasicNameValuePair("password", Password));
//        urlParameters.add(new BasicNameValuePair("code", Code));
//        HttpPost request = new HttpPost(url);
//        request.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
//        HttpResponse response = client.execute(request, context);
//        String dataIn = null;
//        try {
//            CookieStore cookieStore = context.getCookieStore();
//            CS.cookie = cookieStore;
////}
////	System.out.println("Response Code : "
////                + response.getStatusLine().getStatusCode());
//            BufferedReader rd = new BufferedReader(
//            new InputStreamReader(response.getEntity().getContent()));
//
//            StringBuilder result = new StringBuilder();
//            String line = "";
//            while ((line = rd.readLine()) != null) {
//                result.append(line);
//            }
//            dataIn = result.toString();
//            //System.err.println(dataIn);
//            //System.err.println(response.getStatusLine().getStatusCode() + ":" + response.getStatusLine().getReasonPhrase());
//            if (response.getStatusLine().getStatusCode() != 200) {
//
//                try {
//                    Error.putAll((JSONObject) new JSONParser().parse(dataIn));
//                } catch (Exception blah) {
//                }
//            }
//////System.err.println(dataIn);
//        } finally {
//            client.close();
//        }
//
//        if (Error.isEmpty()) {
//            return dataIn;
//        } else {
//            return Error.toString();
//        }
//
//    }
    public void GetAuth() throws IOException, ParseException, InterruptedException, UnsupportedEncodingException, ProtocolException, MalformedURLException, ClassNotFoundException, SQLException {

        String AuthKey = "";
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpClientContext context = HttpClientContext.create();
            String url = "https://beam.pro/api/v1/chats/" + ChanID;
            HttpGet request = new HttpGet(url);
            request.addHeader("Authorization", "Bearer " + OAuthHandler.GetAToken());
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
            CS.BeamAuthKey = AuthKey;
            JSONArray endpointsArray = (JSONArray) obj.get("endpoints");

            for (Object t : endpointsArray) {
                EndPoints.add(t.toString());
            }

            Random myRandomizer = new Random();
            String EndPoint = EndPoints.get(myRandomizer.nextInt(EndPoints.size()));
            CS.setEndPoint(EndPoint);
            System.out.println("GETAUTH >  " + dataIn);
        }
    }
    JSONParser parser = new JSONParser();

    Map<String, String> getscottyReturnOnCrash = new HashMap();

    public String GetScotty(String urlString) {
        String dataIn = "";
        //System.err.println(urlString);
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
                    System.out.println(dataIn);
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
}
