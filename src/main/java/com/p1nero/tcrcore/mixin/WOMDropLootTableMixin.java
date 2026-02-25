package com.p1nero.tcrcore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reascer.wom.world.loot.WOMLootDropTables;
import yesman.epicfight.api.forgeevent.SkillLootTableRegistryEvent;

@Mixin(WOMLootDropTables.class)
public class WOMDropLootTableMixin {

    @Inject(method = "SkillLootDrop", at = @At("HEAD"), cancellable = true, remap = false)
    private static void tcr$loot(SkillLootTableRegistryEvent event, CallbackInfo ci) {
        ci.cancel();
    }

}
