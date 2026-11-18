package com.adventure.network;

import com.adventure.Code_Adventure;
import com.adventure.progression.XpExchangeService;
import com.adventure.quest.PlayerProgress;
import com.adventure.quest.Problem;
import com.adventure.quest.ProblemRepository;
import com.adventure.quest.ProgressSavedData;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public class ModNetworking {
    public static final ResourceLocation SYNC_STATE = Code_Adventure.id("sync_state");
    public static final ResourceLocation BUY_LEVEL = Code_Adventure.id("buy_level");

    public static final ResourceLocation SUBMIT = Code_Adventure.id("submit");

    public static void registerC2S() {
        ServerPlayNetworking.registerGlobalReceiver(BUY_LEVEL,(server, player,handler, buf, responseSender)->
        {
            server.execute(()->{
                XpExchangeService.BuyResult result = XpExchangeService.tryBuyLevel(player);
                String msg = switch(result){
                    case SUCCESS -> "Level purchased successfully!";
                    case NOT_ENOUGH_XP -> "Not enough XP!";
                    case MAX_LEVEL_REACHED -> "Max level reached!";
                };
                sendSync(player, msg);
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(SUBMIT, (server, player, handler, buf, responseSender) -> {
            String problemId = buf.readUtf();
            String selectedOption = buf.readUtf(16).trim().toUpperCase(Locale.ROOT);

            server.execute(() -> {
                ProgressSavedData data = ProgressSavedData.get(player.server);
                PlayerProgress progress = data.getOrCreate(player.getUUID());
                Problem problem = ProblemRepository.getById(problemId);

                if (problem == null) {
                    sendSync(player, "Unknown problem.");
                    return;
                }
                if (progress.getBoughtLevel() < problem.level()) {
                    sendSync(player, "You haven't unlocked this problem yet.");
                    return;
                }
                if (progress.isSolved(problemId)) {
                    sendSync(player, "Already solved: " + problem.title());
                    return;
                }

                if (selectedOption.equals(problem.correctOption())) {
                    progress.markSolved(problemId);
                    data.setDirty();
                    player.giveExperienceLevels(5); // example reward
                    sendSync(player, "Correct! " + problem.explanation());
                } else {
                    sendSync(player, "Incorrect. Try again!");
                }
            });
        });
    }
    public static void sendSync(ServerPlayer player, String message) {
        ProgressSavedData data = ProgressSavedData.get(player.server);
        PlayerProgress progress = data.getOrCreate(player.getUUID());

        int boughtLevel = progress.getBoughtLevel();

        Problem current = boughtLevel > 0 ? ProblemRepository.getByLevel(boughtLevel) : null;
        Problem next = ProblemRepository.getByLevel(boughtLevel + 1);

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(boughtLevel);
        buf.writeInt(player.experienceLevel);
        buf.writeUtf(message == null ? "" : message);

        buf.writeBoolean(next != null);
        if (next != null) {
            buf.writeInt(next.level());
            buf.writeInt(next.xpCost());
            buf.writeUtf(next.id());
            buf.writeUtf(next.title());
        }

        buf.writeBoolean(current != null);
        if (current != null) {
            buf.writeUtf(current.id());
            buf.writeUtf(current.title());
            buf.writeUtf(current.prompt());
            buf.writeInt(current.options().size());
            current.options().forEach((key, value) -> {
                buf.writeUtf(key);
                buf.writeUtf(value);
            });
            buf.writeBoolean(progress.isSolved(current.prompt()));
        }
        ServerPlayNetworking.send(player, SYNC_STATE, buf);
    }
}
