package kivo.millennium.millind.pipe.client.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack; // 导入 FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler; // 导入 IFluidHandler

public class BlockNetworkTarget extends NetworkTarget {

    private BlockPos pos;
    // 可能需要存储与 Capability 相关的方向，如果 Capability 是基于方向的
    // private Direction capabilityDirection;

    public BlockNetworkTarget(BlockPos pos) {
        this.pos = pos;
        // this.capabilityDirection = capabilityDirection;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean isValid(Level level) {
        if (level == null || pos == null) {
            return false;
        }
        // 检查方块实体是否存在且仍然是具有 Capability 的类型
        BlockEntity be = level.getBlockEntity(pos);
        // TODO: 检查具体的 Capability 类型，例如 ForgeCapabilities.FLUID_HANDLER 或 ForgeCapabilities.ENERGY
        return be != null && (be.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */).isPresent() ||
                be.getCapability(ForgeCapabilities.ENERGY /*, capabilityDirection */).isPresent() /* 或其他 capability */);
    }

    @Override
    public int addEnergy(Level level, int amount, boolean simulate) {
        if (level == null || level.isClientSide || pos == null) return 0;

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            // 获取 Capability，可能需要考虑方向
            LazyOptional<IEnergyStorage> capability = be.getCapability(ForgeCapabilities.ENERGY /*, capabilityDirection */);
            if (capability.isPresent()) {
                IEnergyStorage energyStorage = capability.orElseThrow(RuntimeException::new);
                return energyStorage.receiveEnergy(amount, simulate);
            }
        }
        return 0;
    }

    @Override
    public int removeEnergy(Level level, int amount, boolean simulate) {
        if (level == null || level.isClientSide || pos == null) return 0;

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            LazyOptional<IEnergyStorage> capability = be.getCapability(ForgeCapabilities.ENERGY /*, capabilityDirection */);
            if (capability.isPresent()) {
                IEnergyStorage energyStorage = capability.orElseThrow(RuntimeException::new);
                return energyStorage.extractEnergy(amount, simulate);
            }
        }
        return 0;
    }

    @Override
    public int fillFluid(Level level, FluidStack fluid, IFluidHandler.FluidAction action) {
        if (level == null || level.isClientSide || pos == null || fluid.isEmpty()) return 0;

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            LazyOptional<IFluidHandler> capability = be.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */);
            if (capability.isPresent()) {
                IFluidHandler fluidHandler = capability.orElseThrow(RuntimeException::new);
                return fluidHandler.fill(fluid, action);
            }
        }
        return 0;
    }

    @Override
    public FluidStack drainFluid(Level level, int amount, IFluidHandler.FluidAction action) {
        if (level == null || level.isClientSide || pos == null || amount <= 0) return FluidStack.EMPTY;

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            LazyOptional<IFluidHandler> capability = be.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */);
            if (capability.isPresent()) {
                IFluidHandler fluidHandler = capability.orElseThrow(RuntimeException::new);
                return fluidHandler.drain(amount, action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drainFluid(Level level, FluidStack fluid, IFluidHandler.FluidAction action) {
        if (level == null || level.isClientSide || pos == null || fluid.isEmpty()) return FluidStack.EMPTY;

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            LazyOptional<IFluidHandler> capability = be.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */);
            if (capability.isPresent()) {
                IFluidHandler fluidHandler = capability.orElseThrow(RuntimeException::new);
                return fluidHandler.drain(fluid, action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("type", "block"); // 标识为 BlockNetworkTarget
        if (pos != null) {
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
        }
        // 保存 capabilityDirection 如果使用了的话
        // if (capabilityDirection != null) {
        //     tag.putInt("capability_direction", capabilityDirection.ordinal());
        // }
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        // 从 tag 中加载 pos 和 capabilityDirection
        this.pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        // if (tag.contains("capability_direction")) {
        //     this.capabilityDirection = Direction.values()[tag.getInt("capability_direction")];
        // }
    }
}