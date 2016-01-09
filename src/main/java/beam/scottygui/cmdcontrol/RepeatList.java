/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.cmdcontrol;

import beam.scottygui.ControlPanel;
import static beam.scottygui.Stores.CS.AuthKey;
import beam.scottygui.Utils.HTTP;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public final class RepeatList extends javax.swing.JFrame {

    HTTP http = new HTTP();
    JSONParser parser = new JSONParser();

    /**
     * Creates new form RepeatList
     */
    public RepeatList() {
        initComponents();
        PopLists();
    }
    DefaultListModel cmdlist = new DefaultListModel();
    DefaultListModel replist = new DefaultListModel();

    public void PopLists() {
        JSONObject CmdOutput = null;
        try {
            CmdOutput = (JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/commands?authkey=" + AuthKey));
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONArray T = (JSONArray) CmdOutput.get("Commands");

        for (Object t : T) {
            JSONObject obj = (JSONObject) t;

            cmdlist.addElement(obj.get("cmd").toString());
        }
        this.CommandList.setModel(cmdlist);

        JSONObject Repeats = null;
        try {
            Repeats = (JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/repeats?authkey=" + AuthKey));
        } catch (ParseException ex) {
            Logger.getLogger(RepeatList.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Object t : Repeats.keySet()) {
            replist.addElement(t.toString());
        }
        this.Repeating.setModel(replist);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        CommandList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        Repeating = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setViewportView(CommandList);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 34, 136, 401));

        jScrollPane2.setViewportView(Repeating);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(293, 35, 143, 400));

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(154, 52, 133, -1));

        jButton2.setText("Remove");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(154, 87, 133, -1));

        jLabel1.setText("Commands");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(44, 11, -1, -1));

        jLabel2.setText("Repeating");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 12, -1, -1));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Hold CTRL to select multiple commands at once.\n");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(154, 161, 133, 125));

        jButton6.setText("Edit Repeat Delay");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(154, 356, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        List<String> ToAdd = this.CommandList.getSelectedValuesList();
        for (String t : ToAdd) {
            if (!replist.contains(t)) {

                try {
                    http.GetScotty("https://api.scottybot.net/repeats/add?authkey=" + AuthKey + "&cmd=" + URLEncoder.encode(t, "UTF-8"));
                    replist.addElement(t);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(RepeatList.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        List<String> ToRem = this.Repeating.getSelectedValuesList();
        for (String t : ToRem) {
            try {
                http.GetScotty("https://api.scottybot.net/repeats/delete?authkey=" + AuthKey + "&cmd=" + URLEncoder.encode(t, "UTF-8"));
                replist.removeElement(t);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RepeatList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String Delay = null;
        Delay = JOptionPane.showInputDialog(rootPane, "Enter new repeat delay in minutes.");
        if (Delay != null) {
            Long LDelay = Long.parseLong(Delay) * 60;
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=RepeatTimes&value=" + LDelay);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

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
            java.util.logging.Logger.getLogger(RepeatList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RepeatList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RepeatList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RepeatList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RepeatList().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList CommandList;
    private javax.swing.JList Repeating;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
