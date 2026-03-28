package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import com.simibubi.create.foundation.utility.BlockHelper;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.util.AbstractBlockBreakQueueExtensionKt;
import io.github.cotrin8672.cem.util.EnchantedItemFactory;
import kotlin.Unit;
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

@Mixin(value = SawBlockEntity.class, remap = false)
public abstract class SawBlockEntityWorldDropsMixin {
    @Inject(method = "onBlockBroken", at = @At("HEAD"), cancellable = true)
    private void cem$applySilkFortuneDrops(BlockState stateToBreak, CallbackInfo ci) {
        SawBlockEntity self = (SawBlockEntity) (Object) this;
        if (!(self instanceof EnchantableBlockEntity enchantable)) return;
        if (self.getLevel() == null || stateToBreak == null) return;

        Level level = self.getLevel();
        BlockPos breakingPos = ((BlockBreakingKineticBlockEntityAccessor) self).cem$getBreakingPos();
        ItemStack tool = EnchantedItemFactory.INSTANCE.getBreakingToolItemStack(enchantable.getEnchantments(), stateToBreak);

        var dynamicTree = TreeCutter.findDynamicTree(stateToBreak.getBlock(), breakingPos);
        if (dynamicTree.isPresent()) {
            AbstractBlockBreakQueueExtensionKt.destroyBlocks(dynamicTree.get(), level, tool, (dropPos, stack) -> {
                self.dropItemFromCutTree(dropPos, stack);
                return Unit.INSTANCE;
            });
            ci.cancel();
            return;
        }

        Vec3 spawnPos = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), level.random, .125f);
        BlockHelper.destroyBlockAs(level, breakingPos, null, tool, 1f, stack -> {
            if (stack.isEmpty()) return;
            if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) return;
            if (level.restoringBlockSnapshots) return;

            ItemEntity itemEntity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, stack);
            itemEntity.setDefaultPickUpDelay();
            itemEntity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(itemEntity);
        });

        AbstractBlockBreakQueueExtensionKt.destroyBlocks(
                TreeCutter.findTree(level, breakingPos, stateToBreak),
                level,
                tool,
                (dropPos, stack) -> {
                    self.dropItemFromCutTree(dropPos, stack);
                    return Unit.INSTANCE;
                }
        );
        ci.cancel();
    }
}
