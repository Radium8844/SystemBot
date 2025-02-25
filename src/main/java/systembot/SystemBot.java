package systembot;


import org.apache.commons.logging.Log;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import systembot.Utils.*;

import static systembot.Utils.readJsonFile;


public class SystemBot {
//    public static final File prefsFile = new File("prefs.properties");
//    public static Net net = new Net();
//    public static Prefs prefs = new Prefs(prefsFile);
//    public GetMap map = new GetMap();

    //    public static JedisPool pool;
    public static DiscordApi api = null;
    public static String prefix = "<";
    public static String live_chat_channel_id = "";
    public static String bot_channel_id = null;
    public static String staff_bot_channel_id = null;
    public static String admin_bot_channel_id = null;
    public static String suggestion_channel_id = null;
    public static String dev_roleid = null;
    public static String serverName = "<untitled>";
    public static JSONObject data; //token, channel_id, role_id
    public static String apiToken = "";
    private static JSONObject alldata;

    private final long CDT = 300L;

    // register event handlers and create variables in the constructor
    public static void main(String[] args) {
        Utils.init();
        try {
            data = alldata = readJsonFile("settings.json");
            bot_channel_id = alldata.getString("bot_channel_id");
            staff_bot_channel_id = alldata.getString("staff_bot_channel_id");
            admin_bot_channel_id = alldata.getString("admin_bot_channel_id");
            suggestion_channel_id = alldata.getString("suggestion_channel_id");
            dev_roleid = alldata.getString("dev_roleid");

            if (data.has("token")) {
                apiToken = data.getString("token");
                System.out.println("Token set successfully");
            }
        } catch (Exception e) {
            System.out.println("Couldn't read settings.json file.");
            e.printStackTrace();
            return;
        }


        try {
            api = new DiscordApiBuilder().setToken(apiToken).login().join();
            System.out.println("logged in as: " + api.getYourself());
        } catch (Exception e) {
            System.out.println("Couldn't log into discord.");
        }
        BotThread bt = new BotThread(api, Thread.currentThread(), alldata);
        bt.setDaemon(false);
        bt.start();

        // setup prefix
        if (data.has("prefix")) {
            prefix = String.valueOf(data.getString("prefix").charAt(0));
        } else {
            System.out.println("Prefix not found, using default '.' prefix.");
        }

        // setup name
        if (data.has("server_name")) {
            serverName = String.valueOf(data.getString("server_name"));
        } else {
            System.out.println("No server name setting detected!");
        }
    }

    public static TextChannel getTextChannel(String id) {
        Optional<Channel> dc = api.getChannelById(id);
        if (dc.isEmpty()) {
            System.out.println("[ERR!] discordplugin: channel not found! " + id);
            return null;
        }
        Optional<TextChannel> dtc = dc.get().asTextChannel();
        if (dtc.isEmpty()) {
            System.out.println("[ERR!] discordplugin: textchannel not found! " + id);
            return null;
        }
        return dtc.get();
    }

}