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
        curPatchNotes.setText("V81:\nAdded ability to chat AS the bot. Why you ask? The better question is, if you choke a smurf, what color do they turn?\n\nV78:\nCode changes to mirror Scottybot back end changes I made.\nAdded the ability to enable command based sound stacking, meaning no longer restricted to 1 sound at a time. Setting is in the Command Sounds window.\n\nV77:\nResized to make it not so darn big.\nAdded ability to change the site restrict level for existing commands.\nAdded ability to change stream ranks (!rank command) name and hours per rank. This can be found under settings.\n\nV76:\nAdded ability to hide parts of or the whole command from the commands list website for your channel when adding a command. (Great for hiding repeating or secret commands from the list). I will add the ability to modify this after creation in a future update.\n\nV75:\nAdded Points Per MSG to the points settings area. Note: These points only tick while the channel is live to prevent abuse.\n\nV74:\nFixed formatting in all pages to be able to handle different operating systems properly.\n\nV73:\nAdded inter-GUI chat for streamers and their mods to help coordinate and communicate in private.\nCan now sort points by either name or points.\n\nV72:\nFix for subscriber emotes not working in chat.\n\nV71:\nAdded a control socket health checker to make sure the connection dosnt stall while staying connected.\n\nV70:\nSome adjustments and additional threading to try to prevent some bottle necks with the control socket.\n\nV69:\nRe-Move update checker to first thing the GUI does, was reverted back to post-login on accident.\n\nV68:\nChange API Login from a Get to a Post Method, how it should have been from the beginning. Also shut up Wolfstar76.\n\nV67:\nMoved command sounds to the commands tab, where it should have been.\n\nV66: \nGUI now checks for updates before asking to log in. Your welcome WolfStar76.\nNow able to set and remove command point costs.\n\nV65:\nFixed an escaping issue that was causing certain characters to not show up due to chat being html based.\n\nV64:\nYou can now check your points database in the Points tab, can also add, remove, giveall, and purge all.\n\nV63: \nFixed a typo in the Scotty control websocket that prevented reconnects from happening.\n\nV62:\nAdded a socket status indicator for the control and command socket (what allows sound on command right now) and greating improved the reliability of reconnecting on failure.\n\nV61:\nAdded ability to play a MP3 based on a command ran. You will find the settings for this under the settings tab. This is disabled when in moderator mode. Also, no volume control yet, so listerner beware.\n\nV60:\nSet button to change channel title and status to go invisible when moderating a channel with the GUI since mods are unable to do this.\n\nV59:\nAdded the ability for streamers to give their authkey to a mod for use within the GUI, allowing mods to log into the GUI on their behalf for their channel. You get this Authkey from the settings tab. Mmmm sexyness.\n\nV58: \nAdded email entry and resending of the verification email. Uses for this will be announced over the next several days.\n\nV57:\nAdded patch notes auto-opening on new update on first login and more code updates in prep for new site sexyness and additional gui features coming soon.\n\nV56:\nCode cleanup and added ability to have scotty join your channel if he is not in it when you log into the gui.\n\nV55:\nFixed a booboo in cookie handling when getting subscriber numbers.\n\nV53:\nAdded subscribers this session to the statistics page (Untested)\n\nV52:\nAdded Total Followers and Subs to the main control panel window.\n\nV50:\nAdded follower count for this session under statistics\n\nV49:\nAdded default settings to the Alert pane for followers, meaning you can set up everything, some things, or setup nothing and it will still work.\nMoved woosh notification sound internal, before it would pull it from a web server once per program startup.\n\nV48:\nGames are now searchable when setting a new game.\n\nV47:\nCan now change the title of your stream and the game in the GUI.\n\nV46:\nBetter chat reconnect handler in case of a socket disconnect.\n\nV45:\nLimited how many lines of chat the GUI will show to 100 lines, this is to stop it from eating memory over a long time of use or when used in active channels.\n\nV44:\nBetter error handling.\nAdded whisper sending and receiving.\nAdded Sub Icons on channels that are partners.\n\nV43:\nAdded Subscriber alert and bonus points settings.\nAdded Twitter alert message and toggle.\nAdded Link Title, User Join/Leave, Leet Speek Donator, and a few other toggles\nProbably added something else I forgot about as well\n\nV42:\nFix for some people not being able to log in. Traced it down to certain special characters in passwords not being handled correctly.\n\nV40:\nLightened up the blue color of normal chat users. Found the dark blue on black (which is default when just using \"Blue\" instead of a hex number, made my eye unhappy.\n\nV39: \nFixed Chat and Emoticons now display properly.\n\nV37:\nMuch improved chat windows. Purged/Deleted messages can now wither have a line through them, or hidden completely. Added a setting to toggle this under the Settings tab.\nTwo new files are created. Last_5_Followers.txt and Last_Follower.txt. You can guess what they do :-)\n\nV36:\nMigrated backend to LiveLoading, so userlists, viewer count, follower alerts, and more now happen instantly and much more smoothly\n\nV35:\nMajor fix to make the follower alert more reliable for channels with over 100 followers.\n\nV34:\nYou can not generate/regenerate a static authkey that can be used outside of ScottyGUI.\n\n\nV32:\nAdded Roulette toggle.\nSet so if points are off all points settings are untouchable.\nAdded Whitelist display.\n");
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
