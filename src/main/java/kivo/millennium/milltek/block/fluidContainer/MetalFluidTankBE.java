package kivo.millennium.milltek.block.fluidContainer;

import kivo.millennium.milltek.capability.MetalTankFluidHandler;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MetalFluidTankBE extends BlockEntity {

    private final MetalTankFluidHandler fluidTank = new MetalTankFluidHandler();
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> fluidTank);

    public MetalFluidTankBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.METAL_FLUID_TANK_BE.get(), pos, state);
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidTank.readFromNBT(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        fluidTank.writeToNBT(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        // 实现标签更新，用于同步数据给客户端
        return this.saveWithoutMetadata();
    }


    public InteractionResult handleInteraction(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        // 尝试使用FluidUtil.fill来自动处理流体和容器的交互
        if (!heldItem.isEmpty() && FluidUtil.getFluidHandler(heldItem).isPresent()) {
            return FluidUtil.interactWithFluidHandler(player, hand, this.fluidTank) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }
}
