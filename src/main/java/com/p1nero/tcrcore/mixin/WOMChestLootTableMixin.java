package com.p1nero.tcrcore.mixin;

import net.minecraftforge.event.LootTableLoadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reascer.wom.world.loot.WOMChestLootTables;

@Mixin(WOMChestLootTables.class)
public class WOMChestLootTableMixin {

    @Inject(method = "modifyVanillaLootPools", at = @At("HEAD"), cancellable = true, remap = false)
    private static void tcr$loot(LootTableLoadEvent event, CallbackInfo ci) {
        ci.cancel();
    }

}
