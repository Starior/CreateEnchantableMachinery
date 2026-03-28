package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import io.github.cotrin8672.cem.util.EnchantedDropHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WrenchItem.class, remap = false)
public class WrenchItemEnchantmentsMixin {
    @Inject(method = "onItemUseOnOther", at = @At("HEAD"), cancellable = true)
    private void cem$preserveEnchantmentsInWrenchPickup(
            UseOnContext context,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        var player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (!(world instanceof ServerLevel serverLevel)) {
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
        AllSoundEvents.WRENCH_REMOVE.playOnServer(world, pos, 1, Create.RANDOM.nextFloat() * .5f + .5f);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
