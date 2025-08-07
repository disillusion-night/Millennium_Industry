package kivo.millennium.milltek.pipe.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraftforge.common.capabilities.Capability;
import kivo.millennium.milltek.init.MillenniumCapabilities;

public class GasPipeBL extends AbstractPipeBL {
    public GasPipeBL() {
        super(Properties.of().noOcclusion());
    }

    @Override
    public double getDefaultWidth() {
        return 0.3;
    }

    @Override
    public Capability<?> getCapabilityType() {
        return MillenniumCapabilities.GAS;
    }

    @Override
    public boolean isSamePipe(Block target) {
        return target instanceof GasPipeBL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GasPipeBE(pos, state);
    }

    @Override
    public BlockEntityType<GasPipeBE> blockEntityType() {
        return MillenniumBlockEntities.GAS_PIPE_BE.get();
    }
}

