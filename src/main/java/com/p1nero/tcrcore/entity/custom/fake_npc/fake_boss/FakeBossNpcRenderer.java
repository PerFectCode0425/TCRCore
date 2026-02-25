package com.p1nero.tcrcore.entity.custom.fake_npc.fake_boss;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FakeBossNpcRenderer extends HumanoidMobRenderer<FakeBossNpc, HumanoidModel<FakeBossNpc>> {

    public final ResourceLocation texturePath;

    public FakeBossNpcRenderer(EntityRendererProvider.Context context, ResourceLocation texturePath) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.texturePath = texturePath;
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull FakeBossNpc pEntity) {
        return texturePath;
    }

}
