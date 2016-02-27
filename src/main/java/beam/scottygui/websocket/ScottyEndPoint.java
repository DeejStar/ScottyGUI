/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.websocket;

import beam.scottygui.ControlPanel;
import static beam.scottygui.ControlPanel.PointsTable;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.playMP3;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.glassfish.tyrus.core.TyrusSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class ScottyEndPoint extends Endpoint {

    JSONParser parser = new JSONParser();

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        if (CS.controlSes != null) {
            if (CS.controlSes.isOpen() && CS.controlSes != session) {
                try {
                    session.close();
                } catch (IOException ex) {
                    Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        JSONObject toAuth = new JSONObject();
        toAuth.put("authkey", CS.AuthKey);
        session.getAsyncRemote().sendText(toAuth.toJSONString());
        System.out.println("Scotty control socket established");
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                try {
                    System.out.println("Scotty control socket: " + message);
                    final JSONObject msgobj = (JSONObject) parser.parse(message);
                    try {
                        ControlPanel.ControlStatus.setBackground(Color.GREEN);
                        ControlPanel.ControlStatus.setText("");
                    } catch (Exception e) {

                    }
                    CS.controlSes = session;
                    if (msgobj.containsKey("ping_in_milli")) {
                        Long Ping = (Long) msgobj.get("ping_in_milli");
                        TyrusSession tses = (TyrusSession) session;
                        tses.setHeartbeatInterval(Ping);
                    }
                    try {
                        if (msgobj.containsKey("settings")) {
                            //System.err.println("SETTINGS TO PARSE " + msgobj);
                            String toParse = msgobj.get("settings").toString();
                            // System.err.println("TO PARSE " + toParse);
                            CS.RefreshSettings((JSONObject) new JSONParser().parse(toParse));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (msgobj.containsKey("wsstatus")) {
                        if (msgobj.get("wsstatus").toString().equals("1")) {
                            ControlPanel.wssocket.setBackground(Color.GREEN);
                            ControlPanel.wssocket.setText("");

                        } else {
                            ControlPanel.wssocket.setBackground(Color.RED);
                            ControlPanel.wssocket.setText("Rejoining");
                        }
                    }
                    if (msgobj.containsKey("llstatus")) {
                        if (msgobj.get("llstatus").toString().equals("1")) {
                            ControlPanel.llsocket.setBackground(Color.GREEN);
                            ControlPanel.llsocket.setText("");

                        } else {
                            ControlPanel.llsocket.setBackground(Color.RED);
                            ControlPanel.llsocket.setText("Rejoining");
                        }
                    }
                    new Thread("PutTheadName") {
                        @Override
                        public void run() {
                            try {
                                if (msgobj.containsKey("points")) {
                                    DefaultTableModel model = (DefaultTableModel) PointsTable.getModel();
                                    JSONObject points = (JSONObject) msgobj.get("points");
                                    if (points.isEmpty()) {
                                        model.setRowCount(0);
                                        PointsTable.revalidate();
                                    }
                                    JSONObject unames = (JSONObject) msgobj.get("unames");

                                    int rows = model.getRowCount();

                                    for (Object T : unames.keySet()) {
                                        int count = 1;
                                        Object user = unames.get(T);
                                        Object PTs = points.get(T);
                                        boolean updated = false;
                                        while (count <= rows) {
                                            Object U = model.getValueAt(count - 1, 0);
                                            if (U.equals(user)) {
                                                model.setValueAt(PTs, count - 1, 1);
                                                updated = true;
                                                break;
                                            }
                                            count++;
                                        }
                                        if (!updated) {
                                            model.addRow(new Object[]{user, PTs});
                                        }
                                        count = 1;
                                    }
                                } else if (msgobj.containsKey("relay")) {
                                    JSONObject msg = (JSONObject) msgobj.get("relay");
                                    String uname = (String) msg.get("user");
                                    String message = (String) msg.get("msg");
                                    if (message.isEmpty()) {
                                        return;
                                    }
                                    DefaultTableModel model = (DefaultTableModel) ControlPanel.smchat.getModel();
                                    while (model.getRowCount() > 100) {
                                        model.removeRow(1);
                                    }
                                    model.addRow(new Object[]{uname + ": " + message});
                                    ControlPanel.smchat.changeSelection(model.getRowCount() - 1, 0, false, false);
                                    ControlPanel.smchat.getSelectionModel().clearSelection();
                                    return;

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    new Thread("PutTheadName") {
                        @Override
                        public void run() {
// Thread code goes here.

                            try {
                                if (msgobj.containsKey("cmdcost")) {
                                    CS.cmdCosts.clear();
                                    CS.cmdCosts.putAll((JSONObject) new JSONParser().parse(msgobj.get("cmdcost").toString()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    new Thread("PutTheadName") {
                        @Override
                        public void run() {
                            if (CS.GUISettings.containsKey("cmdsounds") && msgobj.containsKey("command") && !CS.ModMode) {
                                try {
                                    String toParse = CS.GUISettings.get("cmdsounds").toString();
                                    JSONObject cmdsounds = (JSONObject) parser.parse(toParse);
                                    String cmd = msgobj.get("command").toString();
                                    if (cmdsounds.containsKey(cmd)) {
                                        final FileInputStream fis;
                                        try {
                                            fis = new FileInputStream(cmdsounds.get(cmd).toString());
                                            new Thread("PutTheadName") {
                                                @Override
                                                public void run() {
                                                    int multi = 0;
                                                    if (CS.GUISettings.containsKey("multisound")) {
                                                        multi = Integer.parseInt(CS.GUISettings.get("multisound").toString());
                                                    } else {
                                                        CS.GUISaveSettings("multisound", "0");
                                                    }
                                                    if (multi == 1) {
                                                        try {
                                                            playMP3 = new Player(fis);
                                                        } catch (JavaLayerException ex) {
                                                            Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                                        }
                                                        try {
                                                            playMP3.play();
                                                        } catch (JavaLayerException ex) {
                                                            Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                                        }
                                                    } else if (playMP3 == null) {
                                                        try {
                                                            playMP3 = new Player(fis);
                                                            playMP3.play();
                                                        } catch (JavaLayerException ex) {
                                                            Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                                        }
                                                    } else if (playMP3.isComplete()) {
                                                        try {
                                                            playMP3 = new Player(fis);
                                                            playMP3.play();
                                                        } catch (JavaLayerException ex) {
                                                            Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                }
                                            }.start();

                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    }
                                } catch (ParseException ex) {
                                    Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }
                    }.start();
                } catch (ParseException ex) {
                    Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );
    }

}
