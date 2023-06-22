package me.acablade.bladeduels.arena.features;

import me.acablade.bladeduels.arena.eventmiddleware.annotation.Listen;
import me.acablade.bladeduels.arena.phase.DuelPhase;
import org.bukkit.event.block.BlockBreakEvent;

public class NoBreakFeature extends DuelFeature{
    public NoBreakFeature(DuelPhase abstractPhase) {
        super(abstractPhase);
    }

    @Listen
    public void onBreak(BlockBreakEvent event){
        event.setCancelled(true);
    }
}
