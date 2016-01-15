/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.ControlPanel;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.cp;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.JSONUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class llEndPoint extends Endpoint {

    Long ChanID = null;
    List<String> Followers = null;
    JSONArray followCache = new JSONArray();
    List<String> NewFollowers = new CopyOnWriteArrayList();

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        Thread.currentThread().setName(ChanID + "Liveload watcher");
        if (CS.FollowerQueue == null) {
            CS.FollowerQueue = new FollowLooper();
            CS.WorkerThreads.submit(CS.FollowerQueue);
        }
        final String startedStream = "chat:" + ChanID + ":StartStreaming";
        final String stopedStream = "chat:" + ChanID + ":StopStreaming";
        final String Followed = "channel:" + ChanID + ":followed";
        final String Updated = "channel:" + ChanID + ":update";
        final String Subscribed = "channel:" + ChanID + ":subscribed";
        final String Resubscribed = "channel:" + ChanID + ":resubscribed";
        Followers = CS.followerCache;
        Followers.clear();
        try {
            Followers.addAll(new JSONUtil().GetLastFollowers(ChanID));
        } catch (InterruptedException ex) {
            Logger.getLogger(llEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        ////System.err.println(Arrays.toString(Followers.toArray()));
        String toSub = this.subToEvents(ChanID);

        ////System.err.println(toSub);
        session.getAsyncRemote().sendText(toSub);
        CS.llSocket = session;
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                if (message.equalsIgnoreCase("3")) {
                    //return;
                }
                message = message.replaceFirst("42", "");
                ////System.err.println(message);
                JSONArray input = new JSONArray();
                try {
                    input.addAll((JSONArray) new JSONParser().parse(message));
                } catch (ParseException ex) {
                    //    Logger.getLogger(llEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
                String Slug = input.get(0).toString();
                ////System.err.println("SLUG = " + Slug);
                JSONObject objData = (JSONObject) input.get(1);
                ////System.err.println("DATA = " + objData.toString());
                if (startedStream.equalsIgnoreCase(Slug)) {
                    CS.isLive = true;

                    return;
                }
                if (stopedStream.equalsIgnoreCase(Slug)) {
                    CS.isLive = false;
                    ////System.err.println(ChanID + " Stream False");
                    return;
                }
                if (Subscribed.equalsIgnoreCase(Slug) || Resubscribed.equalsIgnoreCase(Slug)) {
                    CS.SubCount++;
                    ControlPanel.SubsThisSession.setText(CS.SubCount + " subscribers this session.");
                    new Thread("PutTheadName") {
                        @Override
                        public void run() {
                            JSONObject ChanInfo = new JSONObject();
                            String toParse = null;
                            try {
                                toParse = new HTTP().get("https://beam.pro/api/v1/channels/" + CS.ChanID);
                            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                                Logger.getLogger(llEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            while (true) {
                                try {
                                    ChanInfo.putAll((JSONObject) new JSONParser().parse(toParse));
                                    break;
                                } catch (ParseException ex) {
                                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            Long Subs = (Long) ChanInfo.get("numSubscribers");
                            ControlPanel.TotSubs.setText("Total Subscribers: " + Subs);
                        }
                    }.start();
                }

                if (Followed.equalsIgnoreCase(Slug)) {
                    //int UseFollowers = 0;
                    boolean Followed = (boolean) objData.get("following");
                    JSONObject userData = (JSONObject) objData.get("user");
                    Long followerID = (Long) userData.get("id");
                    String followerName = userData.get("username").toString();
                    new Thread("PutTheadName") {
                        @Override
                        public void run() {
                            JSONObject ChanInfo = new JSONObject();
                            String toParse = null;
                            try {
                                toParse = new HTTP().get("https://beam.pro/api/v1/channels/" + CS.ChanID);
                            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                                Logger.getLogger(llEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            while (true) {
                                try {
                                    ChanInfo.putAll((JSONObject) new JSONParser().parse(toParse));
                                    break;
                                } catch (ParseException ex) {
                                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            Long Followers = (Long) ChanInfo.get("numFollowers");
                            ControlPanel.TotFollowers.setText("Total Followers: " + Followers);
                        }
                    }.start();
                    if (Followed) {
                        Followers.add(followerName);
                    } else {
                        Followers.remove(followerName);
                    }

                    if (Followed && !followCache.contains(followerID)) {
                        //System.err.println(ChanID + ":Follow detected from " + followerName);
                        CS.addFollowerToArray(followerName);
                        followCache.add(followerID);
                        NewFollowers.add(followerName);
                        CS.FolCount++;
                        ControlPanel.FolCounter.setText(CS.FolCount + " followers this session.");
                    } else {
                        if (!followCache.contains(followerID)) {
                            followCache.add(followerID);
                            return;
                        }
                    }

                }

                if (Updated.equalsIgnoreCase(Slug)) {
                    if (objData.containsKey("name")) {
                        String newStatus = objData.get("name").toString();
                        CS.chanStatus = newStatus;
                    }
                    if (objData.containsKey("viewersCurrent")) {
                        Long curViewers = Long.parseLong(objData.get("viewersCurrent").toString());
                        CS.cp.CurViewers.setText("Current Viewers: " + curViewers);
                        if (CS.TopViewers < curViewers) {
                            CS.TopViewers = curViewers;
                            cp.TopViewers.setText("Top Viewers: " + curViewers);

                        }
                    }
                }

            }
        }
        );

    }

    public class FollowLooper implements Runnable {

        @Override
        public void run() {
            System.out.println("FollowLooper Ran");
            AlertFrame af = CS.getAlertFrame();
            while (true) {
                while (!af.isVisible()) {
                    NewFollowers.clear();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(llEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (NewFollowers.isEmpty()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(llEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    for (String t : NewFollowers) {
                        while (af.PlayingAudio || af.ShowingImg) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(llEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        NewFollowers.remove(t);
                        af.BeginAlert(t);
                    }
                }
            }
        }

    }

    public String subToEvents(Long ChanID) {
        JSONArray subSend = new JSONArray();
        JSONArray slug = new JSONArray();
        JSONObject slugData = new JSONObject();
        JSONObject data = new JSONObject();
        subSend.add("put");
        slug.add("channel:" + ChanID + ":update");
        slug.add("channel:" + ChanID + ":status");
        slug.add("channel:" + ChanID + ":followed");
        slug.add("channel:" + ChanID + ":subscribed");
        slug.add("channel:" + ChanID + ":subscribed");
        slug.add("channel:" + ChanID + ":resubscribed");
        data.put("method", "put");
        data.put("headers", "");
        slugData.put("slug", slug);
        data.put("data", slugData);
        data.put("url", "/api/v1/live");
        subSend.add(data);
        String toSend = "42" + getSendNum() + subSend.toJSONString();
        return toSend;
    }

    Long send = 0L;

    public Long getSendNum() {
        Long toSend = send;
        send++;
        return toSend;
    }

}
