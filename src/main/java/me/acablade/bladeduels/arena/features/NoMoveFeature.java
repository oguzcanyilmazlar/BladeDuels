package me.acablade.bladeduels.arena.features;

import me.acablade.bladeduels.arena.eventmiddleware.annotation.Listen;
import me.acablade.bladeduels.arena.phase.DuelPhase;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoMoveFeature extends DuelFeature {
    public NoMoveFeature(DuelPhase abstractPhase) {
        super(abstractPhase);
    }

    @Listen
    public void onMove(PlayerMoveEvent event){
        if(getGame().getGameData().getSpectatorList().contains(event.getPlayer().getUniqueId())){
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()){
            event.getPlayer().teleport(event.getFrom());
        }
    }

}
