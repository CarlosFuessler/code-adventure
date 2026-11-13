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

public class CodePcScreen extends AbstractContainerScreen<CodePcMenu> {

    public record PcState(
            int boughtLevel, int xpLevel, String message,
            boolean hasNext, int nextLevel, int nextCost, String nextId, String nextTitle,
            boolean hasCurrent, String curId, String curTitle, String curPrompt, String curStarter, String curLang
    ) {}

    private PcState state;
    private CodeEditor editor;

    public CodePcScreen(CodePcMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 320;
        this.imageHeight = 220;
    }

    public void applyState(PcState newState) {
        this.state = newState;
        // initialize editor content from starter code when we receive a current problem
        if (newState.hasCurrent && this.editor != null) {
            this.editor.setText(newState.curStarter == null ? "" : newState.curStarter);
        }
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;

        this.editor = new CodeEditor(this.font, x + 8, y + 95, this.imageWidth - 16, 85);
        if (state != null && state.hasCurrent) {
            this.editor.setText(state.curStarter == null ? "" : state.curStarter);
        }

        this.addRenderableWidget(Button.builder(Component.literal("Buy Level"), b -> {
            ClientPlayNetworking.send(ModNetworking.BUY_LEVEL, PacketByteBufs.create());
        }).bounds(x + 10, y + 190, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Submit"), b -> {
            if (state != null && state.hasCurrent && this.editor != null) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeUtf(state.curId);
                buf.writeUtf(this.editor.getText(), 32767);
                ClientPlayNetworking.send(ModNetworking.SUBMIT, buf);
            }
        }).bounds(x + 100, y + 190, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(x + 230, y + 190, 80, 20).build());
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
        if (this.editor != null) {
            this.editor.render(guiGraphics);
        }
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
            guiGraphics.drawString(this.font, "Current: " + state.curTitle + " [" + state.curLang + "]", 8, y, 0xFFFFFF, false);
            y += 12;
            // Render prompt (simple single-line draw; for long prompts split/wrap later)
            guiGraphics.drawString(this.font, trim(state.curPrompt, 50), 8, y, 0xDDDDDD, false);
            y += 12;
            guiGraphics.drawString(this.font, "Code editor:", 8, y, 0xAAAAAA, false);
        } else {
            guiGraphics.drawString(this.font, "Buy a level to unlock a coding problem.", 8, y, 0xDDDDDD, false);
        }

        if (state.message != null && !state.message.isEmpty()) {
            guiGraphics.drawString(this.font, "Status: " + state.message, 8, this.imageHeight - 40, 0x55FFFF, false);
        }
    }

    private static String trim(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    @Override public boolean charTyped(char c, int modifiers) {
        if (editor != null && editor.charTyped(c, modifiers)) return true;
        return super.charTyped(c, modifiers);
    }
    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editor != null && editor.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (editor != null && editor.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

}