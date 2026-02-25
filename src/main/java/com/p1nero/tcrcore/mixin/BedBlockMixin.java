package com.p1nero.tcrcore.mixin;

import com.p1nero.tcrcore.capability.TCRQuestManager;
import com.p1nero.tcrcore.capability.TCRQuests;
import com.p1nero.tcrcore.utils.WorldUtil;
import com.p1nero.tcrcore.worldgen.TCRDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.merlin204.wraithon.util.PositionTeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 在那个地方用床就传回主城
 */
@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void tcr$use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (WorldUtil.inReal(player)) {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerLevel sanctum = serverPlayer.server.getLevel(TCRDimensions.SANCTUM_LEVEL_KEY);
                if (sanctum != null) {
                    player.changeDimension(sanctum, new PositionTeleporter(new BlockPos(WorldUtil.START_POS)));
                    if(!TCRQuestManager.hasFinished(serverPlayer, TCRQuests.TALK_TO_AINE_GAME_CLEAR) && TCRQuestManager.hasFinished(serverPlayer, TCRQuests.KILL_MAD_CHRONOS)) {
                        TCRQuests.TALK_TO_AINE_GAME_CLEAR.start(serverPlayer);
                    }
                }
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
        }
    }

}
