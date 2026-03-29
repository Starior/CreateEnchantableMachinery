package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllBlocks
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import io.github.cotrin8672.cem.Cem.Companion.REGISTRATE
import io.github.cotrin8672.cem.content.block.plough.EnchantablePloughBlockEntity
import io.github.cotrin8672.cem.content.block.plough.EnchantablePloughRenderer

object BlockEntityRegistration {
    val ENCHANTABLE_MECHANICAL_PLOUGH: BlockEntityEntry<EnchantablePloughBlockEntity> = REGISTRATE
        .blockEntity<EnchantablePloughBlockEntity>("enchantable_plough", ::EnchantablePloughBlockEntity)
        .validBlocks(AllBlocks.MECHANICAL_PLOUGH)
        .renderer { NonNullFunction(::EnchantablePloughRenderer) }
        .register()

    /**
     * Call from mod construction so this singleton (and Registrate `register()` calls) runs early.
     * Never resolve the deferred holder here — it is unbound until registry events complete.
     */
    fun ensureRegistered() {
        // Object init already registered the deferred entry; no holder resolution needed.
    }
}
