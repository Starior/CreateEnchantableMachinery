package io.github.cotrin8672.cem.util;

import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class EnchantedDropHelper {
    private EnchantedDropHelper() {}

    public static List<ItemStack> remapWrenchDrops(Level level, BlockPos pos, BlockState state, List<ItemStack> drops) {
        if (drops == null || drops.isEmpty()) return drops;
        if (!(level.getBlockEntity(pos) instanceof EnchantableBlockEntity enchantable)) return drops;

        var sourceItem = enchantable.getSourceItem();
        if (sourceItem == null) return drops;

        var defaultDroppedItem = state.getBlock().asItem();
        var enchantments = enchantable.getEnchantments();
        List<ItemStack> remapped = new ArrayList<>(drops.size());
        for (ItemStack oldStack : drops) {
            if (oldStack.getItem() != defaultDroppedItem) {
                remapped.add(oldStack);
                continue;
            }

            ItemStack newStack = new ItemStack(sourceItem, oldStack.getCount());
            if (!enchantments.isEmpty()) {
                newStack.set(DataComponents.ENCHANTMENTS, enchantments);
            }
            remapped.add(newStack);
        }

        return remapped;
    }
}
