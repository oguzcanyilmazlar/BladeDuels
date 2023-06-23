package me.acablade.bladeduels.arena;

import lombok.Getter;
import me.acablade.bladeapi.AbstractGame;
import me.acablade.bladeduels.BladeDuels;
import me.acablade.bladeduels.arena.eventmiddleware.EventMiddleware;
import me.acablade.bladeduels.arena.phase.EndPhase;
import me.acablade.bladeduels.arena.phase.PlayingPhase;
import me.acablade.bladeduels.arena.phase.WaitingPhase;
import me.acablade.bladeduels.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DuelGame extends AbstractGame {

    private final BladeDuels plugin;
    @Getter
    private final EventMiddleware eventMiddleware;

    public DuelGame(String name, JavaPlugin plugin) {
        super(name, plugin);
        setGameData(new DuelGameData());
        this.plugin = (BladeDuels) plugin;
        this.eventMiddleware = new EventMiddleware(this);

        addPhase(WaitingPhase.class);
        addPhase(PlayingPhase.class);
        addPhase(EndPhase.class);
    }


    public void addPlayer(Player player){
        getGameData().getPlayerList().add(player.getUniqueId());
    }

    @Override
    public BladeDuels getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        UUID[] uuids = getGameData().getPlayerList().toArray(new UUID[0]);
        Location[] spawns = getGameData().getDuelMap().getSpawns();
        getPlugin().getInvitationManager().remove(uuids[0]);
        getPlugin().getInvitationManager().remove(uuids[1]);
        getGameData().allPlayers().forEach(plugin.getRollbackManager()::save);

        teleport(uuids[0], spawns[0]);
        teleport(uuids[1], spawns[1]);

    }

    @Override
    public void onDisable() {
        getGameData().allPlayers().forEach(getPlugin().getRollbackManager()::rollback);
        getGameData().spectatorStream().forEach(p -> stopSpectating(p, false, false));
        plugin.getArenaManager().removeArena(this);
        plugin.getArenaManager().getAvailableMaps().push(getGameData().getDuelMap());
        plugin.getArenaManager().popStack();

        getGameData().playerStream().forEach(player -> getGameData().spectatorStream().forEach(player::showPlayer));


    }

    protected void teleport(UUID uuid, Location location){
        Bukkit.getPlayer(uuid).teleport(location);
    }

    public void addSpectator(Player player){
        plugin.getRollbackManager().save(player);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
        player.updateInventory();
        getGameData().playerStream().forEach(pl -> pl.hidePlayer(player));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(getGameData().getDuelMap().getSpawns()[0]);
        getGameData().getSpectatorList().add(player.getUniqueId());
        announce(plugin.getMessageManager().getMessage(MessageManager.STARTED_SPECTATING, new MessageManager.Replaceable("%player%", player.getName())));
    }

    public void leave(Player player){
        getGameData().setWinner(Collections.singleton(getGameData().playerStream()
                .filter(pl -> !pl.equals(player))
                .findAny()
                .get().getUniqueId()));
        endPhase();
        getGameData().getPlayerList().remove(player.getUniqueId());
        plugin.getRollbackManager().rollback(player);
        getGameData().spectatorStream().forEach(player::showPlayer);

    }

    public void stopSpectating(Player player, boolean announce, boolean removeList){
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        getGameData().playerStream().forEach(pl -> pl.showPlayer(player));
        plugin.getRollbackManager().rollback(player);
        if(announce) announce(plugin.getMessageManager().getMessage(MessageManager.STOPPED_SPECTATING, new MessageManager.Replaceable("%player%", player.getName())));
        if(removeList) getGameData().getSpectatorList().remove(player.getUniqueId());
    }

    public void announce(String msg){
        getGameData().allPlayers().forEach(player -> player.sendMessage(msg));
    }

    public void announce(List<String> msg){
        msg.forEach(this::announce);
    }

    public DuelGameData getGameData(){
        return (DuelGameData) super.getGameData();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuelGame duelGame = (DuelGame) o;
        return Objects.equals(getName(), duelGame.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
