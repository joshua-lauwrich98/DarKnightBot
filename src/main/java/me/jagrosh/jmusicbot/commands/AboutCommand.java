package me.jagrosh.jmusicbot.commands;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.awt.Color;
import java.time.temporal.TemporalAccessor;
import me.jagrosh.jdautilities.commandclient.Command;
import me.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutCommand
extends Command {
    public static boolean IS_AUTHOR = true;
    private final Color color;
    private final String description;
    private final long perms;
    private String oauthLink;
    private final String[] features;

    public /* varargs */ AboutCommand(Color color, String description, String[] features, Permission ... requestedPerms) {
        this.color = color;
        this.description = description;
        this.features = features;
        this.name = "about";
        this.help = "shows info about the bot";
        this.guildOnly = false;
        if (requestedPerms == null) {
            this.oauthLink = "";
            this.perms = 0;
        } else {
            long p = 0;
            for (Permission perm : requestedPerms) {
                p += perm.getRawValue();
            }
            this.perms = p;
        }
    }

    protected void execute(CommandEvent event) {
        if (this.oauthLink == null) {
            try {
                JSONObject app = ((JsonNode)Unirest.get((String)"https://discordapp.com/api/oauth2/applications/@me").header("Authorization", event.getJDA().getToken()).asJson().getBody()).getObject();
                boolean isPublic = app.has("bot_public") ? app.getBoolean("bot_public") : true;
                this.oauthLink = isPublic ? "https://discordapp.com/oauth2/authorize?client_id=" + app.getString("id") + "&permissions=" + this.perms + "&scope=bot" : "";
            }
            catch (UnirestException | JSONException e) {
                SimpleLog.getLog((String)"OAuth2").fatal((Object)("Could not generate invite link: " + (Object)e));
                this.oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.getGuild() == null ? this.color : event.getGuild().getSelfMember().getColor());
        builder.setAuthor("All about " + event.getSelfUser().getName() + "!", null, event.getSelfUser().getAvatarUrl());
        String descr = "Hello! I am **" + event.getSelfUser().getName() + "**, " + this.description + "\nI " + (IS_AUTHOR ? "was written in Java" : "am owned") + " by **" + event.getJDA().getUserById(event.getClient().getOwnerId()).getName() + "** using " + "JDA library (" + JDAInfo.VERSION + ") <:jda:230988580904763393>\nType `" + event.getClient().getTextualPrefix() + "help` to see my commands!\n\nSome of my features include: ```css";
        for (String feature : this.features) {
            descr = descr + "\n" + event.getClient().getSuccess() + " " + feature;
        }
        descr = descr + " ```";
        builder.setDescription(descr);
        builder.addField("Stats", "" + event.getJDA().getGuilds().size() + " servers\n" + (event.getJDA().getShardInfo() == null ? "1 shard" : new StringBuilder().append(event.getJDA().getShardInfo().getShardTotal()).append(" shards").toString()), true);
        builder.addField("Users", "" + event.getJDA().getUsers().size() + " unique\n" + event.getJDA().getUsers().stream().filter(u -> {
            try {
                OnlineStatus status = event.getJDA().getGuilds().stream().filter(g -> g.isMember(u)).findAny().get().getMember(u).getOnlineStatus();
                return status == OnlineStatus.ONLINE || status == OnlineStatus.IDLE || status == OnlineStatus.DO_NOT_DISTURB || status == OnlineStatus.INVISIBLE;
            }
            catch (Exception e) {
                return false;
            }
        }
        ).count() + " online", true);
        builder.addField("Channels", "" + event.getJDA().getTextChannels().size() + " Text\n" + event.getJDA().getVoiceChannels().size() + " Voice", true);
        builder.setFooter("Last restart", null);
        builder.setTimestamp((TemporalAccessor)event.getClient().getStartTime());
        event.reply(builder.build());
    }
}