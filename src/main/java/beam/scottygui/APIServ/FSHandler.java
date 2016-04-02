/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.APIServ;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author tjhasty
 */
public class FSHandler {

    static List<String> followed = new CopyOnWriteArrayList();

    public static boolean QueuedFollowers() {
        return !followed.isEmpty();
    }

    public synchronized static void addFollower(String UName) {
        while (!followed.contains(UName)) {
            followed.add(UName);
        }
    }

    public synchronized static String getNextFollower() {
        String toSend = followed.get(0);
        followed.remove(0);
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
