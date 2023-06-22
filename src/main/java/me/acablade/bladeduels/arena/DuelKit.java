package me.acablade.bladeduels.arena;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class DuelKit {

    private final String name;
    private final ItemStack[] inventoryContents;
    private final ItemStack[] armorContents;
}
