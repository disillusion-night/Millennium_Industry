package kivo.millennium.millind.eventHandler;

import kivo.millennium.millind.pipe.client.network.AbstractNetwork;
import kivo.millennium.millind.pipe.client.network.NetworkManagerData;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static kivo.millennium.millind.Main.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerTickEvents {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // 获取所有 ServerLevel，对于全局网络，只需要在主世界 Tick
            // 对于单维度网络，需要在每个维度 Tick

            // 示例：在主世界 Tick 全局网络和主世界的单维度网络
            ServerLevel overworld = event.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
            if (overworld != null) {
                NetworkManagerData networkManager = NetworkManagerData.get(overworld);
                for (AbstractNetwork network : networkManager.getNetworks().values()) {
                    if (network.isGlobal()) {
                        // 全局网络只在主世界 Tick
                        network.handleTick(overworld);
                    } else {
                        // 单维度网络在各自维度 Tick，这里只 Tick 主世界的单维度网络
                        if (overworld.dimension() == overworld.dimension()) { // 恒等判断，仅为示例
                            network.handleTick(overworld);
                        }
                    }
                }

                // TODO: 遍历其他维度，并 Tick 它们的单维度网络
                // 你可能需要一个 Map 来存储每个维度的 NetworkManagerData
                // net.minecraft.server.MinecraftServer.getServer().getAllLevels().forEach(level -> {
                //     if (level != overworld) {
                //         NetworkManagerData dimensionNetworkManager = NetworkManagerData.get(level);
                //         for (AbstractNetwork network : dimensionNetworkManager.getNetworks().values()) {
                //             if (!network.isGlobal()) {
                //                 network.handleTick(level);
                //             }
                //         }
                //     }
                // });
            }
        }
    }
}