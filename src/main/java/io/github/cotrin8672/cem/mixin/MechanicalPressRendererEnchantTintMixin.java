package io.github.cotrin8672.cem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressRenderer;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * When Flywheel visualization is off, Create draws the press head in the BER. Tint the head buffer the same way as
 * {@link com.simibubi.create.content.kinetics.press.PressVisual} does for {@code OrientedInstance} when viz is on.
 */
@Mixin(value = MechanicalPressRenderer.class, remap = false)
public abstract class MechanicalPressRendererEnchantTintMixin {
    @Redirect(
            method = "renderSafe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void cem$enchantTintPressHeadBer(
            SuperByteBuffer headBuffer,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            MechanicalPressBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource bufferSource,
            int light,
            int overlay
    ) {
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            headBuffer.color(EnchantableKineticTint.enchantTintColor());
        }
        headBuffer.renderInto(poseStack, vertexConsumer);
    }
}
