package me.acablade.bladeduels.manager;

import com.google.common.collect.HashMultiset;
import lombok.Getter;
import me.acablade.bladeduels.BladeDuels;
import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.DuelKit;
import me.acablade.bladeduels.arena.DuelMap;
import me.acablade.bladeduels.utils.ConfigurationFile;
import me.acablade.bladeduels.utils.RandomString;
import me.acablade.bladeduels.wizard.MapCreationWizard;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Getter
public class ArenaManager {

    private final BladeDuels plugin;

    private final Set<DuelGame> games;
    private final Stack<DuelMap> availableMaps;
    private final Stack<DuelGame> duelGameStack;

    private final ConfigurationFile mapFile;
    private final Logger logger;


    public ArenaManager(BladeDuels plugin){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.games = new HashSet<>();
        this.availableMaps = new Stack<>();
        this.duelGameStack = new Stack<>();
        this.mapFile = new ConfigurationFile(plugin, "map");
    }


    public void popStack(){
        if(duelGameStack.isEmpty()) return;
        if(availableMaps.isEmpty()) return;
        DuelGame game = duelGameStack.pop();
        DuelMap map = availableMaps.pop();
        game.getGameData().setDuelMap(map);
        game.enable(0,1);
    }

    public boolean createArena(DuelKit duelKit, UUID first, UUID second){
        DuelGame game = new DuelGame("duel_" + (System.currentTimeMillis() + "").substring(0,2), plugin);
        game.getGameData().setDuelKit(duelKit);
        game.getGameData().getPlayerList().addAll(Arrays.asList(first, second));
        if(availableMaps.isEmpty()){
            duelGameStack.push(game);
            return false;
        }else{
            games.add(game);
            game.getGameData().setDuelMap(availableMaps.pop());
            game.enable(0,1);
            return true;
        }
    }

    public void loadMaps(){
        for (String key: mapFile.getConfiguration().getKeys(false)){
            ConfigurationSection section = mapFile.getConfiguration().getConfigurationSection(key);

            String name = section.getName();
            Location first = (Location) section.get("first_spawn");
            Location second = (Location) section.get("second_spawn");
            DuelMap map = new DuelMap();
            map.setSpawns(new Location[]{first, second});
            availableMaps.push(map);
            logger.info(String.format("%s map loaded", name));
        }
    }

    public void createMap(MapCreationWizard.WizardSelection selection){
        if(selection.getFirst() == null || selection.getSecond() == null) return;
        ConfigurationSection section = mapFile.getConfiguration().createSection(new RandomString(ThreadLocalRandom.current().nextInt(5,8)).getString());
        section.set("first_spawn", selection.getFirst());
        section.set("second_spawn", selection.getSecond());
        DuelMap map = new DuelMap();
        map.setSpawns(new Location[]{selection.getFirst(), selection.getSecond()});
        availableMaps.push(map);
        mapFile.save();
    }


}
