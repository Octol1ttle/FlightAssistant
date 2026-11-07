package nl.enjarai.doabarrelroll.compat.flightassistant

//? if do-a-barrel-roll {
import net.minecraft.resources.ResourceLocation
import nl.enjarai.doabarrelroll.DoABarrelRoll
import nl.enjarai.doabarrelroll.api.event.ThrustEvents
import nl.enjarai.doabarrelroll.config.ModConfig
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import kotlin.math.sign

class DaBRThrustComputer(computers: ComputerBus) : Computer(computers), ThrustSource {
    override val priority: ThrustSource.Priority = ThrustSource.Priority.HIGH
    override val supportsReverse: Boolean = true

    override fun subscribeToEvents() {
        ThrustEvents.MODIFY_THRUST_INPUT.register({
            if (computers.data.automationsAllowed() && !computers.thrust.isDisabledOrFaulted()) {
                // TODO: config option *sigh*
                computers.thrust.tickManualThrust(sign(it).toFloat())
                return@register computers.thrust.targetThrust.toDouble()
            }

            return@register it
        }, 10)
    }

    override fun isAvailable(): Boolean {
        return ModConfig.INSTANCE.enableThrust
    }

    override fun tick() {
    }

    override fun reset() {
    }

    companion object {
        val ID: ResourceLocation = DoABarrelRoll.id("thrust")
    }
}

//?}
