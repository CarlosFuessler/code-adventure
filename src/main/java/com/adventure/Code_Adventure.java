package com.adventure;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adventure.registry.ModBlocks;
import com.adventure.registry.ModItems;
import com.adventure.registry.ModMenus;
import com.adventure.network.ModNetworking;

public class Code_Adventure implements ModInitializer {
	public static final String MOD_ID = "code_adventure";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModBlocks.register();
		ModItems.register();
		ModMenus.register();
		ModNetworking.registerC2S();
		LOGGER.info("Hello Fabric world!");
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
