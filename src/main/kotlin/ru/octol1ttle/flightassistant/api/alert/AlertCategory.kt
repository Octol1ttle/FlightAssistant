package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus

/**
 * A class that represents a category of alerts.
 */
class AlertCategory(val categoryText: Component) {
    private val registeredAlerts: MutableList<Alert> = ArrayList()
    val activeAlerts: MutableList<Alert> = ArrayList()
    val ignoredAlerts: MutableList<Alert> = ArrayList()

    fun add(alert: Alert): AlertCategory {
        if (registeredAlerts.contains(alert)) {
            throw IllegalArgumentException("Already registered alert: ${alert.javaClass.name}")
        }

        registeredAlerts.add(alert)
        return this
    }

    fun addAll(alerts: Collection<Alert>): AlertCategory {
        for (alert: Alert in alerts) {
            add(alert)
        }
        return this
    }

    fun updateActiveAlerts(computers: ComputerBus) {
        for (alert: Alert in registeredAlerts) {
            try {
                if (alert.shouldActivate()) {
                    if (!activeAlerts.contains(alert) && !ignoredAlerts.contains(alert)) {
                        activeAlerts.add(alert)
                    }
                } else {
                    activeAlerts.remove(alert)
                    ignoredAlerts.remove(alert)
                }
            } catch (t: Throwable) {
                computers.alert.alertsFaulted = true
                FlightAssistant.logger.error("Exception ticking alert of type: ${alert.javaClass.name}", t)
            }
        }

        activeAlerts.sortBy { it.data.priority + it.priorityOffset }
    }

    fun getHighestPriority(): Int? {
        return if (activeAlerts.isEmpty()) null else activeAlerts[0].priority
    }

    fun getFirstData(filter: ((Alert) -> Boolean)? = null): AlertData? {
        return if (filter != null) activeAlerts.firstOrNull(filter)?.data else activeAlerts.firstOrNull()?.data
    }
}
