package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllBlocks
import com.simibubi.create.api.behaviour.movement.MovementBehaviour
import io.github.cotrin8672.cem.content.block.drill.EnchantableDrillMovementBehaviour
import io.github.cotrin8672.cem.content.block.harvester.EnchantableHarvesterMovementBehaviour
import io.github.cotrin8672.cem.content.block.plough.EnchantablePloughMovementBehaviour
import io.github.cotrin8672.cem.content.block.roller.EnchantableRollerMovementBehaviour
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawMovementBehaviour
import net.minecraft.world.level.block.Block

/**
 * Create registers default movement behaviours on its blocks. CEM replaces those entries so contraptions
 * use enchant-aware subclasses (same pattern as the former duplicate CEM block registrations).
 */
object CemMovementBehaviourSetup {
    fun replaceCreateBehaviours() {
        @Suppress("UNCHECKED_CAST")
        val map = movementBehaviourBackingMap() as MutableMap<Block, MovementBehaviour>
        map[AllBlocks.MECHANICAL_DRILL.get()] = EnchantableDrillMovementBehaviour()
        map[AllBlocks.MECHANICAL_SAW.get()] = EnchantableSawMovementBehaviour()
        map[AllBlocks.MECHANICAL_HARVESTER.get()] = EnchantableHarvesterMovementBehaviour()
        map[AllBlocks.MECHANICAL_PLOUGH.get()] = EnchantablePloughMovementBehaviour()
        map[AllBlocks.MECHANICAL_ROLLER.get()] = EnchantableRollerMovementBehaviour()
    }

    private fun movementBehaviourBackingMap(): Any {
        val registry = MovementBehaviour.REGISTRY
        var c: Class<*> = registry.javaClass
        while (c != Any::class.java) {
            try {
                val f = c.getDeclaredField("registrations")
                f.isAccessible = true
                return f.get(registry) ?: error("registrations map is null")
            } catch (_: NoSuchFieldException) {
                c = c.superclass ?: break
            }
        }
        error("Could not access MovementBehaviour backing map")
    }
}
