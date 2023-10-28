package ru.octol1ttle.flightassistant.computers;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class FireworkManager {
    private final FlightComputer computer;
    public int safeFireworkCount;
    public boolean fireworkResponded = true;
    public float lastUseTime = -1.0f;
    public float lastDiff = Float.MAX_VALUE;
    @Nullable
    public Float lastTogaLock;
    public boolean noFireworks;

    public FireworkManager(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        // TODO: client<->server communication to confirm firework activation
        safeFireworkCount = countSafeFireworks();
        if (computer.speed > 30) {
            fireworkResponded = true;
        }
        if (!fireworkResponded && computer.time.prevMillis != null && lastUseTime > 0) {
            lastDiff = computer.time.prevMillis - lastUseTime;
        }
    }

    private int countSafeFireworks() {
        int i = 0;

        PlayerInventory inventory = computer.player.getInventory();
        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (isFireworkSafe(itemStack)) {
                i += itemStack.getCount();
            }
        }

        return i;
    }

    public boolean activateFirework(boolean togaLock) {
        if (!computer.canAutomationsActivate() || lastUseTime > 0 && computer.time.prevMillis - lastUseTime < 750) {
            return false;
        }
        if (togaLock) {
            this.lastTogaLock = computer.time.prevMillis;
        }

        if (isFireworkSafe(computer.player.getMainHandStack())) {
            return tryActivateFirework(Hand.MAIN_HAND);
        }
        if (isFireworkSafe(computer.player.getOffHandStack())) {
            return tryActivateFirework(Hand.OFF_HAND);
        }

        int i = 0;
        boolean match = false;
        while (PlayerInventory.isValidHotbarIndex(i)) {
            if (isFireworkSafe(computer.player.getInventory().getStack(i))) {
                computer.player.getInventory().selectedSlot = i;
                match = true;
                break;
            }

            i++;
        }

        if (!match) {
            noFireworks = true;
            return false;
        }
        return tryActivateFirework(Hand.MAIN_HAND);
    }

    private boolean tryActivateFirework(Hand hand) {
        noFireworks = false;
        if (!fireworkResponded) {
            return false;
        }

        assert computer.mc.interactionManager != null;
        if (computer.mc.interactionManager.interactItem(computer.player, hand).shouldSwingHand()) {
            lastUseTime = computer.time.prevMillis;
            fireworkResponded = false;
            return true;
        }

        return false;
    }

    private boolean isFireworkSafe(ItemStack stack) {
        if (!(stack.getItem() instanceof FireworkRocketItem)) {
            return false;
        }
        NbtCompound nbtCompound = stack.getSubNbt("Fireworks");
        return nbtCompound == null || nbtCompound.getList("Explosions", NbtElement.COMPOUND_TYPE).isEmpty();
    }
}
