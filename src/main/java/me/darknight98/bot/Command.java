/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.darknight98.bot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author DarKnight98
 */
public interface Command {
    
    public boolean called (String [] args, MessageReceivedEvent event);
    public void action (String [] args, MessageReceivedEvent event);
    public String help ();
    public void executed (boolean success, MessageReceivedEvent event);
    
}
