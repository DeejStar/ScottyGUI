/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.quotecontrol;

import static beam.scottygui.Stores.CentralStore.AuthKey;
import static beam.scottygui.Stores.CentralStore.cp;
import beam.scottygui.Utils.HTTP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public final class delquote extends javax.swing.JFrame {

    public Map<String, String> Reversed = new HashMap();
    public List<String> ToDel = new ArrayList();
    public JSONParser parser = new JSONParser();
    public DefaultListModel quoteList = new DefaultListModel();
    public HTTP http = new HTTP();

    /**
     * Creates new form delquote
     */
    public delquote() {
        initComponents();
        PopList();
    }

    public void PopList() {
        this.QToDelList.setModel(quoteList);
        while (true) {
            try {
                JSONObject QL = (JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/quotes?authkey=" + AuthKey));
                //System.out.println(QL.toString());
                for (Object t : QL.keySet()) {
                    Reversed.put(QL.get(t.toString()).toString(), t.toString());
                    quoteList.addElement(QL.get(t));
                }
                break;
            } catch (ParseException ex) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(delquote.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }

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
        QToDelList = new javax.swing.JList();
        QDel = new javax.swing.JButton();
        QDelCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        QToDelList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(QToDelList);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 21, 528, 170));

        QDel.setText("Delete Selected");
        QDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QDelActionPerformed(evt);
            }
        });
        getContentPane().add(QDel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 204, -1, -1));

        QDelCancel.setText("Cancel");
        QDelCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QDelCancelActionPerformed(evt);
            }
        });
        getContentPane().add(QDelCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 204, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void QDelCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QDelCancelActionPerformed
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_QDelCancelActionPerformed

    private void QDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QDelActionPerformed
        List<String> todel = this.QToDelList.getSelectedValuesList();

        List<String> IDsToDel = new ArrayList();
        for (String t : todel) {
            IDsToDel.add(this.Reversed.get(t));
        }

        for (String t : IDsToDel) {
            while (true) {
                try {
                    http.GetScotty("https://api.scottybot.net/quotes/delete?authkey=" + AuthKey + "&id=" + t);
                    break;
                } catch (Exception ex) {
                    Logger.getLogger(delquote.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        while (true) {
            try {
                cp.PopQuoteList();
                break;
            } catch (ParseException ex) {
                Logger.getLogger(delquote.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.dispose();
    }//GEN-LAST:event_QDelActionPerformed

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
            java.util.logging.Logger.getLogger(delquote.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(delquote.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(delquote.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(delquote.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new delquote().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton QDel;
    private javax.swing.JButton QDelCancel;
    private javax.swing.JList QToDelList;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
