package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.minecraft.core.Direction.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Flywheel uses {@link RotatingInstance#setup} from {@code OrientedRotatingVisual} / {@code SingleAxisRotatingVisual}
 * every frame; BER tint mixins often never run when visualization is active. Apply tint here so drill wheels, shafts,
 * crushing wheels, etc. pick it up reliably.
 */
@Mixin(value = RotatingInstance.class, remap = false)
public abstract class RotatingInstanceEnchantTintMixin {
    @Inject(
        method = "setup(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lnet/minecraft/core/Direction$Axis;F)Lcom/simibubi/create/content/kinetics/base/RotatingInstance;",
        at = @At("RETURN")
    )
    private void cem$applyEnchantTintAfterSetup(KineticBlockEntity be, Axis axis, float speed, CallbackInfoReturnable<RotatingInstance> cir) {
        if (!EnchantableKineticTint.appliesToKinetic(be)) {
            return;
        }
        RotatingInstance self = (RotatingInstance) (Object) this;
        self.setColor(EnchantableKineticTint.enchantTintColor());
        self.setChanged();
    }
}
