/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui;

import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.SendMSG;
import static beam.scottygui.WhisperPanel.whisperPanels;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author tjhasty
 */
public class WhisperPanel extends javax.swing.JFrame {

    public static Map<String, WhisperPanel> whisperPanels = new ConcurrentSkipListMap(String.CASE_INSENSITIVE_ORDER);
    public String Username;

    /**
     * Creates new form WhisperPanel
     */
    public WhisperPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chat = new javax.swing.JTable();
        toWhisper = new javax.swing.JTextField();

        chat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Whisper"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        chat.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        chat.setColumnSelectionAllowed(true);
        chat.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(chat);
        chat.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (chat.getColumnModel().getColumnCount() > 0) {
            chat.getColumnModel().getColumn(0).setResizable(false);
        }

        toWhisper.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                toWhisperKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 999, Short.MAX_VALUE)
                    .addComponent(toWhisper))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(toWhisper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void toWhisperKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_toWhisperKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String msg = "/whisper " + Username + " " + toWhisper.getText().trim();
            try {
                CS.session.getBasicRemote().sendText(SendMSG(msg));
                AddMessage(CS.Username, toWhisper.getText().trim());
                toWhisper.setText("");
            } catch (IOException ex) {
                AddMessage("Error", "Unable to send, issue talking with Beam");
                Logger.getLogger(WhisperPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_toWhisperKeyPressed

    public static void showPanel(String UName) {
        UName = UName.toLowerCase();
        prepPanel(UName);
        whisperPanels.get(UName).setVisible(true);
    }

    public static WhisperPanel getPanel(String UName) {
        UName = UName.toLowerCase();
        prepPanel(UName);
        return whisperPanels.get(UName);
    }

    public static void prepPanel(String UName) {
        String ProperName = UName;
        UName = UName.toLowerCase();
        if (!whisperPanels.containsKey(UName)) {
            whisperPanels.put(UName, new WhisperPanel());
            whisperPanels.get(UName).Username = UName;
            whisperPanels.get(UName).setTitle("Whispers for " + ProperName);
        }
    }

    public void AddMessage(String uname, String message) {
        message = message.trim();
        DefaultTableModel model = (DefaultTableModel) this.chat.getModel();
        while (model.getRowCount() > 100) {
            model.removeRow(1);
        }
        model.addRow(new Object[]{uname + ": " + message});
        this.chat.changeSelection(model.getRowCount() - 1, 0, false, false);
        this.chat.getSelectionModel().clearSelection();
        System.out.println(uname + ": " + message);
        return;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WhisperPanel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable chat;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField toWhisper;
    // End of variables declaration//GEN-END:variables
}
