package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.actors.plough.PloughBlock;
import io.github.cotrin8672.cem.registry.BlockEntityRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Vanilla Create {@link PloughBlock} has no block entity, so {@link BlockItemMixin} could never persist
 * enchantments. Attach {@link io.github.cotrin8672.cem.content.block.plough.EnchantablePloughBlockEntity}
 * (Create Encased ploughs are also {@link PloughBlock} instances).
 */
@Mixin(value = PloughBlock.class, remap = false)
public abstract class PloughBlockMixin implements EntityBlock {
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistration.INSTANCE.getENCHANTABLE_MECHANICAL_PLOUGH().get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}
