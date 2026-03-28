package io.github.cotrin8672.cem.content.block.crusher

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class EnchantableCrushingWheelRenderer(
    context: BlockEntityRendererProvider.Context,
) : KineticBlockEntityRenderer<EnchantableCrushingWheelBlockEntity>(context) {
    override fun renderSafe(
        be: EnchantableCrushingWheelBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
    }
}
