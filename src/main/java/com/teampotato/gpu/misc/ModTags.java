package com.teampotato.gpu.misc;

import com.teampotato.gpu.GamePickUp;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> INTERACTION = register("interaction");
    private static TagKey<Block> register(String id) {
        return BlockTags.create(new ResourceLocation(GamePickUp.MODID, id));
    }
}
