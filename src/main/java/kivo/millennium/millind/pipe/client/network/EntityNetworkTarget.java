package kivo.millennium.millind.pipe.client.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack; // 导入 FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler; // 导入 IFluidHandler

import java.util.UUID;

public class EntityNetworkTarget extends NetworkTarget {

    private UUID entityUuid;
    // 可能需要存储 Capability 相关的方向
    // private Direction capabilityDirection;

    public EntityNetworkTarget(UUID entityUuid) {
        this.entityUuid = entityUuid;
        // this.capabilityDirection = capabilityDirection;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    @Override
    public boolean isValid(Level level) {
        if (level == null || entityUuid == null) {
            return false;
        }
        Entity entity = null;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            entity = serverLevel.getEntity(entityUuid);
        } else {
            // 对于客户端或其他 Level 类型，可能需要不同的获取实体逻辑
            // 或者限制只在 ServerLevel 使用
        }

        // 检查实体是否存在且仍然是具有 Capability 的类型
        // TODO: 检查具体的 Capability 类型，例如 ForgeCapabilities.FLUID_HANDLER 或 ForgeCapabilities.ENERGY
        return entity != null && (entity.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */).isPresent() ||
                entity.getCapability(ForgeCapabilities.ENERGY /*, capabilityDirection */).isPresent() /* 或其他 capability */);
    }

    @Override
    public int addEnergy(Level level, int amount, boolean simulate) {
        if (level == null || level.isClientSide || entityUuid == null) return 0;

        Entity entity = null;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            entity = serverLevel.getEntity(entityUuid);
        } else {
            // 同上，处理非 ServerLevel 的情况
        }

        if (entity != null) {
            LazyOptional<IEnergyStorage> capability = entity.getCapability(ForgeCapabilities.ENERGY /*, capabilityDirection */);
            if (capability.isPresent()) {
                IEnergyStorage energyStorage = capability.orElseThrow(RuntimeException::new);
                return energyStorage.receiveEnergy(amount, simulate);
            }
        }
        return 0;
    }

    @Override
    public int removeEnergy(Level level, int amount, boolean simulate) {
        if (level == null || level.isClientSide || entityUuid == null) return 0;

        Entity entity = null;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            entity = serverLevel.getEntity(entityUuid);
        } else {
            // 同上，处理非 ServerLevel 的情况
        }

        if (entity != null) {
            LazyOptional<IEnergyStorage> capability = entity.getCapability(ForgeCapabilities.ENERGY /*, capabilityDirection */);
            if (capability.isPresent()) {
                IEnergyStorage energyStorage = capability.orElseThrow(RuntimeException::new);
                return energyStorage.extractEnergy(amount, simulate);
            }
        }
        return 0;
    }

    @Override
    public int fillFluid(Level level, FluidStack fluid, IFluidHandler.FluidAction action) {
        if (level == null || level.isClientSide || entityUuid == null || fluid.isEmpty()) return 0;

        Entity entity = null;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            entity = serverLevel.getEntity(entityUuid);
        } else {
            // 同上，处理非 ServerLevel 的情况
        }

        if (entity != null) {
            LazyOptional<IFluidHandler> capability = entity.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */);
            if (capability.isPresent()) {
                IFluidHandler fluidHandler = capability.orElseThrow(RuntimeException::new);
                return fluidHandler.fill(fluid, action);
            }
        }
        return 0;
    }

    @Override
    public FluidStack drainFluid(Level level, int amount, IFluidHandler.FluidAction action) {
        if (level == null || level.isClientSide || entityUuid == null || amount <= 0) return FluidStack.EMPTY;

        Entity entity = null;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            entity = serverLevel.getEntity(entityUuid);
        } else {
            // 同上，处理非 ServerLevel 的情况
        }

        if (entity != null) {
            LazyOptional<IFluidHandler> capability = entity.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */);
            if (capability.isPresent()) {
                IFluidHandler fluidHandler = capability.orElseThrow(RuntimeException::new);
                return fluidHandler.drain(amount, action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drainFluid(Level level, FluidStack fluid, IFluidHandler.FluidAction action) {
        if (level == null || level.isClientSide || entityUuid == null || fluid.isEmpty()) return FluidStack.EMPTY;

        Entity entity = null;
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            entity = serverLevel.getEntity(entityUuid);
        } else {
            // 同上，处理非 ServerLevel 的情况
        }

        if (entity != null) {
            LazyOptional<IFluidHandler> capability = entity.getCapability(ForgeCapabilities.FLUID_HANDLER /*, capabilityDirection */);
            if (capability.isPresent()) {
                IFluidHandler fluidHandler = capability.orElseThrow(RuntimeException::new);
                return fluidHandler.drain(fluid, action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("type", "entity"); // 标识为 EntityNetworkTarget
        if (entityUuid != null) {
            tag.putUUID("entity_uuid", entityUuid);
        }
        // 保存 capabilityDirection 如果使用了的话
        // if (capabilityDirection != null) {
        //     tag.putInt("capability_direction", capabilityDirection.ordinal());
        // }
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        // 从 tag 中加载 entity_uuid 和 capabilityDirection
        if (tag.hasUUID("entity_uuid")) {
            this.entityUuid = tag.getUUID("entity_uuid");
        }
        // if (tag.contains("capability_direction")) {
        //     this.capabilityDirection = Direction.values()[tag.getInt("capability_direction")];
        // }
    }
}