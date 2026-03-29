package io.github.cotrin8672.cem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawRenderer;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * {@link com.simibubi.create.content.kinetics.saw.SawVisual} only instantiates the shaft; the saw blade is drawn in
 * {@link SawRenderer#renderBlade}. Apply the same enchant tint as the shaft / press head when Flywheel is on or off.
 */
@Mixin(value = SawRenderer.class, remap = false)
public abstract class SawRendererEnchantTintMixin {
    @Redirect(
            method = "renderBlade",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void cem$enchantTintSawBlade(
            SuperByteBuffer bladeBuffer,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            SawBlockEntity be,
            PoseStack ms,
            MultiBufferSource buffer,
            int light
    ) {
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            bladeBuffer.color(EnchantableKineticTint.enchantTintColor());
        }
        bladeBuffer.renderInto(poseStack, vertexConsumer);
    }
}
