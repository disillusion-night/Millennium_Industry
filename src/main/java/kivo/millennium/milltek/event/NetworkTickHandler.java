package kivo.millennium.milltek.event;

import kivo.millennium.milltek.world.LevelNetworkSavedData;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "milltek")
public class NetworkTickHandler {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.ServerTickEvent event) {

    }

}