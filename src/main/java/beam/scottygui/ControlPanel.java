/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.ChatHandler.ChatPopOut;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.AuthKey;
import static beam.scottygui.Stores.CS.BadWordsList;
import static beam.scottygui.Stores.CS.ChanID;
import static beam.scottygui.Stores.CS.ChatCache;
import static beam.scottygui.Stores.CS.ChatUserList;
import static beam.scottygui.Stores.CS.GUILoadSettings;
import static beam.scottygui.Stores.CS.GUISaveSettings;
import static beam.scottygui.Stores.CS.GetSettings;
import static beam.scottygui.Stores.CS.SendMSG;
import static beam.scottygui.Stores.CS.Username;
import static beam.scottygui.Stores.CS.chatArray;
import static beam.scottygui.Stores.CS.chatObject;
import static beam.scottygui.Stores.CS.extchat;
import static beam.scottygui.Stores.CS.llSocket;
import static beam.scottygui.Stores.CS.newline;
import static beam.scottygui.Stores.CS.session;
import beam.scottygui.TwitterInfo.TwitterAuthInfo;
import beam.scottygui.Utils.FontChooser;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.JSONUtil;
import beam.scottygui.chanstatus.statuswindow;
import beam.scottygui.cmdSounds.cmdListView;
import beam.scottygui.cmdcontrol.AddEditCMD;
import beam.scottygui.cmdcontrol.CMDCost;
import beam.scottygui.cmdcontrol.DeletePermAdjust;
import beam.scottygui.cmdcontrol.RepeatList;
import beam.scottygui.quotecontrol.addquote;
import beam.scottygui.quotecontrol.delquote;
import beam.scottygui.websocket.WebSocket;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;
import javax.websocket.EncodeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tjhasty
 */
public final class ControlPanel extends javax.swing.JFrame {

    HTTP http = new HTTP();
    JSONParser parser = new JSONParser();
    JSONUtil json = new JSONUtil();
    Integer CurVer = CS.CurVer;
    WebSocket socket = new WebSocket();

    /**
     * Creates new form ControlPanel
     *
     * @return
     */
    public void DumpCurVer() {

        PrintStream VerPrint = null;
        try {
            VerPrint = new PrintStream(new FileOutputStream("CurVer.json"));
        } catch (FileNotFoundException ex) {

        }
        JSONObject curver = new JSONObject();
        curver.put("CurVer", CurVer.toString());
        try {
            curver.put("checksum", CS.getCheckSum());
        } catch (NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        VerPrint.print(curver.toString());
        VerPrint.close();
    }

    public void SetCurViewers(String curnum) {
        this.CurViewers.setText(curnum + " viewers");
    }

    //    public void PopChatList() {
    //        this.Viewers.setModel(ChatUserList);
    //        new Thread("Update Viewer List") {
    //            @Override
    //            public void run() {
    //                while (true) {
    //                    //System.out.println("Populating Viewer List");
    //                    JSONArray InitUserList = null;
    //
    //                    while (true) {
    //                        try {
    //                            InitUserList = (JSONArray) parser.parse(http.BeamGet("https://beam.pro/api/v1/chats/" + ChanID + "/users"));
    //                            break;
    //                        } catch (ParseException ex) {
    //                            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
    //                        }
    //                    }
    //                    ChatUserList.clear();
    //                    for (Object t : InitUserList) {
    //                        JSONObject obj = (JSONObject) t;
    //                        ChatUserList.add(obj.get("user_name").toString());
    //                    }
    //                    try {
    //                        Thread.sleep(15000);
    //                    } catch (InterruptedException ex) {
    //                        Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
    //                    }
    //                }
    //            }
    //        }.start();
    //
    //    }
    public ControlPanel() {

        initComponents();
        GUILoadSettings();
        this.setTitle("ScottyGUI Ver. " + this.CurVer);
        if (CS.ModMode) {
            this.cmdsoundbutton.setVisible(false);
        }
        ControlPanel.ControlStatus.setText("Connecting");
        DumpCurVer();
        CS.extchat = new ChatPopOut();
        //Set chat window to auto-scroll
        DefaultCaret caret = (DefaultCaret) this.ChatOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        try {
            PopCmdText();
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            PopQuoteList();
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        PopBadWords();

        new Thread("LiveLoad Thread Pinger") {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (llSocket != null) {
                            llSocket.getAsyncRemote().sendText("2");
                        }
                    } catch (Exception e) {
                    } finally {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }.start();

        new Thread("5 Minutes CMD Refresh") {
            @Override
            public void run() {
                while (true) {
                    new Thread("PopCmdText") {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    PopCmdText();
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
                    }.start();

                    new Thread("PopQuoteText") {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    PopQuoteList();
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
                    }.start();
//                    new Thread("Populate Settings") {
//                        @Override
//                        public void run() {
//                            try {
//                                RefreshAllSettings();
//                                PopBadWords();
//                            } catch (Exception ex) {
//                                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//
//                        }
//                    }.start();

                    try {
                        Thread.sleep(5 * 60 * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }.start();

        Viewers.setModel(ChatUserList);
        //this.PopChatList();
        this.socket.connect(ChanID);
        this.PopGuiSettings();
//        LiveLoadHandler llh = new LiveLoadHandler();
//        try {
//            llh.attach();
//        } catch (Exception ex) {
//            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
        String html1 = "<html> <body bgcolor=\"black\">";
        String html2 = "</html>";
        ChatOutput.setText(html1 + html2);
        extchat.ChatOutput.setText(html1 + html2);

    }

    public void PopQuoteList() throws ParseException {
        JSONObject QList = (JSONObject) parser.parse(http.GetScotty(CS.apiLoc + "/quotes?authkey=" + AuthKey));
        String output = "";
        int NumOfQuotes = 0;
        for (Object t : QList.keySet()) {
            if (!"failed".equals(t.toString())) {
                NumOfQuotes++;
            }
            if ("".equals(output)) {

                output = "ID: " + t.toString() + " - " + QList.get(t);
            } else {

                output = output + newline + newline + "ID: " + t.toString() + " - " + QList.get(t);
            }

        }
        this.QuotePanel.setText(output);
        this.NumOfQuotes.setText(String.valueOf(NumOfQuotes + " quotes."));
    }

    public void PopCmdText() throws ParseException {
        JSONObject CmdOutput = new JSONObject();
        CmdOutput.putAll((JSONObject) parser.parse(http.GetScotty(CS.apiLoc + "/commands?authkey=" + AuthKey)));
        JSONArray T = new JSONArray();
        System.out.println(CmdOutput.toString());
        T.addAll((JSONArray) CmdOutput.get("Commands"));
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
            out = out + newline + newline + obj.get("cmd") + " - Level: " + restrictlevel + " - " + obj.get("text") + " - Count: " + obj.get("cmdcount");
        }

        this.CmdInfo.setText(out);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jFrame1 = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        jDialog1 = new javax.swing.JDialog();
        jDialog2 = new javax.swing.JDialog();
        jButton6 = new javax.swing.JButton();
        CurViewers = new javax.swing.JLabel();
        TopViewers = new javax.swing.JLabel();
        YouTube = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        CmdInfo = new javax.swing.JTextArea();
        RepeatList = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        RefreshCMDs = new javax.swing.JButton();
        comCost = new javax.swing.JButton();
        cmdsoundbutton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        QuotePanel = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        QEnabled = new javax.swing.JCheckBox();
        NumOfQuotes = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        addquotebutton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        LinksOnOff = new javax.swing.JToggleButton();
        RepeatOnOff = new javax.swing.JToggleButton();
        CapsOnOff = new javax.swing.JToggleButton();
        SymbolsOnOff = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        FOnOff = new javax.swing.JToggleButton();
        CapPercent = new javax.swing.JSlider();
        SymPercent = new javax.swing.JSlider();
        TimoutDuration = new javax.swing.JSlider();
        CapsPercentDis = new javax.swing.JLabel();
        SymPercentDis = new javax.swing.JLabel();
        TimeoutLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        BadWordList = new javax.swing.JList();
        addbadword = new javax.swing.JButton();
        AddBadWord = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        RemoveBadWord = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        settingsTabs = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        PSettings = new javax.swing.JPanel();
        PEnabled = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        PointsName = new javax.swing.JTextField();
        PRenBut = new javax.swing.JButton();
        EditPoints = new javax.swing.JButton();
        PWhenLive = new javax.swing.JTextField();
        PWhenIdle = new javax.swing.JTextField();
        PStartPoints = new javax.swing.JTextField();
        BHEnabled = new javax.swing.JCheckBox();
        REnabled = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        RouletteEnable = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        PSubPoints = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        PPM = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        SettingsPanel = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        FollowSoundSet = new javax.swing.JButton();
        FollowIMGSet = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        SetFollowAlertMsg = new javax.swing.JButton();
        FollowerMSGFont = new javax.swing.JButton();
        SetFontColor = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        WooshMeEnabled = new javax.swing.JCheckBox();
        showPChat = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        FollowEnabled = new javax.swing.JCheckBox();
        EFollowMsg = new javax.swing.JButton();
        OnlyWhenLiveEnabled = new javax.swing.JCheckBox();
        ClearCmdsEnabled = new javax.swing.JCheckBox();
        PSubAlert = new javax.swing.JCheckBox();
        MeOutput = new javax.swing.JCheckBox();
        ESubMessage = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        TwitterAlertMSG = new javax.swing.JButton();
        AutoTweet = new javax.swing.JCheckBox();
        JoinAnnounce = new javax.swing.JCheckBox();
        LeaveAnnounce = new javax.swing.JCheckBox();
        LinkTitle = new javax.swing.JCheckBox();
        CUsernamePassword = new javax.swing.JButton();
        ResetScottyName = new javax.swing.JButton();
        StoredAuthKey = new javax.swing.JTextField();
        ShowStoredKey = new javax.swing.JButton();
        GenNewStoredKey = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        PNotes = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        DonatorPanel = new javax.swing.JPanel();
        DonationPane = new javax.swing.JPanel();
        YodaEnabled = new javax.swing.JCheckBox();
        ChatEnabled = new javax.swing.JCheckBox();
        ChuckEnabled = new javax.swing.JCheckBox();
        LeetSpeek = new javax.swing.JCheckBox();
        setEmail = new javax.swing.JButton();
        resendEmail = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        CustRankTable = new javax.swing.JTable();
        CustRankSave = new javax.swing.JButton();
        UndoRankChanges = new javax.swing.JButton();
        ResetRanksScottyDefaults = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        ChatOutput = new javax.swing.JEditorPane();
        ChatSend = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        Viewers = new javax.swing.JList();
        jLabel14 = new javax.swing.JLabel();
        YouBot = new javax.swing.JToggleButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        showWhitelist = new javax.swing.JTextPane();
        jLabel16 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        PointsTable = new javax.swing.JTable();
        GivePoints = new javax.swing.JButton();
        delpoints = new javax.swing.JButton();
        giveall = new javax.swing.JButton();
        purgepoints = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        smchat = new javax.swing.JTable();
        relaymsg = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        UChatters = new javax.swing.JLabel();
        PercentRetainedViewers = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        SessionMsgCount = new javax.swing.JLabel();
        FolCounter = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        SubsThisSession = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel9 = new javax.swing.JPanel();
        AlertPaneOpen = new javax.swing.JButton();
        StreamSet = new javax.swing.JButton();
        TotSubs = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        TotFollowers = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        ControlStatus = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ScottyGUI Ver. " + this.CurVer);
        setResizable(false);

        jButton6.setText("Chat");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        CurViewers.setText("Offline");
        CurViewers.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        TopViewers.setText("0 Top Viewers");

        YouTube.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                YouTubeMouseClicked(evt);
            }
        });

        CmdInfo.setEditable(false);
        CmdInfo.setColumns(20);
        CmdInfo.setLineWrap(true);
        CmdInfo.setRows(5);
        CmdInfo.setWrapStyleWord(true);
        jScrollPane1.setViewportView(CmdInfo);

        RepeatList.setText("Repeat List");
        RepeatList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RepeatListActionPerformed(evt);
            }
        });

        jButton2.setText("Reset/Permlevel/Delete CMD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Add/Edit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        RefreshCMDs.setText("Refresh");
        RefreshCMDs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshCMDsActionPerformed(evt);
            }
        });

        comCost.setText("Set Command Cost");
        comCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comCostActionPerformed(evt);
            }
        });

        cmdsoundbutton.setText("Command Sounds");
        cmdsoundbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdsoundbuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(RefreshCMDs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comCost, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdsoundbutton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RepeatList, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 133, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(RefreshCMDs)
                    .addComponent(jButton2)
                    .addComponent(comCost)
                    .addComponent(cmdsoundbutton)
                    .addComponent(RepeatList))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        YouTube.addTab("Commands", jPanel1);

        QuotePanel.setEditable(false);
        QuotePanel.setColumns(20);
        QuotePanel.setLineWrap(true);
        QuotePanel.setRows(5);
        QuotePanel.setWrapStyleWord(true);
        jScrollPane2.setViewportView(QuotePanel);

        jButton4.setText("Refresh");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        QEnabled.setText("Quotes Enabled");
        QEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QEnabledActionPerformed(evt);
            }
        });

        NumOfQuotes.setText("jLabel4");

        jButton3.setText("Delete Quotes");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        addquotebutton.setText("Add Quote");
        addquotebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addquotebuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(addquotebutton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NumOfQuotes, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(QEnabled)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addContainerGap(423, Short.MAX_VALUE))))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addquotebutton)
                    .addComponent(jButton3)
                    .addComponent(NumOfQuotes)
                    .addComponent(QEnabled)
                    .addComponent(jButton4))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        YouTube.addTab("Quotes", jPanel2);

        LinksOnOff.setText("Links Switch");
        LinksOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LinksOnOffActionPerformed(evt);
            }
        });

        RepeatOnOff.setText("Repeat Switch");
        RepeatOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RepeatOnOffActionPerformed(evt);
            }
        });

        CapsOnOff.setText("Caps Switch");
        CapsOnOff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                CapsOnOffMouseReleased(evt);
            }
        });
        CapsOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CapsOnOffActionPerformed(evt);
            }
        });

        SymbolsOnOff.setText("Symbols Switch");
        SymbolsOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SymbolsOnOffActionPerformed(evt);
            }
        });

        jLabel5.setText("TimeOut Duration in Minutes");

        FOnOff.setText("Filter Master Switch");
        FOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FOnOffActionPerformed(evt);
            }
        });

        CapPercent.setMinimum(30);
        CapPercent.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                CapPercentMouseDragged(evt);
            }
        });
        CapPercent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                CapPercentMouseReleased(evt);
            }
        });

        SymPercent.setMinimum(30);
        SymPercent.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                SymPercentMouseDragged(evt);
            }
        });
        SymPercent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SymPercentMouseReleased(evt);
            }
        });
        SymPercent.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                SymPercentPropertyChange(evt);
            }
        });

        TimoutDuration.setMaximum(60);
        TimoutDuration.setMinimum(1);
        TimoutDuration.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                TimoutDurationMouseDragged(evt);
            }
        });
        TimoutDuration.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TimoutDurationMouseReleased(evt);
            }
        });
        TimoutDuration.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                TimoutDurationPropertyChange(evt);
            }
        });

        CapsPercentDis.setText("jLabel6");

        SymPercentDis.setText("jLabel7");

        TimeoutLabel.setText("jLabel7");

        BadWordList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(BadWordList);

        addbadword.setText("Add word to list");
        addbadword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbadwordActionPerformed(evt);
            }
        });

        jLabel6.setText("New Bad Word");

        jLabel7.setText("Bad Words Filter");

        RemoveBadWord.setText("Remove From List");
        RemoveBadWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveBadWordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(SymbolsOnOff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(CapsOnOff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(SymPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(SymPercentDis, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel14Layout.createSequentialGroup()
                                                .addComponent(TimoutDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                                .addComponent(CapPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(CapsPercentDis, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(TimeoutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(RepeatOnOff, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                                    .addComponent(LinksOnOff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(237, 237, 237)
                        .addComponent(FOnOff, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddBadWord, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addbadword, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(RemoveBadWord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(FOnOff, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(LinksOnOff)
                                .addGap(25, 25, 25)
                                .addComponent(RepeatOnOff)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addComponent(CapsOnOff)
                                        .addGap(22, 22, 22)
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(SymPercentDis)
                                            .addComponent(SymbolsOnOff)))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel14Layout.createSequentialGroup()
                                                .addComponent(CapsPercentDis)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addGroup(jPanel14Layout.createSequentialGroup()
                                                .addComponent(CapPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(SymPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TimeoutLabel)
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(TimoutDuration, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(AddBadWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addbadword))))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RemoveBadWord)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(197, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        YouTube.addTab("Filtering", jPanel3);

        settingsTabs.setToolTipText("");
        settingsTabs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                settingsTabsFocusGained(evt);
            }
        });
        settingsTabs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                settingsTabsMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                settingsTabsMouseClicked(evt);
            }
        });
        settingsTabs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                settingsTabsKeyReleased(evt);
            }
        });

        PEnabled.setText("Points Enabled");
        PEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PEnabledActionPerformed(evt);
            }
        });

        jLabel8.setText("Points Name");

        PointsName.setEditable(false);
        PointsName.setText("jTextField1");

        PRenBut.setText("Rename");
        PRenBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PRenButActionPerformed(evt);
            }
        });

        EditPoints.setText("Edit");
        EditPoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditPointsActionPerformed(evt);
            }
        });

        PWhenLive.setEditable(false);
        PWhenLive.setText("jTextField2");

        PWhenIdle.setEditable(false);
        PWhenIdle.setText("jTextField2");
        PWhenIdle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PWhenIdleActionPerformed(evt);
            }
        });

        PStartPoints.setEditable(false);
        PStartPoints.setText("jTextField3");

        BHEnabled.setText("BankHeist Enabled");
        BHEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BHEnabledActionPerformed(evt);
            }
        });

        REnabled.setText("Raffle Enabled");
        REnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                REnabledActionPerformed(evt);
            }
        });

        jLabel9.setText("Points When Live");

        jLabel10.setText("Points When Not Live");

        jLabel11.setText("Starting Points");

        RouletteEnable.setText("Roulette Enabled");
        RouletteEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RouletteEnableActionPerformed(evt);
            }
        });

        jLabel17.setText("Points tick every 15 minutes");

        PSubPoints.setEditable(false);
        PSubPoints.setText("jTextField3");

        jLabel20.setText("Sub Bonus Points");

        PPM.setEditable(false);
        PPM.setText("jTextField1");

        jLabel21.setText("Points Per MSG");

        javax.swing.GroupLayout PSettingsLayout = new javax.swing.GroupLayout(PSettings);
        PSettings.setLayout(PSettingsLayout);
        PSettingsLayout.setHorizontalGroup(
            PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PSettingsLayout.createSequentialGroup()
                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PSettingsLayout.createSequentialGroup()
                        .addGap(180, 180, 180)
                        .addComponent(PEnabled))
                    .addGroup(PSettingsLayout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PSettingsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(BHEnabled)
                        .addGap(5, 5, 5)
                        .addComponent(RouletteEnable)
                        .addGap(3, 3, 3)
                        .addComponent(REnabled))
                    .addGroup(PSettingsLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PSettingsLayout.createSequentialGroup()
                                .addComponent(EditPoints, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                                    .addComponent(PWhenLive))
                                .addGap(28, 28, 28)
                                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(PWhenIdle))
                                .addGap(17, 17, 17)
                                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(PStartPoints)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(27, 27, 27)
                                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(PSubPoints)))
                            .addGroup(PSettingsLayout.createSequentialGroup()
                                .addComponent(PRenBut)
                                .addGap(25, 25, 25)
                                .addComponent(PointsName, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PPM))
                .addGap(53, 53, 53))
        );
        PSettingsLayout.setVerticalGroup(
            PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PSettingsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(PEnabled)
                .addGap(25, 25, 25)
                .addComponent(jLabel8)
                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PSettingsLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PointsName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PSettingsLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PSettingsLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(PRenBut)))
                .addGap(10, 10, 10)
                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel20)
                        .addComponent(jLabel10)
                        .addComponent(jLabel21)))
                .addGap(3, 3, 3)
                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(PWhenLive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(EditPoints))
                    .addComponent(PWhenIdle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(PSubPoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(PStartPoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(PPM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(50, 50, 50)
                .addGroup(PSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BHEnabled)
                    .addComponent(RouletteEnable)
                    .addComponent(REnabled))
                .addContainerGap(247, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(PSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(PSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        settingsTabs.addTab("Points", jPanel5);

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        FollowSoundSet.setText("Set Follower Sound");
        FollowSoundSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowSoundSetActionPerformed(evt);
            }
        });

        FollowIMGSet.setText("Set Follower Image");
        FollowIMGSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowIMGSetActionPerformed(evt);
            }
        });

        jLabel15.setText("Alert Pane Settings");

        SetFollowAlertMsg.setText("Set Follower Message");
        SetFollowAlertMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetFollowAlertMsgActionPerformed(evt);
            }
        });

        FollowerMSGFont.setText("Set Follower Font");
        FollowerMSGFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowerMSGFontActionPerformed(evt);
            }
        });

        SetFontColor.setText("Set Font Color");
        SetFontColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetFontColorActionPerformed(evt);
            }
        });

        jButton8.setText("Reset Alerts");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLayeredPane1.setLayer(FollowSoundSet, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(FollowIMGSet, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel15, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(SetFollowAlertMsg, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(FollowerMSGFont, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(SetFontColor, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jButton8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel15)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FollowSoundSet, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(FollowIMGSet, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SetFollowAlertMsg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                            .addComponent(FollowerMSGFont, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SetFontColor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(5, 5, 5)
                .addComponent(FollowSoundSet)
                .addGap(5, 5, 5)
                .addComponent(FollowIMGSet)
                .addGap(5, 5, 5)
                .addComponent(SetFollowAlertMsg)
                .addGap(4, 4, 4)
                .addComponent(FollowerMSGFont)
                .addGap(4, 4, 4)
                .addComponent(SetFontColor)
                .addGap(4, 4, 4)
                .addComponent(jButton8))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setText("Chat Window Settings");

        WooshMeEnabled.setText("Woosh me");
        WooshMeEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WooshMeEnabledActionPerformed(evt);
            }
        });

        showPChat.setText("Show Deleted Chat");
        showPChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPChatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel12))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WooshMeEnabled)
                    .addComponent(showPChat)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel12)
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WooshMeEnabled)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(showPChat))))
        );

        FollowEnabled.setText("Follower Alert Enabled");
        FollowEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowEnabledActionPerformed(evt);
            }
        });

        EFollowMsg.setText("Edit Chat Follower Message");
        EFollowMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EFollowMsgActionPerformed(evt);
            }
        });

        OnlyWhenLiveEnabled.setText("OnlyWhenLive Enabled");
        OnlyWhenLiveEnabled.setToolTipText("This will prevent most commands from running when you are not online.");
        OnlyWhenLiveEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnlyWhenLiveEnabledActionPerformed(evt);
            }
        });

        ClearCmdsEnabled.setText("Clear Commands Enabled");
        ClearCmdsEnabled.setToolTipText("When enabled, will clear all commands by Non-mods once ran. It will still act on those commands.");
        ClearCmdsEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearCmdsEnabledActionPerformed(evt);
            }
        });

        PSubAlert.setText("Enable Subscriber Alert");
        PSubAlert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PSubAlertActionPerformed(evt);
            }
        });

        MeOutput.setText("Set /me Bot Output");
        MeOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MeOutputActionPerformed(evt);
            }
        });

        ESubMessage.setText("Edit New Subscriber Message");
        ESubMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ESubMessageActionPerformed(evt);
            }
        });

        jButton7.setText("Set Twitter Auth Info");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        TwitterAlertMSG.setText("Edit Auto Tweet Message");
        TwitterAlertMSG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TwitterAlertMSGActionPerformed(evt);
            }
        });

        AutoTweet.setText("Enable Auto Tweet");
        AutoTweet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AutoTweetActionPerformed(evt);
            }
        });

        JoinAnnounce.setText("Enable User Join Announce");
        JoinAnnounce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JoinAnnounceActionPerformed(evt);
            }
        });

        LeaveAnnounce.setText("Enable User Leave Announce");
        LeaveAnnounce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeaveAnnounceActionPerformed(evt);
            }
        });

        LinkTitle.setText("Enable Link Title Showing");
        LinkTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LinkTitleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ESubMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .addComponent(EFollowMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AutoTweet)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(TwitterAlertMSG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(216, 216, 216)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(JoinAnnounce)
                                    .addComponent(LeaveAnnounce)))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LinkTitle)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(FollowEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(OnlyWhenLiveEnabled)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ClearCmdsEnabled)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(MeOutput)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PSubAlert)))))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OnlyWhenLiveEnabled)
                    .addComponent(ClearCmdsEnabled)
                    .addComponent(MeOutput)
                    .addComponent(PSubAlert)
                    .addComponent(FollowEnabled))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LinkTitle)
                .addGap(67, 67, 67)
                .addComponent(AutoTweet)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EFollowMsg)
                            .addComponent(jButton7))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ESubMessage)
                            .addComponent(TwitterAlertMSG)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(JoinAnnounce)
                        .addGap(5, 5, 5)
                        .addComponent(LeaveAnnounce))))
        );

        CUsernamePassword.setText("Set Custom Bot Username/Password");
        CUsernamePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CUsernamePasswordActionPerformed(evt);
            }
        });

        ResetScottyName.setText("Reset Bot Name Back To Scottybot");
        ResetScottyName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetScottyNameActionPerformed(evt);
            }
        });

        StoredAuthKey.setEditable(false);
        StoredAuthKey.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ShowStoredKey.setText("Show Key");
        ShowStoredKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowStoredKeyActionPerformed(evt);
            }
        });

        GenNewStoredKey.setText("Generate New Key");
        GenNewStoredKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenNewStoredKeyActionPerformed(evt);
            }
        });

        jLabel18.setText("Stored Auth Key (Usable and static)");

        jButton5.setText("Check Updates");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        PNotes.setText("Patch Notes");
        PNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PNotesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SettingsPanelLayout = new javax.swing.GroupLayout(SettingsPanel);
        SettingsPanel.setLayout(SettingsPanelLayout);
        SettingsPanelLayout.setHorizontalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SettingsPanelLayout.createSequentialGroup()
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SettingsPanelLayout.createSequentialGroup()
                                .addGap(228, 228, 228)
                                .addComponent(jLabel18))
                            .addGroup(SettingsPanelLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(SettingsPanelLayout.createSequentialGroup()
                                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(SettingsPanelLayout.createSequentialGroup()
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(28, 28, 28)
                                        .addComponent(ShowStoredKey)
                                        .addGap(8, 8, 8)
                                        .addComponent(GenNewStoredKey))
                                    .addGroup(SettingsPanelLayout.createSequentialGroup()
                                        .addComponent(PNotes)
                                        .addGap(22, 22, 22)
                                        .addComponent(StoredAuthKey, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(CUsernamePassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ResetScottyName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(79, 79, 79)
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        SettingsPanelLayout.setVerticalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SettingsPanelLayout.createSequentialGroup()
                        .addGap(480, 480, 480)
                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(SettingsPanelLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(SettingsPanelLayout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(jLabel18)
                                .addGap(4, 4, 4)
                                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(PNotes)
                                    .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(StoredAuthKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(CUsernamePassword)))
                                .addGap(8, 8, 8)
                                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton5)
                                    .addComponent(ShowStoredKey)
                                    .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(GenNewStoredKey)
                                        .addComponent(ResetScottyName))))))
                    .addGroup(SettingsPanelLayout.createSequentialGroup()
                        .addGap(209, 209, 209)
                        .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        settingsTabs.addTab("Settings", SettingsPanel);

        YodaEnabled.setText("Yoda Enabled");
        YodaEnabled.setToolTipText("Enable Scottybot to speak like Yoda, you will!");
        YodaEnabled.setEnabled(false);
        YodaEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                YodaEnabledActionPerformed(evt);
            }
        });

        ChatEnabled.setText("!Chat Enabled");
        ChatEnabled.setToolTipText("Type !chat and a message, it will respond to you \"intelligently\"");
        ChatEnabled.setEnabled(false);
        ChatEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatEnabledActionPerformed(evt);
            }
        });

        ChuckEnabled.setText("!Chuck Enabled");
        ChuckEnabled.setToolTipText("Enabled !chuck commands for jokes");
        ChuckEnabled.setEnabled(false);
        ChuckEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChuckEnabledActionPerformed(evt);
            }
        });

        LeetSpeek.setText("L33t Enabled");
        LeetSpeek.setToolTipText("d035 7h15 m4k3 y0u h4ppy d0n3f34r?");
        LeetSpeek.setEnabled(false);
        LeetSpeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LeetSpeekActionPerformed(evt);
            }
        });

        setEmail.setText("Set your email address");
        setEmail.setEnabled(false);
        setEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setEmailActionPerformed(evt);
            }
        });

        resendEmail.setText("Resend verification email");
        resendEmail.setEnabled(false);
        resendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resendEmailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DonationPaneLayout = new javax.swing.GroupLayout(DonationPane);
        DonationPane.setLayout(DonationPaneLayout);
        DonationPaneLayout.setHorizontalGroup(
            DonationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DonationPaneLayout.createSequentialGroup()
                .addGroup(DonationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DonationPaneLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(ChuckEnabled))
                    .addGroup(DonationPaneLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(ChatEnabled))
                    .addGroup(DonationPaneLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(YodaEnabled))
                    .addGroup(DonationPaneLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(LeetSpeek))
                    .addGroup(DonationPaneLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(setEmail))
                    .addGroup(DonationPaneLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(resendEmail)))
                .addContainerGap(708, Short.MAX_VALUE))
        );
        DonationPaneLayout.setVerticalGroup(
            DonationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DonationPaneLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(ChuckEnabled)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ChatEnabled)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(YodaEnabled)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LeetSpeek)
                .addGap(283, 283, 283)
                .addComponent(setEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resendEmail))
        );

        javax.swing.GroupLayout DonatorPanelLayout = new javax.swing.GroupLayout(DonatorPanel);
        DonatorPanel.setLayout(DonatorPanelLayout);
        DonatorPanelLayout.setHorizontalGroup(
            DonatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DonationPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        DonatorPanelLayout.setVerticalGroup(
            DonatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DonationPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        settingsTabs.addTab("Donator Stuff", DonatorPanel);

        CustRankTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, ""},
                {null, null}
            },
            new String [] {
                "Hours", "Rank Text"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        CustRankTable.setColumnSelectionAllowed(true);
        CustRankTable.getTableHeader().setReorderingAllowed(false);
        CustRankTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                CustRankTableFocusLost(evt);
            }
        });
        jScrollPane9.setViewportView(CustRankTable);
        CustRankTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (CustRankTable.getColumnModel().getColumnCount() > 0) {
            CustRankTable.getColumnModel().getColumn(0).setMinWidth(150);
            CustRankTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            CustRankTable.getColumnModel().getColumn(0).setMaxWidth(150);
        }

        CustRankSave.setText("Save");
        CustRankSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustRankSaveActionPerformed(evt);
            }
        });

        UndoRankChanges.setText("Undo/Refresh");
        UndoRankChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UndoRankChangesActionPerformed(evt);
            }
        });

        ResetRanksScottyDefaults.setText("Reset to Defaults");
        ResetRanksScottyDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetRanksScottyDefaultsActionPerformed(evt);
            }
        });

        jButton10.setText("Add Row");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Delete Row");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel2.setText("Use (_hours_) variable to show hours (Case Sensetive)");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(UndoRankChanges)
                        .addGap(18, 18, 18)
                        .addComponent(CustRankSave)
                        .addGap(18, 18, 18)
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton11)
                        .addGap(408, 408, 408)
                        .addComponent(ResetRanksScottyDefaults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(278, 278, 278)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CustRankSave)
                    .addComponent(UndoRankChanges)
                    .addComponent(ResetRanksScottyDefaults)
                    .addComponent(jButton10)
                    .addComponent(jButton11))
                .addContainerGap())
        );

        settingsTabs.addTab("Custom Ranks", jPanel18);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(settingsTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 1047, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(settingsTabs)
        );

        YouTube.addTab("Settings", jPanel4);

        ChatOutput.setEditable(false);
        ChatOutput.setBackground(new java.awt.Color(0, 0, 0));
        ChatOutput.setBorder(new javax.swing.border.MatteBorder(null));
        ChatOutput.setContentType("text/html"); // NOI18N
        ChatOutput.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        ChatOutput.setText("");
        ChatOutput.setToolTipText("");
        ChatOutput.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ChatOutput.setDoubleBuffered(true);
        ChatOutput.setMaximumSize(getPreferredSize());
        ChatOutput.setMinimumSize(getPreferredSize());
        jScrollPane5.setViewportView(ChatOutput);

        ChatSend.setCaretColor(new java.awt.Color(255, 255, 255));
        ChatSend.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        ChatSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatSendActionPerformed(evt);
            }
        });
        ChatSend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ChatSendKeyPressed(evt);
            }
        });

        Viewers.setBackground(new java.awt.Color(0, 0, 0));
        Viewers.setForeground(new java.awt.Color(255, 255, 255));
        Viewers.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        Viewers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        Viewers.setAutoscrolls(false);
        Viewers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ViewersMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(Viewers);

        jLabel14.setText("Double click to purge");

        YouBot.setText("You");
        YouBot.setToolTipText("Switch between chatting as you or the bot.");
        YouBot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                YouBotActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 827, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addComponent(ChatSend)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(YouBot)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .addComponent(jScrollPane6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ChatSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(YouBot))
                .addGap(0, 446, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 5, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        YouTube.addTab("Chat", jPanel8);

        showWhitelist.setEditable(false);
        showWhitelist.setDoubleBuffered(true);
        jScrollPane4.setViewportView(showWhitelist);

        jLabel16.setText("There will be functions here to Add/Remove whitelist users in the future");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel16)))
                .addGap(9, 9, 9))
        );

        YouTube.addTab("Whitelist", jPanel11);

        PointsTable.setAutoCreateRowSorter(true);
        PointsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Viewer", "Points"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        PointsTable.setAutoscrolls(false);
        PointsTable.setColumnSelectionAllowed(true);
        PointsTable.getTableHeader().setReorderingAllowed(false);
        PointsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                PointsTableKeyReleased(evt);
            }
        });
        jScrollPane7.setViewportView(PointsTable);
        PointsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        GivePoints.setText("Give Points");
        GivePoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GivePointsActionPerformed(evt);
            }
        });

        delpoints.setText("Remove Points");
        delpoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delpointsActionPerformed(evt);
            }
        });

        giveall.setText("Give All Points");
        giveall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveallActionPerformed(evt);
            }
        });

        purgepoints.setText("Purge All Points");
        purgepoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purgepointsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(GivePoints, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(delpoints, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(giveall, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(purgepoints))
                .addContainerGap(595, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(GivePoints)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delpoints)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(giveall)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(purgepoints))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)))
                .addContainerGap())
        );

        YouTube.addTab("Points", jPanel12);

        smchat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Steamer and Mod Chat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        smchat.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        smchat.setFocusable(false);
        jScrollPane8.setViewportView(smchat);

        relaymsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relaymsgActionPerformed(evt);
            }
        });
        relaymsg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                relaymsgKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 1023, Short.MAX_VALUE)
                    .addComponent(relaymsg))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(relaymsg, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        YouTube.addTab("Streamer/Mod Chat", jPanel17);

        UChatters.setText("0 Unique Chatters This Session.");

        PercentRetainedViewers.setText("Not enough info yet for Retained Viewer Stats");

        jLabel13.setText("<<< BROKEN IGNORE");

        SessionMsgCount.setText("0 Messages This Session");

        FolCounter.setText("No followers yet this session");

        jButton9.setText("Reset Follower Counter");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        SubsThisSession.setText("No subscribers yet this session");

        jToggleButton1.setText("Reset Sub Counter");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(UChatters, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PercentRetainedViewers, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SessionMsgCount, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FolCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SubsThisSession, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            .addComponent(jButton9))))
                .addContainerGap(522, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(UChatters)
                .addGap(5, 5, 5)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PercentRetainedViewers)
                    .addComponent(jLabel13))
                .addGap(4, 4, 4)
                .addComponent(SessionMsgCount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FolCounter)
                .addGap(5, 5, 5)
                .addComponent(SubsThisSession)
                .addGap(217, 217, 217)
                .addComponent(jButton9)
                .addGap(5, 5, 5)
                .addComponent(jToggleButton1))
        );

        YouTube.addTab("Statistics", jPanel7);

        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        AlertPaneOpen.setText("Alert Pane");
        AlertPaneOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlertPaneOpenActionPerformed(evt);
            }
        });

        StreamSet.setText("Channel Name and Game");
        StreamSet.setActionCommand("Stream Title");
        StreamSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StreamSetActionPerformed(evt);
            }
        });

        TotSubs.setText("jLabel19");

        TotFollowers.setText("0 Followers");

        ControlStatus.setBackground(new java.awt.Color(255, 0, 0));
        ControlStatus.setEnabled(false);

        jLabel3.setText("Control Socket Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(YouTube, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CurViewers, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TopViewers, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TotFollowers, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(StreamSet)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(AlertPaneOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ControlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1010, 1010, 1010)
                                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(TotSubs, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 30, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CurViewers)
                        .addGap(0, 0, 0)
                        .addComponent(TopViewers)
                        .addGap(2, 2, 2)
                        .addComponent(TotFollowers))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(StreamSet)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton6)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(AlertPaneOpen)
                                .addComponent(ControlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))))
                .addGap(9, 9, 9)
                .addComponent(TotSubs)
                .addGap(11, 11, 11)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(YouTube, javax.swing.GroupLayout.PREFERRED_SIZE, 550, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void YouTubeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_YouTubeMouseClicked
        //        String tab = (ControlTab.getTitleAt(ControlTab.getSelectedIndex()));
        //        switch (tab.toLowerCase()) {
        //            case "commands": {
        //                try {
        //                    this.PopCmdText();
        //                } catch (ParseException ex) {
        //                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        //                }
        //            }
        //            break;
        //            case "quotes": {
        //                try {
        //                    this.PopQuoteList();
        //                } catch (ParseException ex) {
        //                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        //                }
        //            }
        //            break;
        //            case "filtering":
        //                this.PopFilterSettings();
        //                break;
        //        }
    }//GEN-LAST:event_YouTubeMouseClicked

    public void PopGuiSettings() {
        if (!CS.GUISettings.containsKey("WooshME")) {
            CS.GUISaveSettings("WooshME", "false");
        }
        if ("true".equals(CS.GUIGetSetting("WooshME"))) {
            this.WooshMeEnabled.setSelected(true);
        } else {
            this.WooshMeEnabled.setSelected(false);
        }
        if (!CS.GUISettings.containsKey("showpurged")) {
            CS.GUISaveSettings("showpurged", "false");
        }
        if ("false".equalsIgnoreCase(CS.GUIGetSetting("showpurged"))) {
            this.showPChat.setSelected(false);
        } else {
            this.showPChat.setSelected(true);
        }
    }
    private void ResetScottyNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetScottyNameActionPerformed
        try {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=CUsername&value=" + URLEncoder.encode("NULL", "UTF-8"));
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=CPassword&value=" + URLEncoder.encode("NULL", "UTF-8"));
            JOptionPane.showMessageDialog(rootPane, "Done, now type !rejoin in your channel");
        } catch (Exception e) {

        }
    }//GEN-LAST:event_ResetScottyNameActionPerformed

    private void CUsernamePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CUsernamePasswordActionPerformed
        String CUsername = null;
        String CPassword = null;
        CUsername = JOptionPane.showInputDialog(rootPane, "Enter Username For Your Custom Bot Name");
        if (CUsername == null) {
            return;
        }
        if (CUsername.toLowerCase().equals(Username.toLowerCase())) {
            JOptionPane.showMessageDialog(rootPane, "It's not smart to make the bot name your username.");
            return;
        }
        CPassword = JOptionPane.showInputDialog(rootPane, "Enter Password For Your Custom Bot Name");
        if (CPassword == null) {
            return;
        }
        if (CUsername != null && CPassword != null & !"".equals(CUsername) && !"".equals(CPassword)) {
            try {
                http.CLogin(CUsername, CPassword);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPane, "Username and/or Password failed.");
                return;
            }
            try {
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=CUsername&value=" + URLEncoder.encode(CUsername, "UTF-8"));
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=CPassword&value=" + URLEncoder.encode(CPassword, "UTF-8"));
                JOptionPane.showMessageDialog(rootPane, "Success, now in your channel just type !rejoin");
            } catch (UnsupportedEncodingException ex) {
                try {
                    http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=CUsername&value=" + URLEncoder.encode("NULL", "UTF-8"));
                    http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=CPassword&value=" + URLEncoder.encode("NULL", "UTF-8"));
                    JOptionPane.showMessageDialog(this, "Had an issue setting name, atempted to put back to default");
                } catch (UnsupportedEncodingException ex1) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }

        }
    }//GEN-LAST:event_CUsernamePasswordActionPerformed

    private void YodaEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YodaEnabledActionPerformed
        if (this.YodaEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseYoda&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseYoda&value=0");
        }
    }//GEN-LAST:event_YodaEnabledActionPerformed

    private void ChatEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatEnabledActionPerformed
        if (this.ChatEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseClever&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseClever&value=0");
        }
    }//GEN-LAST:event_ChatEnabledActionPerformed

    private void ChuckEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChuckEnabledActionPerformed
        if (this.ChuckEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseChuck&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseChuck&value=0");
        }
    }//GEN-LAST:event_ChuckEnabledActionPerformed

    private void PSubAlertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PSubAlertActionPerformed
        if (this.PSubAlert.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=subalert&value=" + 1);
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=subalert&value=" + 0);
        }
    }//GEN-LAST:event_PSubAlertActionPerformed

    private void ClearCmdsEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearCmdsEnabledActionPerformed
        if (this.ClearCmdsEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=PurgeCommands&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=PurgeCommands&value=0");
        }
    }//GEN-LAST:event_ClearCmdsEnabledActionPerformed

    private void OnlyWhenLiveEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnlyWhenLiveEnabledActionPerformed
        if (this.OnlyWhenLiveEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=OnlyWhenLive&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=OnlyWhenLive&value=0");
        }
    }//GEN-LAST:event_OnlyWhenLiveEnabledActionPerformed

    private void EFollowMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EFollowMsgActionPerformed
        String OldFollowMSG = GetSettings().get("FollowMSG").toString();
        String NewFollowMSG = JOptionPane.showInputDialog(rootPane, "Set your follower message. use (_follower_) as a placeholder for the follower.", OldFollowMSG);
        try {
            if (!NewFollowMSG.equals(OldFollowMSG)) {
                try {
                    http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=FollowMSG&value=" + URLEncoder.encode(NewFollowMSG, "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception e) {
            //cop out
        }
        try {
            RefreshAllSettings();
            this.PopulateAllSettings();
        } catch (Exception ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_EFollowMsgActionPerformed

    private void FollowEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FollowEnabledActionPerformed
        if (this.FollowEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseFollower&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseFollower&value=0");
        }
    }//GEN-LAST:event_FollowEnabledActionPerformed

    private void addbadwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbadwordActionPerformed

        try {
            String[] ToAdd = this.AddBadWord.getText().split(" ");
            http.GetScotty(CS.apiLoc + "/badwords/add?authkey=" + AuthKey + "&word=" + URLEncoder.encode(ToAdd[0], "UTF-8"));
            this.PopBadWords();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.AddBadWord.setText("");
    }//GEN-LAST:event_addbadwordActionPerformed

    private void RemoveBadWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveBadWordActionPerformed
        List<String> ToRemove = new ArrayList();
        ToRemove.addAll(this.BadWordList.getSelectedValuesList());
        for (String t : ToRemove) {
            try {
                http.GetScotty(CS.apiLoc + "/badwords/delete?authkey=" + AuthKey + "&word=" + URLEncoder.encode(t, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.PopBadWords();
    }//GEN-LAST:event_RemoveBadWordActionPerformed

    private void TimoutDurationPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_TimoutDurationPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_TimoutDurationPropertyChange

    private void TimoutDurationMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TimoutDurationMouseReleased
        http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=TimeOutLength&value=" + String.valueOf(this.TimoutDuration.getValue()));
    }//GEN-LAST:event_TimoutDurationMouseReleased

    private void TimoutDurationMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TimoutDurationMouseDragged
        this.TimeoutLabel.setText(String.valueOf(this.TimoutDuration.getValue()) + " Minutes");
    }//GEN-LAST:event_TimoutDurationMouseDragged

    private void CapPercentMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CapPercentMouseReleased
        http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=CapPercent&value=" + this.CapPercent.getValue());
    }//GEN-LAST:event_CapPercentMouseReleased

    private void CapPercentMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CapPercentMouseDragged
        this.CapsPercentDis.setText(String.valueOf(this.CapPercent.getValue()) + " Percent");        // TODO add your handling code here:
    }//GEN-LAST:event_CapPercentMouseDragged

    private void SymPercentPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SymPercentPropertyChange

    }//GEN-LAST:event_SymPercentPropertyChange

    private void SymPercentMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SymPercentMouseReleased
        http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=SymbolCount&value=" + this.SymPercent.getValue());        // TODO add your handling code here:
    }//GEN-LAST:event_SymPercentMouseReleased

    private void SymPercentMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SymPercentMouseDragged
        this.SymPercentDis.setText(String.valueOf(this.SymPercent.getValue()) + " Percent");        // TODO add your handling code here:
    }//GEN-LAST:event_SymPercentMouseDragged

    private void SymbolsOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SymbolsOnOffActionPerformed
        if (this.SymbolsOnOff.isSelected()) {
            this.SymbolsOnOff.setText("Symbols Enabled");
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseSymbols&value=1");
        } else {
            this.SymbolsOnOff.setText(("Symbols Disabled"));
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseSymbols&value=0");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_SymbolsOnOffActionPerformed

    private void CapsOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CapsOnOffActionPerformed
        if (this.CapsOnOff.isSelected()) {
            this.CapsOnOff.setText("Caps Enabled");
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseCapitals&value=1");
        } else {
            this.CapsOnOff.setText(("Caps Disabled"));
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseCapitals&value=0");
        }
    }//GEN-LAST:event_CapsOnOffActionPerformed

    private void CapsOnOffMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CapsOnOffMouseReleased

    }//GEN-LAST:event_CapsOnOffMouseReleased

    private void RepeatOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RepeatOnOffActionPerformed
        if (this.RepeatOnOff.isSelected()) {
            this.RepeatOnOff.setText("Repeat Enabled");
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseRepeat&value=1");
        } else {
            this.RepeatOnOff.setText(("Repeat Disabled"));
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseRepeat&value=0");
        }
    }//GEN-LAST:event_RepeatOnOffActionPerformed

    private void LinksOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LinksOnOffActionPerformed
        if (this.LinksOnOff.isSelected()) {
            this.LinksOnOff.setText("Links Enabled");
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseLinks&value=1");
        } else {
            this.LinksOnOff.setText(("Links Disabled"));
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseLinks&value=0");
        }
    }//GEN-LAST:event_LinksOnOffActionPerformed

    private void FOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FOnOffActionPerformed
        if (this.FOnOff.isSelected()) {
            this.FOnOff.setText("All Filtering Enabled");
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseFilter&value=1");
        } else {
            this.FOnOff.setText(("All Filtering Disabled"));
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseFilter&value=0");
        }
    }//GEN-LAST:event_FOnOffActionPerformed

    private void QEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QEnabledActionPerformed
        if (this.QEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseQuotes&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseQuotes&value=0");
        }
    }//GEN-LAST:event_QEnabledActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        while (true) {
            try {
                this.PopQuoteList();
                break;
            } catch (ParseException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }    // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        delquote dq = new delquote();
        dq.setVisible(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void addquotebuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addquotebuttonActionPerformed
        addquote aq = new addquote();
        aq.setVisible(true);
    }//GEN-LAST:event_addquotebuttonActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (!CS.CheckNewVer()) {
            JOptionPane.showMessageDialog(rootPane, "No new updates.");
        }
    }//GEN-LAST:event_jButton5ActionPerformed
    AlertFrame af = CS.getAlertFrame();
    private void AlertPaneOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlertPaneOpenActionPerformed

//        if (!GUISettings.containsKey("FollowSound")) {
//            JOptionPane.showMessageDialog(rootPane, "No Audio File Set");
//            return;
//        }
//        if (!GUISettings.containsKey("FollowerMSG")) {
//            JOptionPane.showMessageDialog(rootPane, "No Follower Message Set");
//            return;
//        }
//        if (!GUISettings.containsKey("FFontSize") || !GUISettings.containsKey("FFontColor") || !GUISettings.containsKey("FFontStyle") || !GUISettings.containsKey("FFontName")) {
//            JOptionPane.showMessageDialog(rootPane, "No Follower Message Font Settings set");
//            return;
//        }
        if (!af.isVisible()) {
            af.setVisible(true);
        }
    }//GEN-LAST:event_AlertPaneOpenActionPerformed

    private void FollowSoundSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FollowSoundSetActionPerformed
        FileNameExtensionFilter SoundFilter = new FileNameExtensionFilter("MP3 Files", "mp3");
        JFileChooser Open = new JFileChooser();
        String FileLoc = null;
        Open.setAcceptAllFileFilterUsed(false);
        Open.addChoosableFileFilter(SoundFilter);
        if (Open.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            FileLoc = Open.getSelectedFile().getAbsolutePath();
            CS.GUISaveSettings("FollowSound", FileLoc);
        }
//        final String Audio = FileLoc;
//        new Thread("Alert!") {
//            @Override
//            public void run() {
//                FileInputStream fis = null;
//                try {
//                    fis = new FileInputStream(Audio);
//                } catch (FileNotFoundException ex) {
//                    Logger.getLogger(AlertFrame.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                try {
//                    Player playMP3 = new Player(fis);
//                    playMP3.play();
//                } catch (JavaLayerException ex) {
//                    Logger.getLogger(AlertFrame.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }.start();
    }//GEN-LAST:event_FollowSoundSetActionPerformed

    private void FollowIMGSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FollowIMGSetActionPerformed
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image Files", "jpg", "gif", "png", "bmp");
        JFileChooser Open = new JFileChooser();
        String FileLoc = null;
        Open.setAcceptAllFileFilterUsed(false);
        Open.addChoosableFileFilter(imageFilter);
        if (Open.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            FileLoc = Open.getSelectedFile().getAbsolutePath();
            CS.GUISaveSettings("FollowIMG", FileLoc);
        }
    }//GEN-LAST:event_FollowIMGSetActionPerformed

    private void SetFollowAlertMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SetFollowAlertMsgActionPerformed
        String msg = null;
        msg = JOptionPane.showInputDialog("Set Follower Message, use (_follower_) where the follower will be.");
        if (msg != null) {
            GUISaveSettings("FollowerMSG", msg);
        }
    }//GEN-LAST:event_SetFollowAlertMsgActionPerformed

    private void FollowerMSGFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FollowerMSGFontActionPerformed
        Font font = FontChooser.showDialog(this, "Font Chooser", new Font("Dialog", 0, 12));
        GUISaveSettings("FFontSize", String.valueOf(font.getSize()));
        GUISaveSettings("FFontName", font.getName());
        GUISaveSettings("FFontStyle", String.valueOf(font.getStyle()));
    }//GEN-LAST:event_FollowerMSGFontActionPerformed

    private void SetFontColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SetFontColorActionPerformed
        int RGB = JColorChooser.showDialog(this, "Pick Follower Color", null).getRGB();
        GUISaveSettings("FFontColor", String.valueOf(RGB));
    }//GEN-LAST:event_SetFontColorActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        extchat.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void WooshMeEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WooshMeEnabledActionPerformed
        if (this.WooshMeEnabled.isSelected()) {
            CS.GUISaveSettings("WooshME", "true");
        } else {
            CS.GUISaveSettings("WooshME", "false");
        }

    }//GEN-LAST:event_WooshMeEnabledActionPerformed

    private void PNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PNotesActionPerformed
        PatchNotes pnoteswindow = new PatchNotes();
        pnoteswindow.setVisible(true);
    }//GEN-LAST:event_PNotesActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        TwitterAuthInfo tai = new TwitterAuthInfo();
        tai.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void ShowStoredKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowStoredKeyActionPerformed
        JSONObject key = new JSONObject();
        String ToParse = new HTTP().GetScotty(CS.apiLoc + "/storedauth?authkey=" + AuthKey);
        try {
            key.putAll((JSONObject) new JSONParser().parse(ToParse));
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.StoredAuthKey.setText(key.get("Key").toString());
    }//GEN-LAST:event_ShowStoredKeyActionPerformed

    private void GenNewStoredKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenNewStoredKeyActionPerformed
        int verify = JOptionPane.showConfirmDialog(StreamSet, "Are you sure, any programs that use this key will need to be updated.");
        if (verify != 0) {
            return;
        }
        JSONObject key = new JSONObject();
        String ToParse = new HTTP().GetScotty(CS.apiLoc + "/storedauth/new?authkey=" + AuthKey);
        try {
            key.putAll((JSONObject) new JSONParser().parse(ToParse));
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.StoredAuthKey.setText(key.get("Key").toString());
        CS.AuthKey = key.get("Key").toString();
    }//GEN-LAST:event_GenNewStoredKeyActionPerformed

    private void showPChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPChatActionPerformed
        String html1 = "<html>";
        String html2 = "</html>";
        String newline = "<br>";
        if (showPChat.isSelected()) {
            CS.GUISaveSettings("showpurged", "true");

        } else {
            CS.GUISaveSettings("showpurged", "false");
        }
        ChatCache = "";
        for (Object t : chatArray) {
            String msgID = t.toString();
            JSONObject msgObj = (JSONObject) chatObject.get(msgID);
            ////System.err.println(ID);
            String msgTXT = msgObj.get("msg").toString();
            if ((boolean) msgObj.get("purged")) {
                if (Boolean.parseBoolean(CS.GUIGetSetting("showpurged"))) {
                    msgTXT = "<strike>" + msgTXT + "</strike>";
                    ChatCache = ChatCache + msgTXT + newline;
                    //System.err.println(msgObj.toJSONString());
                }
            } else {
                ChatCache = ChatCache + msgTXT + newline;
            }
            CS.cp.ChatOutput.setText(html1 + ChatCache + html2);
            CS.extchat.ExtChatOutput.setText(html1 + ChatCache + html2);
            //CentralStore.cp.ChatOutput.setCaretPosition(CS.cp.ChatOutput.getDocument().getLength());
            //CentralStore.extchat.ExtChatOutput.setCaretPosition(CS.extchat.ExtChatOutput.getDocument().getLength());
        }
    }//GEN-LAST:event_showPChatActionPerformed

    private void MeOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MeOutputActionPerformed
        if (this.MeOutput.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseME&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseME&value=0");
        }
    }//GEN-LAST:event_MeOutputActionPerformed

    private void ESubMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ESubMessageActionPerformed
        String OldSubMSG = GetSettings().get("subMsg").toString();
        String NewSubMSG = JOptionPane.showInputDialog(rootPane, "Set your SubAlert message. Use \"(_user_)\" as a placeholder for the SubScriber.", OldSubMSG);
        try {
            if (!NewSubMSG.equals(OldSubMSG)) {
                try {
                    http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=subMSG&value=" + URLEncoder.encode(NewSubMSG, "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception e) {
            //cop out
        }
        try {
            RefreshAllSettings();
            this.PopulateAllSettings();
        } catch (Exception ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_ESubMessageActionPerformed

    private void TwitterAlertMSGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TwitterAlertMSGActionPerformed
        String OldSubMSG = GetSettings().get("AutoTweetMsg").toString();
        String NewSubMSG = JOptionPane.showInputDialog(rootPane, "Set your Auto-Tweet message. Use \"(_status_)\" as a placeholder for the stream title.", OldSubMSG);
        try {
            if (!NewSubMSG.equals(OldSubMSG)) {
                try {
                    http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=AutoTweetMsg&value=" + URLEncoder.encode(NewSubMSG, "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception e) {
            //cop out
        }
        try {
            RefreshAllSettings();
            this.PopulateAllSettings();
        } catch (Exception ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }        // TODO add your handling code here:        // TODO add your handling code here:
    }//GEN-LAST:event_TwitterAlertMSGActionPerformed

    private void AutoTweetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AutoTweetActionPerformed
        try {
            String toSend = CS.apiLoc + "/twitterauth?authkey=" + URLEncoder.encode(AuthKey, "UTF-8");
            //System.err.println(toSend);
            String CKey = null;
            String CSecret = null;
            String AToken = null;
            String ATokenSecret = null;
            String toParse = new HTTP().GetScotty(toSend);
            JSONObject toCheck = new JSONObject();
            toCheck.putAll((JSONObject) new JSONParser().parse(toParse));
            try {
                CKey = (String) toCheck.get("CKey");
                CSecret = (String) toCheck.get("CSecret");
                AToken = (String) toCheck.get("AToken");
                ATokenSecret = (String) toCheck.get("ATokenSecret");
            } catch (Exception throwaway) {

            }
            if (!CKey.isEmpty() && !CSecret.isEmpty() && !AToken.isEmpty() && !ATokenSecret.isEmpty()) {
                if (this.AutoTweet.isSelected()) {
                    http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=useAutoTweet&value=1");
                } else {
                    http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=useAutoTweet&value=0");
                }
            } else {
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=useAutoTweet&value=0");
                JOptionPane.showMessageDialog(rootPane, "Please set Twitter Auth Settings before enabling this.");
                this.AutoTweet.setSelected(false);
            }
        } catch (Exception e) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=useAutoTweet&value=0");
            JOptionPane.showMessageDialog(rootPane, "Errored talking to API, try again in a few minutes.");
            this.AutoTweet.setSelected(false);
            return;
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_AutoTweetActionPerformed

    private void JoinAnnounceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JoinAnnounceActionPerformed
        if (this.JoinAnnounce.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=joinAnnounce&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=joinAnnounce&value=0");
        }
    }//GEN-LAST:event_JoinAnnounceActionPerformed

    private void LeaveAnnounceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeaveAnnounceActionPerformed
        if (this.LeaveAnnounce.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=leaveAnnounce&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=leaveAnnounce&value=0");
        }// TODO add your handling code here:
    }//GEN-LAST:event_LeaveAnnounceActionPerformed

    private void LeetSpeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeetSpeekActionPerformed
        if (this.LeetSpeek.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseLeet&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseLeet&value=0");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_LeetSpeekActionPerformed

    private void LinkTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LinkTitleActionPerformed
        if (this.LinkTitle.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=useurl&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=useurl&value=0");
        }
    }//GEN-LAST:event_LinkTitleActionPerformed
    statuswindow sw = new statuswindow();
    private void StreamSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StreamSetActionPerformed
        sw.setcurgame();
        sw.setVisible(true);
    }//GEN-LAST:event_StreamSetActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        CS.GUISaveSettings("FollowerMSG", null);
        CS.GUISaveSettings("FFontName", null);
        CS.GUISaveSettings("FFontSize", null);
        CS.GUISaveSettings("FFontStyle", null);
        CS.GUISaveSettings("FFontColor", null);
        CS.GUISaveSettings("FollowIMG", null);
        CS.GUISaveSettings("FollowSound", null);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        CS.FolCount = 0;
        ControlPanel.FolCounter.setText("No followers this session.");// TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        CS.SubCount = 0;
        ControlPanel.SubsThisSession.setText("No subscribers this session.");// TODO add your handling code here:        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void setEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setEmailActionPerformed
        try {
            Map<String, String> toSend = new HashMap();
            String email = JOptionPane.showInputDialog("Please provide your email address.");
            toSend.put("authkey", CS.AuthKey);
            toSend.put("email", email);
            String response = http.put(toSend, CS.apiLoc + "/setemail");
            JSONObject obj = (JSONObject) parser.parse(response);
            if (obj.containsKey("error")) {
                JOptionPane.showMessageDialog(StreamSet, obj.get("error").toString());
            } else {
                JOptionPane.showMessageDialog(StreamSet, obj.get("result").toString() + ": " + obj.get("status").toString());
            }
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_setEmailActionPerformed

    private void resendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resendEmailActionPerformed
        try {
            Map<String, String> toSend = new HashMap();
            toSend.put("authkey", CS.AuthKey);
            String response = http.post(toSend, CS.apiLoc + "/resendemail");
            JSONObject obj = (JSONObject) parser.parse(response);
            if (obj.containsKey("error")) {
                JOptionPane.showMessageDialog(StreamSet, obj.get("error").toString());
            } else {
                JOptionPane.showMessageDialog(StreamSet, obj.get("sent").toString());
            }
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_resendEmailActionPerformed

    private void GivePointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GivePointsActionPerformed
        if (PointsTable.getSelectedColumn() == 1) {
            return;
        }
        int Row = PointsTable.getSelectedRow();
        int Col = PointsTable.getSelectedColumn();
        Object name = PointsTable.getValueAt(Row, Col);
        Long UID = Long.parseLong(new JSONUtil().GetUserID(name.toString()));
        String PTP = "";
        Long Points = 0L;
        PTP = JOptionPane.showInputDialog("How many points to give to " + name + "?").trim();
        if (PTP.isEmpty()) {
            return;
        }
        try {
            Points = Long.parseLong(PTP);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(StreamSet, "Whoops, appears this wasn't all numbers.");
            return;
        }
        JSONObject req = new JSONObject();
        req.put("request", "addpoints");
        req.put("userid", UID);
        req.put("points", Points);
        CS.controlSes.getAsyncRemote().sendText(req.toJSONString());
    }//GEN-LAST:event_GivePointsActionPerformed

    private void delpointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delpointsActionPerformed
        if (PointsTable.getSelectedColumn() == 1) {
            return;
        }
        int Row = PointsTable.getSelectedRow();
        int Col = PointsTable.getSelectedColumn();
        Object name = PointsTable.getValueAt(Row, Col);
        Long UID = Long.parseLong(new JSONUtil().GetUserID(name.toString()));
        String PTP = "";
        Long Points = 0L;
        PTP = JOptionPane.showInputDialog("How many points to remove from " + name + "?").trim();
        if (PTP.isEmpty()) {
            return;
        }
        try {
            Points = Long.parseLong(PTP);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(StreamSet, "Whoops, appears this wasn't all numbers.");
            return;
        }
        JSONObject req = new JSONObject();
        req.put("request", "delpoints");
        req.put("userid", UID);
        req.put("points", Points);
        CS.controlSes.getAsyncRemote().sendText(req.toJSONString());    }//GEN-LAST:event_delpointsActionPerformed

    private void PointsTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PointsTableKeyReleased

    }//GEN-LAST:event_PointsTableKeyReleased

    private void giveallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveallActionPerformed
        String PTP = "";
        Long Points = 0L;
        PTP = JOptionPane.showInputDialog("How many points to give everyone?").trim();
        if (PTP.isEmpty()) {
            return;
        }
        try {
            Points = Long.parseLong(PTP);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(StreamSet, "Whoops, appears this wasn't all numbers.");
            return;
        }
        JSONObject req = new JSONObject();
        req.put("request", "giveallpoints");
        req.put("points", Points);
        CS.controlSes.getAsyncRemote().sendText(req.toJSONString());
        System.err.println(req);
    }//GEN-LAST:event_giveallActionPerformed

    private void purgepointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purgepointsActionPerformed
        int check1 = JOptionPane.showConfirmDialog(StreamSet, "You realized you clicked the purge all points button, are you sure?".toUpperCase());
        if (check1 == 0) {
            int check2 = JOptionPane.showConfirmDialog(StreamSet, "Your really really sure? This is unreversable!");
            if (check2 == 0) {
                JSONObject req = new JSONObject();
                req.put("request", "purgeallpoints");
                CS.controlSes.getAsyncRemote().sendText(req.toJSONString());
            }
        }
    }//GEN-LAST:event_purgepointsActionPerformed

    private void relaymsgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_relaymsgKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            String msg = this.relaymsg.getText();
            if (msg.isEmpty()) {
                return;
            }
            Long UID = CS.UserID;
            JSONObject relay = new JSONObject();
            JSONObject obj = new JSONObject();
            obj.put("userid", UID);
            obj.put("message", msg);
            relay.put("relay", obj);
            CS.lastrelay = msg;
            relaymsg.setText("");
            CS.controlSes.getAsyncRemote().sendObject(relay);
        }
    }//GEN-LAST:event_relaymsgKeyReleased

    private void relaymsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relaymsgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_relaymsgActionPerformed

    private void cmdsoundbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdsoundbuttonActionPerformed
        cmdListView showme = new cmdListView();
        showme.setVisible(true);
    }//GEN-LAST:event_cmdsoundbuttonActionPerformed

    private void comCostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comCostActionPerformed
        CMDCost cost = new CMDCost();
        cost.setVisible(true);
    }//GEN-LAST:event_comCostActionPerformed

    private void RefreshCMDsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshCMDsActionPerformed
        while (true) {
            try {
                PopCmdText();
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
    }//GEN-LAST:event_RefreshCMDsActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        AddEditCMD cmd = new AddEditCMD();
        cmd.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        DeletePermAdjust dpa = new DeletePermAdjust();
        dpa.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void RepeatListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RepeatListActionPerformed
        RepeatList rl = new RepeatList();
        rl.setVisible(true);
    }//GEN-LAST:event_RepeatListActionPerformed

    private void RouletteEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RouletteEnableActionPerformed
        if (this.RouletteEnable.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseRoulette&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseRoulette&value=0");
        }
    }//GEN-LAST:event_RouletteEnableActionPerformed

    private void REnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REnabledActionPerformed
        if (this.REnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseRaffle&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseRaffle&value=0");
        }
    }//GEN-LAST:event_REnabledActionPerformed

    private void BHEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BHEnabledActionPerformed
        if (this.BHEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseBankheist&value=1");
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=UseBankheist&value=0");
        }
    }//GEN-LAST:event_BHEnabledActionPerformed

    private void PWhenIdleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PWhenIdleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PWhenIdleActionPerformed

    private void EditPointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditPointsActionPerformed

        if (this.PWhenLive.isEditable()) {
            String idle = this.PWhenIdle.getText();
            String notidle = this.PWhenLive.getText();
            String StartPoints = this.PStartPoints.getText();
            String SubBonus = this.PSubPoints.getText();
            String pointsper = this.PPM.getText();
            try {
                Integer.parseInt(idle);
                Integer.parseInt(notidle);
                Integer.parseInt(StartPoints);
                Integer.parseInt(SubBonus);
                Integer.parseInt(pointsper);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, "An entry is not numeric.");
                return;
            }

            this.PWhenIdle.setEditable(false);
            this.PWhenLive.setEditable(false);
            this.PStartPoints.setEditable(false);
            this.PSubPoints.setEditable(false);
            this.PPM.setEditable(false);
            try {
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=idlepoints&value=" + URLEncoder.encode(idle, "UTF-8"));
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=notidlepoints&value=" + URLEncoder.encode(notidle, "UTF-8"));
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=startpoints&value=" + URLEncoder.encode(StartPoints, "UTF-8"));
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=subbonus&value=" + URLEncoder.encode(SubBonus, "UTF-8"));
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=pointspermsg&value=" + URLEncoder.encode(pointsper, "UTF-8"));

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.EditPoints.setText("Edit");
        } else {
            this.EditPoints.setText("Save");
            this.PWhenIdle.setEditable(true);
            this.PWhenLive.setEditable(true);
            this.PStartPoints.setEditable(true);
            this.PSubPoints.setEditable(true);
            this.PPM.setEditable(true);
        }
    }//GEN-LAST:event_EditPointsActionPerformed

    private void PRenButActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PRenButActionPerformed
        if (this.PointsName.isEditable()) {
            this.PointsName.setEditable(false);
            this.PRenBut.setText("Rename");
            try {
                String pname = this.PointsName.getText().replace("!", "");
                http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=PointsName&value=" + URLEncoder.encode(pname, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.PointsName.setEditable(true);
            this.PRenBut.setText("Save");
        }
    }//GEN-LAST:event_PRenButActionPerformed

    private void PEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PEnabledActionPerformed
        if (this.PEnabled.isSelected()) {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=Points&value=1");
            Component[] ToEnable = this.PSettings.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(true);
            }
        } else {
            http.GetScotty(CS.apiLoc + "/settings/change?authkey=" + AuthKey + "&setting=Points&value=0");
            Component[] ToEnable = this.PSettings.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(false);
            }
            this.PEnabled.setEnabled(true);
        }
    }//GEN-LAST:event_PEnabledActionPerformed

    private void ViewersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ViewersMouseClicked
        if (evt.getClickCount() == 2) {
            try {
                session.getBasicRemote().sendText(CS.SendMSG("+p " + this.Viewers.getSelectedValue().toString()).trim());
            } catch (IOException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ViewersMouseClicked

    private void ChatSendKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ChatSendKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                if (!sendasbot) {
                    CS.session.getBasicRemote().sendText(SendMSG(ChatSend.getText().trim()));
                } else {
                    JSONObject bethebot = new JSONObject();
                    bethebot.put("bethebot", ChatSend.getText().trim());
                    CS.controlSes.getBasicRemote().sendObject(bethebot);
                }
                ChatSend.setText("");
            } catch (IOException | EncodeException ex) {
            }
        }
    }//GEN-LAST:event_ChatSendKeyPressed

    private void ChatSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChatSendActionPerformed

    private void settingsTabsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_settingsTabsKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_settingsTabsKeyReleased

    private void settingsTabsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsTabsMouseClicked

    }//GEN-LAST:event_settingsTabsMouseClicked

    private void settingsTabsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsTabsMouseReleased

    }//GEN-LAST:event_settingsTabsMouseReleased

    private void UndoRankChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UndoRankChangesActionPerformed
        this.PopCustRanks();
    }//GEN-LAST:event_UndoRankChangesActionPerformed

    private void CustRankSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustRankSaveActionPerformed
        if (CustRankTable.isEditing()) {
            CustRankTable.getCellEditor().stopCellEditing();
        }
        JSONObject toSave = new JSONObject();
        int rows = this.CustRankTable.getRowCount();
        rows = rows - 1;
        int counted = 0;
        while (counted <= rows) {
            try {
                String hours = this.CustRankTable.getValueAt(counted, 0).toString();
                String Rank = this.CustRankTable.getValueAt(counted, 1).toString();

                if (!Rank.isEmpty()) {
                    toSave.put(hours, Rank);
                }
            } catch (Exception copout) {

            }
            counted++;
        }
        Map<String, String> toPut = new HashMap();
        toPut.put("jsonranks", toSave.toJSONString());
        toPut.put("authkey", CS.AuthKey);
        try {
            http.put(toPut, CS.apiLoc + "/rankscheme/set");
            //System.err.println(toPut);
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(AlertPaneOpen, "Error Saving, try again in a few moments.");
            return;
        }
        this.PopCustRanks();

    }//GEN-LAST:event_CustRankSaveActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        DefaultTableModel CRModel = (DefaultTableModel) this.CustRankTable.getModel();
        int row = CustRankTable.getSelectedRow();
        if (row != -1) {
            int rows = CRModel.getRowCount() - 1;
            DefaultTableModel newModel = new DefaultTableModel();
            newModel.addColumn(CRModel.getColumnName(0));
            newModel.addColumn(CRModel.getColumnName(1));
            int cnt = 0;
            boolean added = false;
            while (cnt <= rows) {
                if (cnt == row && !added) {
                    newModel.addRow(new Object[]{null, ""});
                    added = true;
                } else {
                    try {
                        Long hours = (Long) CRModel.getValueAt(cnt, 0);
                        String rank = CRModel.getValueAt(cnt, 1).toString();
                        newModel.addRow(new Object[]{hours, rank});
                    } catch (Exception e) {
                        newModel.addRow(new Object[]{null, ""});
                    }

                    cnt++;
                }
            }
            this.CustRankTable.setModel(newModel);
            if (CustRankTable.getColumnModel().getColumnCount() > 0) {
                CustRankTable.getColumnModel().getColumn(0).setMinWidth(150);
                CustRankTable.getColumnModel().getColumn(0).setPreferredWidth(150);
                CustRankTable.getColumnModel().getColumn(0).setMaxWidth(150);
            }
            //CustRankTable.getColumn(0).setPreferredWidth(150);
        } else {

            CRModel.setRowCount(CRModel.getRowCount() + 1);
        }

    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        try {
            int toDel = this.CustRankTable.getSelectedRow();
            DefaultTableModel CRModel = (DefaultTableModel) this.CustRankTable.getModel();
            CRModel.removeRow(toDel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(AlertPaneOpen, "No Row Selected.");
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void ResetRanksScottyDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetRanksScottyDefaultsActionPerformed
        int check1 = JOptionPane.showConfirmDialog(AlertPaneOpen, "Are you sure you wish to reset, this can not be undone!");
        if (check1 == 0) {
            int check2 = JOptionPane.showConfirmDialog(AlertPaneOpen, "Really? For Truly?");
            if (check2 != 0) {
                return;
            }
        } else {
            return;
        }
        try {
            http.deleteCustRanks();
            this.PopCustRanks();
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(AlertPaneOpen, "Unable to reset, try again in a few minutes or contact Pocketpac");
        }
    }//GEN-LAST:event_ResetRanksScottyDefaultsActionPerformed

    private void CustRankTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CustRankTableFocusLost

    }//GEN-LAST:event_CustRankTableFocusLost

    private void settingsTabsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_settingsTabsFocusGained

    }//GEN-LAST:event_settingsTabsFocusGained
    boolean sendasbot = false;
    private void YouBotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YouBotActionPerformed
        if (YouBot.isSelected()) {
            YouBot.setText("Bot");
            this.sendasbot = true;
        } else {
            YouBot.setText("You");
            this.sendasbot = false;
        }
    }//GEN-LAST:event_YouBotActionPerformed

    private void PopCustRanks() {
        JSONObject custRanks = new JSONObject();
        try {
            String toParse = http.get(CS.apiLoc + "/rankscheme?chanid=" + ChanID);
            custRanks.putAll((JSONObject) parser.parse(toParse));
            List<Long> Hours = new ArrayList();
            for (Object T : custRanks.keySet()) {
                Hours.add(Long.parseLong(T.toString()));
            }
            Collections.sort(Hours);
            Collections.reverse(Hours);
            DefaultTableModel CRModel = (DefaultTableModel) this.CustRankTable.getModel();
            CRModel.setRowCount(0);
            for (Long T : Hours) {
                String Rank = custRanks.get(T.toString()).toString();
                CRModel.addRow(new Object[]{T, Rank});
            }
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        this.CustRankTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);

    }

    private void PopWhiteList() {
        JSONObject whitelist = null;
        try {
            whitelist = (JSONObject) new JSONParser().parse(http.GetScotty(CS.apiLoc + "/whitelist?channame=" + ChanID));
        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        String ToDisplay = "";
        for (Object t : whitelist.keySet()) {
            String player = t.toString();
            JSONObject obj = (JSONObject) whitelist.get(t);
            String Duration = "";
            String beamid = "";

            Long dur = (Long) obj.get("duration");
            Long bid = (Long) obj.get("beamid");
            if (dur == null) {
                Duration = "N/A";
            } else {
                Long DaysInMilli = 24 * 60L * 60 * 1000;
                Long TimeLeft = dur - System.currentTimeMillis();
                //System.err.println(DaysInMilli + ":" + TimeLeft + ":" + System.currentTimeMillis() + ":" + dur);
                if (TimeLeft < DaysInMilli) {
                    Duration = "Less than a day.";
                } else {
                    Duration = String.valueOf(Math.round((float) TimeLeft / (float) DaysInMilli)) + " days left";
                }
            }

            if (bid == null) {
                beamid = "N/A";
            } else {
                JSONObject getUsername = null;
                try {
                    getUsername = (JSONObject) new JSONParser().parse(http.get("https://beam.pro/api/v1/users/" + bid));
                } catch (ParseException | IOException | InterruptedException | ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                beamid = (String) getUsername.get("username");
            }
            if (ToDisplay.equals("")) {
                ToDisplay = player + "(" + beamid + ") " + Duration;
            } else {
                ToDisplay = ToDisplay + newline + player + "(" + beamid + ") " + Duration;
            }
        }
        this.showWhitelist.setText(ToDisplay);

    }
    boolean custrankpop = false;

    private void PopulateAllSettings() {
        String PName = GetSettings().get("PointsName").toString();
        if (!"!".equals(String.valueOf(PName.charAt(0)))) {
            PName = "!" + PName;
        }
        this.PointsName.setText(PName);
        if ("1".equals(GetSettings().get("Points"))) {
            this.PEnabled.setSelected(true);
        } else {
            this.PEnabled.setSelected(false);
        }
        this.PWhenLive.setText(GetSettings().get("notidlepoints").toString());
        this.PWhenIdle.setText(GetSettings().get("idlepoints").toString());
        this.PStartPoints.setText(GetSettings().get("startpoints").toString());
        this.PSubPoints.setText(GetSettings().get("subbonus").toString());
        this.PPM.setText(GetSettings().get("PointsPerMsg").toString());

        if ("1".equals(GetSettings().get("UseBankheist"))) {
            this.BHEnabled.setSelected(true);
        } else {
            this.BHEnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("UseRaffle"))) {
            this.REnabled.setSelected(true);
        } else {
            this.REnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("UseRoulette"))) {
            this.RouletteEnable.setSelected(true);
        } else {
            this.RouletteEnable.setSelected(false);
        }
        if ("1".equals(GetSettings().get("UseQuotes"))) {
            this.QEnabled.setSelected(true);
        } else {
            this.QEnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("UseFollower"))) {
            this.FollowEnabled.setSelected(true);
        } else {
            this.FollowEnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("OnlyWhenLive"))) {
            this.OnlyWhenLiveEnabled.setSelected(true);
        } else {
            this.OnlyWhenLiveEnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("PurgeCommands"))) {
            this.ClearCmdsEnabled.setSelected(true);
        } else {
            this.ClearCmdsEnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("subalert"))) {
            this.PSubAlert.setSelected(true);
        } else {
            this.PSubAlert.setSelected(false);
        }
        if ("1".equals(GetSettings().get("useme"))) {
            this.MeOutput.setSelected(true);
        } else {
            this.MeOutput.setSelected(false);
        }

        if ("1".equals(GetSettings().get("Donated"))) {
            //this.JSettingsPane.setEnabledAt(2, true);

            Component[] ToEnable = this.DonationPane.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(true);
            }
        } else {
//            this.JSettingsPane.setEnabledAt(2, false);
            Component[] ToEnable = this.DonationPane.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(false);
            }
        }

        if ("1".equals(GetSettings().get("UseChuck"))) {
            this.ChuckEnabled.setSelected(true);
        } else {
            this.ChuckEnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("UseClever"))) {
            this.ChatEnabled.setSelected(true);
        } else {
            this.ChatEnabled.setSelected(false);
        }

        if ("1".equals(GetSettings().get("UseLeet"))) {
            this.LeetSpeek.setSelected(true);
        } else {
            this.LeetSpeek.setSelected(false);
        }

        if ("1".equals(GetSettings().get("useurl"))) {
            this.LinkTitle.setSelected(true);
        } else {
            this.LinkTitle.setSelected(false);
        }
        if ("1".equals(GetSettings().get("useAutoTweet"))) {
            this.AutoTweet.setSelected(true);
        } else {
            this.AutoTweet.setSelected(false);
        }

        if ("1".equals(GetSettings().get("subalert"))) {
            this.PSubAlert.setSelected(true);
        } else {
            this.PSubAlert.setSelected(false);
        }
        if ("1".equals(GetSettings().get("joinAnnounce"))) {
            this.JoinAnnounce.setSelected(true);
        } else {
            this.JoinAnnounce.setSelected(false);
        }
        if ("1".equals(GetSettings().get("leaveAnnounce"))) {
            this.LeaveAnnounce.setSelected(true);
        } else {
            this.LeaveAnnounce.setSelected(false);
        }

        if (this.PEnabled.isSelected()) {

            Component[] ToEnable = this.PSettings.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(true);
            }
        } else {

            Component[] ToEnable = this.PSettings.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(false);
            }
            this.PEnabled.setEnabled(true);
        }

        this.PopWhiteList();
        if (!custrankpop) {
            this.PopCustRanks();
            custrankpop = true;
        }

    }

    public void PopBadWords() {
        BadWordsList.clear();
        this.BadWordList.setModel(BadWordsList);
        JSONObject ToPopulate = null;
        while (true) {
            try {
                ToPopulate = (JSONObject) parser.parse(http.GetScotty(CS.apiLoc + "/badwords?authkey=" + AuthKey));
                break;
            } catch (ParseException ex) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                }
            }
        }

        for (Object t : ToPopulate.values()) {
            BadWordsList.addElement(t.toString());
        }

    }

    public void socketRefreshSettings() {
        PopFilterSettings();
        PopulateAllSettings();
        if (!this.isVisible()) {
            this.setVisible(true);
        }
    }

    public void RefreshAllSettings() {
        PopFilterSettings();
        PopulateAllSettings();
        JSONObject ChanInfo = new JSONObject();
        String toParse = null;
        try {
            toParse = http.get("https://beam.pro/api/v1/channels/" + CS.ChanID);
        } catch (IOException | ParseException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            try {
                ChanInfo.putAll((JSONObject) new JSONParser().parse(toParse));
                break;
            } catch (ParseException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Long Followers = (Long) ChanInfo.get("numFollowers");
        Long Subs = (Long) ChanInfo.get("numSubscribers");
        ControlPanel.TotFollowers.setText("Total Followers: " + Followers);
        ControlPanel.TotSubs.setText("Total Subscribers: " + Subs);

    }

    public void PopFilterSettings() {

        JSONObject FSettings = GetSettings();
        System.out.println("FSETTINGS " + FSettings);
        if ("1".equals(FSettings.get("UseFilter").toString())) {
            this.FOnOff.setSelected(true);
            this.FOnOff.setText("All Filtering Enabled");
        } else {
            this.FOnOff.setSelected(false);
            this.FOnOff.setText(("All Filtering Disabled"));
        }

        if ("1".equals(FSettings.get("UseLinks").toString())) {
            this.LinksOnOff.setSelected(true);
            this.LinksOnOff.setText("Links Enabled");
        } else {
            this.LinksOnOff.setSelected(false);
            this.LinksOnOff.setText(("Links Disabled"));
        }

        if ("1".equals(FSettings.get("UseRepeat"))) {
            this.RepeatOnOff.setSelected(true);
            this.RepeatOnOff.setText("Repeat Enabled");
        } else {
            this.RepeatOnOff.setText("Repeat Disabled");
            this.RepeatOnOff.setSelected(false);
        }

        if ("1".equals(FSettings.get("UseCapitals"))) {
            this.CapsOnOff.setSelected(true);
            this.CapsOnOff.setText("Caps Enabled");
        } else {
            this.CapsOnOff.setText("Caps Disabled");
            this.CapsOnOff.setSelected(false);
        }

        if ("1".equals(FSettings.get("UseSymbols"))) {
            this.SymbolsOnOff.setSelected(true);
            this.SymbolsOnOff.setText("Symbols Enabled");
        } else {
            this.SymbolsOnOff.setText("Symbols Disabled");
            this.SymbolsOnOff.setSelected(false);
        }

        this.CapPercent.setValue(Integer.parseInt(FSettings.get("CapPercent").toString()));
        this.SymPercent.setValue(Integer.parseInt(FSettings.get("SymbolCount").toString()));
        this.TimoutDuration.setValue(Integer.parseInt(FSettings.get("TimeOutLength").toString()));

        this.TimeoutLabel.setText(String.valueOf(this.TimoutDuration.getValue()) + " Minutes");
        this.CapsPercentDis.setText(String.valueOf(this.CapPercent.getValue()) + " Percent");
        this.SymPercentDis.setText(String.valueOf(this.SymPercent.getValue()) + " Percent");

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
            java.util.logging.Logger.getLogger(ControlPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControlPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControlPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControlPanel.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControlPanel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AddBadWord;
    private javax.swing.JButton AlertPaneOpen;
    private javax.swing.JCheckBox AutoTweet;
    private javax.swing.JCheckBox BHEnabled;
    private javax.swing.JList BadWordList;
    private javax.swing.JButton CUsernamePassword;
    private javax.swing.JSlider CapPercent;
    private javax.swing.JToggleButton CapsOnOff;
    private javax.swing.JLabel CapsPercentDis;
    private javax.swing.JCheckBox ChatEnabled;
    public javax.swing.JEditorPane ChatOutput;
    private javax.swing.JTextField ChatSend;
    private javax.swing.JCheckBox ChuckEnabled;
    private javax.swing.JCheckBox ClearCmdsEnabled;
    private javax.swing.JTextArea CmdInfo;
    public static javax.swing.JTextField ControlStatus;
    public javax.swing.JLabel CurViewers;
    private javax.swing.JButton CustRankSave;
    private javax.swing.JTable CustRankTable;
    private javax.swing.JPanel DonationPane;
    private javax.swing.JPanel DonatorPanel;
    private javax.swing.JButton EFollowMsg;
    private javax.swing.JButton ESubMessage;
    private javax.swing.JButton EditPoints;
    private javax.swing.JToggleButton FOnOff;
    public static javax.swing.JLabel FolCounter;
    private javax.swing.JCheckBox FollowEnabled;
    private javax.swing.JButton FollowIMGSet;
    private javax.swing.JButton FollowSoundSet;
    private javax.swing.JButton FollowerMSGFont;
    private javax.swing.JButton GenNewStoredKey;
    private javax.swing.JButton GivePoints;
    private javax.swing.JCheckBox JoinAnnounce;
    private javax.swing.JCheckBox LeaveAnnounce;
    private javax.swing.JCheckBox LeetSpeek;
    private javax.swing.JCheckBox LinkTitle;
    private javax.swing.JToggleButton LinksOnOff;
    private javax.swing.JCheckBox MeOutput;
    private javax.swing.JLabel NumOfQuotes;
    private javax.swing.JCheckBox OnlyWhenLiveEnabled;
    private javax.swing.JCheckBox PEnabled;
    private javax.swing.JButton PNotes;
    private javax.swing.JTextField PPM;
    private javax.swing.JButton PRenBut;
    private javax.swing.JPanel PSettings;
    private javax.swing.JTextField PStartPoints;
    private javax.swing.JCheckBox PSubAlert;
    private javax.swing.JTextField PSubPoints;
    private javax.swing.JTextField PWhenIdle;
    private javax.swing.JTextField PWhenLive;
    public javax.swing.JLabel PercentRetainedViewers;
    private javax.swing.JTextField PointsName;
    public static javax.swing.JTable PointsTable;
    private javax.swing.JCheckBox QEnabled;
    private javax.swing.JTextArea QuotePanel;
    private javax.swing.JCheckBox REnabled;
    private javax.swing.JButton RefreshCMDs;
    private javax.swing.JButton RemoveBadWord;
    private javax.swing.JButton RepeatList;
    private javax.swing.JToggleButton RepeatOnOff;
    private javax.swing.JButton ResetRanksScottyDefaults;
    private javax.swing.JButton ResetScottyName;
    private javax.swing.JCheckBox RouletteEnable;
    public javax.swing.JLabel SessionMsgCount;
    private javax.swing.JButton SetFollowAlertMsg;
    private javax.swing.JButton SetFontColor;
    private javax.swing.JPanel SettingsPanel;
    private javax.swing.JButton ShowStoredKey;
    private javax.swing.JTextField StoredAuthKey;
    public static javax.swing.JButton StreamSet;
    public static javax.swing.JLabel SubsThisSession;
    private javax.swing.JSlider SymPercent;
    private javax.swing.JLabel SymPercentDis;
    private javax.swing.JToggleButton SymbolsOnOff;
    private javax.swing.JLabel TimeoutLabel;
    private javax.swing.JSlider TimoutDuration;
    public javax.swing.JLabel TopViewers;
    public static javax.swing.JLabel TotFollowers;
    public static javax.swing.JLabel TotSubs;
    private javax.swing.JButton TwitterAlertMSG;
    public javax.swing.JLabel UChatters;
    private javax.swing.JButton UndoRankChanges;
    public javax.swing.JList Viewers;
    public javax.swing.JCheckBox WooshMeEnabled;
    private javax.swing.JCheckBox YodaEnabled;
    private javax.swing.JToggleButton YouBot;
    private javax.swing.JTabbedPane YouTube;
    private javax.swing.JButton addbadword;
    private javax.swing.JButton addquotebutton;
    private javax.swing.JButton cmdsoundbutton;
    private javax.swing.JButton comCost;
    private javax.swing.JButton delpoints;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton giveall;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JButton purgepoints;
    private javax.swing.JTextField relaymsg;
    private javax.swing.JButton resendEmail;
    private javax.swing.JButton setEmail;
    private javax.swing.JTabbedPane settingsTabs;
    public javax.swing.JCheckBox showPChat;
    private javax.swing.JTextPane showWhitelist;
    public static javax.swing.JTable smchat;
    // End of variables declaration//GEN-END:variables
}
