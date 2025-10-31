package com.deku.instantbonemeal.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;
import java.nio.file.*;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class InstantBonemealClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean enabled = false;
    private static int radius = 5; // configurable and adjustable
    private static final int TICKS_BETWEEN = 10;
    private static int tickCounter = 0;

    private static final Path CONFIG_PATH = Path.of("config/growaura.json");

    @Override
    public void onInitializeClient() {
        loadConfig();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.growaura.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.growaura"
        ));

        // Key toggle handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null)
                    client.player.sendSystemMessage(Component.literal("Instant Bonemeal: " + (enabled ? "ON" : "OFF")));
            }

            if (!enabled || client.player == null || client.level == null) return;

            tickCounter = (tickCounter + 1) % TICKS_BETWEEN;
            if (tickCounter != 0) return;

            applyBonemealSameY(client.player, client.level, radius);
        });

        // Register command /bmr [radius]
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(Commands.literal("bmr")
                .executes(ctx -> {
                    if (ctx.getSource().getEntity() instanceof Player player) {
                        player.sendSystemMessage(Component.literal("Current bonemeal radius: " + radius));
                    }
                    return 1;
                })
                .then(Commands.argument("radius", net.minecraft.commands.arguments.IntegerArgumentType.integer(1, 20))
                    .executes(ctx -> {
                        int newRadius = net.minecraft.commands.arguments.IntegerArgumentType.getInteger(ctx, "radius");
                        radius = newRadius;
                        saveConfig();
                        if (ctx.getSource().getEntity() instanceof Player player) {
                            player.sendSystemMessage(Component.literal("Bonemeal radius set to " + radius + " blocks."));
                        }
                        return 1;
                    })));

        });
    }

    private void applyBonemealSameY(Player player, Level level, int radius) {
        BlockPos center = player.blockPosition();
        int y = center.getY();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos pos = new BlockPos(center.getX() + dx, y, center.getZ() + dz);
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof BonemealableBlock) {
                    BonemealableBlock growable = (BonemealableBlock) state.getBlock();
                    if (growable.isValidBonemealTarget(level, pos, state, false)) {
                        if (!level.isClientSide()) {
                            growable.performBonemeal(level, level.random, pos, state);
                        } else {
                            player.swing(InteractionHand.MAIN_HAND);
                            player.getUseItem();
                        }
                    }
                }
            }
        }
    }

    private static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                JsonObject obj = new Gson().fromJson(json, JsonObject.class);
                radius = obj.has("radius") ? obj.get("radius").getAsInt() : radius;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveConfig();
        }
    }

    private static void saveConfig() {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("radius", radius);
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, new Gson().toJson(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
