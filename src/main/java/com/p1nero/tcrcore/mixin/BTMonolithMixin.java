package com.p1nero.tcrcore.mixin;

import com.brass_amber.ba_bt.entity.block.BTMonolith;
import com.brass_amber.ba_bt.init.BTEntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *  简化为一个钥匙即可
 */
@Mixin(value = BTMonolith.class)
public abstract class BTMonolithMixin extends Entity {

    @Shadow(remap = false)
    @Final
    private EntityType<?> monolithType;

    @Shadow(remap = false)
    @Final
    private Item correctMonolithKey;

    @Shadow(remap = false)
    public abstract void setEyeSlotDisplayed();

    @Shadow(remap = false) public abstract void setKeyCountInEntity(int count);

    @Shadow(remap = false) protected abstract void playKeyInteractionSound();

    @Shadow(remap = false) protected abstract void spawnGolem();

    @Shadow(remap = false) protected abstract void playSpawnSound();

    public BTMonolithMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void tcr$interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.monolithType != null) {
            Item itemInHand = player.getItemInHand(hand).getItem();
            if (itemInHand.equals(this.correctMonolithKey)) {
                if(this.monolithType.equals(BTEntityType.END_MONOLITH.get())) {
                    //末地不消耗，保留
                    spawnGolem();
                    if (!player.isCreative()) {
                        player.getItemInHand(hand).shrink(1);
                    }
                    this.playSpawnSound();
                    this.setKeyCountInEntity(0);
                } else {
                    this.setKeyCountInEntity(3);
                    this.playKeyInteractionSound();
                    if (!player.isCreative()) {
                        player.getItemInHand(hand).shrink(1);
                    }
                    this.setEyeSlotDisplayed();
                }
                cir.setReturnValue(InteractionResult.sidedSuccess(this.getCommandSenderWorld().isClientSide()));
            }
        }
        cir.setReturnValue(super.interact(player, hand));
    }
}
