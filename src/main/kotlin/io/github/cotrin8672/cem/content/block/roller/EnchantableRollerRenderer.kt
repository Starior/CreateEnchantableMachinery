package io.github.cotrin8672.cem.content.block.roller

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import io.github.cotrin8672.cem.util.use
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

class EnchantableRollerRenderer(
    @Suppress("UNUSED_PARAMETER") context: BlockEntityRendererProvider.Context,
) : SmartBlockEntityRenderer<EnchantableRollerBlockEntity>(context) {
    override fun renderSafe(
        be: EnchantableRollerBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)

        val blockState = be.blockState
        val facing = blockState.getValue(RollerBlock.FACING)
        val superBuffer = CachedBuffers.partial(AllPartialModels.ROLLER_WHEEL, blockState)

        ms.use {
            ms.translate(0.0, -0.25, 0.0)
            superBuffer.translate(Vec3.atLowerCornerOf(facing.normal).scale((17 / 16f).toDouble()))
            HarvesterRenderer.transform(be.level, facing, superBuffer, be.animatedSpeed, Vec3.ZERO)
            if (EnchantableKineticTint.appliesToBlockEntity(be)) {
                superBuffer.color<SuperByteBuffer>(EnchantableKineticTint.enchantTintColor())
            }
            superBuffer.translate(0.0, -0.5, 0.5)
                .rotateYDegrees(90f)
                .light<SuperByteBuffer>(light)
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))
        }

        val frame = CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
            .rotateCentered(AngleHelper.rad((AngleHelper.horizontalAngle(facing) + 180.0)), Direction.UP)
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            frame.color<SuperByteBuffer>(EnchantableKineticTint.enchantTintColor())
        }
        frame.light<SuperByteBuffer>(light)
            .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))
    }

    companion object {
        fun renderInContraption(
            context: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffers: MultiBufferSource,
        ) {
            val blockState = context.state
            val facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            val superBuffer = CachedBuffers.partial(AllPartialModels.ROLLER_WHEEL, blockState)
            var speed = if (!VecHelper.isVecPointingTowards(context.relativeMotion, facing.opposite))
                context.animationSpeed else 0f
            if (context.contraption.stalled) speed = 0f

            val viewProjection = matrices.viewProjection
            val contraptionWorldLight = LevelRenderer.getLightColor(renderWorld, context.localPos)

            viewProjection.use {
                superBuffer
                    .transform(matrices.model)
                    .translate(Vec3.atLowerCornerOf(facing.normal).scale((17.0 / 16)))
                HarvesterRenderer.transform(context.world, facing, superBuffer, speed, Vec3.ZERO)
                viewProjection.translate(0.0, -0.25, 0.0)

                superBuffer.translate(0.0, -0.5, 0.5)
                    .rotateYDegrees(90f)
                    .light<SuperByteBuffer>(contraptionWorldLight)
                    .renderInto(viewProjection, buffers.getBuffer(RenderType.cutoutMipped()))
            }

            viewProjection.use {
                CachedBuffers.partial(AllPartialModels.ROLLER_FRAME, blockState)
                    .transform(matrices.model)
                    .rotateCentered(AngleHelper.rad((AngleHelper.horizontalAngle(facing) + 180.0)), Direction.UP)
                    .light<SuperByteBuffer>(contraptionWorldLight)
                    .renderInto(viewProjection, buffers.getBuffer(RenderType.cutoutMipped()))
            }
        }
    }
}
