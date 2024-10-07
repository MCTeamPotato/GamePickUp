package com.teampotato.gpu.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

public class HitResultUtil {
    public static EntityHitResult hitEntity(Player player){
        HitResult hitResult = getHitResult(player.level(), player);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            return entityHitResult;
        }
        return null;
    }

    public static BlockState hitBlock(Player player){
        HitResult hitResult = getHitResult(player.level(), player);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            return player.level().getBlockState(blockHitResult.getBlockPos());
        }
        return null;
    }

    public static HitResult getHitResult(Level level, Player player){
        return getHitResult(level, player, 3);
    }

    public static HitResult getHitResult(Level level, Player player, double range) {
        Vec3 eyePosition = player.getEyePosition();  // 获取玩家的眼睛位置
        Vec3 viewVector = player.getViewVector(1.0f); // 下一帧向量
        Vec3 endPosition = eyePosition.add(viewVector.scale(range));

        HitResult hitResult = ProjectileUtil.getEntityHitResult(level,
                player,
                eyePosition,
                endPosition,
                new AABB(eyePosition, endPosition)
                        .expandTowards(viewVector.scale(range)),
                entity -> !entity.isSpectator(),
                0.0f
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
