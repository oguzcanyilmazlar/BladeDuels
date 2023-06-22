package me.acablade.bladeduels.elo;

import lombok.RequiredArgsConstructor;
import me.acablade.bladeduels.BladeDuels;
import me.acablade.bladeduels.utils.ConfigurationFile;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EloSystem {

    private final ConfigurationFile configurationFile;

    public EloSystem(BladeDuels plugin){
        this.configurationFile = new ConfigurationFile(plugin, "elo");
    }

    public int getElo(Player player){
        return getElo(player.getUniqueId());
    }

    public int getElo(UUID uuid){
        return configurationFile.getConfiguration().getInt("elo."+uuid.toString().replace("-", ""), 800);
    }

    public void setElo(UUID uuid, int elo){
        configurationFile.getConfiguration().set("elo."+uuid.toString().replace("-", ""), elo);
        configurationFile.save();
    }

    public void setElo(Player player, int elo){
        setElo(player.getUniqueId(), elo);
    }

    private int getKFactor(UUID uuid){
        if(getElo(uuid) < 2100) return 32;
        if(getElo(uuid) > 2400) return 16;
        return 24;
    }

    /**
     * @return 0th element is firsts expected, 1st is the seconds
     */
    public float[] getExpectedScorings(UUID first, UUID second){
        int firstElo = getElo(first);
        int secondElo = getElo(second);

        float firstExpected =  (float) (1f / (1 + Math.pow(10, (secondElo - firstElo)/400f)));
        float secondExpected = (float) (1f / (1 + Math.pow(10, (firstElo - secondElo)/400f)));

        return new float[]{firstExpected, secondExpected};
    }


    public int getNewElo(UUID uuid, float expected, float actual){
        return (int) (getElo(uuid) + getKFactor(uuid) * (actual - expected));
    }


}
