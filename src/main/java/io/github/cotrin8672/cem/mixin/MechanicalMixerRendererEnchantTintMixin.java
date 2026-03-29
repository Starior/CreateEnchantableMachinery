package io.github.cotrin8672.cem.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerRenderer;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * When Flywheel visualization is off, the mixer pole and head are drawn here (the cogwheel shaft is tinted via
 * {@link KineticBlockEntityRendererEnchantTintMixin}).
 */
@Mixin(value = MechanicalMixerRenderer.class, remap = false)
public abstract class MechanicalMixerRendererEnchantTintMixin {
    @Redirect(
            method = "renderSafe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V",
                    ordinal = 1
            )
    )
    private void cem$enchantTintMixerPole(
            SuperByteBuffer poleBuffer,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            MechanicalMixerBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            poleBuffer.color(EnchantableKineticTint.enchantTintColor());
        }
        poleBuffer.renderInto(poseStack, vertexConsumer);
    }

    @Redirect(
            method = "renderSafe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V",
                    ordinal = 2
            )
    )
    private void cem$enchantTintMixerHead(
            SuperByteBuffer headBuffer,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            MechanicalMixerBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        if (EnchantableKineticTint.appliesToBlockEntity(be)) {
            headBuffer.color(EnchantableKineticTint.enchantTintColor());
        }
        headBuffer.renderInto(poseStack, vertexConsumer);
    }
}
