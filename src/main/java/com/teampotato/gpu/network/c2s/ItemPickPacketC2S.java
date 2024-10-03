package com.teampotato.gpu.network.c2s;

import com.teampotato.gpu.GamePickUp;
import com.teampotato.gpu.client.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.teampotato.gpu.event.ForgeEvent.getHitResult;

public record ItemPickPacketC2S() {
    public static ItemPickPacketC2S pickEntity(){
        return new ItemPickPacketC2S();
    }

    public static void encode(ItemPickPacketC2S packet, FriendlyByteBuf friendlyByteBuf) {}

    public static ItemPickPacketC2S decode(FriendlyByteBuf friendlyByteBuf) {
        return new ItemPickPacketC2S();
    }

    public static void handle(ItemPickPacketC2S packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if (serverPlayer == null) return;
            HitResult hitResult = getHitResult(serverPlayer.level(), serverPlayer, 4);
            if (hitResult instanceof EntityHitResult entityHitResult) {
                if (entityHitResult.getEntity() instanceof ItemEntity itemEntity) {
                    serverPlayer.addItem(itemEntity.getItem());
                    itemEntity.discard();
                    GamePickUp.LOGGER.info(itemEntity.getItem().getDescriptionId());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
