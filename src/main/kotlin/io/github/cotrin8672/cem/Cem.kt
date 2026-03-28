package io.github.cotrin8672.cem

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.config.ModConfigs
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.level.BlockDropsEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(Cem.MOD_ID)
class Cem(container: ModContainer) {
    companion object {
        const val MOD_ID = "createenchantablemachinery"
        val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)

        fun asResource(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
        }
    }

    init {
        NeoForge.EVENT_BUS.addListener(this::onBlockDrops)
        REGISTRATE.registerEventListeners(MOD_BUS)
        container.registerConfig(ModConfig.Type.CLIENT, CemConfig.CONFIG_SPEC)
        ModConfigs.register(ModLoadingContext.get(), container)
    }

    private fun onBlockDrops(event: BlockDropsEvent) {
        val enchantableBlockEntity = event.blockEntity as? EnchantableBlockEntity ?: return
        val sourceItem = enchantableBlockEntity.getSourceItem() ?: return
        val defaultDroppedItem = event.state.block.asItem()
        val blockEnchantments = enchantableBlockEntity.getEnchantments()

        for (drop in event.drops) {
            val oldStack = drop.item
            if (oldStack.item != defaultDroppedItem) continue

            val newStack = ItemStack(sourceItem, oldStack.count)
            if (!blockEnchantments.isEmpty) {
                newStack.set(DataComponents.ENCHANTMENTS, blockEnchantments)
            }
            drop.item = newStack
        }
    }
}
