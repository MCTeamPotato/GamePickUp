package com.teampotato.gpu.event;

import com.teampotato.gpu.GamePickUp;
import com.teampotato.gpu.Util.HitResultUtil;
import com.teampotato.gpu.client.KeyBindings;
import com.teampotato.gpu.network.NetworkHandler;
import com.teampotato.gpu.network.c2s.ItemPickPacketC2S;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GamePickUp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void KeyPick(TickEvent.ClientTickEvent event){
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        EntityHitResult entityHitResult = HitResultUtil.hitItemEntity(localPlayer, 4);
        if (entityHitResult == null) return;

        if (entityHitResult.getEntity() instanceof ItemEntity) {
            localPlayer.displayClientMessage(Component.translatable("message.gpu.pick", KeyBindings.PICK.get().getKey().getDisplayName()), true);
            if (KeyBindings.PICK.get().consumeClick()) {
                NetworkHandler.CHANNEL.sendToServer(ItemPickPacketC2S.pickEntity());
            }
        }
    }
}
