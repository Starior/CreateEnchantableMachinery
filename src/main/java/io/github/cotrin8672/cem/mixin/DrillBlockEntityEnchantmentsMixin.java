package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.util.EnchantedItemFactory;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DrillBlockEntity.class, remap = false)
public abstract class DrillBlockEntityEnchantmentsMixin {
    @Inject(method = "onBlockBroken", at = @At("HEAD"), cancellable = true)
    private void cem$applySilkFortuneDrops(BlockState stateToBreak, CallbackInfo ci) {
        DrillBlockEntity self = (DrillBlockEntity) (Object) this;
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;
        if (self.getLevel() == null || stateToBreak == null) return;

        // Keep Create's dedicated cobble-gen optimization path unchanged.
        if (self.optimiseCobbleGen(stateToBreak)) return;

        Level level = self.getLevel();
        BlockPos breakingPos = ((BlockBreakingKineticBlockEntityAccessor) self).cem$getBreakingPos();
        Vec3 spawnPos = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), level.random, .125f);

        BlockHelper.destroyBlockAs(
                level,
                breakingPos,
                null,
                EnchantedItemFactory.INSTANCE.getBreakingToolItemStack(enchantable.getEnchantments(), stateToBreak),
                1f,
                (ItemStack stack) -> {
                    if (stack.isEmpty()) return;
                    if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) return;
                    if (level.restoringBlockSnapshots) return;

                    ItemEntity entity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, stack);
                    entity.setDefaultPickUpDelay();
                    entity.setDeltaMovement(Vec3.ZERO);
                    level.addFreshEntity(entity);
                }
        );
        ci.cancel();
    }
}
