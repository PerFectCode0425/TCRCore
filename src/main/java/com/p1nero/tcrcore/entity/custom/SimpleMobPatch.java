package com.p1nero.tcrcore.entity.custom;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public class SimpleMobPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {

    public SimpleMobPatch(Faction faction) {
        super(faction);
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        super.commonMobUpdateMotion(considerInaction);
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, AnimationsX.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, AnimationsX.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.CHASE, AnimationsX.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.FALL, AnimationsX.BIPED_FALL);
        animator.addLivingAnimation(LivingMotions.MOUNT, AnimationsX.BIPED_MOUNT);
        animator.addLivingAnimation(LivingMotions.DEATH, AnimationsX.BIPED_DEATH);
    }
}
