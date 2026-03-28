package io.github.cotrin8672.cem.util

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.state.BlockState

object EnchantableRules {
    @JvmField
    val ENCHANTABLE_BLOCK_ENTITIES_TAG: TagKey<Block> = TagKey.create(
        Registries.BLOCK,
        ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_block_entities")
    )

    @JvmField
    val ENCHANTABLE_BLOCKS_ITEM_TAG: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_blocks")
    )

    @JvmStatic
    fun isEnchantableItemStack(stack: ItemStack): Boolean {
        return stack.`is`(ENCHANTABLE_BLOCKS_ITEM_TAG)
    }

    @JvmStatic
    fun isEnchantableBlockState(state: BlockState): Boolean {
        if (state.`is`(ENCHANTABLE_BLOCK_ENTITIES_TAG)) return true
        val item = state.block.asItem()
        if (item == Items.AIR) return false
        return isEnchantableItemStack(ItemStack(item))
    }
}
