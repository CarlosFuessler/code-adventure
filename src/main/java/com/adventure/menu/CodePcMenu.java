package com.adventure.menu;

import com.adventure.registry.ModMenus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
public class CodePcMenu extends AbstractContainerMenu {
    public CodePcMenu(int containerId, Inventory playerInventory) {
        super(ModMenus.CODE_PC, containerId);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
