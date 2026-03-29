package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.util.EnchantableRules;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NBT hooks must target {@link BlockEntity}: {@code loadAdditional}/{@code saveAdditional} are not declared on
 * {@link HarvesterBlockEntity}, so a mixin on the subclass cannot resolve those method names at apply time.
 */
@Mixin(BlockEntity.class)
public abstract class HarvesterBlockEntityNbtMixin {
    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void cem$readHarvesterEnchantments(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getClass() != HarvesterBlockEntity.class) {
            return;
        }
        if (!EnchantableRules.isEnchantableBlockState(self.getBlockState())) {
            return;
        }
        ((EnchantableBlockEntity) self).readEnchantments(tag, registries);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void cem$writeHarvesterEnchantments(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getClass() != HarvesterBlockEntity.class) {
            return;
        }
        if (!EnchantableRules.isEnchantableBlockState(self.getBlockState())) {
            return;
        }
        ((EnchantableBlockEntity) self).writeEnchantments(tag, registries);
    }
}
