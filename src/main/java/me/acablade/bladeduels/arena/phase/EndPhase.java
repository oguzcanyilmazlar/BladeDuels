package me.acablade.bladeduels.arena.phase;

import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.features.NoBreakFeature;
import me.acablade.bladeduels.arena.features.NoPvPFeature;
import me.acablade.bladeduels.elo.EloSystem;

import java.time.Duration;
import java.util.UUID;

public class EndPhase extends DuelPhase {
    public EndPhase(DuelGame game) {
        super(game);
        addFeature(new NoPvPFeature(this));
        addFeature(new NoBreakFeature(this));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        UUID first;
        float actualFirstScore = 1.0f;
        if(getGame().getGameData().getWinner().isEmpty()){
            first = getGame().getGameData().getPlayerList().toArray(new UUID[0])[0];
            actualFirstScore = 0.5f;
        }else{
            first = getGame().getGameData().getWinner().toArray(new UUID[0])[0];
        }
        UUID second = getGame().getGameData().getPlayerList().stream().filter(uuid -> !uuid.equals(first)).findFirst().get();

        EloSystem eloSystem = getGame().getPlugin().getEloSystem();

        float[] scorings = eloSystem.getExpectedScorings(first, second);

        int firstNewElo = eloSystem.getNewElo(first, scorings[0], actualFirstScore);
        int secondNewElo = eloSystem.getNewElo(second, scorings[1], 1f - actualFirstScore);


        eloSystem.setElo(first, firstNewElo);
        eloSystem.setElo(second, secondNewElo);


    }

    @Override
    public Duration duration() {
        return Duration.ofSeconds(10);
    }
}
