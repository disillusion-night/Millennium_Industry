package kivo.millennium.millind.pipe.client.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class NetworkTarget {

    /**
     * 获取目标在给定 Level 中的有效性。
     *
     * @param level 目标所在的 Level。
     * @return 如果目标仍然有效（例如，方块实体未被移除，实体仍然存在），则为 true。
     */
    public abstract boolean isValid(Level level);

    /**
     * 尝试向目标添加能量。
     *
     * @param level    目标所在的 Level。
     * @param amount   尝试添加的能量数量。
     * @param simulate 如果为 true，则只模拟操作，不实际添加。
     * @return 实际添加的能量数量。
     */
    public abstract int addEnergy(Level level, int amount, boolean simulate);

    /**
     * 尝试从目标移除能量。
     *
     * @param level    目标所在的 Level。
     * @param amount   尝试移除的能量数量。
     * @param simulate 如果为 true，则只模拟操作，不实际移除。
     * @return 实际移除的能量数量。
     */
    public abstract int removeEnergy(Level level, int amount, boolean simulate);

    /**
     * 尝试向目标填充流体。
     *
     * @param level    目标所在的 Level。
     * @param fluid    尝试填充的流体堆栈。
     * @param action   填充的动作 (EXECUTE 或 SIMULATE)。
     * @return 实际填充的流体数量。
     */
    public abstract int fillFluid(Level level, FluidStack fluid, IFluidHandler.FluidAction action);

    /**
     * 尝试从目标排出流体。
     *
     * @param level    目标所在的 Level。
     * @param amount   尝试排出的流体数量。
     * @param action   排出的动作 (EXECUTE 或 SIMULATE)。
     * @return 实际排出的流体堆栈。
     */
    public abstract FluidStack drainFluid(Level level, int amount, IFluidHandler.FluidAction action);

    /**
     * 尝试从目标排出指定类型的流体。
     *
     * @param level    目标所在的 Level。
     * @param fluid    尝试排出的流体堆栈（仅类型和 NBT 有效）。
     * @param action   排出的动作 (EXECUTE 或 SIMULATE)。
     * @return 实际排出的流体堆栈。
     */
    public abstract FluidStack drainFluid(Level level, FluidStack fluid, IFluidHandler.FluidAction action);


    /**
     * 将 NetworkTarget 的信息保存到 CompoundTag。
     *
     * @param tag 要保存到的 CompoundTag。
     * @return 保存后的 CompoundTag。
     */
    public abstract CompoundTag save(CompoundTag tag);

    /**
     * 从 CompoundTag 加载 NetworkTarget 的信息。
     *
     * @param tag 要加载的 CompoundTag。
     */
    public abstract void load(CompoundTag tag);

    /**
     * 根据 CompoundTag 中的信息创建对应的 NetworkTarget 实例。
     * 这是一个工厂方法，用于从保存的数据中重建 NetworkTarget。
     *
     * @param tag 要加载的 CompoundTag。
     * @return 创建的 NetworkTarget 实例。
     */
    public static NetworkTarget fromNbt(CompoundTag tag) {
        // TODO: 实现根据 tag 中的类型信息，创建相应的子类实例
        // 例如：
        // String type = tag.getString("type");
        // if ("block".equals(type)) {
        //     BlockNetworkTarget target = new BlockNetworkTarget(null); // 临时创建，后续 load
        //     target.load(tag);
        //     return target;
        // } else if ("entity".equals(type)) {
        //     EntityNetworkTarget target = new EntityNetworkTarget(null); // 临时创建，后续 load
        //     target.load(tag);
        //     return target;
        // }
        return null; // 或抛出异常
    }
}