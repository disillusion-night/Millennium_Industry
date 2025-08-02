package kivo.millennium.milltek.init;

import kivo.millennium.milltek.network.DeviceButtonPacket;
import kivo.millennium.milltek.network.SyncFluidSlotPacket;
import kivo.millennium.milltek.network.SyncGasSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class MillenniumNetworkPacket {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("milltek", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, DeviceButtonPacket.class,
                DeviceButtonPacket::toBytes,
                DeviceButtonPacket::new,
                (msg, ctx) -> msg.handle(ctx));
        INSTANCE.registerMessage(id++, SyncFluidSlotPacket.class,
                SyncFluidSlotPacket::toBytes,
                SyncFluidSlotPacket::new,
                (msg, ctx) -> msg.handle(ctx));
        INSTANCE.registerMessage(id++, SyncGasSlotPacket.class,
                SyncGasSlotPacket::toBytes,
                SyncGasSlotPacket::new,
                (msg, ctx) -> msg.handle(ctx));
    }
}
