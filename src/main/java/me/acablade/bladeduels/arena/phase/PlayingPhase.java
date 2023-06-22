package me.acablade.bladeduels.arena.phase;

import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.eventmiddleware.annotation.Listen;
import me.acablade.bladeduels.arena.features.DuelFeature;
import me.acablade.bladeduels.arena.features.NoBreakFeature;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayingPhase extends DuelPhase {
    public PlayingPhase(DuelGame game) {
        super(game);
        addFeature(new NoBreakFeature(this));
        addFeature(new TrackBlockFeature(this));
        addFeature(new EndFeature(this));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getGame().getGameData().allPlayers().forEach(pl -> {
            pl.getInventory().setArmorContents(getGame().getGameData().getDuelKit().getArmorContents());
            pl.getInventory().setContents(getGame().getGameData().getDuelKit().getInventoryContents());
        });
    }

    @Override
    public Duration duration() {
        return Duration.ofMinutes(5);
    }

    public static class TrackBlockFeature extends DuelFeature {

        private final Set<Block> blockSet;

        public TrackBlockFeature(DuelPhase abstractPhase) {
            super(abstractPhase);
            blockSet = new HashSet<>();
        }

        @Override
        public void onDisable(){
            super.onDisable();
            blockSet.forEach(block -> block.setType(Material.AIR));
        }

        @Listen
        public void onBlockPlace(BlockPlaceEvent event){
            blockSet.add(event.getBlockPlaced());
        }

        @Listen
        public void onFluidFlow(BlockFromToEvent event){
            blockSet.add(event.getToBlock());
        }

        @Listen
        public void onBlockBreak(BlockBreakEvent event){
            blockSet.remove(event.getBlock());
        }

    }

    public static class EndFeature extends DuelFeature {

        public EndFeature(DuelPhase abstractPhase) {
            super(abstractPhase);
        }

        @Listen
        public void onDeath(PlayerDeathEvent event){

            Player killer = event.getEntity().getKiller();
            getGame().getGameData().setWinner(Collections.singleton(killer.getUniqueId()));
            getGame().endPhase();


        }

    }

}
