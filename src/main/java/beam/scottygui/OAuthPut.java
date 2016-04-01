/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui;

import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.http;
import java.awt.event.KeyEvent;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.http.client.CookieStore;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author tjhasty
 */
public class OAuthPut extends javax.swing.JFrame {

    /**
     * Creates new form OAuthPut
     */
    public OAuthPut() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        LoginField = new javax.swing.JTextField();
        PassField = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        CodeField = new javax.swing.JPasswordField();
        Login = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Custom Bot Name Set");

        jLabel1.setText("Custom Bot Name ");

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

        PassField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PassFieldKeyPressed(evt);
            }
        });

        jLabel2.setText("Password");

        jLabel5.setText("2-Factor (optional)");

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

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(Login, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LoginField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(CodeField)
                                .addComponent(PassField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LoginField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Login)
                    .addComponent(jButton1))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void LoginFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LoginFieldActionPerformed

    private void LoginFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LoginFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Login();
        }         // TODO add your handling code here:
    }//GEN-LAST:event_LoginFieldKeyPressed

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

    private void LoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginMouseClicked

    }//GEN-LAST:event_LoginMouseClicked

    private void LoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginActionPerformed

        Login();        // TODO add your handling code here:
    }//GEN-LAST:event_LoginActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(OAuthPut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OAuthPut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OAuthPut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OAuthPut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OAuthPut().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField CodeField;
    private javax.swing.JButton Login;
    private javax.swing.JTextField LoginField;
    private javax.swing.JPasswordField PassField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    // End of variables declaration//GEN-END:variables

    private void Login() {
        String CUsername = null;
        String CPassword = null;
        String CTwoFA = null;
        CUsername = this.LoginField.getText();
        CPassword = new String(this.PassField.getPassword());
        CTwoFA = new String(this.CodeField.getPassword());
        if (CUsername == null) {
            return;
        }
        //if (CUsername.toLowerCase().equals(Username.toLowerCase())) {
        //  JOptionPane.showMessageDialog(rootPane, "It's not smart to make the bot name your username.");
        //   return;
        //}
        if (CPassword == null) {
            return;
        }

        if (CUsername != null && CPassword != null) {
            CookieStore cookie = null;
            try {
                cookie = http.CUserLogin(CUsername, CPassword, CTwoFA);
                if (cookie == null) {
                    return;
                }
            } catch (Exception ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Error with logging in or getting OAuth, try again later.");
            }
            try {
                String ClientID = "a07c74f49a9af1e8127efd4b8b71a1e78d955abd5e8689e5";
                String Scope = "user:details:self user:analytics:self channel:update:self chat:bypass_slowchat chat:connect chat:clear_messages chat:edit_options chat:remove_message";

                String URL = "https://beam.pro/api/v1/oauth/authorize?response_type=code&client_id=" + ClientID + "&redirect_uri=https://scottybot.net&scope=" + URLEncoder.encode(Scope, "UTF-8");

                String response = null;
                System.out.println(URL);
                response = http.getCUser(URL, cookie);
                System.out.println(response);
                JSONObject code = new JSONObject();
                code.putAll((JSONObject) JSONValue.parse(response));
                String OAcode = (String) code.get("code");
                Map<String, String> toPut = new HashMap();
                toPut.put("authkey", CS.AuthKey);
                toPut.put("code", OAcode);
                String blah = http.put(toPut, CS.apiLoc + "/setoauthbotname");
                JSONObject obj = new JSONObject();
                obj.putAll((JSONObject) JSONValue.parse(blah));
                JOptionPane.showMessageDialog(null, obj.get("status"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
