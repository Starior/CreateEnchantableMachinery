package io.github.cotrin8672.cem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.fluids.spout.SpoutRenderer;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * BER for {@code create:spout} — tints spout partials when the block entity is enchantable.
 * Create 6.0.x uses one {@code renderInto} call site inside a loop over top/middle/bottom partials (three iterations).
 */
@Mixin(value = SpoutRenderer.class, remap = false)
public abstract class SpoutRendererEnchantTintMixin {
    @Redirect(
            method = "renderSafe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void cem$tintSpoutParts(
            SuperByteBuffer partBuffer,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            SpoutBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        cem$applyTintAndRender(partBuffer, poseStack, vertexConsumer, be);
    }

    private static void cem$applyTintAndRender(
            SuperByteBuffer partBuffer,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            SpoutBlockEntity be
    ) {
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            partBuffer.color(EnchantableKineticTint.enchantTintColor());
        }
        partBuffer.renderInto(poseStack, vertexConsumer);
    }
}
