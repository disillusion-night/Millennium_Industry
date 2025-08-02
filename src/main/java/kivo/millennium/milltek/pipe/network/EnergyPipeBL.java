package kivo.millennium.milltek.pipe.network;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EnergyPipeBL extends AbstractPipeBL {
    public EnergyPipeBL() {
        super(Properties.of().noOcclusion());
    }

    @Override
    public double getDefaultWidth() {
        return 0.3;
    }

    @Override
    public Capability<?> getCapabilityType() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public boolean isSamePipe(Block target) {
        return target instanceof EnergyPipeBL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyPipeBE(pos, state);
    }

    @Override
    public BlockEntityType<EnergyPipeBE> blockEntityType() {
        return MillenniumBlockEntities.ENERGY_PIPE_BE.get();
    }
}
