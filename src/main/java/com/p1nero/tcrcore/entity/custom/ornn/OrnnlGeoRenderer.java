package com.p1nero.tcrcore.entity.custom.ornn;

import com.p1nero.tcrcore.TCRCoreMod;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class OrnnlGeoRenderer extends GeoEntityRenderer<OrnnEntity> {
    public OrnnlGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(TCRCoreMod.MOD_ID, "ornn"), false));
    }

    @Override
    public boolean shouldRender(@NotNull OrnnEntity ornnEntity, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }
}
