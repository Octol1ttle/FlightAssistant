package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;

public class FireworkController implements ITickableComputer {
    private final TimeComputer time;
    private final AirDataComputer data;
    private final ClientPlayerInteractionManager interaction;
    public int safeFireworkCount = Integer.MAX_VALUE;
    public boolean fireworkResponded = true;
    public float lastUseTime = -1.0f;
    public float lastDiff = Float.MAX_VALUE;
    @Nullable
    public Float lastTogaLock;
    public boolean noFireworks = false;
    public boolean unsafeFireworks = false;
    public boolean activationInProgress = false;

    public FireworkController(TimeComputer time, AirDataComputer data, ClientPlayerInteractionManager interaction) {
        this.time = time;
        this.data = data;
        this.interaction = interaction;
    }

    public void tick() {
        safeFireworkCount = countSafeFireworks();
        if (time.prevMillis != null && lastUseTime > 0) {
            lastDiff = time.prevMillis - lastUseTime;
        }

        PlayerInventory inventory = data.player.getInventory();
        int i = 0;
        while (PlayerInventory.isValidHotbarIndex(i)) {
            if (isFireworkSafe(inventory.getStack(i))) {
                noFireworks = false;
                break;
            }

            i++;
        }
    }

    private int countSafeFireworks() {
        int i = 0;

        PlayerInventory inventory = data.player.getInventory();
        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (isFireworkSafe(itemStack)) {
                i += itemStack.getCount();
            }
        }

        return i;
    }

    public void activateFirework(boolean togaLock) {
        if (!data.canAutomationsActivate() || lastUseTime > 0 && time.prevMillis != null && time.prevMillis - lastUseTime < 750) {
            return;
        }
        if (togaLock && time.prevMillis != null) {
            this.lastTogaLock = time.prevMillis;
        }

        if (isFireworkSafe(data.player.getMainHandStack())) {
            tryActivateFirework(Hand.MAIN_HAND, togaLock);
            return;
        }
        if (isFireworkSafe(data.player.getOffHandStack())) {
            tryActivateFirework(Hand.OFF_HAND, togaLock);
            return;
        }

        int i = 0;
        boolean match = false;
        PlayerInventory inventory = data.player.getInventory();
        while (PlayerInventory.isValidHotbarIndex(i)) {
            if (isFireworkSafe(inventory.getStack(i))) {
                inventory.selectedSlot = i;
                match = true;
                break;
            }

            i++;
        }

        if (!match) {
            noFireworks = true;
            return;
        }
        tryActivateFirework(Hand.MAIN_HAND, togaLock);
    }

    private void tryActivateFirework(Hand hand, boolean force) {
        if (!force && !fireworkResponded) {
            return;
        }

        activationInProgress = true;
        interaction.interactItem(data.player, hand);
        activationInProgress = false;
    }

    public boolean isFireworkSafe(ItemStack stack) {
        if (!(stack.getItem() instanceof FireworkRocketItem)) {
            return false;
        }
        NbtCompound nbtCompound = stack.getSubNbt("Fireworks");
        return nbtCompound == null || nbtCompound.getList("Explosions", NbtElement.COMPOUND_TYPE).isEmpty();
    }

    @Override
    public String getId() {
        return "frwk_ctl";
    }

    @Override
    public void reset() {
        safeFireworkCount = Integer.MAX_VALUE;
        fireworkResponded = true;
        lastUseTime = -1.0f;
        lastDiff = Float.MAX_VALUE;
        lastTogaLock = null;
        noFireworks = false;
        unsafeFireworks = false;
        activationInProgress = false;
    }
}
