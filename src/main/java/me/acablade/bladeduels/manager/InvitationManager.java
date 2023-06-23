package me.acablade.bladeduels.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.acablade.bladeduels.arena.DuelKit;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InvitationManager {


    private final Cache<UUID, Pair<UUID, DuelKit>> invitationCache = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public boolean sendInvite(UUID sender, UUID receiver, DuelKit kit){
        if(invitationCache.asMap().containsKey(receiver)) return false;
        invitationCache.put(receiver, Pair.of(sender, kit));
        return true;
    }

    public void removeInvite(UUID uuid){
        invitationCache.invalidate(uuid);
    }

    public void remove(UUID uuid){
        removeInvite(uuid);
        removeInviteAsSender(uuid);
    }

    public void removeInviteAsSender(UUID uuid){
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, Pair<UUID, DuelKit>> entry: invitationCache.asMap().entrySet()){
            if(entry.getValue().getLeft().equals(uuid)){
                toRemove.add(entry.getKey());
            }
        }
        invitationCache.invalidateAll(toRemove);
    }

    public Pair<UUID, DuelKit> getInvite(UUID receiver){
        return invitationCache.getIfPresent(receiver);
    }



}
