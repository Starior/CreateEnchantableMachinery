package io.github.cotrin8672.cem.content.block.plough

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class EnchantablePloughRenderer(
    @Suppress("UNUSED_PARAMETER") context: BlockEntityRendererProvider.Context,
) : SafeBlockEntityRenderer<EnchantablePloughBlockEntity>() {
    override fun renderSafe(
        be: EnchantablePloughBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        // Base plough has no extra BER geometry; contraption rendering uses Create defaults.
    }
}
