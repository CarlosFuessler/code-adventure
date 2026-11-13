package com.adventure.client;

import com.adventure.client.gui.CodePcScreen;
import com.adventure.client.network.ClientNetworking;
import com.adventure.registry.ModMenus;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class Code_AdventureClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.CODE_PC, CodePcScreen::new);
		ClientNetworking.registerS2C();
	}
}