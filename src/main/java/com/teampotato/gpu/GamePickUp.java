package com.teampotato.gpu;

import com.mojang.logging.LogUtils;
import com.teampotato.gpu.network.NetworkHandler;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(GamePickUp.MODID)
public class GamePickUp {
    public static final String MODID = "gpu";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GamePickUp(){
        NetworkHandler.register();
    }
}
