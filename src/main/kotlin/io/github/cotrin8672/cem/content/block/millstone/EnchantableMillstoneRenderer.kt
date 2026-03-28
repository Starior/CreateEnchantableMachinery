package io.github.cotrin8672.cem.content.block.millstone

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.BlockState

class EnchantableMillstoneRenderer(
    @Suppress("UNUSED_PARAMETER") context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableMillstoneBlockEntity>(context) {
    override fun getRotatedModel(be: EnchantableMillstoneBlockEntity, state: BlockState): SuperByteBuffer? {
        return CachedBuffers.partial(AllPartialModels.MILLSTONE_COG, state)
    }

    override fun renderSafe(
        be: EnchantableMillstoneBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
    }
}
