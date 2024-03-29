/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.cmdcontrol;

import beam.scottygui.Stores.CS;
import beam.scottygui.Utils.HTTP;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public class CMDCost extends javax.swing.JFrame {

    /**
     * Creates new form CMDCost
     */
    public CMDCost() {
        List<Object> cmds = new ArrayList();
        JSONArray cmdJSON = new JSONArray();
        String toParse = "";
        int cnt = 0;
        while (cnt < 5) {
            try {
                toParse = new HTTP().get(CS.apiLoc + "/showcoms?chanid=" + CS.ChanID + "&output=json");
                break;
            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                cnt++;
            }
        }
        try {
            cmdJSON.addAll((JSONArray) new JSONParser().parse(toParse));
        } catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, "Failed pulling command list, try again later");
            return;
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Object T : cmdJSON) {
            JSONObject cmd = (JSONObject) T;
            model.addElement(cmd.get("cmd"));
        }
        initComponents();
        for (Object T : CS.cmdCosts.keySet()) {
            model.addElement(T);
        }
        this.cmdlist.setModel(model);
        Object cmd = this.cmdlist.getSelectedItem();
        if (CS.cmdCosts.containsKey(cmd)) {
            this.curCost.setText("Cur. Cost: " + CS.cmdCosts.get(cmd));
        } else {
            this.curCost.setText("No cost");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdlist = new javax.swing.JComboBox();
        setcost = new javax.swing.JButton();
        curCost = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Command Cost");

        cmdlist.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmdlist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cmdlistMouseReleased(evt);
            }
        });
        cmdlist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdlistActionPerformed(evt);
            }
        });
        cmdlist.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cmdlistKeyReleased(evt);
            }
        });

        setcost.setText("Set Cost");
        setcost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setcostActionPerformed(evt);
            }
        });

        curCost.setText("jLabel1");

        jButton2.setText("Remove Cost");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(curCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdlist, 0, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setcost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(curCost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdlist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setcost)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdlistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdlistActionPerformed
        Object cmd = this.cmdlist.getSelectedItem();
        if (CS.cmdCosts.containsKey(cmd)) {
            this.curCost.setText("Cur. Cost: " + CS.cmdCosts.get(cmd));
        } else {
            this.curCost.setText("No cost");
        }
    }//GEN-LAST:event_cmdlistActionPerformed

    private void cmdlistKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmdlistKeyReleased

    }//GEN-LAST:event_cmdlistKeyReleased

    private void cmdlistMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdlistMouseReleased

    }//GEN-LAST:event_cmdlistMouseReleased

    private void setcostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setcostActionPerformed
        String coststr = JOptionPane.showInputDialog("How much should this command cost?").trim();
        Long cost = 0L;
        try {
            cost = Long.parseLong(coststr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Oops, dosn't appear to be a number.");
            return;
        }
        JSONObject toSend = new JSONObject();
        toSend.put("request", "addcmdcost");
        toSend.put("cmd", this.cmdlist.getSelectedItem().toString());
        toSend.put("cost", cost);
        CS.controlSes.getAsyncRemote().sendText(toSend.toJSONString());
        this.curCost.setText("Cur. Cost: " + cost);
    }//GEN-LAST:event_setcostActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int check = JOptionPane.showConfirmDialog(rootPane, "Remove cost from this command?");
        if (check != 0) {
            return;
        }
        JSONObject toSend = new JSONObject();
        toSend.put("request", "delcmdcost");
        toSend.put("cmd", this.cmdlist.getSelectedItem().toString());
        CS.controlSes.getAsyncRemote().sendText(toSend.toJSONString());
        this.curCost.setText("No cost");
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(CMDCost.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CMDCost.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CMDCost.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CMDCost.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CMDCost().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmdlist;
    private javax.swing.JLabel curCost;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton setcost;
    // End of variables declaration//GEN-END:variables
}
