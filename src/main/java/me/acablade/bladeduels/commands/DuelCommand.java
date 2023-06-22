package me.acablade.bladeduels.commands;

import me.acablade.bladeduels.arena.DuelKit;
import me.acablade.bladeduels.elo.EloSystem;
import me.acablade.bladeduels.manager.ArenaManager;
import me.acablade.bladeduels.manager.InvitationManager;
import me.acablade.bladeduels.manager.KitManager;
import me.acablade.bladeduels.manager.MessageManager;
import me.acablade.bladeduels.wizard.MapCreationWizard;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public class DuelCommand {

    @Dependency
    private ArenaManager arenaManager;
    @Dependency
    private MessageManager messageManager;
    @Dependency
    private KitManager kitManager;
    @Dependency
    private EloSystem eloSystem;
    @Dependency
    private InvitationManager invitationManager;
    @Dependency
    private MapCreationWizard wizard;


    @Command("duel invite")
    public void duel(Player sender, Player sent, DuelKit kit){

        if(sender.equals(sent)){
            messageManager.sendMessage(MessageManager.CANT_DUEL_SELF, sender);
            return;
        }

        if(invitationManager.sendInvite(sender.getUniqueId(), sent.getUniqueId(), kit)){
            messageManager.sendMessage(MessageManager.INVITE_SENT, sender, new MessageManager.Replaceable("%player%", sent.getName()));

            messageManager.sendMessage(MessageManager.INVITE, sent,
                    new MessageManager.Replaceable("%player%", sender.getName()),
                    new MessageManager.Replaceable("%kit%", kit.getName()),
                    new MessageManager.Replaceable("%elo%", String.valueOf(eloSystem.getElo(sender))));
        }


    }

    @Command("duel accept")
    public void duelAccept(Player sender){
        Pair<UUID, DuelKit> uuidDuelKitPair;
        if((uuidDuelKitPair = invitationManager.getInvite(sender.getUniqueId())) == null){
            messageManager.sendMessage(MessageManager.NO_INVITE_FOUND, sender);
            return;
        }

        if(!arenaManager.createArena(uuidDuelKitPair.getRight(), uuidDuelKitPair.getLeft(), sender.getUniqueId())){
            messageManager.sendMessage(MessageManager.DUEL_IN_QUEUE, sender);
            messageManager.sendMessage(MessageManager.DUEL_IN_QUEUE, Bukkit.getPlayer(uuidDuelKitPair.getLeft()));
        }

    }


    @Command("duel createmap")
    public void createMap(Player sender, CreationEnum parameter){

        if(parameter == CreationEnum.STICK){
            sender.getInventory().addItem(MapCreationWizard.CREATION_STICK);
            messageManager.sendMessage(MessageManager.CREATION_STICK, sender);
        }else{
            arenaManager.createMap(wizard.getSelection(sender.getUniqueId()));
            wizard.removeSelection(sender.getUniqueId());
        }

    }

    @Command("duel createkit")
    @CommandPermission("duel.admin.createkit")
    public void createKit(Player sender, String name){

        kitManager.registerKit(name, new DuelKit(name, sender.getInventory().getContents(), sender.getInventory().getArmorContents()));
        messageManager.sendMessage(MessageManager.KIT_CREATED, sender, new MessageManager.Replaceable("%kit%", name));
    }

    @Command("duel elo")
    public void elo(BukkitCommandActor sender, @Optional Player player){

        if(sender.isConsole() && player == null){
            messageManager.sendMessage(MessageManager.SPECIFY_PLAYER, sender.getSender());
            return;
        }

        if(sender.isPlayer() && player == null){
            player = sender.getAsPlayer();
        }

        messageManager.sendMessage(MessageManager.ELO, sender.getSender(),
                new MessageManager.Replaceable("%player%", player.getName()),
                new MessageManager.Replaceable("%elo%", String.valueOf(eloSystem.getElo(player))));

    }

    public enum CreationEnum{
        STICK,
        CREATE
    }




}
