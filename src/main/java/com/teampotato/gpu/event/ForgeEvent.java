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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GamePickUp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @SubscribeEvent
    public static void KeyPick(TickEvent.ClientTickEvent event){
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        EntityHitResult entityHitResult = HitResultUtil.hitItemEntity(localPlayer, 4);
        if (entityHitResult == null) return;

        if (entityHitResult.getEntity() instanceof ItemEntity) {
            localPlayer.displayClientMessage(Component.literal("按 [Mouse 5] 拾取"), true);
            if (KeyBindings.PICK.get().consumeClick()) {
                NetworkHandler.CHANNEL.sendToServer(ItemPickPacketC2S.pickEntity());
            }
        }
    }
}
