package ru.octol1ttle.flightassistant.api.computer

/**
 * A class responsible for computing data and providing it to [ru.octol1ttle.flightassistant.api.display.Display]s and [ru.octol1ttle.flightassistant.api.alert.Alert]s
 */
abstract class Computer(val computers: ComputerBus) {
    /**
     * Whether or not this computer is enabled. Disabled computers do not tick.
     */
    var enabled: Boolean = true
        internal set

    /**
     * Whether or not this computer has faulted. This value is set to false when the computer is turned off.
     */
    var faulted: Boolean = false
        internal set

    /**
     * The amount of times this computer has faulted.
     */
    var faultCount: Int = 0
        internal set

    fun isDisabledOrFaulted(): Boolean {
        return !enabled || faulted
    }

    /**
     * Called once per level render
     *
     * If this method throws an exception or error, it is caught and the computer is considered "faulted".
     * It won't be ticked until it is reset and an alert about the issue will be displayed
     */
    abstract fun tick()

    /**
     * Called when this computer should be reset. This computer's state should be reset to the initial ("everything is good") state.
     * The computer will be ticked again after it is reset.
     */
    abstract fun reset()

    /**
     * Called when another computer dispatches a ComputerEvent.
     */
    open fun <Event : ComputerEvent> processEvent(event: Event) {}

    /**
     * Called when another computer dispatches a ComputerQuery.
     */
    open fun <Response> processQuery(query: ComputerQuery<Response>) {}

    /**
     * Called once after all computers have been registered. Subscribe to any events provided by other computers here.
     * Be careful calling other computers' code here! Depending on where events are invoked, a fault may cause a game crash.
     * Use [ComputerBus.guardedCall] to invoke computers safely or manually guard your call with [Computer.isDisabledOrFaulted]
     */
    @Deprecated("Use ComputerBus.dispatchEvent and Computer.processEvent instead. For non-computer events, register them in initializers")
    open fun subscribeToEvents() {}

    /**
     * Called once after [subscribeToEvents]. Invoke events that your computer provides here.
     */
    @Deprecated("Use ComputerBus.dispatchEvent and Computer.processEvent instead")
    open fun invokeEvents() {}
}
