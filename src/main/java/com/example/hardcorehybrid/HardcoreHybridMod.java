package com.example.hardcorehybrid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Date;
import java.util.UUID;

public class HardcoreHybridMod implements ModInitializer {

    @Override
    public void onInitialize() {

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {

            if (!(entity instanceof ServerPlayerEntity player)) return;

            MinecraftServer server = player.getServer();
            if (server == null) return;

            UUID uuid = player.getUuid();
            boolean isBedrock = FloodgateApi.getInstance().isFloodgatePlayer(uuid);

            server.execute(() -> {

                if (isBedrock) {
                    Text reason = Text.literal(
                        "§cYou died.\n§7Hardcore mode: Bedrock players are banned on death."
                    );

                    server.getPlayerManager().getUserBanList().add(
                        new BannedPlayerEntry(
                            player.getGameProfile(),
                            new Date(),
                            "HardcoreHybrid",
                            null,
                            reason.getString()
                        )
                    );

                    player.networkHandler.disconnect(reason);

                } else {
                    player.changeGameMode(GameMode.SPECTATOR);
                    player.sendMessage(
                        Text.literal("§cYou died.\n§7Hardcore mode: You are now a spectator."),
                        false
                    );
                }
            });
        });
    }
}
