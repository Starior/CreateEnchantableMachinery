package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import io.github.cotrin8672.cem.util.EnchantedItemFactory;
import io.github.cotrin8672.cem.util.AbstractBlockBreakQueueExtensionKt;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SawMovementBehaviour.class, remap = false)
public abstract class SawMovementBehaviourEnchantmentsMixin {
    @Inject(method = "onBlockBroken", at = @At("HEAD"), cancellable = true)
    private void cem$applySilkFortuneToTreeDrops(
            MovementContext context,
            BlockPos pos,
            BlockState brokenState,
            CallbackInfo ci
    ) {
        if (context == null || pos == null || brokenState == null) return;
        if (brokenState.is(BlockTags.LEAVES)) return;

        var tool = EnchantedItemFactory.INSTANCE.getBreakingToolItemStack(context.blockEntityData, context, brokenState);
        SawMovementBehaviour self = (SawMovementBehaviour) (Object) this;

        var dynamicTree = TreeCutter.findDynamicTree(brokenState.getBlock(), pos);
        if (dynamicTree.isPresent()) {
            AbstractBlockBreakQueueExtensionKt.destroyBlocks(
                    dynamicTree.get(),
                    context.world,
                    tool,
                    (dropPos, stack) -> {
                        self.dropItemFromCutTree(context, dropPos, stack);
                        return Unit.INSTANCE;
                    }
            );
            ci.cancel();
            return;
        }

        AbstractBlockBreakQueueExtensionKt.destroyBlocks(
                TreeCutter.findTree(context.world, pos, brokenState),
                context.world,
                tool,
                (dropPos, stack) -> {
                    self.dropItemFromCutTree(context, dropPos, stack);
                    return Unit.INSTANCE;
                }
        );
        ci.cancel();
    }
}
