package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SingleAxisRotatingVisual.class, remap = false)
public abstract class SingleAxisRotatingVisualEnchantTintMixin {
    @Shadow
    protected RotatingInstance rotatingModel;

    @Inject(method = "tick", at = @At("TAIL"))
    private void cem$enchantTint(SimpleTickableVisual.Context context, CallbackInfo ci) {
        BlockEntity be = ((AbstractBlockEntityVisualAccessor) (Object) this).cem$getBlockEntity();
        if (!(be instanceof KineticBlockEntity kinetic) || !EnchantableKineticTint.appliesToKinetic(kinetic)) {
            return;
        }
        rotatingModel.setColor(EnchantableKineticTint.enchantTintColor());
        rotatingModel.setChanged();
    }
}
