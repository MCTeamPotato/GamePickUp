package com.teampotato.gpu.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.teampotato.gpu.GamePickUp;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = GamePickUp.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class KeyBindings {
    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        event.register(PICK.get());
    }

    public static final Lazy<KeyMapping> PICK = Lazy.of(() -> new KeyMapping(
        "key.gpu.pick",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        "key.categories.movement"
    ));
}
