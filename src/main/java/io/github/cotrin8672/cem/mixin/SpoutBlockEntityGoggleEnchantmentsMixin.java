package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Vanilla {@link SpoutBlockEntity#addToGoggleTooltip} only forwards fluid info. Enchantments live on the BE via
 * {@link SmartBlockEntityEnchantmentsMixin} but were not shown until this mixin appends them.
 */
@Mixin(value = SpoutBlockEntity.class, remap = false)
public abstract class SpoutBlockEntityGoggleEnchantmentsMixin {
    @Inject(method = "addToGoggleTooltip", at = @At("RETURN"), cancellable = true)
    private void cem$appendSpoutEnchantmentsToGoggles(
            List<Component> tooltip,
            boolean isPlayerSneaking,
            CallbackInfoReturnable<Boolean> cir
    ) {
        SpoutBlockEntity self = (SpoutBlockEntity) (Object) this;
        if (!(self instanceof EnchantableBlockEntity enchantable) || enchantable.getEnchantments().isEmpty()) {
            return;
        }
        for (var entry : enchantable.getEnchantments().entrySet()) {
            CreateLang.builder()
                    .add(Enchantment.getFullname(entry.getKey(), entry.getIntValue()))
                    .forGoggles(tooltip, 1);
        }
        cir.setReturnValue(true);
    }
}
