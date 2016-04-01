/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.chanstatus;

import beam.scottygui.ControlPanel;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.ChanID;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.JSONUtil;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
public class statuswindow extends javax.swing.JFrame {

    String curGame = "";

    /**
     * Creates new form statuswindow
     */
    public statuswindow() {
        initComponents();

    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        StreamTitle = new javax.swing.JTextArea();
        gamelistbox = new javax.swing.JComboBox();
        save = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        GSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                formPropertyChange(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        StreamTitle.setColumns(20);
        StreamTitle.setRows(5);
        jScrollPane1.setViewportView(StreamTitle);

        gamelistbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Begin Search" }));
        gamelistbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamelistboxActionPerformed(evt);
            }
        });

        save.setText("Save");
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        GSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GSearchActionPerformed(evt);
            }
        });
        GSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                GSearchKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GSearchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                GSearchKeyReleased(evt);
            }
        });

        jLabel1.setText("Search");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filler1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(210, 210, 210)
                            .addComponent(save, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(35, 35, 35)
                            .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(GSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(97, 97, 97)))
                        .addComponent(gamelistbox, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(gamelistbox, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(save, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void gamelistboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gamelistboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gamelistboxActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        HTTP http = new HTTP();
        String newtitle = this.StreamTitle.getText();
        if (newtitle != null) {
            Map<String, String> toSend = new HashMap();
            toSend.put("name", newtitle);
            String newGame = gamelistbox.getSelectedItem().toString();
            Long ID = (Long) CS.GameListJSON.get(newGame);
            JSONArray IDs = new JSONArray();
            IDs.add(ID);
            toSend.put("typeId", ID.toString());
            try {
                http.put(toSend, "https://beam.pro/api/v1/channels/" + CS.ChanID);
                this.setVisible(false);
                this.dispose();
            } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "Error occured, try again");
            }
        }
    }//GEN-LAST:event_saveActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        this.setVisible(false);
        this.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_cancelActionPerformed

    private void GSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GSearchActionPerformed

    }//GEN-LAST:event_GSearchActionPerformed
    long toWait = 1 * 1000 + System.currentTimeMillis();

    private void GSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GSearchKeyTyped

    }//GEN-LAST:event_GSearchKeyTyped

    private void GSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GSearchKeyPressed

    }//GEN-LAST:event_GSearchKeyPressed
    boolean searching = false;
    private void GSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GSearchKeyReleased

        toWait = 1000 + System.currentTimeMillis();

        if (!searching) {
            searching = true;
            new Thread("PutTheadName") {
                @Override
                public void run() {
                    while (toWait > System.currentTimeMillis()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(statuswindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    searching = false;
                    try {
                        String Search = GSearch.getText().toUpperCase();
                        String toParse = new HTTP().get("https://beam.pro/api/v1/types?query=" + URLEncoder.encode(Search, "UTF-8") + "&fields=id,name&limit=100");
                        gamelistbox.setModel(CS.popGames(toParse, Search));
                    } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(statuswindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        }

    }//GEN-LAST:event_GSearchKeyReleased

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated

    }//GEN-LAST:event_formWindowActivated

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange

    }//GEN-LAST:event_formPropertyChange

    public void setcurgame() {
        GetTitle();
        int cnt = 0;
        while (cnt < 5) {
            try {
                String query = "";
                if (GSearch.getText().isEmpty() || GSearch.getText().equals("")) {
                    query = new JSONUtil().getcurGame(ChanID);
                } else {
                    query = GSearch.getText().trim();
                }
                String url = "https://beam.pro/api/v1/types?query=" + URLEncoder.encode(query, "UTF-8") + "&fields=id,name&limit=100";

                String toParse = new HTTP().get(url);
                System.out.println(url);
                gamelistbox.setModel(CS.popGames(toParse, query));
                gamelistbox.setSelectedItem(query);
                break;

            } catch (SQLException | IOException | ParseException | InterruptedException | ClassNotFoundException ex) {
                Logger.getLogger(CS.class.getName()).log(Level.SEVERE, null, ex);
                cnt++;
            }
        }
    }
    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus

    }//GEN-LAST:event_formWindowGainedFocus

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
            java.util.logging.Logger.getLogger(statuswindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(statuswindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(statuswindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(statuswindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new statuswindow().setVisible(true);
            }
        });

    }

    void GetTitle() {
        HTTP http = new HTTP();
        String toParse = null;
        try {
            toParse = http.get("https://beam.pro/api/v1/channels/" + CS.ChanID);
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(statuswindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONObject chaninfo = new JSONObject();
        try {
            chaninfo.putAll((JSONObject) new JSONParser().parse(toParse));
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        String title = null;
        if (!chaninfo.isEmpty()) {
            title = chaninfo.get("name").toString();
        }
        JSONObject type = (JSONObject) chaninfo.get("type");
        String game = (String) type.get("name");
        curGame = game;
        gamelistbox.setModel(CS.setgamelistmodel(game));
        gamelistbox.setSelectedItem(game);
        this.StreamTitle.setText(title);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField GSearch;
    private javax.swing.JTextArea StreamTitle;
    private javax.swing.JButton cancel;
    private javax.swing.Box.Filler filler1;
    private static javax.swing.JComboBox gamelistbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton save;
    // End of variables declaration//GEN-END:variables
}
