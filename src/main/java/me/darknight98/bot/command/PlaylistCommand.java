/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.darknight98.bot.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.darknight98.bot.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author DarKnight98
 */
public class PlaylistCommand implements Command {
    
    private final String HELP = "USAGE: d!help math";

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            event.getChannel().sendMessage(
                String.format("<@%s>, Command not available!", event.getAuthor().getId()));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        String authorId = event.getAuthor().getId();
        
        switch (args[0]) {
            case "list" :
                listCommand(args, event, authorId);
                break;
            case "create" :
                try {
                    createPlaylist(authorId, args[1], event);
                } catch (IndexOutOfBoundsException ex) {
                    event.getChannel().sendMessage(String.format("<@%s>, use d!playlist create [title] to create a new playlist", authorId));
                }
                break;
            case "remove" :
                if (args.length == 2) {
                    deletePlaylist(authorId, args[1], event);
                } else if (args.length == 3) {
                    try {
                        deleteSong(authorId, args[1], Integer.parseInt(args[2]), event);
                        event.getChannel().sendMessage(String.format("<@%s>, Successfully deleted the song!", authorId));
                    } catch (IOException ex) {
                        Logger.getLogger(PlaylistCommand.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NumberFormatException ex2) {
                        event.getChannel().sendMessage(String.format("<@%s>, use d!playlist remove [playlist] [songNumber] to remove a song in a playlist", authorId));
                    }
                }
                break;
            case "add" :
                try {
                    String songTitle = "";
                    for (int i = 2; i < args.length-1; i++) {
                        songTitle += args[i];
                        if (i != args.length-2) songTitle += " ";
                    }
                    if (addSong(authorId, args[1], event, songTitle, args[args.length-1]))
                        event.getChannel().sendMessage(String.format("<@%s>, Successfully add song", authorId));
                } catch (IOException ex) {
                    event.getChannel().sendMessage(String.format("<@%s>, use d!playlist add [playlist] [song] [link] to add a song to a playlist", authorId));
                }
                break;
            case "song" :
                try {
                    listSong(authorId, args[1], event);
                } catch (IOException ex) {
                    event.getChannel().sendMessage(String.format("<@%s>, Playlist not found!", authorId));
                }
                break;
            case "play" :
                List<String[]> songList = null;
                try {
                    songList = getFileContent("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + authorId + "-" + args[1] + ".txt");
                } catch (IOException ex) {
                    event.getChannel().sendMessage(String.format("<@%s>, Playlist not found!", authorId));
                    break;
                }
                
                if (args.length == 3) playSong(songList, Integer.parseInt(args[2]), event);
                else if (args.length == 2) playSongInPlaylist(songList, event);
                
                break;
            case "rename" :
                try {
                    renamePlaylist(authorId, args[1], args[2], event);
                    event.getChannel().sendMessage(String.format("<@%s>, Successfully rename playlist!", authorId));
                } catch (IOException ex) {
                    Logger.getLogger(PlaylistCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case "help" :
                showHelp(authorId, event);
                break;
        }
    }

    @Override
    public String help() {
        return HELP;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        
    }
    
    //List Command
    private void listCommand (String [] args, MessageReceivedEvent e, String id) {
        List<String> results = new ArrayList<>();


        File[] files = new File("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 

        try {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().toLowerCase().endsWith(".txt") && file.getName().toLowerCase().startsWith(id)) {
                        String [] fileTitle = file.getName().split("-");
                        results.add(fileTitle[1]);
                    }
                }
            }
        } catch (NullPointerException ex) {
            e.getChannel().sendMessage(String.format("<@%s>, You haven't create any playlist yet!", id));
            return;
        }
        
        if (results.isEmpty()) {
            e.getChannel().sendMessage(String.format("<@%s>, You haven't create any playlist yet!", id));
        } else {
            String res = "These are your playlist:\n";
            for (int i = 0; i < results.size(); i++) {
                res += (i+1) + ". ";
                String title = results.get(i);
                res += title.substring(0, title.length()-4);
                if (i != results.size()-1) res += "\n";
            }

            e.getChannel().sendMessage(String.format("<@%s>, %s", id, res));
        }
    }
    
    //Getting all content in a file 
    private List getFileContent(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        List<String []> lines;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String [] content = line.split(" ");
                lines.add(content);
            }
        }
        return lines;
    }
    
    //Creating a new playlist
    private void createPlaylist(String id, String title, MessageReceivedEvent e) {
        String path = "D:" + File.separator + "IF UNPAR" + File.separator + "BOT\\FILE\\PLAYLIST\\" + id + "-" + title + ".txt";
        // Use relative path for Unix systems
        File f = new File(path);

        f.getParentFile().mkdirs(); 
        try {
            f.createNewFile();
            e.getChannel().sendMessage(String.format("<@%s>, Successfully create playlist!", id));
        } catch (IOException ex) {
            e.getChannel().sendMessage(String.format("<@%s>, There are errors in server!", id));
        }
    }
    
    //Delete a playlist --> A File
    private void deletePlaylist(String id, String title, MessageReceivedEvent e) {
        String path = "D:" + File.separator + "IF UNPAR" + File.separator + "BOT\\FILE\\PLAYLIST\\" + id + "-" + title + ".txt";
        
        File file = new File(path);
        if (file.delete()) {
            e.getChannel().sendMessage(String.format("<@%s>, Playlist deleted successfully!", id));
        } else {
            e.getChannel().sendMessage(String.format("<@%s>, Playlist not found or errors in server!", id));
        }
    }
    
    //Adding a song to a playlist --> update a file
    private boolean addSong(String id, String title, MessageReceivedEvent e, String songTitle, String songWeb) throws FileNotFoundException, IOException {
        File originalFile = new File("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + id + "-" + title + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        
        File tempFile = new File("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + id + "-" + title + "temp.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        
        String line = null;
        
        while ((line = br.readLine()) != null) {
            pw.println(line);
            pw.flush();
        }
        pw.println(songTitle + " " + songWeb);
        
        pw.close();
        br.close();
        
        if (!originalFile.delete()) {
            e.getChannel().sendMessage(String.format("<@%s>, Error detected! Cannot delete original file", id));
            return false;
        }
        
        if (!tempFile.renameTo(originalFile)) {
            e.getChannel().sendMessage(String.format("<@%s>, Error detected! Cannot rename file", id));
            return false;
        }
        
        return true;
    }
    
    //Rename a playlist name
    private void renamePlaylist (String id, String title, String newTitle, MessageReceivedEvent e) throws IOException {
        File originalFile = new File("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + id + "-" + title + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        
        File tempFile = new File("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + id + "-" + newTitle + ".txt");
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        
        String line = null;
        
        while ((line = br.readLine()) != null) {
            pw.println(line);
            pw.flush();
        }
        
        pw.close();
        br.close();
        
        if (!originalFile.delete()) {
            e.getChannel().sendMessage(String.format("<@%s>, Error detected! Cannot delete original file", id));
        }
    }
    
    //Getting a list of all songs in a playlist
    private void listSong(String id, String title, MessageReceivedEvent e) throws IOException {
        List<String[]> content = getFileContent("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + id + "-" + title + ".txt");
        String res = "";
        if (!content.isEmpty()) res = "These are your song list in " + title + ":\n";
        
        for (int i = 0; i < content.size(); i++) {
            res += (i+1) + ". ";
            for (int j = 0; j < content.get(i).length-1; j++) {
                res += content.get(i)[j] + " ";
            }
            if (i != content.size()) res += "\n";
        }
        
        e.getChannel().sendMessage(String.format("<@%s>, %s", id, res));
    }
    
    //Show help for this command
    private void showHelp(String id, MessageReceivedEvent e) {
        String res = "\n";
        res += "d!playlist list\t\t\t--> to show list of your playlist\n";
        res += "d!playlist create\t\t\t--> to add a playlist\n";
        res += "d!playlist add [playlist] [title] [web]\t\t\t--> to add a song in your playlist\n";
        res += "d!playlist remove [playlist]\t\t\t--> to delete a playlist\n";
        res += "d!playlist song [playlist]\t\t\t--> to show list of your song in the playlist\n";
        res += "d!playlist play [songNumber]\t\t\t--> to play a song in playlist\n";
        res += "d!playlist rename [playlist] [newTitle]";
        
        e.getChannel().sendMessage(String.format("<@%s>, %s", id, res));
    }
    
    //Play a song from a playlist
    private void playSong(List<String[]> songList, int index, MessageReceivedEvent e) {
        String web = songList.get(index-1)[songList.get(index-1).length-1];
        e.getChannel().sendMessage(String.format("%splay %s", "`", web));
    }
    
    //Play all song in a playlist
    private void playSongInPlaylist(List<String[]> songList, MessageReceivedEvent e) {
        for (int i = 0; i < songList.size(); i++) {
            String web = songList.get(i)[songList.get(i).length-1];
            e.getChannel().sendMessage(String.format("%splay %s", "`", web));
        }
    }
    
    //Delete a song in a playlist
    private void deleteSong (String id, String title, int index, MessageReceivedEvent e) throws IOException {
        File originalFile = new File("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + id + "-" + title + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        
        File tempFile = new File("D:\\IF UNPAR\\BOT\\FILE\\PLAYLIST\\" + id + "-" + title + "temp.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        
        String line = null;
        int currentIdx = 0;
        
        while ((line = br.readLine()) != null) {
            if (currentIdx == index-1)  {
                currentIdx++;
                continue;
            }
            pw.println(line);
            pw.flush();
            currentIdx++;
        }
        
        pw.close();
        br.close();
        
        if (!originalFile.delete()) {
            e.getChannel().sendMessage(String.format("<@%s>, Error detected! Cannot delete original file", id));
        }
        
        if (!tempFile.renameTo(originalFile)) {
            e.getChannel().sendMessage(String.format("<@%s>, Error detected! Cannot rename file", id));
        }
    }
}