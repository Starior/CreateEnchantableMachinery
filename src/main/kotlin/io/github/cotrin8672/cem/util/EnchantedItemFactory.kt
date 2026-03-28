package io.github.cotrin8672.cem.util

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.Unbreakable
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.block.state.BlockState

object EnchantedItemFactory {
    private val pickaxeCache: MutableMap<ItemEnchantments, ItemStack> = mutableMapOf()
    private val toolCache: MutableMap<Pair<Item, ItemEnchantments>, ItemStack> = mutableMapOf()

    fun getPickaxeItemStack(enchantmentSet: ItemEnchantments): ItemStack {
        return pickaxeCache.getOrPut(enchantmentSet) {
            ItemStack(Items.NETHERITE_PICKAXE).apply {
                if (enchantmentSet.isEmpty) return@apply
                set(DataComponents.UNBREAKABLE, Unbreakable(false))
                set(DataComponents.ENCHANTMENTS, enchantmentSet)
            }
        }
    }

    fun getPickaxeItemStack(tag: CompoundTag?, context: MovementContext?): ItemStack {
        if (tag == null) return getPickaxeItemStack(ItemEnchantments.EMPTY)
        if (context == null) return getPickaxeItemStack(ItemEnchantments.EMPTY)
        val enchantments = getEnchantments(tag, context)
        if (context.temporaryData is ItemStack) {
            val cached = context.temporaryData as ItemStack
            val cachedEnchantments = cached.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
            if (cached.item == Items.NETHERITE_PICKAXE && cachedEnchantments == enchantments) return cached
        }

        val stack = getPickaxeItemStack(enchantments)
        context.temporaryData = stack
        return stack
    }

    fun getBreakingToolItemStack(tag: CompoundTag?, context: MovementContext?, state: BlockState?): ItemStack {
        if (tag == null || context == null || state == null) return getPickaxeItemStack(ItemEnchantments.EMPTY)
        val enchantments = getEnchantments(tag, context)
        return getBreakingToolItemStack(enchantments, state)
    }

    fun getBreakingToolItemStack(enchantmentSet: ItemEnchantments, state: BlockState?): ItemStack {
        if (state == null) return getPickaxeItemStack(enchantmentSet)
        val candidates = listOf(Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE)

        var bestStack = getPickaxeItemStack(enchantmentSet)
        var bestScore = Double.NEGATIVE_INFINITY

        for (tool in candidates) {
            val stack = getToolItemStack(tool, enchantmentSet)
            val correctToolBonus = if (!state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state)) 1000.0 else 0.0
            val score = correctToolBonus + stack.getDestroySpeed(state).toDouble()
            if (score > bestScore) {
                bestScore = score
                bestStack = stack
            }
        }

        return bestStack
    }

    fun getToolItemStack(tool: Item, enchantmentSet: ItemEnchantments): ItemStack {
        val key = Pair(tool, enchantmentSet)
        return toolCache.getOrPut(key) {
            ItemStack(tool).apply {
                if (enchantmentSet.isEmpty) return@apply
                set(DataComponents.UNBREAKABLE, Unbreakable(false))
                set(DataComponents.ENCHANTMENTS, enchantmentSet)
            }
        }
    }

    fun getEnchantments(tag: CompoundTag?, context: MovementContext?): ItemEnchantments {
        if (tag == null || context == null) return ItemEnchantments.EMPTY
        var enchantments: ItemEnchantments = ItemEnchantments.EMPTY
        val registryOps = context.world.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        ItemEnchantments.CODEC
            .parse(registryOps, tag.get("Enchantments"))
            .resultOrPartial()
            .ifPresent { enchantments = it }
        return enchantments
    }

    fun clearCache() {
        pickaxeCache.clear()
        toolCache.clear()
    }
}
