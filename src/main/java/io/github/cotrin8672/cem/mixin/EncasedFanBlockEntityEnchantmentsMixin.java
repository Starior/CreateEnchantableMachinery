package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.content.block.fan.EnchantableAirCurrent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EncasedFanBlockEntity.class, remap = false)
public abstract class EncasedFanBlockEntityEnchantmentsMixin {
    @Unique
    private int cem$lastEfficiencyLevel = Integer.MIN_VALUE;

    @Inject(method = "tick", at = @At("HEAD"))
    private void cem$replaceAirCurrentWithEnchantableVersion(CallbackInfo ci) {
        EncasedFanBlockEntity self = (EncasedFanBlockEntity) (Object) this;
        if (self.getLevel() == null) return;
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;

        var efficiency = self.getLevel().holderLookup(Registries.ENCHANTMENT).get(Enchantments.EFFICIENCY).orElse(null);
        if (efficiency == null) return;
        int enchantLevel = enchantable.getEnchantmentLevel(efficiency);

        if (enchantLevel <= 0) {
            if (self.airCurrent instanceof EnchantableAirCurrent) {
                self.airCurrent = new AirCurrent(self);
            }
            this.cem$lastEfficiencyLevel = 0;
            return;
        }

        if (!(self.airCurrent instanceof EnchantableAirCurrent) || this.cem$lastEfficiencyLevel != enchantLevel) {
            self.airCurrent = new EnchantableAirCurrent(self, enchantLevel);
            this.cem$lastEfficiencyLevel = enchantLevel;
        }
    }
}
