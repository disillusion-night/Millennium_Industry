package kivo.millennium.milltek.pipe.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FluidPipeBL extends AbstractPipeBL {
    public FluidPipeBL() {
        super(Properties.of().noOcclusion());
    }

    @Override
    public double getDefaultWidth() {
        return 0.3;
    }

    @Override
    public Capability<?> getCapabilityType() {
        return ForgeCapabilities.FLUID_HANDLER;
    }

    @Override
    public boolean isSamePipe(Block target) {
        return target instanceof FluidPipeBL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBE(pos, state);
    }

    @Override
    public BlockEntityType<FluidPipeBE> blockEntityType() {
        return MillenniumBlockEntities.FLUID_PIPE_BE.get();
    }
}

