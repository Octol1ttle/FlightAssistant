package ru.octol1ttle.flightassistant.api.computer

import ru.octol1ttle.flightassistant.api.ModuleView
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FireworkComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.HeadingComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.PitchComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.RollComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.ThrustComputer
import ru.octol1ttle.flightassistant.impl.computer.data.AirDataComputer
import ru.octol1ttle.flightassistant.impl.computer.data.HudDisplayDataComputer
import ru.octol1ttle.flightassistant.impl.computer.safety.*

interface ComputerBus : ModuleView<Computer> {
    fun <C, T> guardedCall(computer: C, call: (C) -> T): T?
    fun <Event : ComputerEvent> dispatchEvent(event: Event)
    fun <Response> dispatchQuery(query: ComputerQuery<Response>): Collection<Response>

    val data: AirDataComputer
        get() = get(AirDataComputer.ID) as AirDataComputer

    val hudData: HudDisplayDataComputer
        get() = get(HudDisplayDataComputer.ID) as HudDisplayDataComputer

    val autoflight: AutoFlightComputer
        get() = get(AutoFlightComputer.ID) as AutoFlightComputer

    val firework: FireworkComputer
        get() = get(FireworkComputer.ID) as FireworkComputer

    val plan: FlightPlanComputer
        get() = get(FlightPlanComputer.ID) as FlightPlanComputer

    val heading: HeadingComputer
        get() = get(HeadingComputer.ID) as HeadingComputer

    val pitch: PitchComputer
        get() = get(PitchComputer.ID) as PitchComputer

    val roll: RollComputer
        get() = get(RollComputer.ID) as RollComputer

    val thrust: ThrustComputer
        get() = get(ThrustComputer.ID) as ThrustComputer

    val alert: AlertComputer
        get() = get(AlertComputer.ID) as AlertComputer

    val chunk: ChunkStatusComputer
        get() = get(ChunkStatusComputer.ID) as ChunkStatusComputer

    val elytra: ElytraStatusComputer
        get() = get(ElytraStatusComputer.ID) as ElytraStatusComputer

    val protections: FlightProtectionsComputer
        get() = get(FlightProtectionsComputer.ID) as FlightProtectionsComputer

    val gpws: GroundProximityComputer
        get() = get(GroundProximityComputer.ID) as GroundProximityComputer

    val stall: StallComputer
        get() = get(StallComputer.ID) as StallComputer

    val voidProximity: VoidProximityComputer
        get() = get(VoidProximityComputer.ID) as VoidProximityComputer
}
