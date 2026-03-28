package io.github.cotrin8672.cem.content.block.press

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import io.github.cotrin8672.cem.util.use
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.world.level.block.state.BlockState

class EnchantableMechanicalPressRenderer(
    @Suppress("UNUSED_PARAMETER") context: Context,
) : KineticBlockEntityRenderer<EnchantableMechanicalPressBlockEntity>(context) {
    override fun shouldRenderOffScreen(be: EnchantableMechanicalPressBlockEntity): Boolean {
        return true
    }

    override fun renderSafe(
        be: EnchantableMechanicalPressBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)

        val state = be.blockState
        val headModel = CachedBuffers.partialFacing(
            AllPartialModels.MECHANICAL_PRESS_HEAD,
            state,
            state.getValue(HORIZONTAL_FACING)
        )
        val pressingBehaviour = be.getPressingBehaviour()
        val renderedHeadOffset =
            pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset

        // Without Flywheel: Create draws head here. With Flywheel: EnchantablePressVisual draws the same partial;
        // vertex tint on OrientedInstance is unreliable for pole+plate, so duplicate this pass when enchanted so
        // SuperByteBuffer.color matches crushing wheels / other BER-tinted parts.
        ms.use {
            val viz = VisualizationManager.supportsVisualization(be.level)
            val drawHeadInBer = !viz || EnchantableKineticTint.appliesToBlockEntity(be)
            if (drawHeadInBer) {
                val head = headModel.translate(0.0, -renderedHeadOffset.toDouble(), 0.0)
                if (EnchantableKineticTint.appliesToBlockEntity(be)) {
                    head.color<SuperByteBuffer>(EnchantableKineticTint.enchantTintColor())
                }
                head.light<SuperByteBuffer>(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()))
            }
        }
    }

    override fun getRenderedBlockState(be: EnchantableMechanicalPressBlockEntity): BlockState {
        return shaft(getRotationAxisOf(be))
    }
}
