package ru.octol1ttle.flightassistant.compat.dragonsurvival

import dev.architectury.platform.Platform
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import ru.octol1ttle.flightassistant.FlightAssistant

/**
 * Optional integration with the DragonSurvival mod.
 *
 * Uses reflection to avoid a hard dependency: if DragonSurvival is not present (or its internals change),
 * FlightAssistant keeps working and simply falls back to vanilla flight detection.
 */
object DragonSurvivalCompat {
    private const val MOD_ID = "dragonsurvival"

    private const val DRAGON_STATE_PROVIDER = "by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider"
    private const val FLIGHT_DATA = "by.dragonsurvivalteam.dragonsurvival.registry.attachments.FlightData"

    private var initialized = false
    private var available = false

    private var isDragonInvoker: Invoker? = null
    private var getDataInvoker: Invoker? = null
    private var hasFlightAccessor: BooleanAccessor? = null
    private var wingsSpreadAccessor: BooleanAccessor? = null

    fun isDragonFlying(player: LocalPlayer): Boolean {
        if (!ensureInitialized()) return false
        return isDragonFlyingInternal(player, requireAirborne = true)
    }

    fun isDragonWingsSpread(player: LocalPlayer): Boolean {
        if (!ensureInitialized()) return false
        return isDragonFlyingInternal(player, requireAirborne = false)
    }

    private fun isDragonFlyingInternal(player: LocalPlayer, requireAirborne: Boolean): Boolean {
        val isDragon = (isDragonInvoker?.invoke(player as Entity) as? Boolean) == true
        if (!isDragon) return false

        val flightData = getDataInvoker?.invoke(player as Player) ?: return false

        val hasFlight = hasFlightAccessor?.get(flightData) == true
        if (!hasFlight) return false

        val wingsSpread = wingsSpreadAccessor?.get(flightData) == true
        if (!wingsSpread) return false

        if (!requireAirborne) return true

        return !player.onGround() && !player.isInWater && !player.isInLava && !player.isPassenger
    }

    private fun ensureInitialized(): Boolean {
        if (initialized) return available
        initialized = true

        if (!Platform.isModLoaded(MOD_ID)) {
            available = false
            return false
        }

        try {
            val dragonStateProviderClass = Class.forName(DRAGON_STATE_PROVIDER)
            val flightDataClass = Class.forName(FLIGHT_DATA)

            isDragonInvoker = resolveInvoker(dragonStateProviderClass, "isDragon", Entity::class.java)
            getDataInvoker = resolveInvoker(flightDataClass, "getData", Player::class.java)

            hasFlightAccessor = resolveBooleanAccessor(flightDataClass, "hasFlight")
            wingsSpreadAccessor = resolveBooleanAccessor(flightDataClass, "areWingsSpread")

            available = isDragonInvoker != null && getDataInvoker != null && hasFlightAccessor != null && wingsSpreadAccessor != null
            if (!available) {
                FlightAssistant.logger.warn("DragonSurvival is installed, but integration could not be initialized; falling back to vanilla flight detection.")
            }
        } catch (t: Throwable) {
            available = false
            FlightAssistant.logger.warn("DragonSurvival is installed, but integration failed to initialize; falling back to vanilla flight detection.", t)
        }

        return available
    }

    private fun resolveInvoker(ownerClass: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Invoker? {
        val directMethod = resolveMethod(ownerClass, methodName, *parameterTypes)
        if (directMethod != null) {
            if (Modifier.isStatic(directMethod.modifiers)) {
                return Invoker(null, directMethod)
            }
            val instance = ownerClass.kotlinObjectInstanceOrNull() ?: return null
            return Invoker(instance, directMethod)
        }

        val companionInstance = ownerClass.companionInstanceOrNull() ?: return null
        val companionMethod = resolveMethod(companionInstance.javaClass, methodName, *parameterTypes) ?: return null
        val target = if (Modifier.isStatic(companionMethod.modifiers)) null else companionInstance
        return Invoker(target, companionMethod)
    }

    private fun Class<*>.kotlinObjectInstanceOrNull(): Any? =
        runCatching { getField("INSTANCE").get(null) }.getOrNull()

    private fun Class<*>.companionInstanceOrNull(): Any? =
        runCatching { getField("Companion").get(null) }.getOrNull()

    private fun resolveMethod(ownerClass: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method? =
        runCatching { ownerClass.getMethod(methodName, *parameterTypes) }.getOrNull()
            ?: runCatching { ownerClass.getDeclaredMethod(methodName, *parameterTypes).apply { isAccessible = true } }.getOrNull()

    private fun resolveBooleanAccessor(ownerClass: Class<*>, propertyName: String): BooleanAccessor? {
        val field =
            runCatching { ownerClass.getField(propertyName).apply { isAccessible = true } }.getOrNull()
                ?: runCatching { ownerClass.getDeclaredField(propertyName).apply { isAccessible = true } }.getOrNull()

        val getterName = "get" + propertyName.replaceFirstChar { it.uppercaseChar() }
        val isName = "is" + propertyName.replaceFirstChar { it.uppercaseChar() }

        val getter =
            runCatching { ownerClass.getMethod(getterName) }.getOrNull()
                ?: runCatching { ownerClass.getDeclaredMethod(getterName).apply { isAccessible = true } }.getOrNull()
                ?: runCatching { ownerClass.getMethod(isName) }.getOrNull()
                ?: runCatching { ownerClass.getDeclaredMethod(isName).apply { isAccessible = true } }.getOrNull()

        if (field == null && getter == null) return null
        return BooleanAccessor(field, getter)
    }

    private class Invoker(private val target: Any?, private val method: Method) {
        fun invoke(argument: Any): Any? =
            try {
                method.invoke(target, argument)
            } catch (_: Throwable) {
                null
            }
    }

    private class BooleanAccessor(private val field: Field?, private val getter: Method?) {
        fun get(instance: Any): Boolean? =
            try {
                when {
                    field != null -> field.getBoolean(instance)
                    getter != null -> getter.invoke(instance) as? Boolean
                    else -> null
                }
            } catch (_: Throwable) {
                null
            }
    }
}
