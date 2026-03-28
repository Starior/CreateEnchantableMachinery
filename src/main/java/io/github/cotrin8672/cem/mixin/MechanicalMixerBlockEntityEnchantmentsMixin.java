package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MechanicalMixerBlockEntity.class, remap = false)
public abstract class MechanicalMixerBlockEntityEnchantmentsMixin {
    @Unique
    private boolean cem$processingTicksAdjustedThisCycle = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void cem$applyEfficiencyToProcessingTicks(CallbackInfo ci) {
        MechanicalMixerBlockEntity self = (MechanicalMixerBlockEntity) (Object) this;
        if (self.getLevel() == null || self.getLevel().isClientSide()) return;

        if (!self.running || self.runningTicks != 20) {
            this.cem$processingTicksAdjustedThisCycle = false;
            return;
        }
        if (this.cem$processingTicksAdjustedThisCycle) return;
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;
        if (self.processingTicks <= 1) return;

        var efficiency = self.getLevel().holderLookup(Registries.ENCHANTMENT).get(Enchantments.EFFICIENCY).orElse(null);
        if (efficiency == null) return;
        int enchantLevel = enchantable.getEnchantmentLevel(efficiency);
        if (enchantLevel <= 0) return;

        double efficiencyModifier = 1d + (enchantLevel * 0.2d);
        int adjustedTicks = Mth.clamp((int) Math.ceil(self.processingTicks / efficiencyModifier), 1, 512);
        if (adjustedTicks < self.processingTicks) {
            self.processingTicks = adjustedTicks;
            self.sendData();
        }
        this.cem$processingTicksAdjustedThisCycle = true;
    }
}
