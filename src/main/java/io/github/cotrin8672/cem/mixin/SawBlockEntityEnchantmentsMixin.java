package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SawBlockEntity.class, remap = false)
public abstract class SawBlockEntityEnchantmentsMixin {
    private static final float CEM_MIN_RECIPE_DURATION = 10f;

    @Inject(method = "start", at = @At("TAIL"))
    private void cem$applyEfficiencyToCuttingDuration(ItemStack inserted, CallbackInfo ci) {
        SawBlockEntity self = (SawBlockEntity) (Object) this;
        if (self.getLevel() == null || self.getLevel().isClientSide()) return;
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;
        if (self.inventory.recipeDuration <= 0f || self.inventory.remainingTime <= 0f) return;

        var efficiency = self.getLevel().holderLookup(Registries.ENCHANTMENT).get(Enchantments.EFFICIENCY).orElse(null);
        if (efficiency == null) return;
        int enchantLevel = enchantable.getEnchantmentLevel(efficiency);
        if (enchantLevel <= 0) return;

        float modifier = Math.max(0.1f, 1f - (enchantLevel * 0.1f));
        float adjustedDuration = Math.max(CEM_MIN_RECIPE_DURATION, self.inventory.recipeDuration * modifier);
        self.inventory.recipeDuration = adjustedDuration;
        self.inventory.remainingTime = Math.min(self.inventory.remainingTime, adjustedDuration);
        self.sendData();
    }
}
