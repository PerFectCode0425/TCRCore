package com.p1nero.tcrcore.entity.custom.fake_npc.fake_boss;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FakeBossNpcRenderer extends HumanoidMobRenderer<FakeBossNpc, HumanoidModel<FakeBossNpc>> {

    public FakeBossNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull FakeBossNpc pEntity) {
        return pEntity.getTextureLocation();
    }

    @Override
    public boolean shouldRender(@NotNull FakeBossNpc fakeSkyGolem, @NotNull Frustum frustum, double p_115470_, double p_115471_, double p_115472_) {
        if(Minecraft.getInstance().player != null) {
            return fakeSkyGolem.shouldRender(Minecraft.getInstance().player);
        }
        return false;
    }
}
