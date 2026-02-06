package com.p1nero.tcrcore.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResonanceStoneItem extends SimpleDescriptionItem{
    public ResonanceStoneItem(Properties properties) {
        super(properties, true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        //TODO 添加目的地描述
        super.appendHoverText(itemStack, level, list, flag);
        if(level != null && level.isClientSide) {
            String dimension = itemStack.getOrCreateTag().getString("target_dimension");
            String targetBoss = itemStack.getOrCreateTag().getString("target_boss");
            //维度：
            //使徒：
        }
    }
}
