package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.util.CemCrushingControllerDataAccess;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CrushingWheelBlock.class, remap = false)
public abstract class CrushingWheelBlockEnchantmentsMixin {
    @Inject(method = "updateControllers", at = @At("TAIL"))
    private void cem$updateControllerEnchantments(BlockState state, Level level, net.minecraft.core.BlockPos pos, Direction side, CallbackInfo ci) {
        if (side.getAxis() == state.getValue(CrushingWheelBlock.AXIS)) return;
        if (level == null) return;

        net.minecraft.core.BlockPos controllerPos = pos.relative(side);
        net.minecraft.core.BlockPos otherWheelPos = pos.relative(side, 2);

        var ownBe = level.getBlockEntity(pos);
        var otherBe = level.getBlockEntity(otherWheelPos);
        var controllerBe = level.getBlockEntity(controllerPos);
        if (!(ownBe instanceof EnchantableBlockEntity ownEnchantable)) return;
        if (!(otherBe instanceof EnchantableBlockEntity otherEnchantable)) return;
        if (!(controllerBe instanceof EnchantableBlockEntity controllerEnchantable)) return;

        boolean mayHaveAnyEnchantments = !ownEnchantable.getEnchantments().isEmpty()
                || !otherEnchantable.getEnchantments().isEmpty();
        float effectiveFortuneRounded = 0f;
        int silkTouchWheelCount = 0;
        ItemEnchantments itemEnchantments = ItemEnchantments.EMPTY;

        if (mayHaveAnyEnchantments) {
            var enchantmentLookup = ownBe.getLevel().holderLookup(Registries.ENCHANTMENT);
            var efficiency = enchantmentLookup.getOrThrow(Enchantments.EFFICIENCY);
            var fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);
            var silkTouch = enchantmentLookup.getOrThrow(Enchantments.SILK_TOUCH);

            var mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

            int ownEfficiencyLevel = ownEnchantable.getEnchantmentLevel(efficiency);
            int otherEfficiencyLevel = otherEnchantable.getEnchantmentLevel(efficiency);
            int totalEfficiencyLevel = ownEfficiencyLevel + otherEfficiencyLevel;
            if (totalEfficiencyLevel > 0) mutable.set(efficiency, totalEfficiencyLevel);

            int ownFortuneLevel = ownEnchantable.getEnchantmentLevel(fortune);
            int otherFortuneLevel = otherEnchantable.getEnchantmentLevel(fortune);
            double effectiveFortune = (ownFortuneLevel + otherFortuneLevel) / 2.0;
            effectiveFortuneRounded = (float) (Math.round(effectiveFortune * 100) / 100.0);
            int displayFortuneLevel = Math.max(0, Math.min(3, (int) effectiveFortuneRounded));
            if (displayFortuneLevel > 0) mutable.set(fortune, displayFortuneLevel);

            int ownSilkTouchLevel = ownEnchantable.getEnchantmentLevel(silkTouch);
            int otherSilkTouchLevel = otherEnchantable.getEnchantmentLevel(silkTouch);
            silkTouchWheelCount = (ownSilkTouchLevel > 0 ? 1 : 0) + (otherSilkTouchLevel > 0 ? 1 : 0);
            if (silkTouchWheelCount > 0) mutable.set(silkTouch, 1);

            itemEnchantments = mutable.toImmutable();
        }

        controllerEnchantable.setEnchantment(itemEnchantments);
        controllerEnchantable.setSourceItem(null);
        if (controllerBe instanceof CemCrushingControllerDataAccess extraData) {
            extraData.cem$setEffectiveFortuneLevel(effectiveFortuneRounded);
            extraData.cem$setSilkTouchWheelCount(silkTouchWheelCount);
        }

        var componentMap = net.minecraft.core.component.DataComponentMap.builder()
                .addAll(controllerBe.components())
                .set(DataComponents.ENCHANTMENTS, itemEnchantments)
                .build();
        controllerBe.setComponents(componentMap);
        controllerBe.setChanged();
    }
}
