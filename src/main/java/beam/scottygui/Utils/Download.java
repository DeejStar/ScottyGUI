/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.Utils;

//import java.io.*;
//import java.net.*;
import static beam.scottygui.Stores.CS.extchat;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import javax.swing.JOptionPane;

public class Download extends Observable implements Runnable {

// Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;

// These are the status names.
    public static final String STATUSES[] = {"Downloading",
        "Paused", "Complete", "Cancelled", "Error"};

// These are the status codes.
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    private URL url; // download URL
    private int size; // size of download in bytes
    private int downloaded; // number of bytes downloaded
    private int status; // current status of download
    private int redowncnt = 0;
    private String checksum;

    public String getCheckSum() throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        String datafile = getFileName(url);
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(datafile);
        byte[] dataBytes = new byte[1024];
        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("Digest(in hex format):: " + sb.toString());
        return sb.toString();
    }

    public boolean checksumMatch(String checksum) throws NoSuchAlgorithmException, IOException {
        String good = getCheckSum();
        System.out.println("New " + checksum + " : Checked " + good);
        return checksum.equals(good.trim());

    }

// Constructor for Download.
    public Download(URL url, String checksum) {
        this.url = url;
        this.checksum = checksum;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;

        // Begin the download.
    }

// Get this download's URL.
    public String getUrl() {
        return url.toString();
    }

// Get this download's size.
    public int getSize() {
        return size;
    }

// Get this download's progress.
    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

// Get this download's status.
    public int getStatus() {
        return status;
    }

// Pause this download.
    public void pause() {
        status = PAUSED;
        stateChanged();
    }

// Resume this download.
    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        download();
    }

// Cancel this download.
    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }

// Mark this download as having an error.
    private void error() {
        status = ERROR;
        stateChanged();
    }

// Start or resume downloading.
    public void download() {
        //File f = new File(url.getFile());
        //f.delete();
        //System.out.println("Old file deleted, continuing");
        Thread thread = new Thread(this);
        thread.start();
    }

// Get file name portion of URL.
    private String getFileName(URL url) {
        String filename = url.getFile();
        return filename.substring(filename.lastIndexOf('/') + 1);
    }

// Download file.
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            // Open connection to URL.
            HttpURLConnection connection
                    = (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range",
                    "bytes=" + downloaded + "-");

            // Connect to server.
            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                System.out.println(connection.getResponseMessage());
                error();
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                System.out.println(contentLength + " : " + connection.getResponseMessage());
                error();
            }

            /* Set the size for this download if it
     hasn't been already set. */
            if (size == -1) {
                size = contentLength;
                stateChanged();
            }

            // Open file and seek to the end of it.
            file = new RandomAccessFile(getFileName(url), "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == DOWNLOADING) {
                /* Size buffer according to how much of the
       file is left to download. */
                byte buffer[];
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1) {
                    break;
                }

                // Write buffer to file.
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }

            /* Change status to complete if this point was
     reached because downloading has finished. */
            if (status == DOWNLOADING) {
                status = COMPLETE;
                stateChanged();
                if (!this.checksumMatch(checksum) && this.redowncnt < 5) {
                    System.out.println("BOOT");
                    this.redowncnt++;
                    this.download();
                    return;
                }
                File oldf = new File(getFileName(url) + ".old");
                File newf = new File(getFileName(url));
                if (!this.checksumMatch(checksum)) {
                    newf.delete();
                    Files.copy(oldf, new File(getFileName(url)));
                    try {
                        String java = "\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\"";
                        String os = System.getProperty("os.name");
                        JOptionPane.showMessageDialog(null, "Failed to update, reverting to old copy");
                        if (os.equalsIgnoreCase("Linux")) {
                            System.out.println("Linux Detected");
                            Runtime.getRuntime().exec(new String[]{"sh", "-c", java + " -jar " + "./ScottyGUI.jar"});
                        } else {
                            Runtime.getRuntime().exec(new String[]{"cmd", "/C", java + " -jar " + "./ScottyGUI.jar"});
                        }
                        System.exit(0);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(extchat, "Unable to restart automatically, please do so manually.");
                    }
                } else {
                    oldf.deleteOnExit();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            error();
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
        }
    }
    // Notify observers that this download's status has changed.

    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
}
