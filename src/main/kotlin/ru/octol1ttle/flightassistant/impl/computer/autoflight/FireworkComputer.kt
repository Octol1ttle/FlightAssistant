package ru.octol1ttle.flightassistant.impl.computer.autoflight

import dev.architectury.event.events.common.InteractionEvent
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.FireworkRocketItem
import net.minecraft.world.item.ItemStack
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.LimitedFIFOQueue
import ru.octol1ttle.flightassistant.api.util.event.FireworkBoostCallback
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.display.StatusDisplay

class FireworkComputer(computers: ComputerBus, private val mc: Minecraft) : Computer(computers), ThrustSource {
    override val priority: ThrustSource.Priority = ThrustSource.Priority.LOW
    override val supportsReverse: Boolean = false
    override val optimumClimbPitch: Float = 55.0f
    override val altitudeHoldPitch: Float = 2.0f

    private var safeFireworkCount: Int = 0

    private var safeFireworkSlot: Int? = null
    var waitingForResponse: Boolean = false
    var lastActivationTime: Int = 0

    var responseTimes: LimitedFIFOQueue<Int> = LimitedFIFOQueue(5)

    override fun subscribeToEvents() {
        ThrustSourceRegistrationCallback.EVENT.register { it.accept(this) }
        InteractionEvent.RIGHT_CLICK_ITEM.register(InteractionEvent.RightClickItem { player, hand ->
            val stack: ItemStack = player.getItemInHand(hand)
            if (!player.level().isClientSide()) {
//? if >=1.21.2 {
                /*return@RightClickItem net.minecraft.world.InteractionResult.PASS
*///?} else
                return@RightClickItem dev.architectury.event.CompoundEventResult.pass()

            }

            if (FAConfig.safety.fireworkLockExplosive && !isEmptyOrSafe(player, hand)) {
//? if >=1.21.2 {
                /*return@RightClickItem net.minecraft.world.InteractionResult.FAIL
*///?} else
                return@RightClickItem dev.architectury.event.CompoundEventResult.interruptFalse(stack)
            }

            if (!waitingForResponse && stack.item is FireworkRocketItem) {
                lastActivationTime = FATickCounter.totalTicks
                waitingForResponse = true
            }

//? if >=1.21.2 {
            /*return@RightClickItem net.minecraft.world.InteractionResult.PASS
*///?} else
            return@RightClickItem dev.architectury.event.CompoundEventResult.pass()
        })
        FireworkBoostCallback.EVENT.register(FireworkBoostCallback { _, _ ->
            if (waitingForResponse) {
                waitingForResponse = false
                responseTimes.add(FATickCounter.totalTicks - lastActivationTime)
            }
        })
    }

    override fun tick() {
        if (!computers.data.flying) {
            waitingForResponse = false
        }

        safeFireworkSlot = null
        var lastSlotCount = 0
        for (slot: Int in 0..<Inventory.getSelectionSize()) {
            val stack: ItemStack = computers.data.player.inventory.getItem(slot)
            if (isFireworkAndSafe(stack)) {
                if (safeFireworkSlot == null || stack.count < lastSlotCount) {
                    safeFireworkSlot = slot
                    lastSlotCount = stack.count
                }
            }
        }

        safeFireworkCount = 0
        for (stack: ItemStack in
//? if >=1.21.5 {
        /*computers.data.player.inventory.nonEquipmentItems
*///?} else
        computers.data.player.inventory.items
        ) {
            if (isFireworkAndSafe(stack)) {
                safeFireworkCount += stack.count
            }
        }
        if (isFireworkAndSafe(computers.data.player.offhandItem)) {
            safeFireworkCount += computers.data.player.offhandItem.count
        }
    }

    fun isEmptyOrSafe(player: Player, hand: InteractionHand): Boolean {
        return hasNoExplosions(player.getItemInHand(hand))
    }

    private fun isFireworkAndSafe(stack: ItemStack): Boolean {
        return stack.item is FireworkRocketItem && hasNoExplosions(stack)
    }

    private fun hasNoExplosions(stack: ItemStack): Boolean {
//? if >=1.21 {
        /*return stack.get(net.minecraft.core.component.DataComponents.FIREWORKS)?.explosions?.isEmpty() != false
*///?} else
        return stack.getTagElement("Fireworks")?.getList("Explosions", net.minecraft.nbt.Tag.TAG_COMPOUND.toInt())?.isEmpty() != false
    }

    private fun tryActivateFirework(player: Player) {
        if (FATickCounter.totalTicks < lastActivationTime + 10) {
            return
        }

        if (isFireworkAndSafe(player.offhandItem)) {
            useFirework(player, InteractionHand.OFF_HAND)
        } else if (safeFireworkSlot != null) {
//? if >=1.21.5 {
            /*player.inventory.selectedSlot = safeFireworkSlot!!
*///?} else
            player.inventory.selected = safeFireworkSlot!!
            useFirework(player, InteractionHand.MAIN_HAND)
        }
    }

    private fun useFirework(player: Player, hand: InteractionHand) {
        mc.gameMode!!.useItem(player, hand)
        lastActivationTime = FATickCounter.totalTicks
        waitingForResponse = true
    }

    override fun isAvailable(): Boolean {
        return safeFireworkSlot != null
    }

    override fun tickThrust(currentThrust: Float) {
        if (currentThrust > computers.data.forwardVelocity.length() * 20.0f / 30.0f) {
            tryActivateFirework(computers.data.player)
        }
    }

    override fun <Response> processQuery(query: ComputerQuery<Response>) {
        if (query is StatusDisplay.StatusMessageQuery && computers.thrust.getThrustSource() == this) {
            query.respond(Component.translatable("status.flightassistant.firework_count", safeFireworkCount))
        }
    }

    override fun reset() {
        safeFireworkCount = 0
        safeFireworkSlot = null
        waitingForResponse = false
        lastActivationTime = 0
        responseTimes.clear()
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("firework")
    }
}
