package kivo.millennium.millind.container.Device;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

// 自定义槽位来显示流体
public class FluidSlot extends Slot {
    private final FluidTank fluidTank;

    public FluidSlot(FluidTank fluidTank, int x, int y) {
        super(null, -1, x, y); // 我们不直接管理物品，所以 ItemHandler 为 null，index 为 -1
        this.fluidTank = fluidTank;
    }

    public FluidStack getFluidStack() {
        return fluidTank.getFluid();
    }

    @Override
    public boolean hasItem() {
        return !fluidTank.getFluid().isEmpty();
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY; // 流体槽位不包含实际的物品
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false; // 不允许放入物品
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false; // 不允许取出物品
    }
}