/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.darknight98.bot;

import me.jagrosh.jmusicbot.JMusicBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author DarKnight98
 */
public class BotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith("d!") && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            JMusicBot.handleCommand(JMusicBot.parser.parse(event.getMessage().getContent(), event));
        }
            
    }
}
