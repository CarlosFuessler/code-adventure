package com.adventure.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
public class CodeEditor {
    private final int x, y ,width, height;
    private final Font font;
    private final List<StringBuilder> lines = new ArrayList<>();
    private int cursorRow = 0, cursorCol = 0;
    private int scroll = 0;

    public CodeEditor(Font font, int x, int y, int width, int height){
        this.font = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        lines.add(new StringBuilder());
    }

    public void setText(String text) {
        lines.clear();
        String t = text == null ? "" : text.replace("\r", "");
        for (String line : t.split("\n", -1)) lines.add(new StringBuilder(line));
        if (lines.isEmpty()) lines.add(new StringBuilder());
        cursorRow = 0;
        cursorCol = 0;
        scroll = 0;
    }

    public String getText(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<lines.size(); i++){
            sb.append(lines.get(i));
            if(i<lines.size()-1) sb.append("\n");
        }
        return sb.toString();
    }

    private int visibleLines(){
        return Math.max(1, height/10);
    }
    private void clampCursor(){
        cursorRow = Mth.clamp(cursorRow, 0, lines.size()-1);
        cursorCol = Mth.clamp(cursorCol, 0, lines.get(cursorRow).length());
        if(cursorRow < scroll) scroll = cursorRow;
        if(cursorRow >= scroll+ visibleLines()) scroll = cursorRow- visibleLines()+1;
        scroll = Mth.clamp(scroll, 0, Math.max(0, lines.size()-visibleLines()));
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        StringBuilder line = lines.get(cursorRow);
        switch(keyCode){
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (cursorCol > 0){
                    line.deleteCharAt(cursorCol-1); cursorCol--;
                }
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (cursorCol < line.length()){
                    line.deleteCharAt(cursorCol);
                }
                else if (cursorRow < lines.size()-1){
                    line.append(lines.get(cursorRow+1));
                    lines.remove(cursorRow+1);
                }
            }

            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                String rest = line.substring(cursorCol);
                line.delete(cursorCol, line.length());
                lines.add(cursorRow + 1, new StringBuilder(rest));
                cursorRow++; cursorCol = 0;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (cursorCol > 0) cursorCol--; else if (cursorRow > 0) { cursorRow--; cursorCol = lines.get(cursorRow).length(); }
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (cursorCol < line.length()) cursorCol++; else if (cursorRow < lines.size() - 1) { cursorRow++; cursorCol = 0; }
            }
            case GLFW.GLFW_KEY_UP -> { cursorRow--; cursorCol = Math.min(cursorCol, lines.get(Mth.clamp(cursorRow, 0, lines.size()-1)).length()); }
            case GLFW.GLFW_KEY_DOWN -> { cursorRow++; cursorCol = Math.min(cursorCol, lines.get(Mth.clamp(cursorRow, 0, lines.size()-1)).length()); }
            case GLFW.GLFW_KEY_HOME -> cursorCol = 0;
            case GLFW.GLFW_KEY_END -> cursorCol = line.length();
            default -> { return false; }
        }
        clampCursor();
        return true;
    }

    public boolean charTyped(char c, int modifiers) {
        if (Character.isISOControl(c)) return false;
        lines.get(cursorRow).insert(cursorCol, c);
        cursorCol++;
        clampCursor();
        return true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) return false;
        int row = scroll + (int) ((mouseY - y) / 10);
        row = Mth.clamp(row, 0, lines.size() - 1);
        cursorRow = row;
        String s = lines.get(row).toString();
        int col = 0;
        while (col < s.length() && font.width(s.substring(0, col + 1)) < (mouseX - x - 2)) col++;
        cursorCol = col;
        clampCursor();
        return true;
    }
    public void render(GuiGraphics g) {
        g.fill(x, y, x + width, y + height, 0xFF111111);
        int vis = visibleLines();
        for (int i = 0; i < vis && scroll + i < lines.size(); i++) {
            String text = lines.get(scroll + i).toString().replace("\t", "  ");
            g.drawString(font, text, x + 2, y + 1 + i * 10, 0xE0E0E0, false);
        }

        if (cursorRow >= scroll && cursorRow < scroll + vis) {
            String before = lines.get(cursorRow).substring(0, cursorCol).replace("\t", "  ");
            int cx = x + 2 + font.width(before);
            int cy = y + 1 + (cursorRow - scroll) * 10;
            if ((System.currentTimeMillis() / 500) % 2 == 0) {
                g.fill(cx, cy, cx + 1, cy + 9, 0xFFFFFFFF);
            }
        }
    }
}
