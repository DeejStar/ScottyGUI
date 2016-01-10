package beam.scottygui;

import beam.scottygui.Stores.CS;
import beam.scottygui.websocket.WebSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class MainApp {

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

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
                        }
                    } catch (Exception e) {

                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

}
