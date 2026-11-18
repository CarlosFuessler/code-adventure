package com.adventure.client.gui;

import com.adventure.menu.CodePcMenu;
import com.adventure.network.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodePcScreen extends AbstractContainerScreen<CodePcMenu> {

    public record PcState(
            int boughtLevel, int xpLevel, String message,
            boolean hasNext, int nextLevel, int nextCost, String nextId, String nextTitle,
            boolean hasCurrent, String curId, String curTitle, String curPrompt, Map<String, String> options,
            boolean curSolved
    ) {}

    private PcState state;
    private String selectedOption = "";
    private final List<Button> optionButtons = new ArrayList<>();

    public CodePcScreen(CodePcMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 320;
        this.imageHeight = 220;
    }

    public void applyState(PcState newState) {
        boolean problemChanged = this.state == null
                || !this.state.hasCurrent
                || !newState.hasCurrent
                || !this.state.curId.equals(newState.curId);
        this.state = newState;
        if (problemChanged) {
            this.selectedOption = "";
        }
        rebuildWidgets();
    }

    @Override
    protected void init() {
        super.init();
        rebuildWidgets();
    }

    @Override
    protected void rebuildWidgets() {
        this.clearWidgets();
        optionButtons.clear();

        int x = this.leftPos;
        int y = this.topPos;

        this.addRenderableWidget(Button.builder(Component.literal("Buy Level"), b -> {
            ClientPlayNetworking.send(ModNetworking.BUY_LEVEL, PacketByteBufs.create());
        }).bounds(x + 10, y + 190, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Submit"), b -> {
            if (state != null && state.hasCurrent && !state.curSolved && !selectedOption.isEmpty()) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeUtf(state.curId);
                buf.writeUtf(selectedOption, 16);
                ClientPlayNetworking.send(ModNetworking.SUBMIT, buf);
            }
        }).bounds(x + 100, y + 190, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(x + 230, y + 190, 80, 20).build());

        if (state != null && state.hasCurrent && !state.curSolved) {
            int optY = y + 95;
            for (Map.Entry<String, String> entry : state.options.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String label = (key.equals(selectedOption) ? "[x] " : "[ ] ") + key + ": " + trim(value, 36);
                Button btn = Button.builder(Component.literal(label), b -> {
                    this.selectedOption = key;
                    rebuildWidgets();
                }).bounds(x + 10, optY, this.imageWidth - 20, 20).build();
                optionButtons.add(btn);
                this.addRenderableWidget(btn);
                optY += 22;
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF1E1E1E);
        guiGraphics.fill(x + 2, y + 2, x + this.imageWidth - 2, y + this.imageHeight - 2, 0xFF2D2D2D);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int y = 6;
        guiGraphics.drawString(this.font, this.title, 8, y, 0xFFFFFF, false);
        y += 12;

        if (state == null) {
            guiGraphics.drawString(this.font, "Loading...", 8, y, 0xCCCCCC, false);
            return;
        }

        guiGraphics.drawString(this.font, "Bought level: " + state.boughtLevel + " | Your XP levels: " + state.xpLevel, 8, y, 0xCCCCCC, false);
        y += 12;

        if (state.hasNext) {
            guiGraphics.drawString(this.font, "Next: L" + state.nextLevel + " " + state.nextTitle + " (cost: " + state.nextCost + " lvls)", 8, y, 0x80FF80, false);
        } else {
            guiGraphics.drawString(this.font, "No more levels to buy.", 8, y, 0x80FF80, false);
        }
        y += 14;

        if (state.hasCurrent) {
            String titleText = "Current: " + state.curTitle + (state.curSolved ? " [SOLVED]" : "");
            guiGraphics.drawString(this.font, titleText, 8, y, state.curSolved ? 0x80FF80 : 0xFFFFFF, false);
            y += 12;
            guiGraphics.drawString(this.font, trim(state.curPrompt, 50), 8, y, 0xDDDDDD, false);
            y += 12;

            if (state.curSolved) {
                guiGraphics.drawString(this.font, "✔ CORRECT!", this.imageWidth / 2 - 34, 108, 0x55FF55, true);
                guiGraphics.drawString(this.font, "Problem completed", this.imageWidth / 2 - 45, 128, 0xCCFFCC, false);
                guiGraphics.drawString(this.font, "Buy the next level to continue", this.imageWidth / 2 - 72, 142, 0xAAAAAA, false);
            } else {
                guiGraphics.drawString(this.font, "Choose an answer:", 8, y, 0xAAAAAA, false);
            }
        } else {
            guiGraphics.drawString(this.font, "Buy a level to unlock a coding problem.", 8, y, 0xDDDDDD, false);
        }

        if (state.message != null && !state.message.isEmpty() && !(state.hasCurrent && state.curSolved)) {
            guiGraphics.drawString(this.font, "Status: " + trim(state.message, 50), 8, this.imageHeight - 40, 0x55FFFF, false);
        }
    }

    private static String trim(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}