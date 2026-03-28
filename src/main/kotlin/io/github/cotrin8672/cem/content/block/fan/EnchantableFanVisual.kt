package io.github.cotrin8672.cem.content.block.fan

import com.simibubi.create.AllPartialModels
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual
import com.simibubi.create.content.kinetics.base.RotatingInstance
import com.simibubi.create.foundation.render.AllInstanceTypes
import dev.engine_room.flywheel.api.instance.Instance
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.model.Models
import net.createmod.catnip.theme.Color
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.function.Consumer

class EnchantableFanVisual(
    context: VisualizationContext,
    blockEntity: EnchantableEncasedFanBlockEntity,
    partialTick: Float,
) : KineticBlockEntityVisual<EnchantableEncasedFanBlockEntity>(context, blockEntity, partialTick) {
    private val shaft: RotatingInstance = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        Models.partial(AllPartialModels.SHAFT_HALF)
    )
        .createInstance()
    private val fan: RotatingInstance = instancerProvider().instancer(
        AllInstanceTypes.ROTATING,
        Models.partial(AllPartialModels.ENCASED_FAN_INNER)
    )
        .createInstance()

    private val direction: Direction = blockState.getValue(BlockStateProperties.FACING)
    private val opposite: Direction = direction.opposite

    init {
        shaft.setup(blockEntity)
            .setPosition(visualPosition)
            .rotateToFace(Direction.SOUTH, opposite)
            .setChanged()

        fan.setup(blockEntity, getFanSpeed())
            .setPosition(visualPosition)
            .rotateToFace(Direction.SOUTH, opposite)
            .setChanged()
    }

    private fun getFanSpeed(): Float {
        var speed = blockEntity.speed * 5
        if (speed > 0f) speed = Mth.clamp(speed, 80f, (64 * 20f))
        if (speed < 0f) speed = Mth.clamp(speed, (-64 * 20f), -80f)
        return speed
    }

    override fun update(pt: Float) {
        shaft.setup(blockEntity)
        fan.setup(blockEntity, getFanSpeed())
        val tint =
            if (EnchantableKineticTint.appliesToKinetic(blockEntity)) EnchantableKineticTint.enchantTintColor() else Color.WHITE
        shaft.setColor(tint)
        fan.setColor(tint)
        shaft.setChanged()
        fan.setChanged()
    }

    override fun updateLight(partialTick: Float) {
        val behind = pos.relative(opposite)
        relight(behind, shaft)
        val inFront = pos.relative(direction)
        relight(inFront, fan)
    }

    override fun _delete() {
        shaft.delete()
        fan.delete()
    }

    override fun collectCrumblingInstances(consumer: Consumer<Instance?>) {
        consumer.accept(shaft)
        consumer.accept(fan)
    }
}
