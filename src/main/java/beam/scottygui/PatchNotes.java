/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui;

/**
 *
 * @author tjhasty
 */
public class PatchNotes extends javax.swing.JFrame {

    /**
     * Creates new form PatchNotes
     */
    public PatchNotes() {
        initComponents();
        this.curPatchNotes.setCaretPosition(0);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        curPatchNotes = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ScottyGUI Patch Notes");
        setAlwaysOnTop(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        curPatchNotes.setEditable(false);
        curPatchNotes.setColumns(20);
        curPatchNotes.setLineWrap(true);
        curPatchNotes.setRows(5);
        curPatchNotes.setText("V59:\nAdded the ability for streamers to give their authkey to a mod for use within the GUI, allowing mods to log into the GUI on their behalf for their channel. You get this Authkey from the settings tab. Mmmm sexyness.\n\nV58: \nAdded email entry and resending of the verification email. Uses for this will be announced over the next several days.\n\nV57:\nAdded patch notes auto-opening on new update on first login and more code updates in prep for new site sexyness and additional gui features coming soon.\n\nV56:\nCode cleanup and added ability to have scotty join your channel if he is not in it when you log into the gui.\n\nV55:\nFixed a booboo in cookie handling when getting subscriber numbers.\n\nV53:\nAdded subscribers this session to the statistics page (Untested)\n\nV52:\nAdded Total Followers and Subs to the main control panel window.\n\nV50:\nAdded follower count for this session under statistics\n\nV49:\nAdded default settings to the Alert pane for followers, meaning you can set up everything, some things, or setup nothing and it will still work.\nMoved woosh notification sound internal, before it would pull it from a web server once per program startup.\n\nV48:\nGames are now searchable when setting a new game.\n\nV47:\nCan now change the title of your stream and the game in the GUI.\n\nV46:\nBetter chat reconnect handler in case of a socket disconnect.\n\nV45:\nLimited how many lines of chat the GUI will show to 100 lines, this is to stop it from eating memory over a long time of use or when used in active channels.\n\nV44:\nBetter error handling.\nAdded whisper sending and receiving.\nAdded Sub Icons on channels that are partners.\n\nV43:\nAdded Subscriber alert and bonus points settings.\nAdded Twitter alert message and toggle.\nAdded Link Title, User Join/Leave, Leet Speek Donator, and a few other toggles\nProbably added something else I forgot about as well\n\nV42:\nFix for some people not being able to log in. Traced it down to certain special characters in passwords not being handled correctly.\n\nV40:\nLightened up the blue color of normal chat users. Found the dark blue on black (which is default when just using \"Blue\" instead of a hex number, made my eye unhappy.\n\nV39: \nFixed Chat and Emoticons now display properly.\n\nV37:\nMuch improved chat windows. Purged/Deleted messages can now wither have a line through them, or hidden completely. Added a setting to toggle this under the Settings tab.\nTwo new files are created. Last_5_Followers.txt and Last_Follower.txt. You can guess what they do :-)\n\nV36:\nMigrated backend to LiveLoading, so userlists, viewer count, follower alerts, and more now happen instantly and much more smoothly\n\nV35:\nMajor fix to make the follower alert more reliable for channels with over 100 followers.\n\nV34:\nYou can not generate/regenerate a static authkey that can be used outside of ScottyGUI.\n\n\nV32:\nAdded Roulette toggle.\nSet so if points are off all points settings are untouchable.\nAdded Whitelist display.\n");
        curPatchNotes.setWrapStyleWord(true);
        jScrollPane2.setViewportView(curPatchNotes);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 910, 560));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(PatchNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PatchNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PatchNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PatchNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PatchNotes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea curPatchNotes;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
