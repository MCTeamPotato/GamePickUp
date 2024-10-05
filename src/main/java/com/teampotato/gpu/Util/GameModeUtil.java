package com.teampotato.gpu.Util;

import com.teampotato.gpu.mixin.accessor.MultiPlayerGameModeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;

@OnlyIn(Dist.CLIENT)
public class GameModeUtil {

    public static InteractionResult useItemOn(LocalPlayer pPlayer, InteractionHand pHand, BlockHitResult pResult) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiPlayerGameModeAccessor gameModeAccessor = (MultiPlayerGameModeAccessor) minecraft.gameMode;
        if (gameModeAccessor == null) return InteractionResult.FAIL;

        gameModeAccessor.ensureHasSentCarriedItemAccessor();
        if (!minecraft.level.getWorldBorder().isWithinBounds(pResult.getBlockPos())) {
            return InteractionResult.FAIL;
        } else {
            MutableObject<InteractionResult> mutableobject = new MutableObject<>();
            gameModeAccessor.startPredictionAccessor(minecraft.level, (pAction) -> {
                mutableobject.setValue(performUseItemOn(pPlayer, pHand, pResult));
                if (mutableobject.getValue() == InteractionResult.PASS || mutableobject.getValue() == InteractionResult.FAIL){
                    return new ServerboundUseItemOnPacket(pHand,
                            BlockHitResult.miss(
                                    minecraft.player.getEyePosition(1.0F),
                                    minecraft.player.getDirection(),
                                    pResult.getBlockPos()
                            ),
                            pAction
                    );
                }
                return new ServerboundUseItemOnPacket(pHand, pResult, pAction);
            });
            return mutableobject.getValue();
        }
    }
    private static InteractionResult performUseItemOn(LocalPlayer pPlayer, InteractionHand pHand, BlockHitResult pResult) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiPlayerGameModeAccessor gameModeAccessor = (MultiPlayerGameModeAccessor) minecraft.gameMode;
        BlockPos blockpos = pResult.getBlockPos();
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks.onRightClickBlock(pPlayer, pHand, blockpos, pResult);
        if (event.isCanceled()) {
            return event.getCancellationResult();
        }
        if (gameModeAccessor.localPlayerModeAccessor() == GameType.SPECTATOR) {
            return InteractionResult.SUCCESS;
        } else {
            UseOnContext useoncontext = new UseOnContext(pPlayer, pHand, pResult);
            if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
                InteractionResult result = itemstack.onItemUseFirst(useoncontext);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }

            if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) {
                return InteractionResult.PASS;
            }
            if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (!itemstack.isEmpty() && !pPlayer.getCooldowns().isOnCooldown(itemstack.getItem()))) {
                InteractionResult interactionresult1;
                if (gameModeAccessor.localPlayerModeAccessor().isCreative()) {
                    int i = itemstack.getCount();
                    itemstack.setCount(i);
                }
                interactionresult1 = itemstack.useOn(useoncontext);

                return interactionresult1;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public static InteractionResult interactiveBlockOn(LocalPlayer pPlayer, InteractionHand pHand, BlockHitResult pResult) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiPlayerGameModeAccessor gameModeAccessor = (MultiPlayerGameModeAccessor) minecraft.gameMode;
        if (gameModeAccessor == null) return InteractionResult.FAIL;

        gameModeAccessor.ensureHasSentCarriedItemAccessor();
        if (!minecraft.level.getWorldBorder().isWithinBounds(pResult.getBlockPos())) {
            return InteractionResult.FAIL;
        } else {
            MutableObject<InteractionResult> mutableobject = new MutableObject<>();
            gameModeAccessor.startPredictionAccessor(minecraft.level, (pAction) -> {
                mutableobject.setValue(performInteractiveBlockOn(pPlayer, pHand, pResult));
                if (mutableobject.getValue() == InteractionResult.PASS){
                    return new ServerboundUseItemOnPacket(pHand,
                            BlockHitResult.miss(
                                    minecraft.player.getEyePosition(1.0F),
                                    minecraft.player.getDirection(),
                                    pResult.getBlockPos()
                            ),
                            pAction
                    );
                }
                return new ServerboundUseItemOnPacket(pHand, pResult, pAction);
            });
            return mutableobject.getValue();
        }
    }
    private static InteractionResult performInteractiveBlockOn(LocalPlayer pPlayer, InteractionHand pHand, BlockHitResult pResult) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiPlayerGameModeAccessor gameModeAccessor = (MultiPlayerGameModeAccessor) minecraft.gameMode;
        BlockPos blockpos = pResult.getBlockPos();
        net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks.onRightClickBlock(pPlayer, pHand, blockpos, pResult);
        if (event.isCanceled()) {
            return event.getCancellationResult();
        }
        if (gameModeAccessor.localPlayerModeAccessor() == GameType.SPECTATOR) {
            return InteractionResult.SUCCESS;
        } else {
            boolean flag = !pPlayer.getMainHandItem().doesSneakBypassUse(pPlayer.level(), blockpos, pPlayer) || !pPlayer.getOffhandItem().doesSneakBypassUse(pPlayer.level(), blockpos, pPlayer);
            boolean flag1 = pPlayer.isSecondaryUseActive() && flag;
            BlockState blockstate = minecraft.level.getBlockState(blockpos);
            if (!gameModeAccessor.connectionAccessor().isFeatureEnabled(blockstate.getBlock().requiredFeatures())) {
                return InteractionResult.FAIL;
            }

            if (event.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY && !flag1)) {
                InteractionResult interactionresult = blockstate.use(minecraft.level, pPlayer, pHand, pResult);
                if (interactionresult.consumesAction()) {
                    return interactionresult;
                }
            }

            if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) {
                return InteractionResult.PASS;
            }
            return InteractionResult.PASS;
        }
    }
}
