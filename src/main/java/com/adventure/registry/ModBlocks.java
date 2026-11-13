package com.adventure.registry;

import com.adventure.Code_Adventure;
import com.adventure.block.CodePcBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Block CODE_PC = register("code_pc",
            new CodePcBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(3.5f).requiresCorrectToolForDrops()));

    private static Block register(String name, Block block){
        return Registry.register(BuiltInRegistries.BLOCK, Code_Adventure.id(name), block);
    }

    public static void register(){
        Code_Adventure.LOGGER.info("Registering blocks for " + Code_Adventure.MOD_ID);
    }
}


