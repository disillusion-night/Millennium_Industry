package kivo.millennium.milltek.network;

import kivo.millennium.milltek.gas.GasStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class SyncGasSlotPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger("SyncGasSlotPacket");
    private final int containerId;
    private final int slotIndex;
    private final int capacity;
    private final GasStack gasStack;

    public SyncGasSlotPacket(int containerId, int slotIndex, int capacity, GasStack gasStack) {
        this.containerId = containerId;
        this.slotIndex = slotIndex;
        this.capacity = capacity;
        this.gasStack = gasStack.copy();
    }

    public SyncGasSlotPacket(FriendlyByteBuf buf) {
        this.containerId = buf.readVarInt();
        this.slotIndex = buf.readVarInt();
        this.capacity = buf.readVarInt();
        this.gasStack = GasStack.readFromPacket(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        LOGGER.info("[SyncGasSlotPacket][SEND] containerId={}, slotIndex={}, gasStack={}", containerId, slotIndex,
                gasStack);
        buf.writeVarInt(containerId);
        buf.writeVarInt(slotIndex);
        buf.writeVarInt(capacity);
        gasStack.writeToPacket(buf);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LOGGER.info("[SyncGasSlotPacket][RECV] containerId={}, slotIndex={}, gasStack={}", containerId, slotIndex,
                    gasStack);
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            var player = mc != null ? mc.player : null;
            if (player == null)
                return;
            var menu = player.containerMenu;
            if (menu != null && menu.containerId == this.containerId
                    && menu instanceof kivo.millennium.milltek.container.Device.AbstractDeviceMenu<?> devMenu) {
                var gasSlots = devMenu.getGasSlots();
                if (slotIndex >= 0 && slotIndex < gasSlots.size()) {
                    gasSlots.get(slotIndex).setGasStack(gasStack);
                }
            }
        });
        return true;
    }

    public int getContainerId() {
        return containerId;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public GasStack getGasStack() {
        return gasStack;
    }
}
