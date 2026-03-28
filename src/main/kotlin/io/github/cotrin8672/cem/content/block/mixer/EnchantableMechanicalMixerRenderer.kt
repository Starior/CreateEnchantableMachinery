package io.github.cotrin8672.cem.content.block.mixer

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.animation.AnimationTickHolder
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import kotlin.math.PI

class EnchantableMechanicalMixerRenderer(
    @Suppress("UNUSED_PARAMETER") context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableMechanicalMixerBlockEntity>(context) {
    override fun shouldRenderOffScreen(be: EnchantableMechanicalMixerBlockEntity): Boolean {
        return true
    }

    override fun renderSafe(
        be: EnchantableMechanicalMixerBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val blockState = be.blockState
        val vb = buffer.getBuffer(RenderType.solid())
        val superBuffer = CachedBuffers.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState)

        val renderedHeadOffset = be.getRenderedHeadOffset(partialTicks).toDouble()
        val speed = be.getRenderedHeadRotationSpeed(partialTicks)
        val time = AnimationTickHolder.getRenderTime(be.nonNullLevel)
        val angle = ((time * speed * 6 / 10f) % 360) / 180 * PI
        val poleRender = CachedBuffers.partial(AllPartialModels.MECHANICAL_MIXER_POLE, blockState)

        if (!VisualizationManager.supportsVisualization(be.level)) {
            standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb)

            val pole = poleRender.translate(0.0, -renderedHeadOffset, 0.0)
            if (EnchantableKineticTint.appliesToBlockEntity(be)) {
                pole.color<SuperByteBuffer>(EnchantableKineticTint.enchantTintColor())
            }
            pole.light<SuperByteBuffer>(light)
                .renderInto(ms, vb)

            val vbCutout = buffer.getBuffer(RenderType.cutoutMipped())
            val headRender = CachedBuffers.partial(AllPartialModels.MECHANICAL_MIXER_HEAD, blockState)
                .rotateCentered(angle.toFloat(), Direction.UP)
                .translate(0.0, -renderedHeadOffset, 0.0)
            if (EnchantableKineticTint.appliesToBlockEntity(be)) {
                headRender.color<SuperByteBuffer>(EnchantableKineticTint.enchantTintColor())
            }
            headRender.light<SuperByteBuffer>(light)
                .renderInto(ms, vbCutout)
        }
    }
}
