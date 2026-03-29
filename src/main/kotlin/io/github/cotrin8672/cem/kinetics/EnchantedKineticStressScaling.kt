package io.github.cotrin8672.cem.kinetics

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import io.github.cotrin8672.cem.Cem
import io.github.cotrin8672.cem.config.ModConfigs
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block

object EnchantedKineticStressScaling {
    private val ENCHANTABLE_BLOCK_ENTITIES_BLOCK_TAG: TagKey<Block> = TagKey.create(
        Registries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(Cem.MOD_ID, "enchantable_block_entities")
    )

    @JvmStatic
    fun stressImpactMultiplier(be: KineticBlockEntity): Float {
        return multiplier(be, forCapacity = false)
    }

    @JvmStatic
    fun stressCapacityMultiplier(be: KineticBlockEntity): Float {
        return multiplier(be, forCapacity = true)
    }

    private fun multiplier(be: KineticBlockEntity, forCapacity: Boolean): Float {
        val section = try {
            ModConfigs.common().kinetics.enchantedKineticStress
        } catch (_: UninitializedPropertyAccessException) {
            return 1f
        }
        if (!section.enable.get()) return 1f
        if (be !is EnchantableBlockEntity) return 1f
        if (!be.blockState.`is`(ENCHANTABLE_BLOCK_ENTITIES_BLOCK_TAG)) return 1f
        val level = be.level ?: return 1f
        val effHolder = level.registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .get(Enchantments.EFFICIENCY)
            .orElse(null) ?: return 1f
        val effLevel = be.getEnchantmentLevel(effHolder)
        if (effLevel <= 0) return 1f
        val c = if (forCapacity) {
            section.capacityCoeffPerLevel.get()
        } else {
            section.impactCoeffPerLevel.get()
        }
        val m = 1.0 + effLevel * c
        if (m <= 0.0 || m.isNaN()) return 1f
        return m.toFloat()
    }
}
