package com.p1nero.tcrcore.mixin;

import com.wintercogs.beyonddimensions.Api.DataBase.Stack.FluidStackType;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.IStackType;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.ItemStackType;
import com.wintercogs.beyonddimensions.Api.DataBase.Storage.UnifiedStorage;
import com.wintercogs.beyonddimensions.Fluid.ModFluids;
import com.wintercogs.beyonddimensions.Item.Custom.BaseMachineItem;
import com.wintercogs.beyonddimensions.Item.Custom.NetMagnetItem;
import com.wintercogs.beyonddimensions.Item.Custom.NetedItem;
import com.wintercogs.beyonddimensions.Machine.*;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(NetMagnetItem.class)
public abstract class NetMagnetItemMixin extends BaseMachineItem {

    @Shadow(remap = false) protected abstract void fluidCollect(FilterMode filterMode, List<IStackType<?>> filterSlots, UnifiedStorage storage, Level level, AABB searchArea);

    @Shadow(remap = false) protected abstract boolean matchesFilter(FilterMode filterMode, List<IStackType<?>> filterSlots, IStackType<?> otherStack);

    @Shadow(remap = false) protected abstract AABB getSearchArea(HopperRangeMode hopperRangeMode, Level level, Vec3i pos);

    @Shadow(remap = false) protected abstract List<ItemEntity> refreshItemEntityCache(HopperNBTMode hopperNBTMode, Level level, AABB searchArea);

    @Shadow(remap = false) protected abstract List<ExperienceOrb> refreshXpEntityCache(Level level, AABB searchArea);

    public NetMagnetItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "workContent", at = @At("HEAD"), remap = false, cancellable = true)
    private void tcr$workContent(ItemStack stack, Level level, Entity holder, int slotId, boolean isSelected, CallbackInfo ci) {
        super.workContent(stack, level, holder, slotId, isSelected);

        FilterMode filterMode = getFilterModeOrDefault(stack, FilterMode.BLACK);
        HopperItemMode hopperItemMode = getHopperItemModeOrDefault(stack, HopperItemMode.ALLOW);
        HopperXpMode hopperXpMode = getHopperXpModeOrDefault(stack, HopperXpMode.DENY);
        HopperNBTMode hopperNBTMode = getHopperNBTModeOrDefault(stack, HopperNBTMode.DENY);
        HopperFluidMode hopperFluidMode = getHopperFluidModeOrDefault(stack, HopperFluidMode.DENY);
        HopperRangeMode hopperRangeMode = getHopperRangeModeOrDefault(stack, HopperRangeMode.RADIUS_MID);
        List<IStackType<?>> filterSlots = getFilterSlotsOrDefault(stack, new ArrayList<>());

        AABB searchArea = getSearchArea(hopperRangeMode, level, holder.getOnPos());

        List<ItemEntity> itemEntities = hopperItemMode == HopperItemMode.ALLOW ? refreshItemEntityCache(hopperNBTMode, level, searchArea) : new ArrayList<>();
        List<ExperienceOrb> xpEntities = hopperXpMode == HopperXpMode.ALLOW ? refreshXpEntityCache(level, searchArea) : new ArrayList<>();


        UnifiedStorage storage = NetedItem.getNet(stack, level.getServer()).getUnifiedStorage();

        // 开始收集物品
        if (hopperItemMode == HopperItemMode.ALLOW)
        {
            for (ItemEntity itemEntity : itemEntities)
            {
                if (itemEntity != null && !itemEntity.isRemoved())
                {
                    ItemStack itemStack = itemEntity.getItem().copy();
                    ItemStackType typedStack = new ItemStackType(itemStack);
                    if (matchesFilter(filterMode, filterSlots, typedStack))
                    {

                        if (storage.insert(typedStack, true).isEmpty()) // 表示能成功插入
                        {
                            itemEntity.discard();
                            // workContent之前已经由shouldWork检查过net的存在性
                            storage.insert(typedStack, false);
                            if(holder instanceof Player player) {
                                ForgeEventFactory.firePlayerItemPickupEvent(player, itemEntity, itemStack);
                            }
                        }
                    }
                }
            }
        }
        // 开始收集经验球
        if (hopperXpMode == HopperXpMode.ALLOW)
        {
            for (ExperienceOrb orb : xpEntities)
            {
                if (orb != null && !orb.isRemoved())
                {
                    int xp = orb.getValue();
                    if (xp > 0)
                    {
                        long xpFluid = xp * 20L;
                        FluidStackType xpStack = new FluidStackType(new FluidStack(ModFluids.XP_FLUID.source().get(), 1), xpFluid);

                        if (storage.insert(xpStack, true).isEmpty())
                        {
                            orb.discard();
                            storage.insert(xpStack, false);
                        }
                    }
                }
            }
        }
        // 开始抽取流体
        if (hopperFluidMode == HopperFluidMode.ALLOW)
        {
            fluidCollect(filterMode, filterSlots, storage, level, searchArea);
        }
        ci.cancel();
    }

}
