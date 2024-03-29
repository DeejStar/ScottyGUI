/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui;

import beam.scottygui.OAuth.OAuthHandler;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.CheckNewVer;
import static beam.scottygui.Stores.CS.cp;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.JSONUtil;
import beam.scottygui.websocket.ScottySocket;
import java.awt.Desktop;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class Login extends javax.swing.JFrame {

    HTTP http = new HTTP();
    JSONUtil json = new JSONUtil();

    /**
     * Creates new form Login
     */
    public Login() {
//        for (Map.Entry<Object, Object> T : System.getProperties().entrySet()) {
//            System.out.println(T.getKey() + " : " + T.getValue());
//        }
//        System.exit(0);
        System.setProperty("file.encoding", "UTF-8");
        Field charset = null;
        try {
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        CheckNewVer();
        initComponents();
        JSONObject ChanInfo = new JSONObject();
        if (OAuthHandler.hasToken()) {
            try {
                OAuthHandler.RefreshToken();
                ChanInfo.putAll(HTTP.ChannelwhoAmI());
            } catch (Exception ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (ChanInfo.containsKey("username")) {
                CS.Username = (String) ChanInfo.get("username");
                CS.UserID = (Long) ChanInfo.get("id");
                setOauth.setText("Hello " + CS.Username);
                setOauth.setEnabled(false);
                gotopanel.setEnabled(true);
            } else {
                setOauth.setEnabled(true);
                gotopanel.setEnabled(false);
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel3 = new javax.swing.JLabel();
        streamlabel = new javax.swing.JLabel();
        StreamerList = new javax.swing.JComboBox();
        addstreamer = new javax.swing.JButton();
        delstreamer = new javax.swing.JButton();
        ShowSList = new javax.swing.JToggleButton();
        setOauth = new javax.swing.JButton();
        gotopanel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Log in to Scottybot");

        streamlabel.setText("Streamers (For Mods Of A Channel)");
        streamlabel.setEnabled(false);

        StreamerList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        StreamerList.setEnabled(false);

        addstreamer.setText("Add Streamer");
        addstreamer.setEnabled(false);
        addstreamer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addstreamerActionPerformed(evt);
            }
        });

        delstreamer.setText("Remove Streamer");
        delstreamer.setEnabled(false);
        delstreamer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delstreamerActionPerformed(evt);
            }
        });

        ShowSList.setText("Show Streamer List");
        ShowSList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowSListActionPerformed(evt);
            }
        });

        setOauth.setText("Set OAuth");
        setOauth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                setOauthMousePressed(evt);
            }
        });
        setOauth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOauthActionPerformed(evt);
            }
        });

        gotopanel.setText("Go To Control Panel");
        gotopanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotopanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(192, 192, 192)
                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addComponent(streamlabel))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(92, 92, 92)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(gotopanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ShowSList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(setOauth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 62, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(22, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(StreamerList, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addstreamer)
                                .addGap(18, 18, 18)
                                .addComponent(delstreamer, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setOauth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gotopanel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ShowSList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(streamlabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StreamerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addstreamer)
                    .addComponent(delstreamer))
                .addGap(18, 18, 18)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ShowSListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowSListActionPerformed
        if (this.ShowSList.isSelected()) {
            this.StreamerList.setModel(CS.getModList());
            this.streamlabel.setEnabled(true);
            this.delstreamer.setEnabled(true);
            this.addstreamer.setEnabled(true);
            this.StreamerList.setEnabled(true);
            CS.ModMode = true;
        } else {
            this.streamlabel.setEnabled(false);
            this.delstreamer.setEnabled(false);
            this.addstreamer.setEnabled(false);
            this.StreamerList.setEnabled(false);
            CS.ModMode = false;
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_ShowSListActionPerformed

    private void addstreamerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addstreamerActionPerformed
        String Name = JOptionPane.showInputDialog("Please give the Streamers name.");
        if (Name.isEmpty()) {
            return;
        }
        String uuid = JOptionPane.showInputDialog("Please give the AuthKey provided by the streamer.");
        if (uuid.isEmpty()) {
            return;
        }
        try {
            UUID toCheck = UUID.fromString(uuid);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "AuthKey does not appear to be correct, please confirm with streamer.");
            return;
        }
        CS.AddModList(Name, uuid);
        this.StreamerList.setModel(CS.getModList());
    }//GEN-LAST:event_addstreamerActionPerformed

    private void delstreamerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delstreamerActionPerformed
        String Name = this.StreamerList.getSelectedItem().toString();
        int yesno = JOptionPane.showConfirmDialog(rootPane, "Remove " + Name + " from the list?");
        if (yesno == 0) {
            CS.DelModList(Name);
            this.StreamerList.setModel(CS.getModList());
        }

    }//GEN-LAST:event_delstreamerActionPerformed

    public static boolean serverListening() {
        Socket s = null;
        try {
            s = new Socket("localhost", 9090);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (Exception e) {
                }
            }
        }
    }
    private void setOauthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setOauthActionPerformed
        setOauth.setText("initializing");
        while (!serverListening()) {
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JOptionPane.showMessageDialog(rootPane, "A browser will now open to take you to Beam to approve this connection.");
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://beam.pro/oauth/authorize?response_type=code&redirect_uri=" + URLEncoder.encode("http://localhost:9090/oauthjoin", "UTF-8") + "&scope=" + URLEncoder.encode("user:details:self channel:details:self channel:follow:self channel:update:self chat:bypass_links chat:bypass_slowchat chat:chat chat:clear_messages chat:connect chat:edit_options chat:giveaway_start chat:remove_message", "UTF-8") + "&client_id=" + CS.ClientIDOauth));
            } catch (IOException | URISyntaxException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        new Thread("PutTheadName") {
            @Override
            public void run() {
                while (CS.Username == null) {
                    try {
                        sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                setOauth.setText("Welcome " + CS.Username);
                setOauth.setEnabled(false);
                gotopanel.setEnabled(true);
            }
        }.start();

    }//GEN-LAST:event_setOauthActionPerformed

    private void gotopanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotopanelActionPerformed
        if (StreamerList.getSelectedIndex() != -1 && this.ShowSList.isSelected()) {
            String Streamer = StreamerList.getSelectedItem().toString();
            CS.AuthKey = CS.getStreamerUUID(Streamer);
            String ChanInfo = "";
            try {
                ChanInfo = new HTTP().get("https://beam.pro/api/v1/channels/" + Streamer);
            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            JSONObject obj = new JSONObject();
            obj.putAll((JSONObject) JSONValue.parse(ChanInfo));
            if (obj.isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, "Unable to pull channel information, please try again or check streamer name.");
                return;
            } else {
                CS.ChanID = (Long) obj.get("id");
            }

        } else {
            try {
                String Verified = HTTP.OAuthVerifyScotty(OAuthHandler.GetAToken());
                System.out.println("VERIFIED " + Verified);
                JSONObject chaninfo = (JSONObject) JSONValue.parse(Verified);
                if (chaninfo.containsKey("failed")) {
                    String reason = (String) chaninfo.get("failed");
                    if (reason.toLowerCase().contains("not in your channel".toLowerCase())) {
                        if (Desktop.isDesktopSupported()) {
                            int confirm = JOptionPane.showConfirmDialog(rootPane, "Scottybot is not in your channel, would you like to have him join?");
                            if (confirm == 0) {
                                JOptionPane.showMessageDialog(rootPane, "Opening browser to Scottys Page!");
                                try {
                                    Desktop.getDesktop().browse(new URI("https://scottybot.net"));
                                } catch (IOException | URISyntaxException ex) {
                                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Please go to https://scottybot.net to add him to your channel!");
                        }

                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Unable to login: " + reason);
                    }
                    return;
                }

                CS.ChanID = (Long) chaninfo.get("ChanID");
                CS.AuthKey = (String) chaninfo.get("AuthKeyv2");
            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        CS.cp = new ControlPanel();
        new ScottySocket().connect();
        while (CS.ChanSettings.isEmpty()) {
            try {
                sleep(150);
            } catch (InterruptedException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        cp.setVisible(true);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_gotopanelActionPerformed

    private void setOauthMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setOauthMousePressed
        setOauth.setText("Initializing");        // TODO add your handling code here:
    }//GEN-LAST:event_setOauthMousePressed
    JSONObject ChanObj = new JSONObject();

    public boolean AuthLogin(String AuthKey) {
        try {

            CS.AuthKey = AuthKey;
            String toParse = http.get(CS.apiLoc + "/settings?authkey=" + CS.AuthKey);
            System.out.println(CS.apiLoc + "/settings?authkey=" + CS.AuthKey + " : " + toParse);
            JSONObject chanObj = new JSONObject();
            chanObj.putAll((JSONObject) JSONValue.parse(toParse));
            System.err.println(chanObj.toJSONString());
            CS.ChanID = Long.parseLong(chanObj.get("chanid").toString());
            return true;
        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, "Issue getting info from API, try again or contact @MrPocketpac on twitter.");
            return false;
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class
            .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class
            .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class
            .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class
            .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton ShowSList;
    private javax.swing.JComboBox StreamerList;
    private javax.swing.JButton addstreamer;
    private javax.swing.JButton delstreamer;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton gotopanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton setOauth;
    private javax.swing.JLabel streamlabel;
    // End of variables declaration//GEN-END:variables
}
