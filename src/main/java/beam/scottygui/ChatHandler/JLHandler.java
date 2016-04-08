/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.ChatHandler;

import beam.scottygui.Stores.CS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 *
 * @author tjhasty
 */
public class JLHandler {

    public static void JoinBeep() {
        if ("true".equalsIgnoreCase((String) CS.GUISettings.getOrDefault("joinbeep", "false"))) {
            new Thread("JoinBeep") {
                @Override
                public void run() {
                    try {
                        Player playMP3 = new Player(this.getClass().getResourceAsStream("/assets/userjoin.mp3"));
                        playMP3.play();
                    } catch (JavaLayerException ex) {
                        Logger.getLogger(JLHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }.start();
        }
    }

    public static void LeaveBeep() {
        if ("true".equalsIgnoreCase((String) CS.GUISettings.getOrDefault("leavebeep", "false"))) {
            new Thread("LeaveBeep") {
                @Override
                public void run() {
                    try {
                        Player playMP3 = new Player(this.getClass().getResourceAsStream("/assets/userleave.mp3"));
                        playMP3.play();
                    } catch (JavaLayerException ex) {
                        Logger.getLogger(JLHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }.start();
        }
    }
}
