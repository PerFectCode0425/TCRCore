package com.p1nero.tcrcore.worldgen;

import com.p1nero.cataclysm_dimension.worldgen.CDSurfaceRuleData;
import com.p1nero.tcrcore.TCRCoreMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;

import java.util.List;

public class TCRNoiseSettings {
    public static final ResourceKey<NoiseGeneratorSettings> SEA = createNoiseGeneratorKey("sea");
    public static final ResourceKey<NoiseGeneratorSettings> REAL = createNoiseGeneratorKey("real");

    private static ResourceKey<NoiseGeneratorSettings> createNoiseGeneratorKey(String name) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath(TCRCoreMod.MOD_ID, name));
    }

    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {
        context.register(SEA, new NoiseGeneratorSettings(
                new NoiseSettings(0, 256, 1, 2),
                Blocks.WATER.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                new NoiseRouter(
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero()
                ),
                CDSurfaceRuleData.overworld(),
                List.of(),
                63,
                true,
                false,
                false,
                true));

        context.register(REAL, new NoiseGeneratorSettings(
                new NoiseSettings(-32, 256, 1, 2),
                Blocks.AIR.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                new NoiseRouter(
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(),
                        DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero()
                ),
                //没实际方块，没用？
                SurfaceRules.ifTrue(SurfaceRules.isBiome(TCRBiomes.REAL),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(
                                SurfaceRules.ON_CEILING,
                                SurfaceRules.state(Blocks.WATER.defaultBlockState())
                        ), SurfaceRules.state(Blocks.BARRIER.defaultBlockState()))),
                List.of(),
                63,
                true,
                false,
                false,
                true
        ));
    }
}
