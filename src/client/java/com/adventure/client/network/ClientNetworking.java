package com.adventure.client.network;

import com.adventure.client.gui.CodePcScreen;
import com.adventure.network.ModNetworking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class ClientNetworking {

    public static void registerS2C() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.SYNC_STATE, (client, handler, buf, responseSender) -> {
            int boughtLevel = buf.readInt();
            int xpLevel = buf.readInt();
            String message = buf.readUtf();

            boolean hasNext = buf.readBoolean();
            int nextLevel = 0, nextCost = 0;
            String nextId = "", nextTitle = "";
            if (hasNext) {
                nextLevel = buf.readInt();
                nextCost = buf.readInt();
                nextId = buf.readUtf();
                nextTitle = buf.readUtf();
            }

            boolean hasCurrent = buf.readBoolean();
            String curId = "", curTitle = "", curPrompt = "", curStarter = "", curLang = "";
            if (hasCurrent) {
                curId = buf.readUtf();
                curTitle = buf.readUtf();
                curPrompt = buf.readUtf();
                curStarter = buf.readUtf();
                curLang = buf.readUtf();
            }

            final var state = new CodePcScreen.PcState(
                boughtLevel, xpLevel, message,
                hasNext, nextLevel, nextCost, nextId, nextTitle,
                hasCurrent, curId, curTitle, curPrompt, curStarter, curLang
            );

            client.execute(() -> {
                if (Minecraft.getInstance().screen instanceof CodePcScreen screen) {
                    screen.applyState(state);
                }
            });
        });
    }
}
