package com.teampotato.gpu.event;

import com.mojang.blaze3d.platform.Window;
import com.teampotato.gpu.GamePickUp;
import com.teampotato.gpu.client.KeyBindings;
import com.teampotato.gpu.misc.ModTags;
import com.teampotato.gpu.network.NetworkHandler;
import com.teampotato.gpu.network.c2s.ItemPickPacketC2S;
import com.teampotato.gpu.util.HitResultUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GamePickUp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEvent {
    @SubscribeEvent
    public static void KeyPick(TickEvent.ClientTickEvent event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        EntityHitResult entityHitResult = HitResultUtil.hitEntity(localPlayer);
        if (entityHitResult == null) return;

        if (entityHitResult.getEntity() instanceof ItemEntity) {
            if (KeyBindings.PICK.get().consumeClick()) {
                NetworkHandler.CHANNEL.sendToServer(ItemPickPacketC2S.pickEntity());
            }
        }
    }

    @SubscribeEvent
    public static void showOverlay(RenderGuiOverlayEvent.Pre event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Window window = minecraft.getWindow();
        Level level = minecraft.level;
        Font font = minecraft.font;
        if (level == null || player == null) return;
        EntityHitResult entityHitResult = HitResultUtil.hitEntity(player);
        BlockState blockHitResult = HitResultUtil.hitBlock(player);

        if (entityHitResult != null && entityHitResult.getEntity() instanceof ItemEntity) {
            MutableComponent message = Component.translatable("message.gpu.pick", KeyBindings.PICK.get().getKey().getDisplayName()).withStyle(ChatFormatting.YELLOW);
            guiGraphics.drawString(
                    font,
                    message,
                    window.getGuiScaledWidth() / 2 + 10,
                    window.getGuiScaledHeight() / 2 + 2,
                    0xffffff
            );
        } else if (blockHitResult != null && blockHitResult.is(ModTags.INTERACTION)){
            MutableComponent message = Component.translatable("message.gpu.interaction", KeyBindings.PICK.get().getKey().getDisplayName()).withStyle(ChatFormatting.YELLOW);
            guiGraphics.drawString(
                    font,
                    message,
                    window.getGuiScaledWidth() / 2 + 10,
                    window.getGuiScaledHeight() / 2 + 2,
                    0xffffff
            );
        }
    }
}
