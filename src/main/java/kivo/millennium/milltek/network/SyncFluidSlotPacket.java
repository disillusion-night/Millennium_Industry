package kivo.millennium.milltek.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fluids.FluidStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;

import java.util.function.Supplier;

public class SyncFluidSlotPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger("SyncFluidSlotPacket");
    private final int containerId;
    private final int slotIndex;
    private final int capacity;
    private final FluidStack fluidStack;

    public SyncFluidSlotPacket(int containerId, int slotIndex, int capacity, FluidStack fluidStack) {
        this.containerId = containerId;
        this.slotIndex = slotIndex;
        this.capacity = capacity;
        this.fluidStack = fluidStack.copy();
    }

    public SyncFluidSlotPacket(FriendlyByteBuf buf) {
        this.containerId = buf.readVarInt();
        this.slotIndex = buf.readVarInt();
        this.capacity = buf.readVarInt();
        this.fluidStack = FluidStack.readFromPacket(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        LOGGER.info("[SyncFluidSlotPacket][SEND] containerId={}, slotIndex={}, fluidStack={}", containerId, slotIndex,
                fluidStack);
        buf.writeVarInt(containerId);
        buf.writeVarInt(slotIndex);
        buf.writeVarInt(capacity);
        fluidStack.writeToPacket(buf);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LOGGER.info("[SyncFluidSlotPacket][RECV] containerId={}, slotIndex={}, fluidStack={}", containerId,
                    slotIndex, fluidStack);
            // 客户端：根据containerId和slotIndex更新GUI流体槽内容
            Minecraft mc = Minecraft.getInstance();
            var player = mc != null ? mc.player : null;
            if (player == null)
                return;
            var menu = player.containerMenu;
            if (menu != null && menu.containerId == this.containerId && menu instanceof AbstractDeviceMenu<?> devMenu) {
                var fluidSlots = devMenu.getFluidSlots();
                if (slotIndex >= 0 && slotIndex < fluidSlots.size()) {
                    fluidSlots.get(slotIndex).setFluidStack(fluidStack);
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

    public FluidStack getFluidStack() {
        return fluidStack;
    }
}
