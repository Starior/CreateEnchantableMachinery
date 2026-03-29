package io.github.cotrin8672.cem

import com.simibubi.create.AllBlocks
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.ponder.CemPonderPlugin
import net.createmod.ponder.foundation.PonderIndex
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
            event.register(
                { _, level, pos, _ ->
                    if (level == null || pos == null) {
                        return@register 0xFFFFFF
                    }
                    val be = level.getBlockEntity(pos)
                    if (be is EnchantableBlockEntity && !be.getEnchantments().isEmpty) {
                        EnchantableKineticTint.enchantBlockTintArgb()
                    } else {
                        0xFFFFFF
                    }
                },
                AllBlocks.MECHANICAL_PLOUGH.get(),
            )
        }
        NeoForge.EVENT_BUS.addListener<ClientPlayerNetworkEvent.LoggingIn> {
            EnchantableKineticTint.refreshCachedEnchantTint()
        }
    }
}
