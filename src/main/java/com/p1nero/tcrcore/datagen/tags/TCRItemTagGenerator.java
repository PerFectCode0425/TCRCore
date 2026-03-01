package com.p1nero.tcrcore.datagen.tags;

import com.p1nero.tcrcore.TCRCoreMod;
import com.p1nero.tcrcore.item.TCRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TCRItemTagGenerator extends ItemTagsProvider {
    public TCRItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                               CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, TCRCoreMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(TCRItemTags.CATACLYSM_HUMANOID_BOSS_DROP)
                .add(TCRItems.ABYSS_FRAGMENT.get())
                .add(TCRItems.SOUL_FRAGMENT.get())
                .add(TCRItems.FLAME_FRAGMENT.get())
                .add(TCRItems.NETHERITE_FRAGMENT.get())
                .add(TCRItems.ENDER_FRAGMENT.get())
                .add(TCRItems.DESERT_FRAGMENT.get())
                .add(TCRItems.STORM_FRAGMENT.get())
                .add(TCRItems.MECH_FRAGMENT.get());
    }
}