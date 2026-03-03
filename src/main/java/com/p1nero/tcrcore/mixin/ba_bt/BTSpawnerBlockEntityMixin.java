package com.p1nero.tcrcore.mixin.ba_bt;

import com.brass_amber.ba_bt.block.blockentity.spawner.BTAbstractSpawnerBlockEntity;
import com.p1nero.tcrcore.client.gui.BTSpawnerBlockIndicatorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTAbstractSpawnerBlockEntity.class)
public class BTSpawnerBlockEntityMixin {

    @Inject(method = "clientTick", at = @At("HEAD"), remap = false)
    private static void tcr$clientTick(Level level, BlockPos blockPos, BlockState blockState, BTAbstractSpawnerBlockEntity btSpawnerBlockEntity, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            if(Minecraft.getInstance().player.position().closerThan(blockPos.getCenter(), 20)) {
                BTSpawnerBlockIndicatorRenderer.addTargetSpawner(blockPos);
            } else if(BTSpawnerBlockIndicatorRenderer.hasTarget()){
                BTSpawnerBlockIndicatorRenderer.removeTargetSpawner(blockPos);
            }
        }
    }

}
