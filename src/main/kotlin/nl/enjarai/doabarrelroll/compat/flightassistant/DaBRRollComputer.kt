package nl.enjarai.doabarrelroll.compat.flightassistant

//? if do-a-barrel-roll {
import net.minecraft.resources.ResourceLocation
import nl.enjarai.doabarrelroll.DoABarrelRoll
import nl.enjarai.doabarrelroll.api.RollEntity
import ru.octol1ttle.flightassistant.api.autoflight.RollSource
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.RollComputer

class DaBRRollComputer(computers: ComputerBus) : Computer(computers), RollSource {
    override fun getRoll(): Float {
        return (computers.data.player as RollEntity).`doABarrelRoll$getRoll`(FATickCounter.partialTick)
    }

    override fun addRoll(diff: Float) {
        return (computers.data.player as RollEntity).`doABarrelRoll$setRoll`(getRoll() + diff)
    }

    override fun reset() {
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        if (query is RollComputer.ActiveRollSourceQuery) {
            if ((computers.data.player as RollEntity).`doABarrelRoll$isRolling`()) {
                query.respond(this)
            }
        }
    }

    companion object {
        val ID: ResourceLocation = DoABarrelRoll.id("roll")
    }
}

//?}
