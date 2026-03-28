package io.github.cotrin8672.cem.config

import kotlin.jvm.JvmField
import net.neoforged.neoforge.common.ModConfigSpec

class CemConfig private constructor(builder: ModConfigSpec.Builder) {

    init {
        builder
            .comment(
                "Vertex tint for enchanted kinetic machinery (client only).",
                "Same idea as Create's overstress coloring on shafts and wheels.",
            )
            .translation("createenchantablemachinery.configuration.enchantmentTint")
            .push("enchantmentTint")
    }

    val enchantTintRed: ModConfigSpec.ConfigValue<Double> = builder
        .comment("Red channel (0.0 - 1.0) of the tint color before mixing with white.")
        .translation("createenchantablemachinery.configuration.enchantTintRed")
        .defineInRange("enchantTintRed", 0.58, 0.0, 1.0)

    val enchantTintGreen: ModConfigSpec.ConfigValue<Double> = builder
        .comment("Green channel (0.0 - 1.0) of the tint color before mixing with white.")
        .translation("createenchantablemachinery.configuration.enchantTintGreen")
        .defineInRange("enchantTintGreen", 0.42, 0.0, 1.0)

    val enchantTintBlue: ModConfigSpec.ConfigValue<Double> = builder
        .comment("Blue channel (0.0 - 1.0) of the tint color before mixing with white.")
        .translation("createenchantablemachinery.configuration.enchantTintBlue")
        .defineInRange("enchantTintBlue", 0.94, 0.0, 1.0)

    val enchantTintMix: ModConfigSpec.ConfigValue<Double> = builder
        .comment(
            "Blend strength: how much the tint is mixed with white (0 = invisible, 1 = full color).",
        )
        .translation("createenchantablemachinery.configuration.enchantTintMix")
        .defineInRange("enchantTintMix", 0.34, 0.0, 1.0)

    init {
        builder.pop()
        builder.build()
    }

    companion object {
        private val pair = ModConfigSpec.Builder().configure(::CemConfig)

        @JvmField
        val CONFIG: CemConfig = pair.left

        @JvmField
        val CONFIG_SPEC: ModConfigSpec = pair.right
    }
}
