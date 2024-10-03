package com.teampotato.gpu.event;

import com.teampotato.gpu.GamePickUp;
import com.teampotato.gpu.client.KeyBindings;
import com.teampotato.gpu.network.NetworkHandler;
import com.teampotato.gpu.network.c2s.ItemPickPacketC2S;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GamePickUp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @SubscribeEvent
    public static void KeyPick(TickEvent.ClientTickEvent event){
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        HitResult hitResult = getHitResult(localPlayer.level(), localPlayer, 4);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof ItemEntity) {
                localPlayer.displayClientMessage(Component.literal("按 [Mouse 5] 拾取"), true);
                if (KeyBindings.PICK.get().consumeClick()) {
                    NetworkHandler.CHANNEL.sendToServer(ItemPickPacketC2S.pickEntity());
                }
            }
        }
    }

    public static HitResult getHitResult(Level level, Player player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0f);
        Vec3 endPosition = eyePosition.add(viewVector.x * range, viewVector.y * range, viewVector.z * range);
        HitResult hitResult = ProjectileUtil.getEntityHitResult(level,
                player,
                eyePosition,
                endPosition,
                player.getBoundingBox().expandTowards(viewVector.scale(range)).inflate(1.0, 1.0, 1.0),
                entity -> !entity.isSpectator()
        );
        if (hitResult == null) {
            hitResult = level.clip(new ClipContext(
                    eyePosition,
                    endPosition,
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.SOURCE_ONLY,
                    player
            ));
        }
        return hitResult;
    }
}
