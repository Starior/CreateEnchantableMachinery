package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.actors.plough.PloughBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@link io.github.cotrin8672.cem.registry.BlockEntityRegistration#ENCHANTABLE_MECHANICAL_PLOUGH} lists Create's
 * mechanical plough in {@code validBlocks}. Allow any {@link PloughBlock} (Create + Create Encased variants) to use this type.
 */
@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypePloughIsValidMixin {
    private static final ResourceLocation CEM_ENCHANTABLE_PLOUGH_BE =
            ResourceLocation.fromNamespaceAndPath("createenchantablemachinery", "enchantable_plough");

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void cem$acceptAnyPloughBlock(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockEntityType<?> self = (BlockEntityType<?>) (Object) this;
        ResourceLocation key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(self);
        if (key == null || !key.equals(CEM_ENCHANTABLE_PLOUGH_BE)) {
            return;
        }
        if (state.getBlock() instanceof PloughBlock) {
            cir.setReturnValue(true);
        }
    }
}
