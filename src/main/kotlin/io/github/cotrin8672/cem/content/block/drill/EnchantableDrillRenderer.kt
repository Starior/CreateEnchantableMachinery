package io.github.cotrin8672.cem.content.block.drill

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ContraptionMatrices
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.drill.DrillRenderer
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.BlockState

class EnchantableDrillRenderer(
    context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableDrillBlockEntity>(context) {
    override fun getRotatedModel(be: EnchantableDrillBlockEntity, state: BlockState): SuperByteBuffer {
        return CachedBuffers.partialFacing(AllPartialModels.DRILL_HEAD, state)
    }

    override fun renderSafe(
        be: EnchantableDrillBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
    }

    companion object {
        fun renderInContraption(
            context: MovementContext,
            renderWorld: VirtualRenderWorld,
            matrices: ContraptionMatrices,
            buffer: MultiBufferSource,
        ) {
            DrillRenderer.renderInContraption(context, renderWorld, matrices, buffer)
        }
    }
}
