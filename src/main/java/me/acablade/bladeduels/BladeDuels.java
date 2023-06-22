package me.acablade.bladeduels;

import lombok.Getter;
import me.acablade.bladeduels.arena.DuelKit;
import me.acablade.bladeduels.commands.DuelCommand;
import me.acablade.bladeduels.elo.EloSystem;
import me.acablade.bladeduels.exception.DuelExceptionAdapter;
import me.acablade.bladeduels.exception.InvalidKitException;
import me.acablade.bladeduels.manager.ArenaManager;
import me.acablade.bladeduels.manager.InvitationManager;
import me.acablade.bladeduels.manager.KitManager;
import me.acablade.bladeduels.manager.MessageManager;
import me.acablade.bladeduels.wizard.MapCreationWizard;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

@Getter
public final class BladeDuels extends JavaPlugin {

    private EloSystem eloSystem;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private MessageManager messageManager;
    private InvitationManager invitationManager;
    private MapCreationWizard mapCreationWizard;


    @Override
    public void onEnable() {
        // Plugin startup logic

        eloSystem = new EloSystem(this);
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        messageManager = new MessageManager(this);
        invitationManager = new InvitationManager();
        mapCreationWizard = new MapCreationWizard();

        kitManager.loadKits();
        arenaManager.loadMaps();
        getServer().getPluginManager().registerEvents(mapCreationWizard, this);

        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);
        commandHandler.registerDependency(ArenaManager.class, arenaManager);
        commandHandler.registerDependency(KitManager.class, kitManager);
        commandHandler.registerDependency(MessageManager.class, messageManager);
        commandHandler.registerDependency(EloSystem.class, eloSystem);
        commandHandler.registerDependency(InvitationManager.class, invitationManager);
        commandHandler.registerDependency(MapCreationWizard.class, mapCreationWizard);
        commandHandler.registerValueResolver(DuelKit.class, valueResolverContext -> {
            String value = valueResolverContext.pop();
            if(!kitManager.getKits().containsKey(value)) throw new InvalidKitException(valueResolverContext.parameter(), value);
            return kitManager.getKit(value);
        });
        commandHandler.register(new DuelCommand());
        commandHandler.setExceptionHandler(new DuelExceptionAdapter(messageManager));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
