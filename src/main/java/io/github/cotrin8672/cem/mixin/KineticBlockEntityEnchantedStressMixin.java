package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import io.github.cotrin8672.cem.kinetics.EnchantedKineticStressScaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class KineticBlockEntityEnchantedStressMixin {
    @Inject(method = "calculateStressApplied", at = @At("RETURN"), cancellable = true)
    private void cem$scaleEnchantedStressApplied(CallbackInfoReturnable<Float> cir) {
        KineticBlockEntity self = (KineticBlockEntity) (Object) this;
        float mult = EnchantedKineticStressScaling.stressImpactMultiplier(self);
        if (mult != 1.0f) {
            cir.setReturnValue(cir.getReturnValue() * mult);
        }
    }

    @Inject(method = "calculateAddedStressCapacity", at = @At("RETURN"), cancellable = true)
    private void cem$scaleEnchantedStressCapacity(CallbackInfoReturnable<Float> cir) {
        KineticBlockEntity self = (KineticBlockEntity) (Object) this;
        float mult = EnchantedKineticStressScaling.stressCapacityMultiplier(self);
        if (mult != 1.0f) {
            cir.setReturnValue(cir.getReturnValue() * mult);
        }
    }
}
