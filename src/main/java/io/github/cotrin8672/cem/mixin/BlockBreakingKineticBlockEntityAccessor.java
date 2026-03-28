package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockBreakingKineticBlockEntity.class, remap = false)
public interface BlockBreakingKineticBlockEntityAccessor {
    @Accessor("breakingPos")
    BlockPos cem$getBreakingPos();
}
