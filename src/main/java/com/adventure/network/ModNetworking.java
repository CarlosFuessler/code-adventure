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
import org.apache.logging.log4j.core.jmx.Server;

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
        ServerPlayNetworking.registerGlobalReceiver(SUBMIT, (server, player, handler, buf, responeSender) -> {
            String problemID = buf.readUtf();
            String code = buf.readUtf(32767);
            server.execute(()->{
                sendSync(player, "Submitted for " +problemID+ "(judge not connected yet).");

            });
        });
    }
    public static void sendSync(ServerPlayer player, String message){
        ProgressSavedData data = ProgressSavedData.get(player.server);
        PlayerProgress progress = data.getOrCreate(player.getUUID());

        int boughtLevel = progress.getBoughtLevel();

        Problem current = boughtLevel > 0 ? ProblemRepository.getByLevel(boughtLevel) : null;
        Problem next = ProblemRepository.getByLevel(boughtLevel+1);

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(boughtLevel);
        buf.writeInt(player.experienceLevel);
        buf.writeUtf(message == null ? "" : message);

        buf.writeBoolean(next != null);
        if(next != null){
            buf.writeInt(next.level());
            buf.writeInt(next.xpCost());
            buf.writeUtf(next.id());
            buf.writeUtf(next.title());
        }

        buf.writeBoolean(current != null);
        if(current != null){
            buf.writeUtf(current.id());
            buf.writeUtf(current.title());
            buf.writeUtf(current.prompt());
            buf.writeUtf(current.starterCode());
            buf.writeUtf(current.language());
        }
        ServerPlayNetworking.send(player, SYNC_STATE, buf);
    }
}
