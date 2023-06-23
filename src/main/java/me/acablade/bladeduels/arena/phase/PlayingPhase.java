package me.acablade.bladeduels.arena.phase;

import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.eventmiddleware.annotation.Listen;
import me.acablade.bladeduels.arena.features.DuelFeature;
import me.acablade.bladeduels.arena.features.NoBreakFeature;
import me.acablade.bladeduels.manager.MessageManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        addFeature(new LeaveFeature(this));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getGame().getGameData().playerStream().forEach(pl -> {
            pl.getInventory().setArmorContents(getGame().getGameData().getDuelKit().getArmorContents());
            pl.getInventory().setContents(getGame().getGameData().getDuelKit().getInventoryContents());
        });
        getGame().getGameData().allPlayers().forEach(pl -> {
            pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 0.5f, 1.0f);
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
            if(getGame().getGameData().getSpectatorList().contains(event.getPlayer().getUniqueId())){
                event.setCancelled(true);
                return;
            }
            blockSet.add(event.getBlockPlaced());
        }

        @Listen
        public void onBucketEmpty(PlayerBucketEmptyEvent event){
            if(getGame().getGameData().getSpectatorList().contains(event.getPlayer().getUniqueId())){
                event.setCancelled(true);
                return;
            }
            Block block = event.getBlockClicked().getRelative(event.getBlockFace());
            blockSet.add(block);
        }

        @Listen(checkInGame = false)
        public void onFluidFlow(BlockFromToEvent event){
            if(blockSet.contains(event.getBlock())) blockSet.add(event.getToBlock());
        }

        @Listen(priority = EventPriority.HIGHEST)
        public void onBlockBreak(BlockBreakEvent event){
            if(getGame().getGameData().getSpectatorList().contains(event.getPlayer().getUniqueId())){
                event.setCancelled(true);
                return;
            }
            if(blockSet.contains(event.getBlock())){
                event.setCancelled(false);
                blockSet.remove(event.getBlock());
            }
        }

    }

    public static class EndFeature extends DuelFeature {

        public EndFeature(DuelPhase abstractPhase) {
            super(abstractPhase);
        }


        @Listen
        public void onDeath(EntityDamageByEntityEvent event){
            if(!(event.getEntity() instanceof Player)) return;

            if(getGame().getGameData().getSpectatorList().contains(event.getDamager().getUniqueId())){
                event.setCancelled(true);
                return;
            }

            Player player = ((Player) event.getEntity()).getPlayer();

            if(player.getHealth() <= event.getFinalDamage()){
                event.setCancelled(true);
                getGame().getGameData().setWinner(Collections.singleton(event.getDamager().getUniqueId()));
                getGame().endPhase();
            }

        }

    }

    public static class LeaveFeature extends DuelFeature{

        public LeaveFeature(DuelPhase abstractPhase) {
            super(abstractPhase);
        }

        @Listen
        public void onQuit(PlayerQuitEvent event){
            if(getGame().getGameData().getSpectatorList().contains(event.getPlayer().getUniqueId())){
                getGame().announce(getGame().getPlugin().getMessageManager().getMessage(MessageManager.STOPPED_SPECTATING, new MessageManager.Replaceable("%player%", event.getPlayer().getName())));
                return;
            }
            getGame().getGameData().setWinner(Collections.singleton(getGame().getGameData().playerStream()
                    .filter(pl -> !pl.equals(event.getPlayer()))
                    .findAny()
                    .get().getUniqueId()));
            getGame().endPhase();

        }

    }

}
