package com.teampotato.gpu.mixin.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {
    @Invoker("ensureHasSentCarriedItem")
    void ensureHasSentCarriedItemAccessor();

    @Invoker("startPrediction")
    void startPredictionAccessor(ClientLevel pLevel, PredictiveAction pAction);

    @Accessor("localPlayerMode")
    GameType localPlayerModeAccessor();

    @Accessor("connection")
    ClientPacketListener connectionAccessor();
}
