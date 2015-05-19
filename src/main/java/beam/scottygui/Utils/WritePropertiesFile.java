/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import org.json.simple.JSONObject;

/**
 *
 * @author tjhasty
 */
public class WritePropertiesFile {

    public static void Write() {
        try {
            Properties properties = new Properties();
            JSONObject BlankSettings = new JSONObject();
            properties.setProperty("settings", BlankSettings.toString());
            File file = new File("config.properties");
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "GUIConfig");
            fileOut.close();
        } catch (Exception ex) {
        }

    }
}
