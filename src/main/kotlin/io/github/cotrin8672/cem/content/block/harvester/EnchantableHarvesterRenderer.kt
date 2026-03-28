package io.github.cotrin8672.cem.content.block.harvester

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import io.github.cotrin8672.cem.client.EnchantableKineticTint
import io.github.cotrin8672.cem.util.nonNullLevel
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.Vec3

class EnchantableHarvesterRenderer(
    @Suppress("UNUSED_PARAMETER") context: BlockEntityRendererProvider.Context,
) : SafeBlockEntityRenderer<EnchantableHarvesterBlockEntity>() {
    override fun renderSafe(
        be: EnchantableHarvesterBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val blockState = be.blockState
        val superBuffer = CachedBuffers.partial(AllPartialModels.HARVESTER_BLADE, blockState)
        HarvesterRenderer.transform(
            be.nonNullLevel,
            blockState.getValue(HarvesterBlock.FACING),
            superBuffer,
            be.animatedSpeed,
            PIVOT
        )
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            superBuffer.color<SuperByteBuffer>(EnchantableKineticTint.enchantTintColor())
        }
        superBuffer.light<SuperByteBuffer>(light)
            .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()))
    }

    companion object {
        private val PIVOT = Vec3(0.0, 6.0, 9.0)
    }
}
