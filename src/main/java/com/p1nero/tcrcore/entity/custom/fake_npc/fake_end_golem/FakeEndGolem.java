package com.p1nero.tcrcore.entity.custom.fake_npc.fake_end_golem;

import com.brass_amber.ba_bt.init.BTEntityType;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.hm.efn.registries.EFNItem;
import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.api.component.DialogueComponentBuilder;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.client.screen.builder.DialogueScreenBuilder;
import com.p1nero.tcrcore.TCRCoreMod;
import com.p1nero.tcrcore.capability.PlayerDataManager;
import com.p1nero.tcrcore.capability.TCRQuests;
import com.p1nero.tcrcore.entity.TCREntities;
import com.p1nero.tcrcore.entity.custom.fake_npc.FakeNPCEntity;
import com.p1nero.tcrcore.item.TCRItems;
import com.p1nero.tcrcore.utils.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
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

public class FakeEndGolem extends FakeNPCEntity {

    public FakeEndGolem(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public FakeEndGolem(ServerPlayer serverPlayer) {
        super(TCREntities.FAKE_END_GOLEM.get(), serverPlayer.level());
        this.setOwner(serverPlayer);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float value) {
        return super.hurt(source, 999999);
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
        DialogueScreenBuilder builder = new DialogueScreenBuilder(this, TCRCoreMod.MOD_ID);
        DialogueComponentBuilder dBuilder = builder.getComponentBuildr();
        DialogNode root = new DialogNode(dBuilder.ans(0));
        DialogNode execute = new DialogNode.FinalNode(dBuilder.opt(0), 1);
        DialogNode c1 = new DialogNode(dBuilder.ans(1, EntityType.ENDER_DRAGON.getDescription()), dBuilder.opt(1));
        DialogNode c2 = new DialogNode(dBuilder.ans(1, EntityType.ENDER_DRAGON.getDescription()), dBuilder.opt(2));

        DialogNode next = new DialogNode(dBuilder.ans(2), dBuilder.opt(3))
                .addChild(new DialogNode(dBuilder.ans(3), dBuilder.opt(3))
                        .addChild(new DialogNode(dBuilder.ans(4, ModItems.VOID_EYE.get().getDescription()), dBuilder.opt(3))
                                .addLeaf(dBuilder.opt(4), 2)));

        c1.addChild(next);
        c2.addChild(next);

        root.addChild(execute).addChild(c1).addChild(c2);

        return builder.buildWith(root);
    }

    @Override
    public void handleNpcInteraction(ServerPlayer player, int i) {
        if(i == 1 || i == 2) {
            ExecutionHandler.entityForceExecute(player, this, false);
            ItemUtil.addItemEntity(player, ModItems.VOID_EYE.get(), 1, ChatFormatting.LIGHT_PURPLE.getColor().intValue());
            if(i == 2) {
                ItemUtil.addItemEntity(player, EFNItem.YAMATO_DMC_IN_SHEATH.get(), 1, ChatFormatting.LIGHT_PURPLE.getColor().intValue());
            }
        }
        setConversingPlayer(null);
    }

    public boolean shouldRender(Player player) {
        return player.getUUID().equals(this.getOwnerUUID()) || (player.isCreative() && !FMLEnvironment.production);
    }

    @Override
    public @NotNull Component getName() {
        return  BTEntityType.END_GOLEM.get().getDescription().copy().append("?");
    }

}
