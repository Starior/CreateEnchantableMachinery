package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import io.github.cotrin8672.cem.util.CemCrushingControllerDataAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = CrushingWheelControllerBlockEntity.class, remap = false)
public abstract class CrushingWheelControllerEnchantmentsMixin implements CemCrushingControllerDataAccess {
    @Unique
    private float cem$effectiveFortuneLevel = 0f;
    @Unique
    private int cem$silkTouchWheelCount = 0;

    @Override
    public float cem$getEffectiveFortuneLevel() {
        return this.cem$effectiveFortuneLevel;
    }

    @Override
    public void cem$setEffectiveFortuneLevel(float value) {
        this.cem$effectiveFortuneLevel = value;
    }

    @Override
    public int cem$getSilkTouchWheelCount() {
        return this.cem$silkTouchWheelCount;
    }

    @Override
    public void cem$setSilkTouchWheelCount(int value) {
        this.cem$silkTouchWheelCount = Math.max(0, Math.min(2, value));
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void cem$readExtendedEnchantState(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        this.cem$effectiveFortuneLevel = tag.getFloat("EffectiveFortuneLevel");
        this.cem$silkTouchWheelCount = Math.max(0, Math.min(2, tag.getInt("SilkTouchWheelCount")));
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void cem$writeExtendedEnchantState(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        tag.putFloat("EffectiveFortuneLevel", this.cem$effectiveFortuneLevel);
        tag.putInt("SilkTouchWheelCount", this.cem$silkTouchWheelCount);
    }

    @Inject(method = "applyRecipe", at = @At("HEAD"), cancellable = true)
    private void cem$applyFortuneAndSilkToRecipe(CallbackInfo ci) {
        if (this.cem$effectiveFortuneLevel <= 0f && this.cem$silkTouchWheelCount <= 0) return;

        CrushingWheelControllerBlockEntity self = (CrushingWheelControllerBlockEntity) (Object) this;
        var recipe = self.findRecipe();
        if (recipe.isEmpty()) return;

        var list = new ArrayList<ItemStack>();
        ItemStack inputTemplate = self.inventory.getStackInSlot(0).copyWithCount(1);
        int rolls = self.inventory.getStackInSlot(0).getCount();
        self.inventory.clear();

        double fortuneLevel = this.cem$effectiveFortuneLevel;
        double silkTouchReturnChance = switch (this.cem$silkTouchWheelCount) {
            case 1 -> 0.05;
            case 2 -> 0.10;
            default -> 0.0;
        };

        for (int roll = 0; roll < rolls; roll++) {
            int times;
            if (fortuneLevel <= 0.0) {
                times = 1;
            } else {
                double vanillaAvg = 1.0 / (fortuneLevel + 2.0) + (fortuneLevel + 1.0) / 2.0;
                double targetMean = 0.8 * vanillaAvg;
                double extraMean = Math.max(0.0, targetMean - 1.0);
                if (extraMean <= 0.0) {
                    times = 1;
                } else {
                    int floorEM = (int) Math.floor(extraMean);
                    int ceilEM = (int) Math.ceil(extraMean);
                    double frac = extraMean - floorEM;
                    int extra = self.getLevel().random.nextDouble() < frac ? ceilEM : floorEM;
                    times = 1 + extra;
                }
            }

            for (int i = 0; i < times; i++) {
                for (ItemStack stack : recipe.get().value().rollResults(self.getLevel().random)) {
                    ItemHelper.addToList(stack, list);
                }
            }

            if (silkTouchReturnChance > 0.0 && self.getLevel().random.nextDouble() < silkTouchReturnChance) {
                ItemHelper.addToList(inputTemplate.copy(), list);
            }
        }
        if (inputTemplate.hasCraftingRemainingItem()) {
            ItemHelper.addToList(inputTemplate.getCraftingRemainingItem(), list);
        }

        int slot = 0;
        while (slot < list.size() && slot + 1 < self.inventory.getSlots()) {
            self.inventory.setStackInSlot(slot + 1, list.get(slot));
            slot++;
        }
        ci.cancel();
    }
}
