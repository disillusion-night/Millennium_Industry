package kivo.millennium.milltek.pipe.client.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;

public class BlockEntityNetworkTarget extends AbstractNetworkTarget {
    BlockPos pos;
    Direction direction;

    public BlockEntityNetworkTarget(BlockPos pos) {
        this.pos = pos;
        this.direction = null;
    }

    public BlockEntityNetworkTarget(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }

    public BlockEntityNetworkTarget(CompoundTag compoundTag) {
        int[] pos = compoundTag.getIntArray("pos");
        this.pos = new BlockPos(pos[0], pos[1], pos[2]);
        if (pos.length != 3) {
            throw new IllegalArgumentException("Invalid position data");
        }
        if (compoundTag.contains("direction")) {
            this.direction = Direction.from3DDataValue(compoundTag.getInt("direction"));
        } else {
            this.direction = null;
        }
    }

    public IEnergyStorage getEnergyStorage(Level level) {
        BlockEntity blockEntity = getBlockEntity(level);
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).isPresent()) {
            IEnergyStorage energyStorage = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).orElse(null);
            if (energyStorage == null) {
                return null;
            }
            return energyStorage;
        }
        return null;
    }

    private BlockEntity getBlockEntity(Level level) {
        return level.getBlockEntity(pos);
    }

    // get the energy can be insert in the block entity
    @Override
    public int getMaxInsertEnergy(Level level) {
        IEnergyStorage energyStorage = getEnergyStorage(level);
        if (energyStorage != null) {
            return energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
        }
        return 0;
    }

    @Override
    public int insertEnergy(Level level, int amount) {
        return 0;
    }

    @Override
    public int getMaxInsertFluid(Level level) {
        // TODO: Implement logic to calculate the maximum fluid that can be inserted
        return 0;
    }

    @Override
    public IFluidHandler getFluidStorage(Level level) {
        BlockEntity blockEntity = getBlockEntity(level);
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction).isPresent()) {
            IFluidHandler fluidStorage = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction)
                    .orElse(null);
            if (fluidStorage == null) {
                return null;
            }
            return fluidStorage;
        }
        return null;
    }

    @Override
    public int insertFluid(Level level, FluidStack stack) {
        IFluidHandler fluidStorage = getFluidStorage(level);
        if (fluidStorage != null) {
            int filled = fluidStorage.fill(stack, FluidAction.EXECUTE);
            stack.setAmount(stack.getAmount() - filled);
            return filled;
        }
        return 0;
    }

    @Override
    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("pos", List.of(pos.getX(), pos.getY(), pos.getZ()));
        if (direction != null) {
            tag.putInt("direction", direction.get3DDataValue());
        }
        return tag;
    }

    @Override
    public void readFromNBT(CompoundTag compoundTag) {
        this.pos = null;
        this.direction = null;
        int[] pos = compoundTag.getIntArray("pos");
        if (pos.length != 3) {
            throw new IllegalArgumentException("Invalid position data");
        }
        this.pos = new BlockPos(pos[0], pos[1], pos[2]);
        if (compoundTag.contains("direction")) {
            this.direction = Direction.from3DDataValue(compoundTag.getInt("direction"));
        }
    }
}
