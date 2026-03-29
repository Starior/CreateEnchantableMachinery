package io.github.cotrin8672.cem.util

import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment

object MachineEnchantRules {
    private val MINING_ITEM_TAG: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("minecraft", "enchantable/mining")
    )
    private val MINING_LOOT_ITEM_TAG: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("minecraft", "enchantable/mining_loot")
    )
    private val COMPAT_MINING_TAG: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "machine_enchant_compat_mining")
    )
    private val COMPAT_MINING_LOOT_TAG: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "machine_enchant_compat_mining_loot")
    )
    private val ALLOW_FORTUNE_SILK_PAIR_TAG: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "allow_fortune_silk_pair")
    )

    enum class MachineClass(val key: String) {
        DRILL("drill"),
        SAW("saw"),
        HARVESTER("harvester"),
        PLOUGH("plough"),
        ROLLER("roller"),
        FAN("encased_fan"),
        MILLSTONE("millstone"),
        CRUSHING_WHEEL("crushing_wheel"),
        MIXER("mixer"),
        PRESS("press"),
        SPOUT("spout");

        val itemTag: TagKey<Item> = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "machine_class/$key")
        )
        val enchantmentTag: TagKey<Enchantment> = TagKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "machine_enchantments/$key")
        )
    }

    private fun resolveClass(stack: ItemStack): MachineClass? {
        if (!EnchantableRules.isEnchantableItemStack(stack)) return null
        return MachineClass.entries.firstOrNull { stack.`is`(it.itemTag) }
    }

    @JvmStatic
    fun allowsFortuneSilkPair(stack: ItemStack): Boolean {
        val machineClass = resolveClass(stack) ?: return false
        return stack.`is`(ALLOW_FORTUNE_SILK_PAIR_TAG) && stack.`is`(machineClass.itemTag)
    }

    @JvmStatic
    fun isEnchantmentAllowed(
        stack: ItemStack,
        enchantment: Holder<Enchantment>,
        vanillaAllowed: Boolean
    ): Boolean {
        val machineClass = resolveClass(stack) ?: return vanillaAllowed

        val explicitClassRule = enchantment.`is`(machineClass.enchantmentTag)
        val compatMining = vanillaAllowed && stack.`is`(COMPAT_MINING_TAG) && stack.`is`(MINING_ITEM_TAG)
        val compatMiningLoot = vanillaAllowed && stack.`is`(COMPAT_MINING_LOOT_TAG) && stack.`is`(MINING_LOOT_ITEM_TAG)
        return explicitClassRule || compatMining || compatMiningLoot
    }
}

