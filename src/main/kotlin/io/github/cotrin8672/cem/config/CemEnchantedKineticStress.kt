package io.github.cotrin8672.cem.config

import net.createmod.catnip.config.ConfigBase
import net.neoforged.neoforge.common.ModConfigSpec

class CemEnchantedKineticStress : ConfigBase() {
    lateinit var enable: ModConfigSpec.BooleanValue
    lateinit var impactCoeffPerLevel: ModConfigSpec.ConfigValue<Double>
    lateinit var capacityCoeffPerLevel: ModConfigSpec.ConfigValue<Double>

    override fun registerAll(builder: ModConfigSpec.Builder) {
        builder.comment(
            "Multiply Create's already-resolved stress impact/capacity (BlockStressValues, including other mods) by",
            "(1 + EfficiencyLevel * coefficient) for blocks in createenchantablemachinery:enchantable_block_entities with CEM enchant sync. Only Efficiency affects this layer."
        ).push("enchantedKineticStress")
        enable = builder
            .comment("Master switch for Efficiency-based stress scaling on enchanted kinetic blocks.")
            .define("enableEnchantedKineticStressScaling", true)
        impactCoeffPerLevel = builder
            .comment(
                "Consumer impact multiplier = 1 + efficiencyLevel * this. 0 leaves Create's base impact unchanged.",
                "Example: 0.05 with Efficiency III -> 1 + 3 * 0.05 = 1.15x impact."
            )
            .defineInRange("efficiencyStressImpactCoefficientPerLevel", 0.05, 0.0, 10.0)
        capacityCoeffPerLevel = builder
            .comment(
                "Source capacity multiplier = 1 + efficiencyLevel * this. 0 leaves Create's base capacity unchanged."
            )
            .defineInRange("efficiencyStressCapacityCoefficientPerLevel", 0.0, 0.0, 10.0)
        builder.pop()
    }

    override fun getName(): String {
        return "enchantedKineticStress"
    }
}
