package me.acablade.bladeduels.exception;

import me.acablade.bladeduels.manager.MessageManager;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.*;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.DefaultExceptionHandler;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

public class DuelExceptionAdapter extends DefaultExceptionHandler {


    private final MessageManager messageManager;

    public DuelExceptionAdapter(MessageManager messageManager){
        this.messageManager = messageManager;
    }


    public void senderNotPlayer(CommandActor actor, SenderNotPlayerException exception) {
        messageManager.sendMessage(MessageManager.SENDER_NOT_PLAYER, actor.as(BukkitCommandActor.class).getSender());
    }

    public void missingArgument(CommandActor actor, MissingArgumentException exception) {
        messageManager.sendMessage(MessageManager.MISSING_ARGUMENT, actor.as(BukkitCommandActor.class).getSender(), new MessageManager.Replaceable("%argument%", exception.getParameter().getName()));
    }

    public void senderNotConsole(CommandActor actor, SenderNotConsoleException exception) {
        messageManager.sendMessage(MessageManager.SENDER_NOT_CONSOLE, actor.as(BukkitCommandActor.class).getSender());
    }

    public void invalidPlayer(CommandActor actor, InvalidPlayerException exception) {
        messageManager.sendMessage(MessageManager.INVALID_PLAYER, actor.as(BukkitCommandActor.class).getSender(), new MessageManager.Replaceable("%player%", exception.getInput()));
    }

    public void invalidWorld(CommandActor actor, InvalidWorldException exception) {
        messageManager.sendMessage(MessageManager.INVALID_WORLD, actor.as(BukkitCommandActor.class).getSender(), new MessageManager.Replaceable("%world%", exception.getInput()));
    }

    public void invalidWorld(CommandActor actor, InvalidKitException exception) {
        messageManager.sendMessage(MessageManager.INVALID_KIT, actor.as(BukkitCommandActor.class).getSender(), new MessageManager.Replaceable("%kit%", exception.getInput()));
    }

    public void noPermission(CommandActor actor, NoPermissionException exception) {
        messageManager.sendMessage(MessageManager.NO_PERMISSION, actor.as(BukkitCommandActor.class).getSender());
    }

    public void malformedEntitySelector(CommandActor actor, MalformedEntitySelectorException exception) {
        actor.errorLocalized("invalid-selector", exception.getInput());
    }

    public void moreThanOnePlayer(CommandActor actor, MoreThanOnePlayerException exception) {
        actor.errorLocalized("only-one-player", exception.getInput());
    }

    public void nonPlayerEntities(CommandActor actor, NonPlayerEntitiesException exception) {
        actor.errorLocalized("non-players-not-allowed", exception.getInput());
    }



}
