package io.github.cotrin8672.cem.mixin;

import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AbstractBlockEntityVisual.class, remap = false)
public interface AbstractBlockEntityVisualInvoker {
    @Invoker("getVisualPosition")
    BlockPos cem$invokeGetVisualPosition();

    @Invoker("relight")
    void cem$invokeRelight(FlatLit... relight);
}
