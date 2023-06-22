package me.acablade.bladeduels.wizard;

import lombok.Data;
import me.acablade.bladeduels.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapCreationWizard implements Listener {

    public static ItemStack CREATION_STICK = new ItemBuilder(Material.STICK).setLore("Creation Stick").build();

    private Map<UUID, WizardSelection> wizardMap = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        ItemStack itemStack = event.getItem();
        if(itemStack == null || itemStack.getItemMeta().getLore() == null || !itemStack.getItemMeta().getLore().get(0).equals("Creation Stick")) return;
        event.getPlayer().sendMessage("test");
        UUID uuid = event.getPlayer().getUniqueId();

        if(event.getAction() == Action.LEFT_CLICK_BLOCK){
            putSelection(uuid, event.getClickedBlock().getLocation().clone().add(0.5,1f,0.5), null);
        }else if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            putSelection(uuid, null, event.getClickedBlock().getLocation().clone().add(0.5,1f,0.5));
        }
        event.setCancelled(true);
    }

    public WizardSelection getSelection(UUID uuid){
        return wizardMap.get(uuid);
    }

    private void putSelection(UUID uuid, Location first, Location second){
        WizardSelection selection;
        if((selection = wizardMap.get(uuid)) == null){
            selection = new WizardSelection();
            wizardMap.put(uuid, selection);
        }

        if(first != null)  selection.setFirst(first);
        if(second != null) selection.setSecond(second);

    }

    public void removeSelection(UUID uuid){
        wizardMap.remove(uuid);
    }


    @Data
    public static class WizardSelection{
        private Location first;
        private Location second;
    }


}
