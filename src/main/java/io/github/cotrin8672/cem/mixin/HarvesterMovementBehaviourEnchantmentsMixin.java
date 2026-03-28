package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.github.cotrin8672.cem.util.EnchantedItemFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HarvesterMovementBehaviour.class, remap = false)
public abstract class HarvesterMovementBehaviourEnchantmentsMixin {
    @Inject(method = "visitNewPosition", at = @At("HEAD"), cancellable = true)
    private void cem$applyEnchantableHarvestDrops(MovementContext context, BlockPos pos, CallbackInfo ci) {
        if (context == null || context.world == null || context.world.isClientSide) return;
        HarvesterMovementBehaviour self = (HarvesterMovementBehaviour) (Object) this;

        Level world = context.world;
        BlockState stateVisited = world.getBlockState(pos);
        boolean nonCropCuttable = false;
        if (!self.isValidCrop(world, pos, stateVisited)) {
            if (self.isValidOther(world, pos, stateVisited)) nonCropCuttable = true;
            else return;
        }

        ItemStack item = EnchantedItemFactory.INSTANCE.getBreakingToolItemStack(context.blockEntityData, context, stateVisited);
        float effectChance = 1f;
        if (stateVisited.is(BlockTags.LEAVES)) {
            var enchantments = EnchantedItemFactory.INSTANCE.getEnchantments(context.blockEntityData, context);
            item = EnchantedItemFactory.INSTANCE.getToolItemStack(Items.SHEARS, enchantments);
            effectChance = .45f;
        }

        MutableBoolean seedSubtracted = new MutableBoolean(nonCropCuttable);
        BlockHelper.destroyBlockAs(world, pos, null, item, effectChance, stack -> {
            if (AllConfigs.server().kinetics.harvesterReplants.get() && !seedSubtracted.booleanValue()
                    && ItemHelper.sameItem(stack, new ItemStack(stateVisited.getBlock()))) {
                stack.shrink(1);
                seedSubtracted.setTrue();
            }
            if (!stack.isEmpty()) self.collectOrDropItem(context, stack);
        });

        BlockState cutCrop = cem$cutCrop(world, pos, stateVisited);
        world.setBlockAndUpdate(pos, cutCrop.canSurvive(world, pos) ? cutCrop : Blocks.AIR.defaultBlockState());
        ci.cancel();
    }

    private static BlockState cem$cutCrop(Level world, BlockPos pos, BlockState state) {
        if (!AllConfigs.server().kinetics.harvesterReplants.get()) {
            return state.getFluidState().isEmpty() ? Blocks.AIR.defaultBlockState() : state.getFluidState().createLegacyBlock();
        }

        var block = state.getBlock();
        if (block instanceof CropBlock cropBlock) return cropBlock.getStateForAge(0);
        if (block == Blocks.SWEET_BERRY_BUSH) return state.setValue(BlockStateProperties.AGE_3, 1);
        if (block == Blocks.SUGAR_CANE || block instanceof GrowingPlantBlock) {
            return state.getFluidState().isEmpty() ? Blocks.AIR.defaultBlockState() : state.getFluidState().createLegacyBlock();
        }
        if (state.getCollisionShape(world, pos).isEmpty() || block instanceof CocoaBlock) {
            for (var property : state.getProperties()) {
                if (!(property instanceof IntegerProperty integerProperty)) continue;
                if (!integerProperty.getName().equals(BlockStateProperties.AGE_1.getName())) continue;
                return state.setValue(integerProperty, 0);
            }
        }

        return state.getFluidState().isEmpty() ? Blocks.AIR.defaultBlockState() : state.getFluidState().createLegacyBlock();
    }
}
