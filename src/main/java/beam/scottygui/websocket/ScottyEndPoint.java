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
                    JSONObject msgobj = (JSONObject) parser.parse(message);
                    ControlPanel.ControlStatus.setBackground(Color.GREEN);
                    ControlPanel.ControlStatus.setText("");
                    CS.controlSes = session;

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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (msgobj.containsKey("cmdcost")) {
                            CS.cmdCosts.clear();
                            CS.cmdCosts.putAll((JSONObject) new JSONParser().parse(msgobj.get("cmdcost").toString()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (CS.GUISettings.containsKey("cmdsounds") && msgobj.containsKey("command") && !CS.ModMode) {
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
                                        if (playMP3 == null) {
                                            try {
                                                playMP3 = new Player(fis);
                                                playMP3.play();
                                            } catch (JavaLayerException ex) {
                                                Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        } else {
                                            if (playMP3.isComplete()) {
                                                try {
                                                    playMP3 = new Player(fis);
                                                    playMP3.play();
                                                } catch (JavaLayerException ex) {
                                                    Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            }
                                        }
                                    }
                                }.start();

                            } catch (Exception ex) {
                                //Logger.getLogger(AlertFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(ScottyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );
    }

}
