package io.github.cotrin8672.cem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Tints the harvester blade BER when the block entity holds enchantments (same idea as
 * {@link SpoutRendererEnchantTintMixin}).
 */
@Mixin(value = HarvesterRenderer.class, remap = false)
public abstract class HarvesterRendererEnchantTintMixin {
    @Redirect(
            method = "renderSafe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void cem$tintHarvesterBlade(
            SuperByteBuffer bladeBuffer,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            HarvesterBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            bladeBuffer.color(EnchantableKineticTint.enchantTintColor());
        }
        bladeBuffer.renderInto(poseStack, vertexConsumer);
    }
}
