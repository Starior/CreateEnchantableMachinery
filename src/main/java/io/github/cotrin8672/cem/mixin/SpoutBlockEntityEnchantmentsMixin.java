package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SpoutBlockEntity.class, remap = false)
public abstract class SpoutBlockEntityEnchantmentsMixin {
    @Inject(method = "whenItemHeld", at = @At("RETURN"))
    private void cem$applyEnchantableFillingDurationToItems(
            TransportedItemStack transported,
            TransportedItemStackHandlerBehaviour handler,
            CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir
    ) {
        this.cem$applyEnchantableFillingDuration(false);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void cem$applyEnchantableFillingDurationToBlocks(CallbackInfo ci) {
        this.cem$applyEnchantableFillingDuration(true);
    }

    private void cem$applyEnchantableFillingDuration(boolean fromTick) {
        SpoutBlockEntity self = (SpoutBlockEntity) (Object) this;
        if (fromTick) {
            // Block spouting starts at 20 and decrements in the same tick; adjust right after that first decrement.
            if (self.customProcess == null || self.processingTicks != SpoutBlockEntity.FILLING_TIME - 1) return;
        } else if (self.processingTicks != SpoutBlockEntity.FILLING_TIME) {
            return;
        }
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;
        if (self.getLevel() == null) return;

        var efficiency = self.getLevel().holderLookup(Registries.ENCHANTMENT).get(Enchantments.EFFICIENCY).orElse(null);
        if (efficiency == null) return;
        int enchantLevel = enchantable.getEnchantmentLevel(efficiency);
        if (enchantLevel <= 0) return;

        int enchantedFillingTime = Mth.clamp(SpoutBlockEntity.FILLING_TIME - (2 * enchantLevel), 1, SpoutBlockEntity.FILLING_TIME);
        self.processingTicks = fromTick ? Math.max(0, enchantedFillingTime - 1) : enchantedFillingTime;
    }
}
