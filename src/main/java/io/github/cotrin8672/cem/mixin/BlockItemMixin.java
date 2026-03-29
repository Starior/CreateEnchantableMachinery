package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.util.EnchantableRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
    public BlockItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "updateCustomBlockEntityTag",
            at = @At("RETURN")
    )
    private void cem$applyEnchantmentsToPlacedBlockEntity(
            BlockPos pos,
            Level level,
            Player player,
            ItemStack stack,
            BlockState state,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!stack.isEnchanted()) return;
        if (!EnchantableRules.isEnchantableItemStack(stack)) return;
        if (!EnchantableRules.isEnchantableBlockState(state)) return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof EnchantableBlockEntity enchantableBlockEntity)) return;

        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        enchantableBlockEntity.setEnchantment(enchantments);
        enchantableBlockEntity.setSourceItem(stack.getItem());
        blockEntity.setChanged();
        if (level instanceof ServerLevel && blockEntity instanceof SyncedBlockEntity synced) {
            synced.notifyUpdate();
        }
    }
}
