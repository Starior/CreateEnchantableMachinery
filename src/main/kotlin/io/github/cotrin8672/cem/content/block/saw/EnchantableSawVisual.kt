package io.github.cotrin8672.cem.content.block.saw

import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import com.simibubi.create.content.kinetics.base.RotatingInstance
import com.simibubi.create.content.kinetics.saw.SawVisual
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import net.createmod.catnip.theme.Color
import java.util.function.Consumer

class EnchantableSawVisual(
    context: VisualizationContext?,
    blockEntity: SawBlockEntity?,
    partialTick: Float,
) : KineticBlockEntityVisual<SawBlockEntity>(context, blockEntity, partialTick) {
    private val rotatingModel: RotatingInstance = SawVisual.shaft(instancerProvider(), blockState)
        .setup(blockEntity)
        .setPosition(visualPosition)

    init {
        rotatingModel.setChanged()
    }

    override fun update(partialTick: Float) {
        rotatingModel.setup(blockEntity)
        val tint =
            if (EnchantableKineticTint.appliesToKinetic(blockEntity)) EnchantableKineticTint.enchantTintColor() else Color.WHITE
        rotatingModel.setColor(tint)
        rotatingModel.setChanged()
    }

    override fun _delete() {
        rotatingModel.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        consumer.accept(rotatingModel)
    }

    override fun updateLight(partialTick: Float) {
        relight(rotatingModel)
    }
}
