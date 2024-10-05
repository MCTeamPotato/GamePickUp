package com.teampotato.gpu.event;

import com.mojang.blaze3d.platform.Window;
import com.teampotato.gpu.GamePickUp;
import com.teampotato.gpu.Util.HitResultUtil;
import com.teampotato.gpu.client.KeyBindings;
import com.teampotato.gpu.network.NetworkHandler;
import com.teampotato.gpu.network.c2s.ItemPickPacketC2S;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GamePickUp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEvent {
    public static boolean lookAt;
    @SubscribeEvent
    public static void KeyPick(TickEvent.ClientTickEvent event) {
        lookAt = false;
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        EntityHitResult entityHitResult = HitResultUtil.hitItemEntity(localPlayer);
        if (entityHitResult == null) return;

        if (entityHitResult.getEntity() instanceof ItemEntity) {
            lookAt = true;
            if (KeyBindings.PICK.get().consumeClick()) {
                NetworkHandler.CHANNEL.sendToServer(ItemPickPacketC2S.pickEntity());
            }
        }
    }

    @SubscribeEvent
    public static void showOverlay(RenderGuiOverlayEvent.Pre event) {
        if (!lookAt) return;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        Level level = minecraft.level;
        Font font = minecraft.font;
        if (level == null) return;
        MutableComponent message = Component.translatable("message.gpu.pick", KeyBindings.PICK.get().getKey().getDisplayName()).withStyle(ChatFormatting.YELLOW);
        guiGraphics.drawString(
                font,
                message,
                window.getGuiScaledWidth() / 2 + 10,
                window.getGuiScaledHeight() / 2 + 2,
                0xffffff
        );
    }
}
