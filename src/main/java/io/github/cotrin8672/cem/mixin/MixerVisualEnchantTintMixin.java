package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.mixer.MixerVisual;
import dev.engine_room.flywheel.lib.instance.ColoredLitInstance;
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
 * {@link MixerVisual} draws the cog from {@link com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual}
 * (tinted there) plus separate {@link RotatingInstance} head and {@link OrientedInstance} pole. Those do not use
 * {@link com.simibubi.create.content.kinetics.base.RotatingInstance#setup(KineticBlockEntity, net.minecraft.core.Direction.Axis, float)},
 * so {@link RotatingInstanceEnchantTintMixin} never runs on the head.
 */
@Mixin(value = MixerVisual.class, remap = false)
public abstract class MixerVisualEnchantTintMixin {
    @Shadow
    @Final
    private RotatingInstance mixerHead;

    @Shadow
    @Final
    private OrientedInstance mixerPole;

    @Inject(method = "animate", at = @At("TAIL"))
    private void cem$applyMixerHeadAndPoleEnchantTint(float pt, CallbackInfo ci) {
        BlockEntity be = ((AbstractBlockEntityVisualAccessor) (Object) this).cem$getBlockEntity();
        if (!(be instanceof KineticBlockEntity kinetic)) {
            return;
        }
        EnchantableKineticTint.applyFlywheelTint(kinetic, (ColoredLitInstance) (Object) mixerHead);
        mixerHead.setChanged();
        EnchantableKineticTint.applyFlywheelTint(kinetic, mixerPole);
        mixerPole.setChanged();
    }
}
