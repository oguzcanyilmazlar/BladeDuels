package me.acablade.bladeduels.arena.phase;

import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.features.NoBreakFeature;
import me.acablade.bladeduels.arena.features.NoPvPFeature;
import me.acablade.bladeduels.elo.EloSystem;
import me.acablade.bladeduels.manager.MessageManager;
import org.bukkit.Bukkit;

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
        MessageManager.Message message = MessageManager.DUEL_END_MESSAGE;
        if(getGame().getGameData().getWinner().isEmpty()){
            first = getGame().getGameData().getPlayerList().toArray(new UUID[0])[0];
            actualFirstScore = 0.5f;
            message = MessageManager.DUEL_END_DRAW_MESSAGE;
        }else{
            first = getGame().getGameData().getWinner().toArray(new UUID[0])[0];
        }
        UUID second = getGame().getGameData().getPlayerList().stream().filter(uuid -> !uuid.equals(first)).findFirst().get();

        EloSystem eloSystem = getGame().getPlugin().getEloSystem();

        float[] scorings = eloSystem.getExpectedScorings(first, second);

        int firstNewElo = eloSystem.getNewElo(first, scorings[0], actualFirstScore);
        int secondNewElo = eloSystem.getNewElo(second, scorings[1], 1f - actualFirstScore);

        int firstEloDiff = firstNewElo - eloSystem.getElo(first);
        int secondEloDiff = secondNewElo - eloSystem.getElo(second);


        getGame().announce(getGame().getPlugin().getMessageManager().getMessage(message,
                new MessageManager.Replaceable("%winner%", Bukkit.getPlayer(first).getName()),
                new MessageManager.Replaceable("%loser%", Bukkit.getPlayer(second).getName()),
                new MessageManager.Replaceable("%winnerEloDiff%", String.valueOf(Math.abs(firstEloDiff))),
                new MessageManager.Replaceable("%loserEloDiff%", String.valueOf(Math.abs(secondEloDiff))),
                new MessageManager.Replaceable("%firstEloDiff%", eloDiff(firstEloDiff, true)),
                new MessageManager.Replaceable("%secondEloDiff%", eloDiff(secondEloDiff, true)),
                new MessageManager.Replaceable("%firstEloDiffColorless%", eloDiff(firstEloDiff, false)),
                new MessageManager.Replaceable("%secondEloDiffColorless%", eloDiff(secondEloDiff, false)),
                new MessageManager.Replaceable("%first%", Bukkit.getPlayer(first).getName()),
                new MessageManager.Replaceable("%second%", Bukkit.getPlayer(second).getName())));

        eloSystem.setElo(first, firstNewElo);
        eloSystem.setElo(second, secondNewElo);
    }

    private String eloDiff(int eloDiff, boolean color){
        return (eloDiff > 0 ? (color ? "§a":"") + "+" : (color ? "§c" : "")) + eloDiff;
    }

    @Override
    public Duration duration() {
        return Duration.ofSeconds(10);
    }
}
