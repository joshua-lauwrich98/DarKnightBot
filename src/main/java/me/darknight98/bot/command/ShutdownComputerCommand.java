/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.darknight98.bot.command;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.darknight98.bot.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author DarKnight98
 */
public class ShutdownComputerCommand implements Command {
    private final String HELP = "USAGE: d!shutdowncomp";
    
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        String author = event.getAuthor().getId();
        if (author.equals("292285229798785024")) return true;
        else {
            String msg = String.format("<@%s>, you are not athorized to do the command!", author);
            event.getChannel().sendMessage(msg);
            return false;
        }
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("shutdown -s -t 0");
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(ShutdownComputerCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String help() {
        return HELP;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
         
    }
}
