package com.p1nero.tcrcore.entity.custom.fake_npc.fake_boss;

import com.brass_amber.ba_bt.init.BTEntityType;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.api.component.DialogueComponentBuilder;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.client.screen.builder.DialogueScreenBuilder;
import com.p1nero.dialog_lib.client.screen.builder.StreamDialogueScreenBuilder;
import com.p1nero.tcrcore.TCRCoreMod;
import com.p1nero.tcrcore.capability.PlayerDataManager;
import com.p1nero.tcrcore.capability.TCRQuests;
import com.p1nero.tcrcore.entity.TCREntities;
import com.p1nero.tcrcore.entity.custom.fake_npc.FakeNPCEntity;
import com.p1nero.tcrcore.item.TCRItems;
import com.p1nero.tcrcore.utils.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * 只会哦咩爹多，哦完就似
 */
public class FakeBossNpc extends FakeNPCEntity {

    private static final EntityDataAccessor<String> TEXTURE_LOCATION = SynchedEntityData.defineId(FakeBossNpc.class, EntityDataSerializers.STRING);

    public FakeBossNpc(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public FakeBossNpc(EntityType<? extends PathfinderMob> entityType, ServerPlayer serverPlayer) {
        super(entityType, serverPlayer.level());
        this.setOwner(serverPlayer);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_LOCATION, "");
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(TEXTURE_LOCATION, tag.getString("texture_location"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("texture_location", this.entityData.get(TEXTURE_LOCATION));
    }

    public void setTextureLocation(ResourceLocation textureLocation) {
        this.entityData.set(TEXTURE_LOCATION, textureLocation.toString());
    }

    public ResourceLocation getTextureLocation() {
        return ResourceLocation.parse(this.entityData.get(TEXTURE_LOCATION));
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if(player.getUUID().equals(this.getOwnerUUID()) || (!FMLEnvironment.production && player.isCreative())) {
            if( player instanceof ServerPlayer serverPlayer) {
                this.sendDialogTo(serverPlayer);
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return InteractionResult.FAIL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public DialogueScreen getDialogueScreen(CompoundTag compoundTag) {
        StreamDialogueScreenBuilder builder = new StreamDialogueScreenBuilder(this, TCRCoreMod.MOD_ID);
        builder.start(TCRCoreMod.getInfo("congratulation"))
                .addFinalOption(Component.literal("..."), 1);
        return builder.build();
    }

    @Override
    public void handleNpcInteraction(ServerPlayer player, int i) {
        if(i == 1) {
            this.discard();
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.POOF,                     // 粒子类型：烟雾
                        this.getX(),                             // X坐标
                        this.getY() + this.getBbHeight() * 0.5,  // Y坐标（实体中部）
                        this.getZ(),                             // Z坐标
                        30,                                      // 粒子数量
                        0.5, 0.5, 0.5,                           // 偏移范围（在实体周围半格内随机）
                        0.1                                      // 粒子运动速度（轻微扩散）
                );
                serverLevel.sendParticles(
                        ParticleTypes.CHERRY_LEAVES,
                        this.getX(),
                        this.getY() + this.getBbHeight() * 0.5,
                        this.getZ(),
                        20,
                        0.6, 0.6, 0.6,
                        0.0
                );
            }
        }
        setConversingPlayer(null);
    }

    public boolean shouldRender(Player player) {
        return player.getUUID().equals(this.getOwnerUUID()) || (player.isCreative() && !FMLEnvironment.production);
    }

    @Override
    public @NotNull Component getName() {
        return  BTEntityType.SKY_GOLEM.get().getDescription().copy().append("?");
    }

}
