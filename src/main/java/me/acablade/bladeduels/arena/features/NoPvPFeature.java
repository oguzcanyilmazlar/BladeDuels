package me.acablade.bladeduels.arena.features;

import me.acablade.bladeapi.AbstractPhase;
import me.acablade.bladeapi.features.AbstractFeature;
import me.acablade.bladeduels.arena.eventmiddleware.annotation.Listen;
import me.acablade.bladeduels.arena.phase.DuelPhase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoPvPFeature extends DuelFeature {
    public NoPvPFeature(DuelPhase abstractPhase) {
        super(abstractPhase);
    }

    @Listen
    public void onHit(EntityDamageEvent event){
        event.setCancelled(true);
    }

}
