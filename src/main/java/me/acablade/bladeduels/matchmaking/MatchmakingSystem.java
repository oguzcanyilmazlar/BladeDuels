package me.acablade.bladeduels.matchmaking;

import lombok.Data;
import lombok.Getter;
import me.acablade.bladeduels.arena.DuelKit;
import me.acablade.bladeduels.elo.EloSystem;
import me.acablade.bladeduels.manager.ArenaManager;
import me.acablade.bladeduels.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public class MatchmakingSystem implements Runnable {

    private final ArenaManager arenaManager;
    private final EloSystem eloSystem;
    private final MessageManager messageManager;

    private final int maxRange = 120;

    @Getter
    private final int period;
    private final Set<MatchmakingPlayer> queue;

    public MatchmakingSystem(ArenaManager arenaManager, EloSystem eloSystem, MessageManager messageManager, int period){
        this.arenaManager = arenaManager;
        this.eloSystem = eloSystem;
        this.messageManager = messageManager;
        this.queue = new HashSet<>();
        this.period = period;
    }

    public void addPlayer(UUID uuid, DuelKit kit){
        queue.add(new MatchmakingPlayer(uuid, eloSystem.getElo(uuid), kit));
    }

    @Override
    public void run() {
        Set<MatchmakingPlayer> toRemove = new HashSet<>();
        for(MatchmakingPlayer player : queue){
            Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
            if(!bukkitPlayer.isOnline()) toRemove.add(player);
            if(player.incrementTick() >= (20 / period) * 3){
                player.setTick(0);
                if(player.getRange() < maxRange)
                    player.setRange(player.getRange() + 20);
                messageManager.sendMessage(MessageManager.QUEUE, bukkitPlayer,
                        new MessageManager.Replaceable("%lowbound%", String.valueOf(player.getElo() - player.getRange())),
                        new MessageManager.Replaceable("%highbound%", String.valueOf(player.getElo() + player.getRange())));
            }
            Optional<MatchmakingPlayer> found = queue.stream()
                    .filter(matchmakingPlayer -> !matchmakingPlayer.equals(player))
                    .filter(matchmakingPlayer -> player.getKit().equals(matchmakingPlayer.getKit()))
                    .filter(matchmakingPlayer ->{
                        int diff = Math.abs(player.getElo() - matchmakingPlayer.getElo());
                        return diff <= player.getRange() && diff <= matchmakingPlayer.getRange();
                    })
                    .findAny();
            found.ifPresent(other -> {
                arenaManager.createArena(player.getKit(), player.getUuid(), other.getUuid());
                toRemove.add(other);
                toRemove.add(player);
            });
        }
        queue.removeAll(toRemove);
    }


    @Data
    public static class MatchmakingPlayer{

        private final UUID uuid;
        private final int elo;
        private final DuelKit kit;
        private int range = 20;
        private int tick;

        public int incrementTick(){
            return tick++;
        }

    }


}
