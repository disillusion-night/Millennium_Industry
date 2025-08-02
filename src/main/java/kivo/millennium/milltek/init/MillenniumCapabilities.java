package kivo.millennium.milltek.init;

import kivo.millennium.milltek.gas.IGasHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 能力注册类，仿照Forge标准写法
 * 在mod主类构造中注册：MinecraftForge.EVENT_BUS.register(MillenniumCapabilities.class);
 */
public class MillenniumCapabilities {
  public static final Capability<IGasHandler> GAS = CapabilityManager.get(new CapabilityToken<>() {
  });

  @SubscribeEvent
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.register(IGasHandler.class);
  }
}
