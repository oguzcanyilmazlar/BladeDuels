package me.acablade.bladeduels.arena.eventmiddleware;

import lombok.RequiredArgsConstructor;
import me.acablade.bladeapi.AbstractGame;
import me.acablade.bladeapi.events.GameEvent;
import me.acablade.bladeduels.arena.DuelGame;
import me.acablade.bladeduels.arena.eventmiddleware.annotation.Listen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class EventMiddleware {

	private final DuelGame game;


	private List<EventListener> classList = new ArrayList<>();

	private <T extends Event> void registerListener(Class<T> eventClass, Object obj, Method method, boolean checkInside, EventPriority priority, boolean ignoreCancelled){

		if(!(obj instanceof Listener)) return;

		EventExecutor executor = (ignored, event) -> {
			Entity entity = null;
			try {
				if (!eventClass.isAssignableFrom(event.getClass())) return;
				AbstractGame abstractGame = null;
				if (event instanceof PlayerEvent) {
					entity = ((PlayerEvent) event).getPlayer();
				} else if (event instanceof EntityEvent) {
					entity = ((EntityEvent) event).getEntity();
				} else if (event instanceof BlockPlaceEvent) {
					entity = ((BlockPlaceEvent) event).getPlayer();
				} else if (event instanceof BlockBreakEvent) {
					entity = ((BlockBreakEvent) event).getPlayer();
				} else if (event instanceof GameEvent) {
					abstractGame = ((GameEvent) event).getGame();
				} else if (event instanceof InventoryInteractEvent) {
					entity = ((InventoryInteractEvent) event).getWhoClicked();
				} else if (event instanceof VehicleDamageEvent) {
					entity = ((VehicleDamageEvent) event).getAttacker();
				}


				if (checkInside) {
					Entity finalEntity = entity;
					if ((entity != null && game.getGameData().allPlayers().anyMatch(pl -> pl.getUniqueId() == finalEntity.getUniqueId())) || (abstractGame != null && abstractGame.equals(game))) {
						method.invoke(obj, event);
					}
				} else {
					method.invoke(obj, event);
				}


			} catch (InvocationTargetException e) {
//				e.printStackTrace();

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		};


		Bukkit.getPluginManager().registerEvent(eventClass, (Listener) obj, priority,executor, game.getPlugin(), ignoreCancelled);




	}

	public void addListener(EventListener eventListener){
		Class<? extends EventListener> clazz = eventListener.getClass();
		for(Method method : clazz.getMethods()){
			if(!method.isAnnotationPresent(Listen.class))
				continue;
			Listen listen = method.getAnnotation(Listen.class);
			Class<?> eventClazz = method.getParameters()[0].getType();
			if(!Event.class.isAssignableFrom(eventClazz))
				continue;
			registerListener(eventClazz.asSubclass(Event.class),eventListener,method,listen.checkInGame(),listen.priority(),listen.ignoreCancelled());
		}
		classList.add(eventListener);
	}

	public void unloadAll(){
		classList.removeIf(eventListener -> {
			HandlerList.unregisterAll(eventListener);
			return true;
		});
	}

	public void unload(EventListener eventListener){
		HandlerList.unregisterAll(eventListener);
		classList.remove(eventListener);
	}

}
