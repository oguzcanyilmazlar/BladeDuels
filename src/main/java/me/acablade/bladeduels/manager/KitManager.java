package me.acablade.bladeduels.manager;

import lombok.SneakyThrows;
import me.acablade.bladeduels.BladeDuels;
import me.acablade.bladeduels.arena.DuelKit;
import me.acablade.bladeduels.utils.ConfigurationFile;
import me.acablade.bladeduels.utils.InventorySerialization;
import me.acablade.bladeduels.utils.Slugify;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class KitManager {

    private final Map<String, DuelKit> kits = new HashMap<>();
    private final ConfigurationFile kitConfig;

    private final Logger logger;

    public KitManager(BladeDuels plugin){
        this.logger = plugin.getLogger();
        this.kitConfig = new ConfigurationFile(plugin, "kit");
    }


    @SneakyThrows
    public void loadKits(){

        YamlConfiguration config = kitConfig.getConfiguration();

        for(String kitName: config.getKeys(false)){

            ConfigurationSection section = config.getConfigurationSection(kitName);
            String name = section.getString("name");
            String inventoryBase64 = section.getString("inventoryBase64");
            String armorBase64 = section.getString("armorBase64");
            ItemStack[] inventory = InventorySerialization.itemStackArrayFromBase64(inventoryBase64);
            ItemStack[] armor = InventorySerialization.itemStackArrayFromBase64(armorBase64);

            registerKit(name, new DuelKit(name, inventory, armor));

        }

    }

    public Map<String, DuelKit> getKits() {
        return kits;
    }

    public DuelKit getKit(String name){
        return kits.get(name);
    }

    public void registerKit(String name, DuelKit kit){
        ConfigurationSection section = kitConfig.getConfiguration().createSection(Slugify.slugify(name));
        section.set("name", name);
        section.set("inventoryBase64", InventorySerialization.toBase64(kit.getInventoryContents()));
        section.set("armorBase64", InventorySerialization.toBase64(kit.getArmorContents()));
        kitConfig.save();
        logger.info(String.format("Kit named %s has been loaded", name));
        this.kits.put(name, kit);
    }

}
