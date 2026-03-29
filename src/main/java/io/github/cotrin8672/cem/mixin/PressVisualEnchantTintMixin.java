package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.press.PressVisual;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Create's press uses {@link com.simibubi.create.content.kinetics.base.ShaftVisual} for the shaft (tinted via
 * {@link RotatingInstanceEnchantTintMixin}) but draws the press head as a separate {@link OrientedInstance}. Without
 * this hook only the shaft picks up the enchant tint color.
 */
@Mixin(value = PressVisual.class, remap = false)
public abstract class PressVisualEnchantTintMixin {
    @Shadow
    @Final
    private OrientedInstance pressHead;

    @Inject(method = "transformModels", at = @At("TAIL"))
    private void cem$applyPressHeadEnchantTint(float pt, CallbackInfo ci) {
        BlockEntity be = ((AbstractBlockEntityVisualAccessor) (Object) this).cem$getBlockEntity();
        if (!(be instanceof KineticBlockEntity kinetic)) {
            return;
        }
        EnchantableKineticTint.applyFlywheelTint(kinetic, pressHead);
        pressHead.setChanged();
    }
}
