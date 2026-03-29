package io.github.cotrin8672.cem.config

import io.github.cotrin8672.cem.registrate.CemStress
import net.createmod.catnip.config.ConfigBase

class CemKinetics : ConfigBase() {
    val stressValues: CemStress = nested(1, ::CemStress, "Fine tune the kinetic stats of individual components")
    val enchantedKineticStress: CemEnchantedKineticStress =
        nested(2, ::CemEnchantedKineticStress, "Efficiency scales resolved stress on tagged enchantable kinetic blocks")

    override fun getName(): String {
        return "kinetics"
    }
}
