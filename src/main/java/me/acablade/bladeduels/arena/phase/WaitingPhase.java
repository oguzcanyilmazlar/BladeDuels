package me.acablade.bladeduels.arena.phase;

import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.eventmiddleware.annotation.Listen;
import me.acablade.bladeduels.arena.features.DuelFeature;
import me.acablade.bladeduels.arena.features.NoBreakFeature;
import me.acablade.bladeduels.arena.features.NoMoveFeature;
import me.acablade.bladeduels.arena.features.NoPvPFeature;
import me.acablade.bladeduels.utils.TitleHelper;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Collections;

public class WaitingPhase extends DuelPhase {
    public WaitingPhase(DuelGame game) {
        super(game);
        addFeature(new NoPvPFeature(this));
        addFeature(new NoMoveFeature(this));
        addFeature(new NoBreakFeature(this));
    }

    int tick;

    @Override
    public void onTick() {

        if(tick % 20 == 0){
            if(timeLeft().toMillis() / 1000 <= 2){
                getGame().getGameData().allPlayers().forEach(player -> {
                    TitleHelper.INSTANCE.sendTitle(player, "", "§6" + (timeLeft().getSeconds() + 1), 5, 10, 5);
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.E));
                });
            }
        }

        tick++;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getGame().getGameData().allPlayers().forEach(player -> {
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            player.updateInventory();
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();

    }

    @Override
    public Duration duration() {
        return Duration.ofSeconds(15);
    }

    public static class LeaveFeature extends DuelFeature {

        public LeaveFeature(DuelPhase abstractPhase) {
            super(abstractPhase);
        }

        @Listen
        public void onQuit(PlayerQuitEvent event){
            getGame().disable();

        }

    }

}
