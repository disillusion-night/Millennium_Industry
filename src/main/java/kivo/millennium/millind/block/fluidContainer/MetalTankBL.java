package kivo.millennium.millind.block.fluidContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;


public class MetalTankBL extends Block implements EntityBlock {

    public MetalTankBL() {
        super(Properties.of().noOcclusion());
    }

    public static final VoxelShape SHAPE = Block.box(0,0,0,16,16,16);


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof MetalFluidTankBE) {
            MetalFluidTankBE metalTank = (MetalFluidTankBE) tileentity;
            InteractionResult result = metalTank.handleInteraction(player, hand);
            if (result.consumesAction()) {
                return result;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MetalFluidTankBE(blockPos, blockState);
    }
}
