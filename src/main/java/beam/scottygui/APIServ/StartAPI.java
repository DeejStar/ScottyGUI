/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beam.scottygui.APIServ;

import beam.scottygui.OAuth.OAuthHandler;
import beam.scottygui.Stores.CS;
import static beam.scottygui.Stores.CS.GUISettings;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.io.IOUtils;

@XmlRootElement
@Path("/")
public class StartAPI {

    @GET
    @Path("/oauthjoin")
    @Produces(MediaType.TEXT_PLAIN)
    public String OAuthJoin(@Context UriInfo info) {
        System.out.println(info.getQueryParameters());
        String Code = info.getQueryParameters().getFirst("code");
        System.out.println("Sending " + Code);
        boolean worked = OAuthHandler.GenToken(Code);
        if (worked) {
            return "Success, you may now close this page.";
        }
        return "Failed";
    }

    @GET
    @Path("/followimg")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response FolIMG() {
        String image = "/assets/gir.png";
        if (GUISettings.containsKey("FollowIMG")) {
            image = GUISettings.get("FollowIMG").toString();
            return Response.ok().entity(new File(image)).build();
        } else {
            Response respond = Response.ok().entity(this.getClass().getResourceAsStream(image)).build();
            return respond;
        }
    }

    @GET
    @Path("/subimg")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response SubIMG() {
        String image = "/assets/gir.png";
        if (GUISettings.containsKey("SubIMG")) {
            image = GUISettings.get("SubIMG").toString();
            return Response.ok().entity(new File(image)).build();
        } else {
            Response respond = Response.ok().entity(this.getClass().getResourceAsStream(image)).build();
            return respond;
        }
    }

    @GET
    @Path("/subalert")
    @Produces(MediaType.TEXT_HTML)
    public String SubAlert(@Context UriInfo info) {
        String HostName = info.getBaseUri().getHost();
        System.out.println(HostName);
        if (SHandler.QueuedFollowers()) {
            try {
                try {
                    while (!CS.playMP3.isComplete()) {
                        sleep(150);
                    }
                } catch (Exception ignore) {

                }
                InputStream HTMLFile = this.getClass().getResourceAsStream("/assets/followalert.html");
                String HTML = IOUtils.toString(HTMLFile, "UTF-8");
                String FontName = "Arial";
                if (GUISettings.containsKey("SFontName")) {
                    FontName = GUISettings.get("SFontName").toString();
                }
                int FontSize = 64;
                if (GUISettings.containsKey("SFontSize")) {
                    FontSize = Integer.parseInt(GUISettings.get("SFontSize").toString());
                }
                //System.out.println(FontName + ":" + FontSize);
                Color toWeb = Color.RED;
                if (GUISettings.containsKey("SFontColor")) {
                    String FontColor = "";

                    FontColor = GUISettings.get("SFontColor").toString();
                    toWeb = Color.decode(FontColor);
                }
                String Sub = SHandler.getNextFollower(); //FSHandler.getNextFollower();
                String FollowMessage = GUISettings.getOrDefault("SubMSG", "(_sub_) has subscribed!").toString();
                FollowMessage = FollowMessage.replace("(_sub_)", Sub);
                int FontStyle = 0;
                String FStyle = "";
                if (GUISettings.containsKey("SFontStyle")) {
                    FontStyle = Integer.parseInt(GUISettings.get("SFontStyle").toString());
                    switch (FontStyle) {
                        case Font.BOLD:
                            FStyle = "Bold";
                            break;
                        case Font.ITALIC:
                            FStyle = "Italic";
                            break;
                        default:
                            FStyle = "Plain";
                            break;
                    }
                } else {
                    FStyle = "Plain";
                }
                String NColor = SHandler.toHexString(toWeb);
                String msg = FollowMessage;
                HTML = HTML.replace("(FSTYLE)", FStyle);
                HTML = HTML.replace("(FSIZE)", "" + FontSize);
                HTML = HTML.replace("(FNAME)", FontName);
                HTML = HTML.replace("(FCOLOR)", NColor);
                HTML = HTML.replace("{FOL_MSG}", msg);
                HTML = HTML.replace("{IMGURL}", "http://{HOSTNAME}:9090/subimg");
                HTML = HTML.replace("{REDIRECT}", "http://{HOSTNAME}:9090/subalert");
                HTML = HTML.replace("{HOSTNAME}", HostName);
                SHandler.SubSndStart();
                return HTML;
            } catch (IOException ex) {
                Logger.getLogger(StartAPI.class.getName()).log(Level.SEVERE, null, ex);
                return "<meta http-equiv=\"refresh\" content=\"1; url=http://" + HostName + ":9090/subalert\" />";

            }
        } else {
            return "<meta http-equiv=\"refresh\" content=\"1; url=http://" + HostName + ":9090/subalert\" />";
        }
    }

    @GET
    @Path("/followalert")
    @Produces(MediaType.TEXT_HTML)
    public String FolAlert(@Context UriInfo info) {
        String HostName = info.getBaseUri().getHost();
        System.out.println(HostName);
        if (FHandler.QueuedFollowers()) {
            try {
                try {
                    while (!CS.playMP3.isComplete()) {
                        sleep(150);
                    }
                } catch (Exception ignore) {

                }
                InputStream HTMLFile = this.getClass().getResourceAsStream("/assets/followalert.html");
                String HTML = IOUtils.toString(HTMLFile, "UTF-8");
                String FontName = "Arial";
                if (GUISettings.containsKey("FFontName")) {
                    FontName = GUISettings.get("FFontName").toString();
                }
                int FontSize = 64;
                if (GUISettings.containsKey("FFontSize")) {
                    FontSize = Integer.parseInt(GUISettings.get("FFontSize").toString());
                }
                //System.out.println(FontName + ":" + FontSize);
                Color toWeb = Color.RED;
                if (GUISettings.containsKey("FFontColor")) {
                    String FontColor = "";

                    FontColor = GUISettings.get("FFontColor").toString();
                    toWeb = Color.decode(FontColor);
                }
                String Follower = FHandler.getNextFollower(); //FSHandler.getNextFollower();
                String FollowMessage = GUISettings.getOrDefault("FollowerMSG", "(_follower_) has followed!").toString();
                FollowMessage = FollowMessage.replace("(_follower_)", Follower);
                int FontStyle = 0;
                String FStyle = "";
                if (GUISettings.containsKey("FFontStyle")) {
                    FontStyle = Integer.parseInt(GUISettings.get("FFontStyle").toString());
                    switch (FontStyle) {
                        case Font.BOLD:
                            FStyle = "Bold";
                            break;
                        case Font.ITALIC:
                            FStyle = "Italic";
                            break;
                        default:
                            FStyle = "Plain";
                            break;
                    }
                } else {
                    FStyle = "Plain";
                }
                String NColor = FHandler.toHexString(toWeb);
                String msg = FollowMessage;
                HTML = HTML.replace("(FSTYLE)", FStyle);
                HTML = HTML.replace("(FSIZE)", "" + FontSize);
                HTML = HTML.replace("(FNAME)", FontName);
                HTML = HTML.replace("(FCOLOR)", NColor);
                HTML = HTML.replace("{FOL_MSG}", msg);
                HTML = HTML.replace("{IMGURL}", "http://{HOSTNAME}:9090/followimg");
                HTML = HTML.replace("{REDIRECT}", "http://{HOSTNAME}:9090/followalert");

                HTML = HTML.replace("{HOSTNAME}", HostName);
                FHandler.FolSndStart();
                return HTML;
            } catch (IOException ex) {
                Logger.getLogger(StartAPI.class.getName()).log(Level.SEVERE, null, ex);
                return "<meta http-equiv=\"refresh\" content=\"1; url=http://" + HostName + ":9090/followalert\" />";

            }
        } else {
            return "<meta http-equiv=\"refresh\" content=\"1; url=http://" + HostName + ":9090/followalert\" />";
        }
    }
}
