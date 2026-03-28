package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour;
import com.simibubi.create.content.contraptions.actors.plough.PloughMovementBehaviour;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import io.github.cotrin8672.cem.util.EnchantedItemFactory;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockBreakingMovementBehaviour.class, remap = false)
public abstract class BlockBreakingMovementBehaviourDrillDropsMixin {
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void cem$applyDrillSilkFortuneDrops(MovementContext context, BlockPos breakingPos, CallbackInfo ci) {
        BlockBreakingMovementBehaviour self = (BlockBreakingMovementBehaviour) (Object) this;
        if (!(self instanceof DrillMovementBehaviour)
                && !(self instanceof SawMovementBehaviour)
                && !(self instanceof PloughMovementBehaviour)) {
            return;
        }
        if (context == null || context.world == null) return;

        var state = context.world.getBlockState(breakingPos);
        BlockHelper.destroyBlockAs(
                context.world,
                breakingPos,
                null,
                EnchantedItemFactory.INSTANCE.getBreakingToolItemStack(context.blockEntityData, context, state),
                1f,
                stack -> ((MovementBehaviour) self).collectOrDropItem(context, stack)
        );
        ci.cancel();
    }
}
