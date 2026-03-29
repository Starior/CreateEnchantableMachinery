package io.github.cotrin8672.cem.mixin;

import io.github.cotrin8672.cem.util.EnchantableRules;
import io.github.cotrin8672.cem.util.MachineEnchantRules;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IItemExtension.class, remap = false)
public interface IItemExtensionEnchantmentsMixin {
    @Inject(method = "supportsEnchantment", at = @At("RETURN"), cancellable = true)
    private void cem$applyPerClassEnchantmentRules(
            ItemStack stack,
            Holder<Enchantment> enchantment,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!EnchantableRules.isEnchantableItemStack(stack)) return;
        boolean allowed = MachineEnchantRules.isEnchantmentAllowed(stack, enchantment, cir.getReturnValue());
        cir.setReturnValue(allowed);
    }
}

