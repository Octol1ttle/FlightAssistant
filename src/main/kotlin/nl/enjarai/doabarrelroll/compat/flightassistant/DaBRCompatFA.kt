package nl.enjarai.doabarrelroll.compat.flightassistant

import dev.architectury.platform.Platform
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerRegistrationCallback

object DaBRCompatFA {
//? if do-a-barrel-roll {
    fun init() {
        if (!Platform.isModLoaded("do_a_barrel_roll")) {
            return
        }
        FlightAssistant.logger.info("Initializing support for Do a Barrel Roll")

        ComputerRegistrationCallback.EVENT.register(ComputerRegistrationCallback { computers, registerFunction ->
            registerFunction.accept(DaBRRollComputer.ID, DaBRRollComputer(computers))
            registerFunction.accept(DaBRThrustComputer.ID, DaBRThrustComputer(computers))
        })
    }
//?} else {
    /*fun init() {
        if (Platform.isModLoaded("do_a_barrel_roll")) {
            FlightAssistant.logger.warn("Do a Barrel Roll is present, but this version of FlightAssistant does not have support for it. Update FlightAssistant, or report this issue if there are no updates available")
        }
    }
*///?}
}
