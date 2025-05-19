package kivo.millennium.milltek.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class DeviceButtonPacket {
  // 可扩展字段，这里无数据
  public DeviceButtonPacket() {
  }

  public DeviceButtonPacket(FriendlyByteBuf buf) {
    // 读取数据（如有）
  }

  public void toBytes(FriendlyByteBuf buf) {
    // 写入数据（如有）
  }

  public boolean handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      // 服务端执行：给玩家一个钻石
      ServerPlayer player = ctx.get().getSender();
      if (player != null) {
        player.getInventory().add(new ItemStack(Items.DIAMOND));
      }
    });
    return true;
  }
}
