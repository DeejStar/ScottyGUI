/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.APIServ;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.GUISettings;
import static beam.scottygui.Stores.CS.playMP3;
import java.awt.Color;
import java.io.FileInputStream;
import static java.lang.Thread.sleep;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import static java.lang.Thread.sleep;

/**
 *
 * @author tjhasty
 */
public class SHandler {

    static List<String> subbed = new CopyOnWriteArrayList();

    public static void SubSndStart() {
        new Thread("Sound Alert!") {
            @Override
            public void run() {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(GUISettings.get("SubSound").toString());
                } catch (Exception ex) {
                    //Logger.getLogger(AlertFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    if (GUISettings.containsKey("SubSound")) {
                        playMP3 = new Player(fis);
                    } else {
                        playMP3 = new Player(this.getClass().getResourceAsStream("/assets/gir_follow.mp3"));
                    }
                    playMP3.play();
                } catch (JavaLayerException ex) {
                    Logger.getLogger(AlertFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }.start();
    }

    public synchronized static boolean QueuedFollowers() {
        try {
            while (!CS.playMP3.isComplete()) {
                sleep(100);
            }
        } catch (Exception e) {

        }
        return !subbed.isEmpty();
    }

    public synchronized static void addFollower(String UName) {
        while (!subbed.contains(UName)) {
            subbed.add(UName);
        }
    }

    public synchronized static String getNextFollower() {
        String toSend = subbed.get(0);
        subbed.remove(0);
        return toSend;
    }

    public final static String toHexString(Color colour) throws NullPointerException {
        String hexColour = Integer.toHexString(colour.getRGB() & 0xffffff);
        if (hexColour.length() < 6) {
            hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
        }
        return "#" + hexColour;
    }

}
