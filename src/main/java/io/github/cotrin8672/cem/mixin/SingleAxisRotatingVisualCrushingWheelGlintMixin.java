package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import io.github.cotrin8672.cem.client.EnchantableKineticTint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = SingleAxisRotatingVisual.class, remap = false)
public abstract class SingleAxisRotatingVisualCrushingWheelGlintMixin {
    @Unique
    private RotatingInstance cem$glintRotatingModel;

    @Inject(
        method = "<init>(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;FLnet/minecraft/core/Direction;Ldev/engine_room/flywheel/api/model/Model;)V",
        at = @At("TAIL")
    )
    private void cem$initCrushingWheelGlint(
        VisualizationContext context,
        KineticBlockEntity blockEntity,
        float partialTick,
        Direction from,
        Model model,
        CallbackInfo ci
    ) {
        if (!(blockEntity instanceof CrushingWheelBlockEntity)) {
            return;
        }

        Model glintModel = new BakedModelBuilder(AllPartialModels.CRUSHING_WHEEL.get())
            .materialFunc((group, material) -> Materials.GLINT)
            .build();

        BlockPos visualPos = ((AbstractBlockEntityVisualInvoker) (Object) this).cem$invokeGetVisualPosition();
        Direction.Axis axis = KineticBlockEntityVisual.rotationAxis(blockEntity.getBlockState());

        cem$glintRotatingModel = context.instancerProvider().instancer(AllInstanceTypes.ROTATING, glintModel)
            .createInstance()
            .rotateToFace(from, axis)
            .setup(blockEntity)
            .setPosition(visualPos);
        cem$glintRotatingModel.setChanged();
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void cem$updateCrushingWheelGlint(float pt, CallbackInfo ci) {
        if (cem$glintRotatingModel == null) {
            return;
        }

        BlockEntity be = ((AbstractBlockEntityVisualAccessor) (Object) this).cem$getBlockEntity();
        if (!(be instanceof KineticBlockEntity kinetic)) {
            return;
        }

        cem$glintRotatingModel.setup(kinetic).setChanged();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void cem$tickCrushingWheelGlint(SimpleTickableVisual.Context context, CallbackInfo ci) {
        if (cem$glintRotatingModel == null) {
            return;
        }

        BlockEntity be = ((AbstractBlockEntityVisualAccessor) (Object) this).cem$getBlockEntity();
        if (!(be instanceof KineticBlockEntity kinetic)) {
            return;
        }

        if (EnchantableKineticTint.appliesToKinetic(kinetic)) {
            cem$glintRotatingModel.color(255, 255, 255);
        } else {
            // Hide the glint overlay when the wheel has no enchantments.
            cem$glintRotatingModel.color(0, 0, 0);
        }
        cem$glintRotatingModel.setChanged();
    }

    @Inject(method = "updateLight", at = @At("TAIL"))
    private void cem$updateCrushingWheelGlintLight(float partialTick, CallbackInfo ci) {
        if (cem$glintRotatingModel == null) {
            return;
        }

        ((AbstractBlockEntityVisualInvoker) (Object) this).cem$invokeRelight(cem$glintRotatingModel);
    }

    @Inject(method = "_delete", at = @At("TAIL"))
    private void cem$deleteCrushingWheelGlint(CallbackInfo ci) {
        if (cem$glintRotatingModel != null) {
            cem$glintRotatingModel.delete();
            cem$glintRotatingModel = null;
        }
    }

    @Inject(method = "collectCrumblingInstances", at = @At("TAIL"))
    private void cem$collectCrushingWheelGlint(Consumer<Instance> consumer, CallbackInfo ci) {
        if (cem$glintRotatingModel != null) {
            consumer.accept(cem$glintRotatingModel);
        }
    }
}
