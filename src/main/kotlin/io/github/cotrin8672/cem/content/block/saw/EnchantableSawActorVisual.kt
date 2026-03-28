package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual
import com.simibubi.create.content.kinetics.base.RotatingInstance
import com.simibubi.create.content.kinetics.saw.SawVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

class EnchantableSawActorVisual(
    visualizationContext: VisualizationContext,
    world: BlockAndTintGetter,
    context: MovementContext,
) : ActorVisual(visualizationContext, world, context) {
    private val state: BlockState = context.state
    private val localPos: BlockPos = context.localPos

    private val shaft: RotatingInstance = SawVisual.shaft(instancerProvider, state)

    init {
        val axis = KineticBlockEntityVisual.rotationAxis(state)
        shaft.setRotationAxis(axis)
            .setRotationOffset(KineticBlockEntityVisual.rotationOffset(state, axis, localPos))
            .setPosition(localPos)
            .light(localBlockLight(), 0)
            .setChanged()
    }

    override fun _delete() {
        shaft.delete()
    }
}
