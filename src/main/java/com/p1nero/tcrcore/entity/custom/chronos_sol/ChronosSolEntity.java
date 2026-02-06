package com.p1nero.tcrcore.entity.custom.chronos_sol;

import com.p1nero.dialog_lib.api.component.DialogueComponentBuilder;
import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.api.entity.custom.IEntityNpc;
import com.p1nero.dialog_lib.api.entity.goal.LookAtConservingPlayerGoal;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.client.screen.builder.StreamDialogueScreenBuilder;
import com.p1nero.tcrcore.TCRCoreMod;
import com.p1nero.tcrcore.capability.PlayerDataManager;
import com.p1nero.tcrcore.capability.TCRCapabilityProvider;
import com.p1nero.tcrcore.capability.TCRPlayer;
import com.p1nero.tcrcore.item.TCRItems;
import com.p1nero.tcrcore.save_data.TCRMainLevelSaveData;
import com.p1nero.tcrcore.utils.WorldUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 曦轮
 */
public class ChronosSolEntity extends PathfinderMob implements IEntityNpc, GeoEntity {

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.god_girl.idle2");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private Player conversingPlayer;

    public ChronosSolEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        TCRCapabilityProvider.syncPlayerDataToClient(serverPlayer);//保险
    }

    @Override
    public void tick() {
        super.tick();
        if(level() instanceof ServerLevel) {
            if(conversingPlayer != null && (conversingPlayer.isRemoved() || conversingPlayer.isDeadOrDying() || conversingPlayer.distanceTo(this) > 5)) {
                conversingPlayer = null;
            }
            //位置矫正保险
            if(tickCount % 100 == 0) {
                BlockPos myPos = this.getOnPos();
                if(myPos.getX() != WorldUtil.COL_GUIDER_BLOCK_POS.getX() || myPos.getZ() != WorldUtil.COL_GUIDER_BLOCK_POS.getZ()) {
                    this.setPos(new BlockPos(WorldUtil.COL_GUIDER_BLOCK_POS).getCenter());
                }
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float value) {
        if(source.getEntity() instanceof Player player && player.isCreative()) {
            player.displayClientMessage(Component.translatable("/summon " + ForgeRegistries.ENTITY_TYPES.getKey(this.getType())).withStyle(ChatFormatting.RED), false);
            this.discard();
        }
        return false;
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 114514f)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new LookAtConservingPlayerGoal<>(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double p_21542_) {
        return false;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {

            CompoundTag tag = new CompoundTag();
            tag.putInt("stage", PlayerDataManager.stage.getInt(player));
            tag.putBoolean("finished", TCRMainLevelSaveData.get(serverPlayer.serverLevel()).isAllFinish());
            tag.putBoolean("map_mark", PlayerDataManager.mapMarked.get(serverPlayer));
            tag.putBoolean("fire_eye_get", PlayerDataManager.flameEyeTraded.get(serverPlayer));
            tag.putBoolean("finish_all_eye_boss", PlayerDataManager.isAllEyeGet(serverPlayer));
            tag.putBoolean("finish_all_altar_boss", PlayerDataManager.isAllAltarKilled(serverPlayer));
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.is(TCRItems.ANCIENT_ORACLE_FRAGMENT.get())
                    && (serverPlayer.isCreative()
                        || (itemStack.hasTag() && itemStack.getOrCreateTag().getString(TCRPlayer.PLAYER_NAME).equals(player.getGameProfile().getName())))) {
                tag.putBoolean("is_oracle", true);
            }
            this.sendDialogTo(serverPlayer, tag);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public DialogueScreen getDialogueScreen(CompoundTag compoundTag) {
        int stage = compoundTag.getInt("stage");
        StreamDialogueScreenBuilder treeBuilder = new StreamDialogueScreenBuilder(this, TCRCoreMod.MOD_ID);
        DialogueComponentBuilder dBuilder = treeBuilder.getComponentBuildr();
        DialogNode root = new DialogNode(dBuilder.ans(0), dBuilder.opt(0));

        return treeBuilder.build();

    }

    @Override
    public void handleNpcInteraction(ServerPlayer player, int code) {

        this.setConversingPlayer(null);
    }

    @Override
    public void setConversingPlayer(@Nullable Player player) {
        this.conversingPlayer = player;
    }

    @Override
    public @Nullable Player getConversingPlayer() {
        return conversingPlayer;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::deployAnimController));
    }

    protected <E extends ChronosSolEntity> PlayState deployAnimController(final AnimationState<E> state) {
        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
