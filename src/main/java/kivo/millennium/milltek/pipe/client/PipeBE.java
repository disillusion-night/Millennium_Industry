package kivo.millennium.milltek.pipe.client;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import kivo.millennium.milltek.pipe.client.network.BlockEntityNetworkTarget;
import kivo.millennium.milltek.pipe.client.network.FluidPipeNetwork;
import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PipeBE<T extends AbstractLevelNetwork> extends BlockEntity {
    private int pipeID = -1;

    private final LevelNetworkType<T> networkType;

    public PipeBE(BlockEntityType<?> pType,LevelNetworkType<T> networkType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.networkType = networkType;
    }

    protected T getNetwork() {
        if (pipeID == -1) {
            return null;
        }
        return (T) LevelNetworkSavedData.getInstance().getNetworkById(LevelNetworkType.getName(networkType), pipeID);
    }

    protected void setPipeID(int id) {
        this.pipeID = id;
    }

    protected void connectToNetwork() {
        T network = getNetwork();
        if (network != null) {
            network.addInput(new BlockEntityNetworkTarget(worldPosition));
        } else {
            // 创建新网络
            FluidPipeNetwork newNetwork = new FluidPipeNetwork(LevelNetworkSavedData.getInstance().generateNewNetworkID(LevelNetworkType.getName(networkType)));
            newNetwork.addInput(new BlockEntityNetworkTarget(worldPosition));
            LevelNetworkSavedData.getInstance().addNetwork(LevelNetworkType.getName(networkType), newNetwork);
            setPipeID(newNetwork.getId());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        T network = getNetwork();
        if (network != null) {
            network.removeInput(new BlockEntityNetworkTarget(worldPosition));
        }
    }
}
