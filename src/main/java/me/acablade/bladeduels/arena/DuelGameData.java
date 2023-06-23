package me.acablade.bladeduels.arena;

import lombok.Getter;
import lombok.Setter;
import me.acablade.bladeapi.objects.GameData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
@Setter
public class DuelGameData extends GameData {

    private DuelMap duelMap;
    private DuelKit duelKit;

    private boolean ranked = false;

    private Set<UUID> winner = new HashSet<>();

    @Override
    public Set<UUID> getWinner() {
        return winner;
    }

    public Stream<Player> playerStream(){
        return this.getPlayerList().stream().map(Bukkit::getPlayer);
    }

    public Stream<Player> spectatorStream(){
        return this.getSpectatorList().stream().map(Bukkit::getPlayer);
    }

    public Stream<Player> allPlayers(){
        return Stream.concat(playerStream(), this.spectatorStream());
    }

}
