package me.acablade.bladeduels.arena.eventmiddleware.annotation;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listen {

	boolean checkInGame() default true;
	boolean ignoreCancelled() default false;
	EventPriority priority() default EventPriority.HIGH;

}
