package com.p1nero.tcrcore.datagen.tags;

import com.p1nero.tcrcore.TCRCoreMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TCRItemTags {
    public static final TagKey<Item> CATACLYSM_HUMANOID_BOSS_DROP = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TCRCoreMod.MOD_ID, "cataclysm_humanoid_boss_drop"));
}
