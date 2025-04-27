package kivo.millennium.millind.eventHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static kivo.millennium.millind.Main.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegistryEvent {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // 只在服务器 Tick 结束时处理网络逻辑
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = event.getServer();
            if (server != null) {
                // 遍历所有已加载的 ServerLevel
                for (ServerLevel serverLevel : server.getAllLevels()) {
                    // 获取该维度的 LevelNetworkManagerData
                    //LevelNetworkManagerData networkManager = LevelNetworkManagerData.get();

                    // 遍历该维度的所有 LevelNetwork 实例并执行 Tick 逻辑
                    // 使用 values() 获取网络的 Collection，避免在迭代时因网络合并/分裂修改 Map 导致并发问题
                    // 或者在 Tick 循环前复制一份网络列表
                    //List<AbstractLevelNetwork> networksToTick = networkManager.;

                    //for (AbstractLevelNetwork network : networksToTick) {
                    // 确保网络实例有效且仍应被 Tick (例如，可能在 Tick 过程中被标记为删除)
                    // 你可能需要在 AbstractLevelNetwork 中添加一个 isRemoved() 或 similar 方法
                    // if (!network.isRemoved()) { // 示例检查
                    //network.handleTick(serverLevel); // 调用网络的 Tick 方法
                    // }
                    //}
                }
            }
        }
    }
}
