package com.teampotato.gpu.network.c2s;

import com.teampotato.gpu.GamePickUp;
import com.teampotato.gpu.Util.HitResultUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public record ItemPickPacketC2S() {
    public static ItemPickPacketC2S pickEntity(){
        return new ItemPickPacketC2S();
    }

    public static void encode(ItemPickPacketC2S packet, FriendlyByteBuf friendlyByteBuf) {}

    public static ItemPickPacketC2S decode(FriendlyByteBuf friendlyByteBuf) {
        return new ItemPickPacketC2S();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if (serverPlayer == null) return;

            EntityHitResult entityHitResult = HitResultUtil.hitItemEntity(serverPlayer);
            if (entityHitResult.getEntity() instanceof ItemEntity itemEntity) {
                serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.ITEM_PICKUP, serverPlayer.getSoundSource());
                serverPlayer.addItem(itemEntity.getItem());
                itemEntity.discard();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
