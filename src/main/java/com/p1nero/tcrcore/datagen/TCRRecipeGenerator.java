package com.p1nero.tcrcore.datagen;

import com.p1nero.tcrcore.datagen.tags.TCRItemTags;
import com.p1nero.tcrcore.item.TCRItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class TCRRecipeGenerator extends TCRRecipeProvider implements IConditionBuilder {
    public TCRRecipeGenerator(PackOutput output) {
        super(output);
    }
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCRItems.WITHER_SOUL_STONE_ACTIVATED.get(), 1)
                .requires(TCRItems.WITHER_SOUL_STONE.get())
                .requires(Items.GHAST_TEAR)
                .unlockedBy(getHasName(TCRItems.WITHER_SOUL_STONE.get()), has(TCRItems.WITHER_SOUL_STONE.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TCRItems.ANCIENT_ORACLE_FRAGMENT.get(), 1)
                .requires(TCRItemTags.CATACLYSM_HUMANOID_BOSS_DROP)
                .requires(TCRItemTags.CATACLYSM_HUMANOID_BOSS_DROP)
                .requires(TCRItemTags.CATACLYSM_HUMANOID_BOSS_DROP)
                .requires(TCRItemTags.CATACLYSM_HUMANOID_BOSS_DROP)
                .unlockedBy(getHasName(TCRItems.WITHER_SOUL_STONE.get()), has(TCRItems.WITHER_SOUL_STONE.get()))
                .save(consumer);
    }

}
