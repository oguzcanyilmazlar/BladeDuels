package me.acablade.bladeduels.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RollbackManager {

    private final Map<UUID, ItemStack[]> itemContents = new HashMap<>();
    private final Map<UUID, ItemStack[]> armorContents = new HashMap<>();
    private final Map<UUID, Location> locations = new HashMap<>();
    private final Map<UUID, Double> healths = new HashMap<>();
    private final Map<UUID, Integer> food = new HashMap<>();


    public void save(Player player) {
        UUID uuid = player.getUniqueId();

        itemContents.put(uuid, player.getInventory().getContents());
        armorContents.put(uuid, player.getInventory().getArmorContents());
        locations.put(uuid, player.getLocation());
        healths.put(uuid, player.getHealth());
        food.put(uuid, player.getFoodLevel());
    }

    public void rollback(Player player){
        if(player == null) return;
        UUID uuid = player.getUniqueId();

        if(!itemContents.containsKey(uuid) ||
                !armorContents.containsKey(uuid) ||
                !locations.containsKey(uuid) ||
                !healths.containsKey(uuid) ||
                !food.containsKey(uuid)) return;

        player.getInventory().setContents(itemContents.get(uuid));
        player.getInventory().setArmorContents(armorContents.get(uuid));
        player.teleport(locations.get(uuid));
        player.setHealth(healths.get(uuid));
        player.setFoodLevel(food.get(uuid));

        itemContents.remove(uuid);
        armorContents.remove(uuid);
        locations.remove(uuid);
        healths.remove(uuid);
        food.remove(uuid);

    }

}
