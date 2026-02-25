package com.p1nero.tcrcore.entity.custom.chronos_sol;

import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.tcrcore.TCRCoreMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class ChronosSolGeoRenderer extends GeoEntityRenderer<ChronosSolEntity> {

    public static boolean useRedTexture;

    public ChronosSolGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(TCRCoreMod.MOD_ID, "guider")) {
            @Override
            public void setCustomAnimations(ChronosSolEntity animatable, long instanceId, AnimationState<ChronosSolEntity> animationState) {
                CoreGeoBone head = getAnimationProcessor().getBone("Head");

                if (head != null) {
                    EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

                    head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
                    head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
                }
            }

            @Override
            public ResourceLocation getTextureResource(ChronosSolEntity animatable) {
                if (useRedTexture) {
                    return ResourceLocation.fromNamespaceAndPath(TCRCoreMod.MOD_ID, "textures/entity/guider_red.png");
                }
                return super.getTextureResource(animatable);
            }
        });
    }

    @Override
    public void render(@NotNull ChronosSolEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        //对通关者不渲染
        if (entity.canInteract(localPlayer)) {
            poseStack.pushPose();
            poseStack.scale(1.25F, 1.25F, 1.25F);
            poseStack.translate(0, 0.25F, 0);
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
