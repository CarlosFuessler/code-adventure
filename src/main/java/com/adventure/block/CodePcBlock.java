package com.adventure.block;

import com.adventure.Code_Adventure;
import com.adventure.menu.CodePcMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.adventure.network.ModNetworking;
public class CodePcBlock extends Block {
    public CodePcBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new CodePcMenu(containerId, playerInventory),
                        Component.literal("Code PC")
                ));
                ModNetworking.sendSync(serverPlayer, "Welcome to the Code PC!");

            }
            Code_Adventure.LOGGER.info("PC used by {} at {}", player.getName().getString(), pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}

