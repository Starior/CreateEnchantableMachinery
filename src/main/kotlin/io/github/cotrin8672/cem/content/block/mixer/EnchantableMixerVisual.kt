package io.github.cotrin8672.cem.content.block.mixer

import com.simibubi.create.AllPartialModels
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import com.simibubi.create.content.kinetics.base.RotatingInstance
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual
import com.simibubi.create.foundation.render.AllInstanceTypes
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.visual.DynamicVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.instance.OrientedInstance
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
import net.createmod.catnip.theme.Color
import net.minecraft.core.Direction
import java.util.function.Consumer

class EnchantableMixerVisual(
    context: VisualizationContext,
    blockEntity: EnchantableMechanicalMixerBlockEntity,
    partialTick: Float,
) : SingleAxisRotatingVisual<EnchantableMechanicalMixerBlockEntity>(
    context,
    blockEntity,
    partialTick,
    Models.partial(AllPartialModels.SHAFTLESS_COGWHEEL)
),
    SimpleDynamicVisual {
    private val mixerHead: RotatingInstance = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        Models.partial(AllPartialModels.MECHANICAL_MIXER_HEAD)
    ).createInstance()

    private val mixerPole: OrientedInstance = instancerProvider().instancer(
        InstanceTypes.ORIENTED,
        Models.partial(AllPartialModels.MECHANICAL_MIXER_POLE)
    ).createInstance()

    init {
        mixerHead.setRotationAxis(Direction.Axis.Y)
        animate(partialTick)
    }

    override fun beginFrame(ctx: DynamicVisual.Context) {
        animate(ctx.partialTick())
    }

    private fun animate(pt: Float) {
        val renderedHeadOffset = blockEntity.getRenderedHeadOffset(pt)
        transformPole(renderedHeadOffset)
        transformHead(renderedHeadOffset, pt)
        val tint =
            if (EnchantableKineticTint.appliesToKinetic(blockEntity)) EnchantableKineticTint.enchantTintColor() else Color.WHITE
        mixerHead.setColor(tint)
        mixerHead.setChanged()
    }

    private fun transformHead(renderedHeadOffset: Float, pt: Float) {
        val speed = blockEntity.getRenderedHeadRotationSpeed(pt)
        mixerHead.setPosition(visualPosition)
            .nudge(0f, -renderedHeadOffset, 0f)
            .setRotationalSpeed(speed * 2 * RotatingInstance.SPEED_MULTIPLIER)
            .setChanged()
    }

    private fun transformPole(renderedHeadOffset: Float) {
        mixerPole.position(visualPosition)
            .translatePosition(0f, -renderedHeadOffset, 0f)
            .setChanged()
    }

    override fun updateLight(partialTick: Float) {
        super.updateLight(partialTick)
        relight(pos.below(), mixerHead)
        relight(mixerPole)
    }

    override fun _delete() {
        super._delete()
        mixerHead.delete()
        mixerPole.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        super.collectCrumblingInstances(consumer)
        consumer.accept(mixerHead)
        consumer.accept(mixerPole)
    }
}
