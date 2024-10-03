package com.teampotato.gpu.Util;

import com.teampotato.gpu.client.KeyBindings;
import com.teampotato.gpu.network.NetworkHandler;
import com.teampotato.gpu.network.c2s.ItemPickPacketC2S;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class HitResultUtil {
    public static EntityHitResult hitItemEntity(Player player, double range){
        HitResult hitResult = getHitResult(player.level(), player, range);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            return entityHitResult;
        }
        return null;
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
