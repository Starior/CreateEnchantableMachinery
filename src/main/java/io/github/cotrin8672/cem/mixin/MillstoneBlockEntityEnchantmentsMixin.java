package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MillstoneBlockEntity.class, remap = false)
public abstract class MillstoneBlockEntityEnchantmentsMixin {
    @Inject(method = "getProcessingSpeed", at = @At("RETURN"), cancellable = true)
    private void cem$applyEfficiencyMultiplier(CallbackInfoReturnable<Integer> cir) {
        MillstoneBlockEntity self = (MillstoneBlockEntity) (Object) this;
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;
        if (self.getLevel() == null) return;

        var holder = self.getLevel().holderLookup(Registries.ENCHANTMENT).get(Enchantments.EFFICIENCY).orElse(null);
        if (holder == null) return;
        int level = enchantable.getEnchantmentLevel(holder);
        if (level <= 0) return;
        cir.setReturnValue((int) (cir.getReturnValue() * (1f + (level * 0.2f))));
    }
}
