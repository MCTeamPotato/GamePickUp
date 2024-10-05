package com.teampotato.gpu.mixin;

import com.teampotato.gpu.client.KeyBindings;
import com.teampotato.gpu.misc.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public MultiPlayerGameMode gameMode;

    @Shadow private int rightClickDelay;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Final public Options options;

    @Shadow @Nullable public ClientLevel level;

    @Shadow @Nullable public HitResult hitResult;

    @Shadow @Final public GameRenderer gameRenderer;

    @Shadow protected abstract void startUseItem();

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startUseItem()V", ordinal = 0))
    private void useItemClick(Minecraft instance){
        useItemAction();
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startUseItem()V", ordinal = 1))
    private void useItemKeyHold(Minecraft instance){
        useItemAction();
    }

    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 14), cancellable = true)
    private void interaction(CallbackInfo ci){
        while(KeyBindings.PICK.get().consumeClick()) {
            this.startUseItem();
        }
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1), cancellable = true)
    private void cancelUseItem(CallbackInfo ci){
        ci.cancel();
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 0), cancellable = true)
    private void cancelBlockPlace(CallbackInfo ci){
        BlockHitResult blockhitresult = (BlockHitResult) this.hitResult;
        BlockPos blockPos = blockhitresult.getBlockPos();
        BlockState blockState = this.level.getBlockState(blockPos);
        if (!blockState.is(ModTags.INTERACTION)) ci.cancel();
    }

    @Unique
    private void useItemAction() {
        if (!this.gameMode.isDestroying()) {
            this.rightClickDelay = 4;
            if (!this.player.isHandsBusy()) {

                for(InteractionHand interactionhand : InteractionHand.values()) {
                    var inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(1, this.options.keyUse, interactionhand);
                    if (inputEvent.isCanceled()) {
                        if (inputEvent.shouldSwingHand()) this.player.swing(interactionhand);
                        return;
                    }
                    ItemStack itemstack = this.player.getItemInHand(interactionhand);
                    if (!itemstack.isItemEnabled(this.level.enabledFeatures())) {
                        return;
                    }

                    if (this.hitResult != null) {
                        if (this.hitResult.getType() == HitResult.Type.BLOCK) {
                            BlockHitResult blockhitresult = (BlockHitResult) this.hitResult;
                            BlockPos blockPos = blockhitresult.getBlockPos();
                            BlockState blockState = this.level.getBlockState(blockPos);
                            if (blockState.is(ModTags.INTERACTION)) return;

                            int i = itemstack.getCount();
                            InteractionResult interactionresult1 = this.gameMode.useItemOn(this.player, interactionhand, blockhitresult);
                            if (interactionresult1.consumesAction()) {
                                if (interactionresult1.shouldSwing() && inputEvent.shouldSwingHand()) {
                                    this.player.swing(interactionhand);
                                    if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.gameMode.hasInfiniteItems())) {
                                        this.gameRenderer.itemInHandRenderer.itemUsed(interactionhand);
                                    }
                                }
                                return;
                            }

                            if (interactionresult1 == InteractionResult.FAIL) {
                                return;
                            }
                        }
                    }

                    if (itemstack.isEmpty() && (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS))
                        net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, interactionhand);

                    if (!itemstack.isEmpty()) {
                        InteractionResult interactionresult2 = this.gameMode.useItem(this.player, interactionhand);
                        if (interactionresult2.consumesAction()) {
                            if (interactionresult2.shouldSwing()) {
                                this.player.swing(interactionhand);
                            }

                            this.gameRenderer.itemInHandRenderer.itemUsed(interactionhand);
                            return;
                        }
                    }
                }

            }
        }
    }
}
