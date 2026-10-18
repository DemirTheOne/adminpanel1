package com.thexyro.adminpanel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

/** Dunyaya kaydedilen yetki listesi. Sahip (TheXyro0) listeye bakilmadan her zaman yetkili. */
public class Perms extends SavedData {
    private static final String NAME = "adminpanel_perms";
    private final Set<UUID> allowed = new HashSet<>();

    public static Perms get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(Perms::load, Perms::new, NAME);
    }

    public static Perms load(CompoundTag tag) {
        Perms p = new Perms();
        ListTag list = tag.getList("allowed", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            try {
                p.allowed.add(UUID.fromString(list.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return p;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (UUID id : allowed) {
            list.add(StringTag.valueOf(id.toString()));
        }
        tag.put("allowed", list);
        return tag;
    }

    public boolean add(UUID id) {
        boolean changed = allowed.add(id);
        if (changed) setDirty();
        return changed;
    }

    public boolean remove(UUID id) {
        boolean changed = allowed.remove(id);
        if (changed) setDirty();
        return changed;
    }

    public static boolean isOwner(ServerPlayer p) {
        return p.getGameProfile().getName().equalsIgnoreCase(AdminPanelMod.OWNER_NAME);
    }

    public static boolean isPermitted(ServerPlayer p) {
        return isOwner(p) || get(p.server).allowed.contains(p.getUUID());
    }
}
