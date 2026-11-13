package com.adventure.quest;

import java.lang.management.MonitorInfo;
import java.util.HashMap;
import java.util.Map;
import  java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class ProgressSavedData extends SavedData {
    private static final String DATA_NAME = "code_adventure_progress";
    private final Map<UUID, PlayerProgress> players = new HashMap<>();

    public static ProgressSavedData get(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(ProgressSavedData::load, ProgressSavedData::new, DATA_NAME);
    }

    public PlayerProgress getOrCreate(UUID uuid) {
        return players.computeIfAbsent(uuid, id -> new PlayerProgress());
    }

    public static ProgressSavedData load(CompoundTag tag){
        ProgressSavedData data = new ProgressSavedData();
        CompoundTag playersTag = tag.getCompound("players");
        for (String key : playersTag.getAllKeys()){
            UUID uuid = UUID.fromString(key);
            CompoundTag pTag = playersTag.getCompound(key);
            PlayerProgress progress = new PlayerProgress();
            progress.setBoughtLevel(pTag.getInt("boughtLevel"));
            ListTag solvedList = pTag.getList("solved", Tag.TAG_STRING);
            for (int i = 0; i < solvedList.size(); i++){
                progress.markSolved(solvedList.getString(i));
            }
            data.players.put(uuid, progress);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag){
        CompoundTag playersTag = new CompoundTag();
        players.forEach((uuid, progress)->{
            CompoundTag pTag = new CompoundTag();
            pTag.putInt("boughtLevel", progress.getBoughtLevel());
            ListTag solvedList = new ListTag();
            for (String id : progress.getSolved()){
                solvedList.add(StringTag.valueOf(id));
            }
            pTag.put("solved", solvedList);
            playersTag.put(uuid.toString(), pTag);
        });
        tag.put("players", playersTag);
        return tag;
    }

}

