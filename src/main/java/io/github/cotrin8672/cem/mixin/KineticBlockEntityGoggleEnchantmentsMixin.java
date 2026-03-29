package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class KineticBlockEntityGoggleEnchantmentsMixin {
    @Inject(method = "addToGoggleTooltip", at = @At("RETURN"), cancellable = true)
    private void cem$appendEnchantmentsToGoggles(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir) {
        KineticBlockEntity self = (KineticBlockEntity) (Object) this;
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;
        if (enchantable.getEnchantments().isEmpty()) return;

        for (var entry : enchantable.getEnchantments().entrySet()) {
            CreateLang.builder()
                    .add(Enchantment.getFullname(entry.getKey(), entry.getIntValue()))
                    .forGoggles(tooltip, 1);
        }
        // Create API: true = goggle contribution is valid (IHaveGoggleInformation). GoggleOverlayRenderer only skips
        // the overlay when BOTH goggle and hover return false and both interfaces exist; empty tooltip still aborts
        // elsewhere. Base KineticBlockEntity returns false when stress lines are omitted; we must return true so a
        // tooltip that only lists enchantments still displays.
        cir.setReturnValue(true);
    }
}
