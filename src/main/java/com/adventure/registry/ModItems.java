package com.adventure.registry;

import com.adventure.Code_Adventure;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
public class ModItems {
    public static final Item CODE_PC = register("code_pc",
            new BlockItem(ModBlocks.CODE_PC, new Item.Properties()));
    private static Item register(String name, Item item){
        return Registry.register(BuiltInRegistries.ITEM, Code_Adventure.id(name), item);
    }

    public static void register(){
        Code_Adventure.LOGGER.info("Registering items for " + Code_Adventure.MOD_ID);
    }
}
