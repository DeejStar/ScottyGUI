/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.cmdcontrol;

import beam.scottygui.ControlPanel;
import static beam.scottygui.Stores.CentralStore.AuthKey;
import static beam.scottygui.Stores.CentralStore.cp;
import beam.scottygui.Utils.HTTP;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public final class DeletePermAdjust extends javax.swing.JFrame {

    HTTP http = new HTTP();

    /**
     * Creates new form DeletePermAdjust
     */
    public void PopCmdText() {
        JSONParser parser = new JSONParser();
        HTTP http = new HTTP();
        JSONObject CmdOutput = null;
        try {
            CmdOutput = (JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/commands?authkey=" + AuthKey));
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(CmdOutput.toString());
        JSONArray T = (JSONArray) CmdOutput.get("Commands");
        String out = "";
        for (Object t : T) {
            JSONObject obj = (JSONObject) t;
            String restrictlevel = null;
            int level = Integer.parseInt(obj.get("permlevel").toString());
            switch (level) {
                case 0:
                    restrictlevel = "Everyone";
                    break;
                case 1:
                    restrictlevel = "Mods";
                    break;
                case 2:
                    restrictlevel = "Streamer";
                    break;
                case 3:
                    restrictlevel = "Admin";
                    break;
            }
            this.CMDList.addItem(obj.get("cmd"));
        }

    }

    public DeletePermAdjust() {
        initComponents();
        this.PopCmdText();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        permlevel = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        CMDList = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        permlevel1 = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        DELCMD = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        permlevel.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Streamer", "Mod", "Everyone" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        permlevel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(permlevel);

        jLabel3.setText("Perm Level");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        getContentPane().add(CMDList, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 178, -1));

        permlevel1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Streamer", "Mod", "Everyone" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        permlevel1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(permlevel1);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(52, 71, 151, 80));

        jLabel4.setText("Set New Perm level");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(68, 53, -1, -1));

        DELCMD.setText("Delete Command");
        DELCMD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DELCMDActionPerformed(evt);
            }
        });
        getContentPane().add(DELCMD, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 70, -1, -1));

        jButton1.setText("Change Perm Level");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, 151, -1));

        jButton2.setText("Done");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, 100, 20));

        jButton3.setText("Reset Counter");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void DELCMDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DELCMDActionPerformed

        int Confirm = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to delete this command?");
        if (Confirm == 0) {
            this.DelCmd();
            this.CMDList.removeAllItems();
            this.PopCmdText();
            while (true) {
                try {
                    cp.PopCmdText();
                    break;
                } catch (ParseException ex) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }

    }//GEN-LAST:event_DELCMDActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String ModLevel = "";
        try {
            ModLevel = this.permlevel1.getSelectedValue().toString().toUpperCase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "You didn't pick a permissions level");
            return;
        }
        int PermLevel = 0;
        switch (ModLevel) {
            case "STREAMER":
                PermLevel = 2;
                break;
            case "MOD":
                PermLevel = 1;
                break;
            case "EVERYONE":
                PermLevel = 0;
                break;
        }

        try {
            http.GetScotty("https://api.scottybot.net/commands/permlevel?authkey=" + AuthKey + "&cmd=" + URLEncoder.encode(this.CMDList.getSelectedItem().toString(), "UTF-8") + "&permlevel=" + PermLevel);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DeletePermAdjust.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {
                cp.PopCmdText();
                break;
            } catch (ParseException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            http.GetScotty("https://api.scottybot.net/commands/reset?authkey=" + URLEncoder.encode(AuthKey, "UTF-8") + "&cmd=" + URLEncoder.encode(this.CMDList.getSelectedItem().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DeletePermAdjust.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {
                cp.PopCmdText();
                break;
            } catch (ParseException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void DelCmd() {
        try {
            http.GetScotty("https://api.scottybot.net/commands/delete?authkey=" + URLEncoder.encode(AuthKey, "UTF-8") + "&cmd=" + URLEncoder.encode(this.CMDList.getSelectedItem().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DeletePermAdjust.class.getName()).log(Level.SEVERE, null, ex);
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
            java.util.logging.Logger.getLogger(DeletePermAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DeletePermAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DeletePermAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DeletePermAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DeletePermAdjust().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox CMDList;
    private javax.swing.JButton DELCMD;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList permlevel;
    private javax.swing.JList permlevel1;
    // End of variables declaration//GEN-END:variables
}
