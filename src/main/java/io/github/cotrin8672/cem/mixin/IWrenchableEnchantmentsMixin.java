package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import io.github.cotrin8672.cem.util.EnchantedDropHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IWrenchable.class, remap = false)
public interface IWrenchableEnchantmentsMixin {
    @Inject(method = "onSneakWrenched", at = @At("HEAD"), cancellable = true)
    private void cem$preserveEnchantmentsInWrenchDrop(
            BlockState state,
            UseOnContext context,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        var player = context.getPlayer();

        if (!(world instanceof ServerLevel serverLevel)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (player != null && !player.isCreative()) {
            var drops = Block.getDrops(state, serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand());
            var remapped = EnchantedDropHelper.remapWrenchDrops(world, pos, state, drops);
            remapped.forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }

        state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
        world.destroyBlock(pos, false);
        IWrenchable.playRemoveSound(world, pos);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
