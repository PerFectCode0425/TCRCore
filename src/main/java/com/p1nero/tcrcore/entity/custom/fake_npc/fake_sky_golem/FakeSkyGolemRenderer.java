package com.p1nero.tcrcore.entity.custom.fake_npc.fake_sky_golem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FakeSkyGolemRenderer extends HumanoidMobRenderer<FakeSkyGolem, HumanoidModel<FakeSkyGolem>> {
    ResourceLocation DORMANT = ResourceLocation.fromNamespaceAndPath("tcr_bosses", "textures/entity/golem/sky_golem/sky_golem_dormant.png");

    public FakeSkyGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull FakeSkyGolem pEntity) {
        return this.DORMANT;
    }

    @Override
    public boolean shouldRender(@NotNull FakeSkyGolem fakeSkyGolem, @NotNull Frustum frustum, double p_115470_, double p_115471_, double p_115472_) {
        if(Minecraft.getInstance().player != null) {
            return fakeSkyGolem.shouldRender(Minecraft.getInstance().player);
        }
        return false;
    }
}
