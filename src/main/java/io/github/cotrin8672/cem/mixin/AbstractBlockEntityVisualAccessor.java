package io.github.cotrin8672.cem.mixin;

import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Flywheel stores {@code blockEntity} on {@link AbstractBlockEntityVisual}, not on Create's rotating visual subclasses.
 * Mixin cannot @Shadow inherited fields that are not declared on the direct target class in production.
 */
@Mixin(value = AbstractBlockEntityVisual.class, remap = false)
public interface AbstractBlockEntityVisualAccessor {
    @Accessor("blockEntity")
    BlockEntity cem$getBlockEntity();
}
