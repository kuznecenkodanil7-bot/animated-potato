package ru.raidmine.raidpunish.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.raidmine.raidpunish.ui.PunishmentActionScreen;

public final class RaidPunishClient implements ClientModInitializer {
    public static final String MOD_ID = "raidpunish";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("RaidPunish initialized");
    }

    public static void openPunishmentScreen(String playerName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        client.setScreen(new PunishmentActionScreen(null, playerName));
    }
}
