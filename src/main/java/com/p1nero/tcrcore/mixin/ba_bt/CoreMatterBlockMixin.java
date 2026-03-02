package com.p1nero.tcrcore.mixin.ba_bt;

import com.brass_amber.ba_bt.util.BTTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 尝试移除CoreMater碰撞 FIXME
 */
@Mixin(BlockBehaviour.class)
public class CoreMatterBlockMixin {

    @Inject(method = "getCollisionShape", at = @At("HEAD"))
    private void tcr$getCollisionShape(BlockState blockState, BlockGetter shape, BlockPos pos, CollisionContext p_60575_, CallbackInfoReturnable<VoxelShape> cir) {
        if(blockState.is(BTTags.Blocks.BT_CORE_MATTER_BLOCKS)) {
            blockState.getShape(shape, pos);
        }
    }
}
