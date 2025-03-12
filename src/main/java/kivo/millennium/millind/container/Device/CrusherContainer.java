package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.joml.Vector2i;

public class CrusherContainer extends AbstractDeviceMenu {


    public CrusherContainer(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.CRUSHER_CONTAINER.get(), pContainerId, player, pos, new SimpleContainer(99));
    }

    @Override
    protected void setupSlot(Container container, AbstractDeviceBE deviceBE) {
        super.setupSlot(container, deviceBE);
    }

    @Override
    public Vector2i getBatterySlotPos() {
        return null;
    }

    @Override
    public Vector2i getPlayerInvPos() {
        return new Vector2i(10, 70);
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.CRUSHER_BL.get(); // 返回破碎机方块实例  (请确保你已注册 ModBlocks.CRUSHER_BLOCK)
    }
}