package beam.scottygui;

import static beam.scottygui.APIServ.RestServ.createHttpServer;
import beam.scottygui.OAuth.OAuthHandler;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.GUILoadSettings;
import static beam.scottygui.Stores.CS.extchat;
import beam.scottygui.websocket.LiveLoad;
import beam.scottygui.websocket.WebSocket;
import com.google.common.io.Files;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class MainApp {

    public static void main(final String[] args) {

        if (args.length > 0) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            String java = "\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\"";
            if (args[0].equalsIgnoreCase("prepupdate")) {
                File oldf = new File("ScottyGUI.jar");
                try {
                    Files.copy(oldf, new File("ScottyGUI.jar.old"));
                    try {

                        String os = System.getProperty("os.name");
                        if (os.equalsIgnoreCase("Linux")) {
                            System.out.println("Linux Detected");
                            Runtime.getRuntime().exec(new String[]{"sh", "-c", java + " -jar " + "./ScottyGUI.jar.old update"});
                        } else {
                            Runtime.getRuntime().exec(new String[]{"cmd", "/C", java + " -jar " + "./ScottyGUI.jar.old update"});
                        }
                        System.exit(0);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(extchat, "Unable to restart automatically, please do so manually.");
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }

            }
            if (args[0].equalsIgnoreCase("update")) {
                CS.Updating = true;
                while (true) {
                    try {
                        new File("ScottyGUI.jar").delete();
                        break;
                    } catch (Exception e) {

                    }
                }
                CS.CheckNewVer();
                return;
            }
        }

        File oldf = new File("./ScottyGUI.jar.old");
        while (true) {
            try {
                oldf.delete();
                break;
            } catch (Exception e) {

            }
        }
        PrintStream Errorout = null;
        try {
            Errorout = new PrintStream(new FileOutputStream("ErrorLog.Log", false));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setErr(Errorout);
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to cross-platform
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // not worth my time
            }
        }

        GUILoadSettings();

        Login login = new Login();
        login.setVisible(true);
        new Thread("PutTheadName") {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (CS.ChanID != null) {
                            if (CS.session != null) {
                                if (!CS.session.isOpen()) {
                                    System.err.println("Lost chat connection, reconnecting.");
                                    new WebSocket().connect(CS.ChanID);
                                }
                            }
                            if (CS.llSocket == null) {
                                LiveLoad.llSocket(CS.ChanID);
                            } else if (!CS.llSocket.isOpen()) {
                                LiveLoad.llSocket(CS.ChanID);
                            }
                        }
                    } catch (Exception e) {

                    } finally {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        OAuthHandler.RefreshToken();
                    }
                }
            }
        }.start();
        new Thread("Rest API Thread") {
            @Override
            public void run() {
                System.out.println("Starting Crunchify's Embedded Jersey HTTPServer...");
                try {
                    HttpServer crunchifyHTTPServer = createHttpServer();
                    crunchifyHTTPServer.start();
                    System.out.println("Started Crunchify's Embedded Jersey HTTPServer Successfully !!!");
                } catch (Exception ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

    }

}
