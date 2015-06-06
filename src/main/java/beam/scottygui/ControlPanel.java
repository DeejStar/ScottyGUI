/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui;

import beam.scottygui.Alerts.AlertFrame;
import beam.scottygui.ChatHandler.ChatPopOut;
import beam.scottygui.Stores.CentralStore;
import static beam.scottygui.Stores.CentralStore.AuthKey;
import static beam.scottygui.Stores.CentralStore.BadWordsList;
import static beam.scottygui.Stores.CentralStore.ChanID;
import static beam.scottygui.Stores.CentralStore.ChatUserList;
import static beam.scottygui.Stores.CentralStore.GUILoadSettings;
import static beam.scottygui.Stores.CentralStore.GUISaveSettings;
import static beam.scottygui.Stores.CentralStore.GUISettings;
import static beam.scottygui.Stores.CentralStore.GetSettings;
import static beam.scottygui.Stores.CentralStore.RefreshSettings;
import static beam.scottygui.Stores.CentralStore.SendMSG;
import static beam.scottygui.Stores.CentralStore.Username;
import static beam.scottygui.Stores.CentralStore.cp;
import static beam.scottygui.Stores.CentralStore.extchat;
import static beam.scottygui.Stores.CentralStore.newline;
import static beam.scottygui.Stores.CentralStore.session;
import beam.scottygui.Utils.FontChooser;
import beam.scottygui.Utils.HTTP;
import beam.scottygui.Utils.JSONUtil;
import beam.scottygui.Utils.downloadFromUrl;
import beam.scottygui.cmdcontrol.AddEditCMD;
import beam.scottygui.cmdcontrol.DeletePermAdjust;
import beam.scottygui.cmdcontrol.RepeatList;
import beam.scottygui.quotecontrol.addquote;
import beam.scottygui.quotecontrol.delquote;
import beam.scottygui.websocket.WebSocket;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
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
    Integer CurVer = CentralStore.CurVer;
    WebSocket socket = new WebSocket();

    /**
     * Creates new form ControlPanel
     */
    public void DumpCurVer() {
        PrintStream VerPrint = null;
        try {
            VerPrint = new PrintStream(new FileOutputStream("CurVer.json"));
        } catch (FileNotFoundException ex) {

        }
        JSONObject curver = new JSONObject();
        curver.put("CurVer", CurVer.toString());
        VerPrint.print(curver.toString());
        VerPrint.close();
    }

    public void SetCurViewers(String curnum) {
        this.CurViewers.setText(curnum + " viewers");
    }

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public boolean CheckNewVer() {
        JSONObject VerCheck = null;
        while (true) {
            try {
                VerCheck = (JSONObject) parser.parse(http.GetScotty("http://scottybot.x10host.com/files/CurVer.json"));
                break;
            } catch (ParseException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //System.out.println(VerCheck.toString());
        int NewVer = Integer.parseInt(VerCheck.get("CurVer").toString());
        if (NewVer > CurVer) {
            int Yes = JOptionPane.showConfirmDialog(rootPane, "New version of ScottyGUI" + newline + "Would you like to download?");

            if (Yes == 0) {
                int Attempts = 0;
                while (Attempts < 5) {
                    URL ToDownload = null;
                    try {
                        ToDownload = new URL("http://scottybot.x10host.com/files/ScottyGUI.jar");
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String FileName = "./ScottyGUI.jar";
                    downloadFromUrl download = new downloadFromUrl();
                    try {
                        download.downloadFromUrl(ToDownload, FileName);
                        break;
                    } catch (IOException ex) {
                        Attempts++;
                        Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (Attempts == 5) {
                    JOptionPane.showMessageDialog(rootPane, "Unable to download, try again later");
                } else {
                    //Restart the program
                    JOptionPane.showMessageDialog(rootPane, "Downloaded, Restarting ScottyGUI!");

                    StringBuilder cmd = new StringBuilder();
                    cmd.append("\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\"");
                    for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                        cmd.append(jvmArg + " ");
                    }
                    cmd.append(" -jar ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");

                    try {
                        //System.out.println(cmd.toString());
                        Runtime.getRuntime().exec(cmd.toString());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.exit(0);
                    System.exit(0);
                }

            }
            return true;
        }
        return false;
    }

    public void PopChatList() {
        this.Viewers.setModel(ChatUserList);
        new Thread("Update Viewer List") {
            @Override
            public void run() {
                while (true) {
                    //System.out.println("Populating Viewer List");
                    JSONArray InitUserList = null;

                    while (true) {
                        try {
                            InitUserList = (JSONArray) parser.parse(http.BeamGet("https://beam.pro/api/v1/chats/" + ChanID + "/users"));
                            break;
                        } catch (ParseException ex) {
                            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    ChatUserList.clear();
                    for (Object t : InitUserList) {
                        JSONObject obj = (JSONObject) t;
                        ChatUserList.add(obj.get("user_name").toString());
                    }
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();

    }

    public ControlPanel() {
        this.setTitle("ScottyGUI Ver. " + this.CurVer);
        GUILoadSettings();
        initComponents();
        DumpCurVer();
        CheckNewVer();
        CentralStore.extchat = new ChatPopOut();
        //Set chat window to auto-scroll
        DefaultCaret caret = (DefaultCaret) this.ChatOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        cp = this;
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

        RefreshAllSettings();

        PopFilterSettings();
        PopBadWords();

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
                    new Thread("Populate Settings") {
                        @Override
                        public void run() {
                            try {
                                RefreshAllSettings();
                                PopBadWords();
                            } catch (Exception ex) {
                                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    }.start();

                    try {
                        Thread.sleep(5 * 60 * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }.start();
        this.PopChatList();
        this.socket.connect(ChanID);
        this.PopGuiSettings();
//        LiveLoadHandler llh = new LiveLoadHandler();
//        try {
//            llh.attach();
//        } catch (Exception ex) {
//            Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void PopQuoteList() throws ParseException {
        JSONObject QList = null;
        QList = (JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/quotes?authkey=" + AuthKey));
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
        JSONObject CmdOutput = null;
        CmdOutput = (JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/commands?authkey=" + AuthKey));

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
            out = out + newline + newline + obj.get("cmd") + " - Level: " + restrictlevel + " - " + obj.get("text") + " - Count: " + obj.get("cmdcount");
        }

        this.CmdInfo.setText(out);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        whitelistPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        CmdInfo = new javax.swing.JTextArea();
        RepeatList = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        RefreshCMDs = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        QuotePanel = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
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
        SettingsPanel = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        FollowSoundSet = new javax.swing.JButton();
        FollowIMGSet = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        SetFollowAlertMsg = new javax.swing.JButton();
        FollowerMSGFont = new javax.swing.JButton();
        SetFontColor = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        WooshMeEnabled = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        FollowEnabled = new javax.swing.JCheckBox();
        EFollowMsg = new javax.swing.JButton();
        OnlyWhenLiveEnabled = new javax.swing.JCheckBox();
        ClearCmdsEnabled = new javax.swing.JCheckBox();
        MeOutput = new javax.swing.JCheckBox();
        CUsernamePassword = new javax.swing.JButton();
        ResetScottyName = new javax.swing.JButton();
        DonatorPanel = new javax.swing.JPanel();
        DonationPane = new javax.swing.JPanel();
        YodaEnabled = new javax.swing.JCheckBox();
        ChatEnabled = new javax.swing.JCheckBox();
        ChuckEnabled = new javax.swing.JCheckBox();
        PNotes = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        UChatters = new javax.swing.JLabel();
        PercentRetainedViewers = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        SessionMsgCount = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        ChatOutput = new javax.swing.JEditorPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        Viewers = new javax.swing.JList();
        ChatSend = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        showWhitelist = new javax.swing.JTextPane();
        jLabel16 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        AlertPaneOpen = new javax.swing.JButton();
        RefreshAll = new javax.swing.JButton();

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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton6.setText("Chat");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 100, -1));

        CurViewers.setText("Offline");
        CurViewers.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        getContentPane().add(CurViewers, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 8, 110, -1));

        TopViewers.setText("0 Top Viewers");
        getContentPane().add(TopViewers, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 8, 200, -1));

        whitelistPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                whitelistPaneMouseClicked(evt);
            }
        });

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        CmdInfo.setEditable(false);
        CmdInfo.setColumns(20);
        CmdInfo.setLineWrap(true);
        CmdInfo.setRows(5);
        CmdInfo.setWrapStyleWord(true);
        jScrollPane1.setViewportView(CmdInfo);

        jPanel16.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 51, 950, 452));

        RepeatList.setText("Repeat List");
        RepeatList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RepeatListActionPerformed(evt);
            }
        });
        jPanel16.add(RepeatList, new org.netbeans.lib.awtextra.AbsoluteConstraints(866, 11, -1, -1));

        jLabel2.setText("Auto-Refreshes every 5 minutes.");
        jPanel16.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(488, 15, -1, -1));

        jButton2.setText("Reset/Permlevel/Delete CMD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 11, -1, -1));

        jButton1.setText("Add/Edit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 11, -1, -1));

        RefreshCMDs.setText("Refresh");
        RefreshCMDs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshCMDsActionPerformed(evt);
            }
        });
        jPanel16.add(RefreshCMDs, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        jPanel1.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 510));

        whitelistPane.addTab("Commands", jPanel1);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        QuotePanel.setEditable(false);
        QuotePanel.setColumns(20);
        QuotePanel.setLineWrap(true);
        QuotePanel.setRows(5);
        QuotePanel.setWrapStyleWord(true);
        jScrollPane2.setViewportView(QuotePanel);

        jPanel15.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 66, 940, 440));

        jButton4.setText("Refresh");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 12, -1, -1));

        jLabel3.setText("Auto-Refreshes every 5 minutes.");
        jPanel15.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 16, -1, -1));

        QEnabled.setText("Quotes Enabled");
        QEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QEnabledActionPerformed(evt);
            }
        });
        jPanel15.add(QEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(387, 12, -1, -1));

        NumOfQuotes.setText("jLabel4");
        jPanel15.add(NumOfQuotes, new org.netbeans.lib.awtextra.AbsoluteConstraints(268, 16, 71, -1));

        jButton3.setText("Delete Quotes");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 12, -1, -1));

        addquotebutton.setText("Add Quote");
        addquotebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addquotebuttonActionPerformed(evt);
            }
        });
        jPanel15.add(addquotebutton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 12, 105, -1));

        jPanel2.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        whitelistPane.addTab("Quotes", jPanel2);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        LinksOnOff.setText("Links Switch");
        LinksOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LinksOnOffActionPerformed(evt);
            }
        });
        jPanel14.add(LinksOnOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 108, 105, -1));

        RepeatOnOff.setText("Repeat Switch");
        RepeatOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RepeatOnOffActionPerformed(evt);
            }
        });
        jPanel14.add(RepeatOnOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 158, 105, -1));

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
        jPanel14.add(CapsOnOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 205, 105, -1));

        SymbolsOnOff.setText("Symbols Switch");
        SymbolsOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SymbolsOnOffActionPerformed(evt);
            }
        });
        jPanel14.add(SymbolsOnOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 252, -1, -1));

        jLabel5.setText("TimeOut Duration in Minutes");
        jPanel14.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(56, 299, -1, -1));

        FOnOff.setText("Filter Master Switch");
        FOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FOnOffActionPerformed(evt);
            }
        });
        jPanel14.add(FOnOff, new org.netbeans.lib.awtextra.AbsoluteConstraints(201, 11, 183, 63));

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
        jPanel14.add(CapPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 206, 292, -1));

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
        jPanel14.add(SymPercent, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 272, 292, -1));

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
        jPanel14.add(TimoutDuration, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 344, 292, -1));

        CapsPercentDis.setText("jLabel6");
        jPanel14.add(CapsPercentDis, new org.netbeans.lib.awtextra.AbsoluteConstraints(567, 206, 94, -1));

        SymPercentDis.setText("jLabel7");
        jPanel14.add(SymPercentDis, new org.netbeans.lib.awtextra.AbsoluteConstraints(567, 272, 94, -1));

        TimeoutLabel.setText("jLabel7");
        jPanel14.add(TimeoutLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(567, 344, 94, -1));

        BadWordList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(BadWordList);

        jPanel14.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(667, 126, 117, 181));

        addbadword.setText("Add word to list");
        addbadword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbadwordActionPerformed(evt);
            }
        });
        jPanel14.add(addbadword, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 127, 123, -1));
        jPanel14.add(AddBadWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 87, 123, -1));

        jLabel6.setText("New Bad Word");
        jPanel14.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(491, 63, 85, -1));

        jLabel7.setText("Bad Words Filter");
        jPanel14.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(622, 63, -1, -1));

        RemoveBadWord.setText("Remove From List");
        RemoveBadWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveBadWordActionPerformed(evt);
            }
        });
        jPanel14.add(RemoveBadWord, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 86, -1, -1));

        jPanel3.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 510));

        whitelistPane.addTab("Filtering", jPanel3);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        settingsTabs.setToolTipText("");

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PSettings.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PEnabled.setText("Points Enabled");
        PEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PEnabledActionPerformed(evt);
            }
        });
        PSettings.add(PEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 12, -1, -1));

        jLabel8.setText("Points Name");
        PSettings.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, 116, -1));

        PointsName.setEditable(false);
        PointsName.setText("jTextField1");
        PSettings.add(PointsName, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 82, 30));

        PRenBut.setText("Rename");
        PRenBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PRenButActionPerformed(evt);
            }
        });
        PSettings.add(PRenBut, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 85, 83, -1));

        EditPoints.setText("Edit");
        EditPoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditPointsActionPerformed(evt);
            }
        });
        PSettings.add(EditPoints, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 141, 83, -1));

        PWhenLive.setEditable(false);
        PWhenLive.setText("jTextField2");
        PSettings.add(PWhenLive, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 140, 82, -1));

        PWhenIdle.setEditable(false);
        PWhenIdle.setText("jTextField2");
        PWhenIdle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PWhenIdleActionPerformed(evt);
            }
        });
        PSettings.add(PWhenIdle, new org.netbeans.lib.awtextra.AbsoluteConstraints(311, 142, 102, -1));

        PStartPoints.setEditable(false);
        PStartPoints.setText("jTextField3");
        PSettings.add(PStartPoints, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 142, 70, -1));

        BHEnabled.setText("BankHeist Enabled");
        BHEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BHEnabledActionPerformed(evt);
            }
        });
        PSettings.add(BHEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 210, -1, -1));

        REnabled.setText("Raffle Enabled");
        REnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                REnabledActionPerformed(evt);
            }
        });
        PSettings.add(REnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 210, -1, -1));

        jLabel9.setText("Points When Live");
        PSettings.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 118, -1, -1));

        jLabel10.setText("Points When Not Live");
        PSettings.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(286, 118, -1, -1));

        jLabel11.setText("Starting Points");
        PSettings.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(449, 118, -1, -1));

        RouletteEnable.setText("Roulette Enabled");
        RouletteEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RouletteEnableActionPerformed(evt);
            }
        });
        PSettings.add(RouletteEnable, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, -1, -1));

        jPanel5.add(PSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 950, 471));

        settingsTabs.addTab("Points", jPanel5);

        SettingsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLayeredPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLayeredPane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        FollowSoundSet.setText("Set Follower Sound");
        FollowSoundSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowSoundSetActionPerformed(evt);
            }
        });
        jLayeredPane1.add(FollowSoundSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 34, 160, -1));

        FollowIMGSet.setText("Set Follower Image");
        FollowIMGSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowIMGSetActionPerformed(evt);
            }
        });
        jLayeredPane1.add(FollowIMGSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 63, 160, -1));

        jLabel15.setText("Alert Pane Settings");
        jLayeredPane1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(36, 9, -1, -1));

        SetFollowAlertMsg.setText("Set Follower Message");
        SetFollowAlertMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetFollowAlertMsgActionPerformed(evt);
            }
        });
        jLayeredPane1.add(SetFollowAlertMsg, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 92, 160, -1));

        FollowerMSGFont.setText("Set Follower Font");
        FollowerMSGFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowerMSGFontActionPerformed(evt);
            }
        });
        jLayeredPane1.add(FollowerMSGFont, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 121, 160, -1));

        SetFontColor.setText("Set Font Color");
        SetFontColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetFontColorActionPerformed(evt);
            }
        });
        jLayeredPane1.add(SetFontColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 150, 160, -1));

        SettingsPanel.add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 180, 180));

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setText("Chat Window Settings");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 12, -1, -1));

        WooshMeEnabled.setText("Woosh me");
        WooshMeEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WooshMeEnabledActionPerformed(evt);
            }
        });
        jPanel6.add(WooshMeEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 28, -1, -1));

        SettingsPanel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 280, 180, 60));

        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        FollowEnabled.setText("Follower Alert Enabled");
        FollowEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FollowEnabledActionPerformed(evt);
            }
        });
        jPanel10.add(FollowEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 190, -1));

        EFollowMsg.setText("Edit Chat Follower Message");
        EFollowMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EFollowMsgActionPerformed(evt);
            }
        });
        jPanel10.add(EFollowMsg, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, -1));

        OnlyWhenLiveEnabled.setText("OnlyWhenLive Enabled");
        OnlyWhenLiveEnabled.setToolTipText("This will prevent most commands from running when you are not online.");
        OnlyWhenLiveEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnlyWhenLiveEnabledActionPerformed(evt);
            }
        });
        jPanel10.add(OnlyWhenLiveEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, -1, -1));

        ClearCmdsEnabled.setText("Clear Commands Enabled");
        ClearCmdsEnabled.setToolTipText("When enabled, will clear all commands by Non-mods once ran. It will still act on those commands.");
        ClearCmdsEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearCmdsEnabledActionPerformed(evt);
            }
        });
        jPanel10.add(ClearCmdsEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, -1, -1));

        MeOutput.setText("Set /me Bot Output");
        MeOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MeOutputActionPerformed(evt);
            }
        });
        jPanel10.add(MeOutput, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, -1));

        SettingsPanel.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 900, 100));

        CUsernamePassword.setText("Set Custom Bot Username/Password");
        CUsernamePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CUsernamePasswordActionPerformed(evt);
            }
        });
        SettingsPanel.add(CUsernamePassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 420, 210, -1));

        ResetScottyName.setText("Reset Bot Name Back To Scottybot");
        ResetScottyName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetScottyNameActionPerformed(evt);
            }
        });
        SettingsPanel.add(ResetScottyName, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 450, 210, -1));

        settingsTabs.addTab("Settings", SettingsPanel);

        DonatorPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        DonationPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        YodaEnabled.setText("Yoda Enabled");
        YodaEnabled.setToolTipText("Enable Scottybot to speak like Yoda, you will!");
        YodaEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                YodaEnabledActionPerformed(evt);
            }
        });
        DonationPane.add(YodaEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        ChatEnabled.setText("!Chat Enabled");
        ChatEnabled.setToolTipText("Type !chat and a message, it will respond to you \"intelligently\"");
        ChatEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatEnabledActionPerformed(evt);
            }
        });
        DonationPane.add(ChatEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, -1, -1));

        ChuckEnabled.setText("!Chuck Enabled");
        ChuckEnabled.setToolTipText("Enabled !chuck commands for jokes");
        ChuckEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChuckEnabledActionPerformed(evt);
            }
        });
        DonationPane.add(ChuckEnabled, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, -1));

        DonatorPanel.add(DonationPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 940, 470));

        settingsTabs.addTab("Donator Stuff", DonatorPanel);

        jPanel4.add(settingsTabs, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 17, -1, 520));

        PNotes.setText("Patch Notes");
        PNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PNotesActionPerformed(evt);
            }
        });
        jPanel4.add(PNotes, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 0, -1, -1));

        whitelistPane.addTab("Settings", jPanel4);

        UChatters.setText("0 Unique Chatters This Session.");

        PercentRetainedViewers.setText("Not enough info yet for Retained Viewer Stats");

        jLabel13.setText("<<< EXPERIMENTAL");

        SessionMsgCount.setText("0 Messages This Session");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UChatters, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(PercentRetainedViewers, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel13))
                    .addComponent(SessionMsgCount, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(UChatters)
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PercentRetainedViewers)
                    .addComponent(jLabel13))
                .addGap(6, 6, 6)
                .addComponent(SessionMsgCount))
        );

        whitelistPane.addTab("Statistics", jPanel7);

        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        jLabel14.setText("Double click name to purge");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 726, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 232, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                            .addGap(740, 740, 740)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel13Layout.createSequentialGroup()
                            .addComponent(ChatSend, javax.swing.GroupLayout.PREFERRED_SIZE, 728, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(jLabel14)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 190, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(3, 3, 3)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ChatSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14))
                    .addGap(0, 160, Short.MAX_VALUE)))
        );

        jPanel8.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        whitelistPane.addTab("Chat", jPanel8);

        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        showWhitelist.setEditable(false);
        showWhitelist.setDoubleBuffered(true);
        jScrollPane4.setViewportView(showWhitelist);

        jPanel11.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(33, 35, 291, 467));

        jLabel16.setText("There will be functions here to Add/Remove whitelist users in the future");
        jPanel11.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 210, -1, -1));

        whitelistPane.addTab("Whitelist", jPanel11);

        getContentPane().add(whitelistPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, -1, 550));

        jButton5.setText("Check Updates");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 10, 152, -1));

        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(297, 11, -1, 30));

        AlertPaneOpen.setText("Alert Pane");
        AlertPaneOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlertPaneOpenActionPerformed(evt);
            }
        });
        getContentPane().add(AlertPaneOpen, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 128, -1));

        RefreshAll.setText("Refresh Settings");
        RefreshAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshAllActionPerformed(evt);
            }
        });
        getContentPane().add(RefreshAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 160, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void RefreshAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshAllActionPerformed
        RefreshAllSettings();
    }//GEN-LAST:event_RefreshAllActionPerformed

    private void whitelistPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_whitelistPaneMouseClicked
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
    }//GEN-LAST:event_whitelistPaneMouseClicked

    public void PopGuiSettings() {
        if (!CentralStore.GUISettings.containsKey("WooshME")) {
            CentralStore.GUISaveSettings("WooshME", "false");
        }
        if ("true".equals(CentralStore.GUIGetSetting("WooshME"))) {
            this.WooshMeEnabled.setSelected(true);
        } else {
            this.WooshMeEnabled.setSelected(false);
        }
    }
    private void ResetScottyNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetScottyNameActionPerformed
        try {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=CUsername&value=" + URLEncoder.encode("NULL", "UTF-8"));
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=CPassword&value=" + URLEncoder.encode("NULL", "UTF-8"));
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
                http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=CUsername&value=" + URLEncoder.encode(CUsername, "UTF-8"));
                http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=CPassword&value=" + URLEncoder.encode(CPassword, "UTF-8"));
                JOptionPane.showMessageDialog(rootPane, "Success, now in your channel just type !rejoin");
            } catch (UnsupportedEncodingException ex) {
                try {
                    http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=CUsername&value=" + URLEncoder.encode("NULL", "UTF-8"));
                    http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=CPassword&value=" + URLEncoder.encode("NULL", "UTF-8"));
                    JOptionPane.showMessageDialog(this, "Had an issue setting name, atempted to put back to default");
                } catch (UnsupportedEncodingException ex1) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }

        }
    }//GEN-LAST:event_CUsernamePasswordActionPerformed

    private void YodaEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YodaEnabledActionPerformed
        if (this.YodaEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseYoda&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseYoda&value=0");
        }
    }//GEN-LAST:event_YodaEnabledActionPerformed

    private void ChatEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatEnabledActionPerformed
        if (this.ChatEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseClever&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseClever&value=0");
        }
    }//GEN-LAST:event_ChatEnabledActionPerformed

    private void ChuckEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChuckEnabledActionPerformed
        if (this.ChuckEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseChuck&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseChuck&value=0");
        }
    }//GEN-LAST:event_ChuckEnabledActionPerformed

    private void MeOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MeOutputActionPerformed
        if (this.MeOutput.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseME&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseME&value=0");
        }
    }//GEN-LAST:event_MeOutputActionPerformed

    private void ClearCmdsEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearCmdsEnabledActionPerformed
        if (this.ClearCmdsEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=PurgeCommands&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=PurgeCommands&value=0");
        }
    }//GEN-LAST:event_ClearCmdsEnabledActionPerformed

    private void OnlyWhenLiveEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnlyWhenLiveEnabledActionPerformed
        if (this.OnlyWhenLiveEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=OnlyWhenLive&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=OnlyWhenLive&value=0");
        }
    }//GEN-LAST:event_OnlyWhenLiveEnabledActionPerformed

    private void EFollowMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EFollowMsgActionPerformed
        String OldFollowMSG = GetSettings().get("FollowMSG").toString();
        String NewFollowMSG = JOptionPane.showInputDialog(rootPane, "Set your follower message. use (_follower_) as a placeholder for the follower.", OldFollowMSG);
        try {
            if (!NewFollowMSG.equals(OldFollowMSG)) {
                try {
                    http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=FollowMSG&value=" + URLEncoder.encode(NewFollowMSG, "UTF-8"));
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
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseFollower&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseFollower&value=0");
        }
    }//GEN-LAST:event_FollowEnabledActionPerformed

    private void REnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REnabledActionPerformed
        if (this.REnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseRaffle&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseRaffle&value=0");
        }
    }//GEN-LAST:event_REnabledActionPerformed

    private void BHEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BHEnabledActionPerformed
        if (this.BHEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseBankheist&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseBankheist&value=0");
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
            try {
                Integer.parseInt(idle);
                Integer.parseInt(notidle);
                Integer.parseInt(StartPoints);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, "An entry is not numeric.");
                return;
            }

            this.PWhenIdle.setEditable(false);
            this.PWhenLive.setEditable(false);
            this.PStartPoints.setEditable(false);
            try {
                http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=idlepoints&value=" + URLEncoder.encode(idle, "UTF-8"));
                http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=notidlepoints&value=" + URLEncoder.encode(notidle, "UTF-8"));
                http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=startpoints&value=" + URLEncoder.encode(StartPoints, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.EditPoints.setText("Edit");
        } else {
            this.EditPoints.setText("Save");
            this.PWhenIdle.setEditable(true);
            this.PWhenLive.setEditable(true);
            this.PStartPoints.setEditable(true);
        }
    }//GEN-LAST:event_EditPointsActionPerformed

    private void PEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PEnabledActionPerformed
        if (this.PEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=Points&value=1");
            Component[] ToEnable = this.PSettings.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(true);
            }
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=Points&value=0");
            Component[] ToEnable = this.PSettings.getComponents();
            for (Component t : ToEnable) {
                t.setEnabled(false);
            }
            this.PEnabled.setEnabled(true);
        }
    }//GEN-LAST:event_PEnabledActionPerformed

    private void PRenButActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PRenButActionPerformed
        if (this.PointsName.isEditable()) {
            this.PointsName.setEditable(false);
            this.PRenBut.setText("Rename");
            try {
                String pname = this.PointsName.getText().replace("!", "");
                http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=PointsName&value=" + URLEncoder.encode(pname, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.PointsName.setEditable(true);
            this.PRenBut.setText("Save");
        }
    }//GEN-LAST:event_PRenButActionPerformed

    private void addbadwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbadwordActionPerformed

        try {
            String[] ToAdd = this.AddBadWord.getText().split(" ");
            http.GetScotty("https://api.scottybot.net/badwords/add?authkey=" + AuthKey + "&word=" + URLEncoder.encode(ToAdd[0], "UTF-8"));
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
                http.GetScotty("https://api.scottybot.net/badwords/delete?authkey=" + AuthKey + "&word=" + URLEncoder.encode(t, "UTF-8"));
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
        http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=TimeOutLength&value=" + String.valueOf(this.TimoutDuration.getValue()));
    }//GEN-LAST:event_TimoutDurationMouseReleased

    private void TimoutDurationMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TimoutDurationMouseDragged
        this.TimeoutLabel.setText(String.valueOf(this.TimoutDuration.getValue()) + " Minutes");
    }//GEN-LAST:event_TimoutDurationMouseDragged

    private void CapPercentMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CapPercentMouseReleased
        http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=CapPercent&value=" + this.CapPercent.getValue());
    }//GEN-LAST:event_CapPercentMouseReleased

    private void CapPercentMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CapPercentMouseDragged
        this.CapsPercentDis.setText(String.valueOf(this.CapPercent.getValue()) + " Percent");        // TODO add your handling code here:
    }//GEN-LAST:event_CapPercentMouseDragged

    private void SymPercentPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SymPercentPropertyChange

    }//GEN-LAST:event_SymPercentPropertyChange

    private void SymPercentMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SymPercentMouseReleased
        http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=SymbolCount&value=" + this.SymPercent.getValue());        // TODO add your handling code here:
    }//GEN-LAST:event_SymPercentMouseReleased

    private void SymPercentMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SymPercentMouseDragged
        this.SymPercentDis.setText(String.valueOf(this.SymPercent.getValue()) + " Percent");        // TODO add your handling code here:
    }//GEN-LAST:event_SymPercentMouseDragged

    private void SymbolsOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SymbolsOnOffActionPerformed
        if (this.SymbolsOnOff.isSelected()) {
            this.SymbolsOnOff.setText("Symbols Enabled");
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseSymbols&value=1");
        } else {
            this.SymbolsOnOff.setText(("Symbols Disabled"));
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseSymbols&value=0");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_SymbolsOnOffActionPerformed

    private void CapsOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CapsOnOffActionPerformed
        if (this.CapsOnOff.isSelected()) {
            this.CapsOnOff.setText("Caps Enabled");
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseCapitals&value=1");
        } else {
            this.CapsOnOff.setText(("Caps Disabled"));
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseCapitals&value=0");
        }
    }//GEN-LAST:event_CapsOnOffActionPerformed

    private void CapsOnOffMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CapsOnOffMouseReleased

    }//GEN-LAST:event_CapsOnOffMouseReleased

    private void RepeatOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RepeatOnOffActionPerformed
        if (this.RepeatOnOff.isSelected()) {
            this.RepeatOnOff.setText("Repeat Enabled");
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseRepeat&value=1");
        } else {
            this.RepeatOnOff.setText(("Repeat Disabled"));
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseRepeat&value=0");
        }
    }//GEN-LAST:event_RepeatOnOffActionPerformed

    private void LinksOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LinksOnOffActionPerformed
        if (this.LinksOnOff.isSelected()) {
            this.LinksOnOff.setText("Links Enabled");
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseLinks&value=1");
        } else {
            this.LinksOnOff.setText(("Links Disabled"));
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseLinks&value=0");
        }
    }//GEN-LAST:event_LinksOnOffActionPerformed

    private void FOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FOnOffActionPerformed
        if (this.FOnOff.isSelected()) {
            this.FOnOff.setText("All Filtering Enabled");
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseFilter&value=1");
        } else {
            this.FOnOff.setText(("All Filtering Disabled"));
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseFilter&value=0");
        }
    }//GEN-LAST:event_FOnOffActionPerformed

    private void QEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QEnabledActionPerformed
        if (this.QEnabled.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseQuotes&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseQuotes&value=0");
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

    private void RepeatListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RepeatListActionPerformed
        RepeatList rl = new RepeatList();
        rl.setVisible(true);
    }//GEN-LAST:event_RepeatListActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        DeletePermAdjust dpa = new DeletePermAdjust();
        dpa.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        AddEditCMD cmd = new AddEditCMD();
        cmd.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

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

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (!this.CheckNewVer()) {
            JOptionPane.showMessageDialog(rootPane, "No new updates.");
        }
    }//GEN-LAST:event_jButton5ActionPerformed
    AlertFrame af = new AlertFrame();
    private void AlertPaneOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlertPaneOpenActionPerformed
        if (!GUISettings.containsKey("FollowSound")) {
            JOptionPane.showMessageDialog(rootPane, "No Audio File Set");
            return;
        }
        if (!GUISettings.containsKey("FollowIMG")) {
            JOptionPane.showMessageDialog(rootPane, "No Image File Set");
            return;
        }
        if (!GUISettings.containsKey("FollowerMSG")) {
            JOptionPane.showMessageDialog(rootPane, "No Follower Message Set");
            return;
        }
        if (!GUISettings.containsKey("FFontSize") || !GUISettings.containsKey("FFontColor") || !GUISettings.containsKey("FFontStyle") || !GUISettings.containsKey("FFontName")) {
            JOptionPane.showMessageDialog(rootPane, "No Follower Message Font Settings set");
            return;
        }

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
            CentralStore.GUISaveSettings("FollowSound", FileLoc);
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
            CentralStore.GUISaveSettings("FollowIMG", FileLoc);
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
            CentralStore.GUISaveSettings("WooshME", "true");
        } else {
            CentralStore.GUISaveSettings("WooshME", "false");
        }

    }//GEN-LAST:event_WooshMeEnabledActionPerformed

    private void ChatSendKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ChatSendKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                CentralStore.session.getBasicRemote().sendText(SendMSG(ChatSend.getText().trim()));
                ChatSend.setText("");
            } catch (IOException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ChatSendKeyPressed

    private void ChatSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChatSendActionPerformed

    private void ViewersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ViewersMouseClicked
        if (evt.getClickCount() == 2) {
            try {
                session.getBasicRemote().sendText(CentralStore.SendMSG("+p " + this.Viewers.getSelectedValue().toString()).trim());
            } catch (IOException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ViewersMouseClicked

    private void RouletteEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RouletteEnableActionPerformed
        if (this.RouletteEnable.isSelected()) {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseRoulette&value=1");
        } else {
            http.GetScotty("https://api.scottybot.net/settings/change?authkey=" + AuthKey + "&setting=UseRoulette&value=0");
        }
    }//GEN-LAST:event_RouletteEnableActionPerformed

    private void PNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PNotesActionPerformed
        PatchNotes pnoteswindow = new PatchNotes();
        pnoteswindow.setVisible(true);
    }//GEN-LAST:event_PNotesActionPerformed

    private void PopWhiteList() {
        JSONObject whitelist = null;
        try {
            whitelist = (JSONObject) new JSONParser().parse(http.GetScotty("https://api.scottybot.net/whitelist?channame=" + ChanID));
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
                System.err.println(DaysInMilli + ":" + TimeLeft + ":" + System.currentTimeMillis() + ":" + dur);
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
                } catch (ParseException ex) {
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

        if ("1".equals(GetSettings().get("UseME"))) {
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

        if ("1".equals(GetSettings().get("UseYoda"))) {
            this.YodaEnabled.setSelected(true);
        } else {
            this.YodaEnabled.setSelected(false);
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

    }

    public void PopBadWords() {
        BadWordsList.clear();
        this.BadWordList.setModel(BadWordsList);
        JSONObject ToPopulate = null;
        while (true) {
            try {
                ToPopulate = (JSONObject) parser.parse(http.GetScotty("https://api.scottybot.net/badwords?authkey=" + AuthKey));
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

    public void RefreshAllSettings() {
        try {
            RefreshSettings();

        } catch (ParseException ex) {
            Logger.getLogger(ControlPanel.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        PopFilterSettings();
        PopulateAllSettings();
    }

    public void PopFilterSettings() {

        JSONObject FSettings = GetSettings();

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
    public javax.swing.JLabel CurViewers;
    private javax.swing.JPanel DonationPane;
    private javax.swing.JPanel DonatorPanel;
    private javax.swing.JButton EFollowMsg;
    private javax.swing.JButton EditPoints;
    private javax.swing.JToggleButton FOnOff;
    private javax.swing.JCheckBox FollowEnabled;
    private javax.swing.JButton FollowIMGSet;
    private javax.swing.JButton FollowSoundSet;
    private javax.swing.JButton FollowerMSGFont;
    private javax.swing.JToggleButton LinksOnOff;
    private javax.swing.JCheckBox MeOutput;
    private javax.swing.JLabel NumOfQuotes;
    private javax.swing.JCheckBox OnlyWhenLiveEnabled;
    private javax.swing.JCheckBox PEnabled;
    private javax.swing.JButton PNotes;
    private javax.swing.JButton PRenBut;
    private javax.swing.JPanel PSettings;
    private javax.swing.JTextField PStartPoints;
    private javax.swing.JTextField PWhenIdle;
    private javax.swing.JTextField PWhenLive;
    public javax.swing.JLabel PercentRetainedViewers;
    private javax.swing.JTextField PointsName;
    private javax.swing.JCheckBox QEnabled;
    private javax.swing.JTextArea QuotePanel;
    private javax.swing.JCheckBox REnabled;
    private javax.swing.JButton RefreshAll;
    private javax.swing.JButton RefreshCMDs;
    private javax.swing.JButton RemoveBadWord;
    private javax.swing.JButton RepeatList;
    private javax.swing.JToggleButton RepeatOnOff;
    private javax.swing.JButton ResetScottyName;
    private javax.swing.JCheckBox RouletteEnable;
    public javax.swing.JLabel SessionMsgCount;
    private javax.swing.JButton SetFollowAlertMsg;
    private javax.swing.JButton SetFontColor;
    private javax.swing.JPanel SettingsPanel;
    private javax.swing.JSlider SymPercent;
    private javax.swing.JLabel SymPercentDis;
    private javax.swing.JToggleButton SymbolsOnOff;
    private javax.swing.JLabel TimeoutLabel;
    private javax.swing.JSlider TimoutDuration;
    public javax.swing.JLabel TopViewers;
    public javax.swing.JLabel UChatters;
    public javax.swing.JList Viewers;
    public javax.swing.JCheckBox WooshMeEnabled;
    private javax.swing.JCheckBox YodaEnabled;
    private javax.swing.JButton addbadword;
    private javax.swing.JButton addquotebutton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
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
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
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
    private javax.swing.JTabbedPane settingsTabs;
    private javax.swing.JTextPane showWhitelist;
    private javax.swing.JTabbedPane whitelistPane;
    // End of variables declaration//GEN-END:variables
}
