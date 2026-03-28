package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PressingBehaviour.class, remap = false)
public abstract class PressingBehaviourEnchantmentsMixin {
    @Inject(method = "getRunningTickSpeed", at = @At("RETURN"), cancellable = true)
    private void cem$applyEfficiencyTickBoost(CallbackInfoReturnable<Integer> cir) {
        BlockEntityBehaviour behaviour = (BlockEntityBehaviour) (Object) this;
        SmartBlockEntity blockEntity = behaviour.blockEntity;
        if (blockEntity == null || blockEntity.getLevel() == null) return;
        if (!(blockEntity instanceof EnchantableBlockEntity enchantable)) return;

        var efficiency = blockEntity.getLevel().holderLookup(Registries.ENCHANTMENT).get(Enchantments.EFFICIENCY).orElse(null);
        if (efficiency == null) return;
        int enchantLevel = enchantable.getEnchantmentLevel(efficiency);
        if (enchantLevel <= 0) return;

        int baseTickSpeed = cir.getReturnValue();
        if (baseTickSpeed <= 0) return;
        int boostedTickSpeed = Math.max(baseTickSpeed + 1, (int) Math.floor(baseTickSpeed * (1f + (enchantLevel * 0.2f))));
        cir.setReturnValue(boostedTickSpeed);
    }
}
