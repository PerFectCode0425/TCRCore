package com.p1nero.tcrcore.mixin;

import com.github.dodo.dodosmobs.entity.InternalAnimationMonster.IABossMonsters.Bone_Chimera_Entity;
import com.github.dodo.dodosmobs.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.p1nero.tcrcore.capability.TCREntityCapabilityProvider;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 对话控制开打
 */
@Mixin(Bone_Chimera_Entity.class)
public abstract class Bone_ChimeraMixin extends IABoss_monster {

    @Unique
    private ServerBossEvent tcr$bossBar;

    public Bone_ChimeraMixin(EntityType entity, Level world) {
        super(entity, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void tcr$init(EntityType<?> entity, Level world, CallbackInfo ci) {
        tcr$bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
        MinecraftForge.EVENT_BUS.<PlayerEvent.StartTracking>addListener(startTracking -> {
            if(startTracking.getTarget().equals(this) && startTracking.getEntity() instanceof ServerPlayer serverPlayer) {
                tcr$bossBar.addPlayer(serverPlayer);
            }
        });
        MinecraftForge.EVENT_BUS.<PlayerEvent.StopTracking>addListener(stopTracking -> {
            if(stopTracking.getTarget().equals(this) && stopTracking.getEntity() instanceof ServerPlayer serverPlayer) {
                tcr$bossBar.removePlayer(serverPlayer);
            }
        });
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tcr$tick(CallbackInfo ci) {
        this.tcr$bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        if(!level().isClientSide) {
            if(!TCREntityCapabilityProvider.getTCREntityPatch(this).isFighting()) {
                this.setTarget(null);
                this.attackTicks = 0;
                this.attackCooldown = 100;
                this.setAttackState(0);
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void tcr$hurt(DamageSource damagesource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(!TCREntityCapabilityProvider.getTCREntityPatch(this).isFighting()) {
            cir.setReturnValue(false);
        }
    }

}
