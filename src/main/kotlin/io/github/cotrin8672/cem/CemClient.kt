package io.github.cotrin8672.cem

import com.simibubi.create.content.contraptions.actors.plough.PloughBlock
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.ponder.CemPonderPlugin
import net.createmod.ponder.foundation.PonderIndex
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.common.NeoForge
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(value = Cem.MOD_ID, dist = [Dist.CLIENT])
class CemClient {
    init {
        // Client config is loaded once; tint cache is not refreshed on ModConfigEvent.Reloading
        // (avoids work on every save). Same refresh path for all machinery (crushing wheels, drill, press, …).
        MOD_BUS.addListener<ModConfigEvent.Loading> { e ->
            if (e.config.spec === CemConfig.CONFIG_SPEC) {
                EnchantableKineticTint.refreshCachedEnchantTint()
            }
        }
        MOD_BUS.addListener<FMLClientSetupEvent> {
            EnchantableKineticTint.refreshCachedEnchantTint()
            PonderIndex.addPlugin(CemPonderPlugin)
        }
        MOD_BUS.addListener<RegisterColorHandlersEvent.Block> { event ->
            val ploughTint = { _: BlockState, level: BlockAndTintGetter?, pos: BlockPos?, _: Int ->
                if (level == null || pos == null) {
                    0xFFFFFF
                } else {
                    val be = level.getBlockEntity(pos)
                    if (be is EnchantableBlockEntity && !be.getEnchantments().isEmpty) {
                        EnchantableKineticTint.enchantBlockTintArgb()
                    } else {
                        0xFFFFFF
                    }
                }
            }
            val ploughBlocks = BuiltInRegistries.BLOCK.filter { it is PloughBlock }
            if (ploughBlocks.isNotEmpty()) {
                event.register(ploughTint, *ploughBlocks.toTypedArray())
            }
        }
        NeoForge.EVENT_BUS.addListener<ClientPlayerNetworkEvent.LoggingIn> {
            EnchantableKineticTint.refreshCachedEnchantTint()
        }
    }
}
