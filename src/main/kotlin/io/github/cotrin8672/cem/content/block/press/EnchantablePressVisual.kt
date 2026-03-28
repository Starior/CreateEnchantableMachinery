package io.github.cotrin8672.cem.content.block.press

import com.mojang.math.Axis
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock
import com.simibubi.create.content.kinetics.base.ShaftVisual
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.visual.DynamicVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.instance.OrientedInstance
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
import net.createmod.catnip.math.AngleHelper
import org.joml.Quaternionf
import java.util.function.Consumer

class EnchantablePressVisual(
    context: VisualizationContext,
    blockEntity: EnchantableMechanicalPressBlockEntity,
    partialTick: Float,
) :
    ShaftVisual<EnchantableMechanicalPressBlockEntity>(context, blockEntity, partialTick),
    SimpleDynamicVisual {
    private val pressHead: OrientedInstance = instancerProvider().instancer(
        InstanceTypes.ORIENTED,
        Models.partial(AllPartialModels.MECHANICAL_PRESS_HEAD)
    ).createInstance()

    init {
        val q: Quaternionf = Axis.YP
            .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)))
        pressHead.rotation(q)
        transformModels(partialTick)
    }

    override fun beginFrame(ctx: DynamicVisual.Context) {
        transformModels(ctx.partialTick())
    }

    private fun transformModels(pt: Float) {
        val renderedHeadOffset = getRenderedHeadOffset(pt)
        pressHead.position(visualPosition)
            .translatePosition(0f, -renderedHeadOffset, 0f)
        EnchantableKineticTint.applyFlywheelTint(blockEntity, pressHead)
        pressHead.setChanged()
    }

    private fun getRenderedHeadOffset(pt: Float): Float {
        val pressingBehaviour = blockEntity.getPressingBehaviour()
        return pressingBehaviour.getRenderedHeadOffset(pt) * pressingBehaviour.mode.headOffset
    }

    override fun updateLight(partialTick: Float) {
        super.updateLight(partialTick)
        relight(pressHead)
    }

    override fun _delete() {
        super._delete()
        pressHead.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        super.collectCrumblingInstances(consumer)
        consumer.accept(pressHead)
    }
}
