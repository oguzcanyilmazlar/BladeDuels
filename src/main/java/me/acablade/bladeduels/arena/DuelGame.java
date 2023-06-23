package me.acablade.bladeduels.arena;

import lombok.Getter;
import me.acablade.bladeapi.AbstractGame;
import me.acablade.bladeduels.BladeDuels;
import me.acablade.bladeduels.arena.eventmiddleware.EventMiddleware;
import me.acablade.bladeduels.arena.phase.EndPhase;
import me.acablade.bladeduels.arena.phase.PlayingPhase;
import me.acablade.bladeduels.arena.phase.WaitingPhase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
        plugin.getArenaManager().getAvailableMaps().push(getGameData().getDuelMap());
        plugin.getArenaManager().popStack();

    }

    protected void teleport(UUID uuid, Location location){
        Bukkit.getPlayer(uuid).teleport(location);
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
