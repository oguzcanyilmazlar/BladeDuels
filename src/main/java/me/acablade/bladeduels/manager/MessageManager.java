package me.acablade.bladeduels.manager;

import lombok.Data;
import me.acablade.bladeduels.BladeDuels;
import me.acablade.bladeduels.utils.ConfigurationFile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class MessageManager {

    public static final Message DUEL_IN_QUEUE = new Message("DUEL_IN_QUEUE");
    public static final Message SENDER_NOT_PLAYER = new Message("SENDER_NOT_PLAYER");
    public static final Message SENDER_NOT_CONSOLE = new Message("SENDER_NOT_CONSOLE");
    public static final Message INVALID_PLAYER = new Message("INVALID_PLAYER");
    public static final Message INVALID_WORLD = new Message("INVALID_WORLD");
    public static final Message INVALID_KIT = new Message("INVALID_KIT");
    public static final Message NO_PERMISSION = new Message("NO_PERMISSION");
    public static final Message ELO = new Message("ELO");
    public static final Message KIT_CREATED = new Message("KIT_CREATED");
    public static final Message SPECIFY_PLAYER = new Message("SPECIFY_PLAYER");
    public static final Message MISSING_ARGUMENT = new Message("MISSING_ARGUMENT");
    public static final Message CANT_DUEL_SELF = new Message("CANT_DUEL_SELF");
    public static final Message INVITE_SENT = new Message("INVITE_SENT");
    public static final Message INVITE = new Message("INVITE");
    public static final Message NO_INVITE_FOUND = new Message("NO_INVITE_FOUND");
    public static final Message CREATION_STICK = new Message("CREATION_STICK");




    private final ConfigurationFile messageConfig;

    public MessageManager(Plugin plugin){
        this.messageConfig = new ConfigurationFile(plugin, "messages");
    }

    public void sendMessage(Message message, CommandSender player, Replaceable... replaceables){
        YamlConfiguration config = messageConfig.getConfiguration();
        String key = message.getKey();

        if(!config.contains(key)){
            player.sendMessage("§cMessage with key \""+message.getKey()+"\" not found, contact admins.");
            return;
        }

        if(config.isList(key)){
            List<String> msgs = config.getStringList(key);
            List<String> msgsEdited = msgs.stream().map(s -> {
                for (Replaceable replaceable: replaceables){
                    s = s.replaceAll(replaceable.getToReplace(), replaceable.getReplace());
                }
                return ChatColor.translateAlternateColorCodes('&', s);
            }).collect(Collectors.toList());
            msgsEdited.forEach(player::sendMessage);
        }else{
            String msg = config.getString(key);
            for (Replaceable replaceable: replaceables){
                msg = msg.replaceAll(replaceable.getToReplace(), replaceable.getReplace());
            }
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            player.sendMessage(msg);
        }

    }

    @Data
    public static class Message {
        private final String key;
    }


    @Data
    public static class Replaceable {

        private final String toReplace;
        private final String replace;

    }

}
