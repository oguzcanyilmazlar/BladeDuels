package me.acablade.bladeduels.arena.phase;

import me.acablade.bladeapi.AbstractGame;
import me.acablade.bladeapi.AbstractPhase;
import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.features.NoBreakFeature;
import me.acablade.bladeduels.arena.features.NoMoveFeature;
import me.acablade.bladeduels.arena.features.NoPvPFeature;

import java.time.Duration;

public class WaitingPhase extends DuelPhase {
    public WaitingPhase(DuelGame game) {
        super(game);
        addFeature(new NoPvPFeature(this));
        addFeature(new NoMoveFeature(this));
        addFeature(new NoBreakFeature(this));
    }

    @Override
    public Duration duration() {
        return Duration.ofSeconds(15);
    }
}
