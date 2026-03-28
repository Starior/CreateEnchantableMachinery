package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import io.github.cotrin8672.cem.util.MovementBehaviourExtensionsKt;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBreakingMovementBehaviour.class, remap = false)
public abstract class BlockBreakingMovementBehaviourMixin {
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void cem$applyEfficiencyMultiplier(MovementContext context, CallbackInfoReturnable<Float> cir) {
        int level = MovementBehaviourExtensionsKt.getEnchantmentLevel(context, Enchantments.EFFICIENCY);
        if (level <= 0) return;
        cir.setReturnValue(cir.getReturnValue() * (level + 1));
    }
}
