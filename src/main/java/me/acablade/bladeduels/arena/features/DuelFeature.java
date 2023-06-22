package me.acablade.bladeduels.arena.features;

import me.acablade.bladeapi.AbstractPhase;
import me.acablade.bladeapi.features.AbstractFeature;
import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.DuelGameData;
import me.acablade.bladeduels.arena.eventmiddleware.EventListener;
import me.acablade.bladeduels.arena.phase.DuelPhase;

public abstract class DuelFeature extends AbstractFeature implements EventListener {

    private final DuelPhase phase;

    public DuelFeature(DuelPhase abstractPhase) {
        super(abstractPhase);
        this.phase = abstractPhase;
    }

    @Override
    public DuelPhase getAbstractPhase() {
        return phase;
    }

    protected DuelGame getGame(){
        return getAbstractPhase().getGame();
    }

    @Override
    public void onEnable() {
        getGame().getEventMiddleware().addListener(this);
    }

    @Override
    public void onDisable(){
        getGame().getEventMiddleware().unload(this);
    }

}
