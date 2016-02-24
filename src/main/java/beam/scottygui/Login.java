/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui;

import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.AuthKey;
import static beam.scottygui.Stores.CS.ChanID;
import static beam.scottygui.Stores.CS.CheckNewVer;
import static beam.scottygui.Stores.CS.Password;
import static beam.scottygui.Stores.CS.UserID;
import static beam.scottygui.Stores.CS.Username;
import static beam.scottygui.Stores.CS.newline;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.JSONUtil;
import beam.scottygui.websocket.ScottySocket;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

        System.setProperty("file.encoding", "UTF-8");
        Field charset = null;
        try {
            charset = Charset.class.getDeclaredField("defaultCharset");
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        charset.setAccessible(true);
        try {
            charset.set(null, null);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        CheckNewVer();
        initComponents();
        if (CS.GUISettings.containsKey("username") && CS.GUISettings.containsKey("pass")) {
            if (CS.GUISettings.get("username") != null && CS.GUISettings.get("pass") != null) {
                this.LoginField.setText((String) CS.GUISettings.get("username"));
                this.PassField.setText((String) CS.GUISettings.get("pass"));
                this.RemMe.setSelected(true);
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
        Login = new javax.swing.JButton();
        CodeField = new javax.swing.JPasswordField();
        PassField = new javax.swing.JPasswordField();
        LoginField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        streamlabel = new javax.swing.JLabel();
        StreamerList = new javax.swing.JComboBox();
        addstreamer = new javax.swing.JButton();
        delstreamer = new javax.swing.JButton();
        ShowSList = new javax.swing.JToggleButton();
        RemMe = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Login.setText("Login");
        Login.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LoginMouseClicked(evt);
            }
        });
        Login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginActionPerformed(evt);
            }
        });

        CodeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CodeFieldActionPerformed(evt);
            }
        });
        CodeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CodeFieldKeyPressed(evt);
            }
        });

        PassField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PassFieldKeyPressed(evt);
            }
        });

        LoginField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginFieldActionPerformed(evt);
            }
        });
        LoginField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LoginFieldKeyPressed(evt);
            }
        });

        jLabel4.setText("using your beam info.");

        jLabel1.setText("Username");

        jLabel2.setText("Password");

        jLabel5.setText("2-Factor (optional)");

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

        RemMe.setText("Remember Me");
        RemMe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemMeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LoginField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(CodeField, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                        .addComponent(PassField))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(RemMe)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Login, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(179, 179, 179)
                                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(62, 62, 62)
                                                .addComponent(ShowSList)))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(addstreamer)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(delstreamer, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(streamlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(StreamerList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel4)))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LoginField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(CodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(PassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Login)
                            .addComponent(RemMe))
                        .addGap(25, 25, 25)))
                .addComponent(ShowSList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(streamlabel)
                .addGap(5, 5, 5)
                .addComponent(StreamerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addstreamer)
                    .addComponent(delstreamer))
                .addGap(10, 10, 10))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void LoginFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LoginFieldActionPerformed

    private void LoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginActionPerformed
        if (RemMe.isSelected()) {
            CS.GUISaveSettings("username", this.LoginField.getText());
            CS.GUISaveSettings("pass", new String(this.PassField.getPassword()));
        }
        Login();        // TODO add your handling code here:
    }//GEN-LAST:event_LoginActionPerformed

    private void LoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginMouseClicked

    }//GEN-LAST:event_LoginMouseClicked

    private void PassFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PassFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Login();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_PassFieldKeyPressed

    private void CodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CodeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CodeFieldActionPerformed

    private void CodeFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CodeFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Login();
        }         // TODO add your handling code here:
    }//GEN-LAST:event_CodeFieldKeyPressed

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

    private void RemMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemMeActionPerformed
        if (!RemMe.isSelected()) {
            CS.GUISaveSettings("username", null);
            CS.GUISaveSettings("pass", null);
            this.LoginField.setText("");
            this.PassField.setText("");
        } else {
            int sure = JOptionPane.showConfirmDialog(rootPane, "User/Pass is stored locally, are you sure?");
            if (sure != 0) {
                RemMe.setSelected(false);
            }
        }
    }//GEN-LAST:event_RemMeActionPerformed

    private void LoginFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LoginFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Login();
        }         // TODO add your handling code here:
    }//GEN-LAST:event_LoginFieldKeyPressed

    public void Login() {
        JSONObject ChanObj = new JSONObject();
        Username = this.LoginField.getText();
        CS.UserName = Username;
        Password = new String(this.PassField.getPassword());

        JSONParser parser = new JSONParser();
        String ToParse = "";
        try {
            ToParse = http.Login(Username, Password, new String(this.CodeField.getPassword()));
// TODO add your handling code here:
        } catch (IOException | InterruptedException ex) {
            JOptionPane.showMessageDialog(rootPane, "Error talking to Beam, try again later.");
            ex.printStackTrace(System.err);
            return;
        }
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(ToParse);
            UserID = Long.parseLong(obj.get("id").toString());
            ChanObj.putAll((JSONObject) parser.parse(http.get("https://beam.pro/api/v1/channels/" + Username)));
            //System.out.println(ChanObj);
            try {
                if (ChanObj.containsKey("badge")) {
                    //System.out.println("BADGE FOUND");
                    JSONObject badgeObj = new JSONObject();
                    badgeObj.putAll((JSONObject) ChanObj.get("badge"));
                    if (badgeObj.containsKey("url")) {
                        CS.SubBadge = badgeObj.get("url").toString();
                    }
                }
            } catch (Exception e) {

            }

        } catch (Exception ex) {
            //Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            if (!obj.isEmpty() && obj.containsKey("message")) {
                String error = (String) obj.get("message");
                JOptionPane.showMessageDialog(rootPane, "Error: " + error);
            } else {
                JOptionPane.showMessageDialog(rootPane, "Login failed.");
            }
            return;
        }
        JSONObject AuthReturn = null;
        if (this.StreamerList.getSelectedIndex() == -1 || !this.ShowSList.isSelected()) {

            String URL = null;
            URL = CS.apiLoc + "/login";
            int cnt = 0;
            boolean got = false;
            while (cnt < 5) {
                try {
                    Map<String, String> toPost = new HashMap();
                    toPost.put("username", Username);
                    toPost.put("password", Password);
                    toPost.put("code", new String(this.CodeField.getPassword()));
                    AuthReturn = (JSONObject) parser.parse(http.post(toPost, URL));
                    got = true;
                    break;
                } catch (ParseException | IOException | InterruptedException | ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    cnt++;
                }
            }
            if (!got) {
                JOptionPane.showMessageDialog(rootPane, "Had issue talking with API, contact @mrPocketpac on twitter");
            }
            if (AuthReturn.containsValue("Scottybot is not in your channel")) {
                int Join = JOptionPane.showConfirmDialog(rootPane, "Scottybot is not in your channel, add to your channel?");
                System.err.println("JOIN:" + Join);

                if (Join == 0) {
                    try {

                        Map<String, String> toPost = new HashMap();
                        toPost.put("username", Username);
                        toPost.put("password", URLEncoder.encode(Password, "UTF-8"));
                        toPost.put("code", new String(this.CodeField.getPassword()));
                        String toParse = http.post(toPost, CS.apiLoc + "/joinchan");
                        JSONObject joinobj = (JSONObject) new JSONParser().parse(toParse);
                        if (joinobj.containsKey("APIAuth")) {
                            AuthReturn.clear();
                            AuthReturn.put("AuthKeyv2", joinobj.get("APIAuth"));
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Error occured, please try again or contact @MrPocketpac on Twitter");
                        }
                    } catch (UnsupportedEncodingException | ParseException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException | ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Scottybot is not set to be in your channel" + newline + "Make sure Scottybot is in your channel before logging in.");
                    return;
                }
            }
        }
        if (!this.ShowSList.isSelected() || this.StreamerList.getSelectedIndex() == -1) {
            try {
                ChanID = Long.parseLong(ChanObj.get("id").toString());
                AuthKey = AuthReturn.get("AuthKeyv2").toString();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, "Issue getting info from API, try again or contact @MrPocketpac on twitter.");
            }
        } else {
            try {
                String Streamer = this.StreamerList.getSelectedItem().toString();
                UUID CheckKey = UUID.fromString(CS.getStreamerUUID(Streamer));
                CS.AuthKey = CheckKey.toString();
                String toParse = http.GetScotty(CS.apiLoc + "/settings?authkey=" + CS.AuthKey.toString());
                JSONObject chanObj = new JSONObject();
                chanObj.putAll((JSONObject) parser.parse(toParse));
                System.err.println(chanObj.toJSONString());
                //System.exit(0);
                CS.ChanID = Long.parseLong(chanObj.get("ChanID").toString());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(rootPane, "Does not appear to be a proper UUID, please contact the streamer and verify.");
                return;
            }
        }
        final ControlPanel cp = new ControlPanel();
        CS.cp = cp;

        if (this.ShowSList.isSelected()) {
            ControlPanel.StreamSet.setVisible(false);
        }

        this.dispose();
        ScottySocket sock = new ScottySocket();
        sock.connect();
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
    private javax.swing.JPasswordField CodeField;
    private javax.swing.JButton Login;
    private javax.swing.JTextField LoginField;
    private javax.swing.JPasswordField PassField;
    private javax.swing.JToggleButton RemMe;
    private javax.swing.JToggleButton ShowSList;
    private javax.swing.JComboBox StreamerList;
    private javax.swing.JButton addstreamer;
    private javax.swing.JButton delstreamer;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel streamlabel;
    // End of variables declaration//GEN-END:variables
}
