package com.p1nero.tcrcore.item.custom;

import com.p1nero.tcrcore.TCRCoreMod;
import com.p1nero.tcrcore.capability.TCRCapabilityProvider;
import com.p1nero.tcrcore.capability.TCRPlayer;
import com.p1nero.tcrcore.save_data.TCRMainLevelSaveData;
import com.p1nero.tcrcore.utils.WaypointUtil;
import com.p1nero.tcrcore.utils.WorldUtil;
import com.yesman.epicskills.registry.entry.EpicSkillsSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xaero.hud.minimap.waypoint.WaypointColor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ResonanceStoneItem extends Item {

    private final ResourceLocation targetStructure;
    private final ResourceLocation dimension;
    private final int y;
    private final BiConsumer<BlockPos, ServerPlayer> callback;
    private final Predicate<ServerPlayer> predicate;

    public ResonanceStoneItem(Properties properties, ResourceLocation targetStructure, int y, ResourceLocation dimension, Predicate<ServerPlayer> predicate, BiConsumer<BlockPos, ServerPlayer> callback) {
        super(properties);
        this.targetStructure = targetStructure;
        this.dimension = dimension;
        this.callback = callback;
        this.predicate = predicate;
        this.y = y;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(player instanceof ServerPlayer serverPlayer) {
            ResourceKey<Level> levelResourceKey = ResourceKey.create(Registries.DIMENSION, dimension);
            if(predicate.test(serverPlayer) && level.dimension().equals(levelResourceKey)) {
                CompletableFuture.supplyAsync(() -> {
                    serverPlayer.displayClientMessage(TCRCoreMod.getInfo("resonance_stone_working", this.getDescription()), true);
                    serverPlayer.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(EpicSkillsSounds.GAIN_ABILITY_POINTS.get()), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, player.getRandom().nextInt()));
                    BlockPos pos = null;
                    try {
                        pos = WorldUtil.getNearbyStructurePos(serverPlayer, targetStructure.toString(), y);
                    } catch (Exception e) {
                        TCRCoreMod.LOGGER.error("TCRCore : Error finding structure [{}]: {}", targetStructure, e.getMessage());
                    }
                    return pos;
                })
                .thenAccept(pos -> {
                    TCRPlayer tcrPlayer = TCRCapabilityProvider.getTCRPlayer(player);
                    tcrPlayer.playDirectionParticle(player.getEyePosition(), new Vec3(pos.getX(), player.getEyeY(), pos.getZ()));
                    itemStack.shrink(1);
                    serverPlayer.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(EpicSkillsSounds.GAIN_ABILITY_POINTS.get()), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, player.getRandom().nextInt()));
                    callback.accept(pos, serverPlayer);
                });
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, level, list, flag);
        list.add(TCRCoreMod.getInfo("resonance_stone_usage").withStyle(ChatFormatting.GRAY));
        String dimensionNameKey = Util.makeDescriptionId("travelerstitles", dimension);
        if(Language.getInstance().has(dimensionNameKey)) {
            list.add(Component.translatable(dimensionNameKey));
        }
    }

    @Override
    public boolean isFoil(@NotNull ItemStack itemStack) {
        return true;
    }
}
