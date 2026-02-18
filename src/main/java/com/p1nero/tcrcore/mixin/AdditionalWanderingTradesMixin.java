package com.p1nero.tcrcore.mixin;

import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades;
import net.minecraftforge.event.village.WandererTradesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdditionalWanderingTrades.class)
public class AdditionalWanderingTradesMixin {

    @Inject(method = "addWanderingTrades", at = @At("HEAD"), cancellable = true, remap = false)
    private static void tcr$addWanderingTrades(WandererTradesEvent event, CallbackInfo ci) {
        ci.cancel();
    }

}
