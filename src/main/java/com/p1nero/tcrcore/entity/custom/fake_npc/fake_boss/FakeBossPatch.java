package com.p1nero.tcrcore.entity.custom.fake_npc.fake_boss;

import com.p1nero.tcrcore.gameassets.TCRAnimations;
import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public class FakeBossPatch <T extends PathfinderMob> extends HumanoidMobPatch<T> {

    public FakeBossPatch() {
        super(Factions.VILLAGER);
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        super.commonMobUpdateMotion(considerInaction);
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, TCRAnimations.CLAP);
        animator.addLivingAnimation(LivingMotions.WALK, TCRAnimations.CLAP);
        animator.addLivingAnimation(LivingMotions.CHASE, TCRAnimations.CLAP);
        animator.addLivingAnimation(LivingMotions.FALL, TCRAnimations.CLAP);
        animator.addLivingAnimation(LivingMotions.MOUNT, TCRAnimations.CLAP);
        animator.addLivingAnimation(LivingMotions.DEATH, TCRAnimations.CLAP);
    }
}
