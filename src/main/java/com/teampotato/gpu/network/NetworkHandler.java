package com.teampotato.gpu.network;

import com.teampotato.gpu.GamePickUp;
import com.teampotato.gpu.network.c2s.ItemPickPacketC2S;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GamePickUp.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, ItemPickPacketC2S.class, ItemPickPacketC2S::encode, ItemPickPacketC2S::decode, ItemPickPacketC2S::handle);
    }
}
