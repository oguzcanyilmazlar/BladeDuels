package me.acablade.bladeduels.arena.phase;

import me.acablade.bladeapi.AbstractPhase;
import me.acablade.bladeduels.arena.DuelGame;

public abstract class DuelPhase extends AbstractPhase {

    private DuelGame duelGame;


    public DuelPhase(DuelGame game) {
        super(game);
        this.duelGame = game;
    }

    @Override
    public DuelGame getGame() {
        return duelGame;
    }

}
