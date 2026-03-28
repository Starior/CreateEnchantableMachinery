package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KineticBlockEntityRenderer.class, remap = false)
public abstract class KineticBlockEntityRendererEnchantTintMixin {
    @Inject(method = "kineticRotationTransform", at = @At("RETURN"))
    private static void cem$enchantTint(
            SuperByteBuffer buffer,
            KineticBlockEntity be,
            Direction.Axis axis,
            float angle,
            int light,
            CallbackInfoReturnable<SuperByteBuffer> cir
    ) {
        if (!EnchantableKineticTint.appliesToKinetic(be)) {
            return;
        }
        buffer.color(EnchantableKineticTint.enchantTintColor());
    }
}
