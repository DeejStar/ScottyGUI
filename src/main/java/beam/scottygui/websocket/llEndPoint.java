/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.cp;
import beam.scottygui.Utils.JSONUtil;
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
        if (CentralStore.FollowerQueue == null) {
            CentralStore.FollowerQueue = new FollowLooper();
            CentralStore.WorkerThreads.submit(CentralStore.FollowerQueue);
        }
        final String startedStream = "chat:" + ChanID + ":StartStreaming";
        final String stopedStream = "chat:" + ChanID + ":StopStreaming";
        final String Followed = "channel:" + ChanID + ":followed";
        final String Updated = "channel:" + ChanID + ":update";
        Followers = CentralStore.followerCache;
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
        CentralStore.llSocket = session;
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
                    CentralStore.isLive = true;

                    return;
                }
                if (stopedStream.equalsIgnoreCase(Slug)) {
                    CentralStore.isLive = false;
                    ////System.err.println(ChanID + " Stream False");
                    return;
                }
                if (Followed.equalsIgnoreCase(Slug)) {
                    //int UseFollowers = 0;
                    boolean Followed = (boolean) objData.get("following");
                    JSONObject userData = (JSONObject) objData.get("user");
                    Long followerID = (Long) userData.get("id");
                    String followerName = userData.get("username").toString();

                    if (Followed) {
                        Followers.add(followerName);
                    } else {
                        Followers.remove(followerName);
                    }

                    if (Followed && !followCache.contains(followerID)) {
                        //System.err.println(ChanID + ":Follow detected from " + followerName);
                        CentralStore.addFollowerToArray(followerName);
                        followCache.add(followerID);
                        NewFollowers.add(followerName);
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
                        CentralStore.chanStatus = newStatus;
                    }
                    if (objData.containsKey("viewersCurrent")) {
                        Long curViewers = Long.parseLong(objData.get("viewersCurrent").toString());
                        CentralStore.cp.CurViewers.setText("Current Viewers: " + curViewers);
                        if (CentralStore.TopViewers < curViewers) {
                            CentralStore.TopViewers = curViewers;
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
            AlertFrame af = CentralStore.getAlertFrame();
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
