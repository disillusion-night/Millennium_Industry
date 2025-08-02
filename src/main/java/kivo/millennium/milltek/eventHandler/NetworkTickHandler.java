package kivo.millennium.milltek.eventHandler;

import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkTickHandler {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return; // 只在每个世界的最后一个tick处理
        }
        if (event.level.isClientSide) return;
        
        LevelNetworkSavedData.tick((ServerLevel) event.level);
    }

}