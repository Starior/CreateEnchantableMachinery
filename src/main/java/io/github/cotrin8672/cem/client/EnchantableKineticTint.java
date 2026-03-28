package io.github.cotrin8672.cem.client;

import com.simibubi.create.content.kinetics.KineticDebugger;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticEffectHandler;
import io.github.cotrin8672.cem.config.CemConfig;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.mixin.KineticBlockEntityEffectsAccessor;
import io.github.cotrin8672.cem.mixin.KineticEffectHandlerAccessor;
import dev.engine_room.flywheel.lib.instance.ColoredLitInstance;
import net.createmod.catnip.theme.Color;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Soft blue-violet vertex tint for enchanted machinery (same idea as Create overstress tint).
 * RGB/mix are taken from client {@link CemConfig} via a cached {@link Color} so render paths do not
 * read the config spec every frame. Cache is refreshed on initial config load, client setup, and
 * {@link net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn} (re-entering a world).
 * It is not refreshed on {@link net.neoforged.fml.event.config.ModConfigEvent.Reloading}; after editing
 * tint in the config screen, re-enter the world (or restart the game) to see changes everywhere.
 * Skipped while kinetic debugger is active or overstress color is showing.
 */
public final class EnchantableKineticTint {

    private static volatile Color cachedEnchantTint;

    private EnchantableKineticTint() {}

    /** Latest tint from client config; recomputed by {@link #refreshCachedEnchantTint()}. */
    public static Color enchantTintColor() {
        Color c = cachedEnchantTint;
        if (c == null) {
            refreshCachedEnchantTint();
            c = cachedEnchantTint;
        }
        return c;
    }

    /** Call after client config load/reload so visuals pick up new values without per-frame config reads. */
    public static void refreshCachedEnchantTint() {
        CemConfig cfg = CemConfig.CONFIG;
        Color magic = new Color(
                cfg.getEnchantTintRed().get().floatValue(),
                cfg.getEnchantTintGreen().get().floatValue(),
                cfg.getEnchantTintBlue().get().floatValue(),
                1f);
        cachedEnchantTint = Color.WHITE.mixWith(magic, cfg.getEnchantTintMix().get().floatValue());
    }

    /**
     * Vertex color for Flywheel {@link ColoredLitInstance} ({@code OrientedInstance}, etc.).
     * Catnip {@link Color} channels may be 0-255 ints or 0-1 floats depending on source; pick the correct
     * {@link ColoredLitInstance#color(float, float, float)} inputs.
     */
    public static void applyFlywheelTint(KineticBlockEntity be, ColoredLitInstance instance) {
        if (appliesToKinetic(be)) {
            applyCatnipColorToColoredLit(enchantTintColor(), instance);
        } else {
            instance.color(1f, 1f, 1f);
        }
    }

    private static void applyCatnipColorToColoredLit(Color c, ColoredLitInstance instance) {
        float r = c.getRed();
        float g = c.getGreen();
        float b = c.getBlue();
        if (r > 1f || g > 1f || b > 1f) {
            instance.color(r / 255f, g / 255f, b / 255f);
        } else {
            instance.color(r, g, b);
        }
    }

    public static boolean appliesToKinetic(KineticBlockEntity be) {
        if (!(be instanceof EnchantableBlockEntity e) || e.getEnchantments().isEmpty()) {
            return false;
        }
        if (KineticDebugger.isActive()) {
            return false;
        }
        return getOverStressedEffect(be) == 0f;
    }

    /** Non-kinetic enchantable blocks (harvester, spout, …): no overstress field. */
    public static boolean appliesToBlockEntity(BlockEntity be) {
        if (!(be instanceof EnchantableBlockEntity e) || e.getEnchantments().isEmpty()) {
            return false;
        }
        if (be instanceof KineticBlockEntity k) {
            if (KineticDebugger.isActive()) {
                return false;
            }
            return getOverStressedEffect(k) == 0f;
        }
        return true;
    }

    private static float getOverStressedEffect(KineticBlockEntity be) {
        KineticEffectHandler effects = ((KineticBlockEntityEffectsAccessor) (Object) be).cem$getEffects();
        return ((KineticEffectHandlerAccessor) (Object) effects).cem$getOverStressedEffect();
    }
}
