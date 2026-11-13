package com.adventure.registry;

import com.adventure.Code_Adventure;
import com.adventure.menu.CodePcMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
    public static final MenuType<CodePcMenu> CODE_PC = Registry.register(
            BuiltInRegistries.MENU, Code_Adventure.id("code_pc"),
            new MenuType<>(CodePcMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static void register(){
        Code_Adventure.LOGGER.info("Registering menus for " + Code_Adventure.MOD_ID);
    }
}
