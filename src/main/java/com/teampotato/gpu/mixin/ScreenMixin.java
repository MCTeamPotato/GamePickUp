package com.teampotato.gpu.mixin;

import com.teampotato.gpu.client.KeyBindings;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow public abstract boolean shouldCloseOnEsc();

    @Shadow public abstract void onClose();

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    public void interactionClose(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir){
        if (pKeyCode == KeyBindings.PICK.get().getKey().getValue() && this.shouldCloseOnEsc()){
            this.onClose();
            cir.setReturnValue(true);
        }
    }
}
