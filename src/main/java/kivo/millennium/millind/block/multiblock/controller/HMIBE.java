package kivo.millennium.millind.block.multiblock.controller;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HMIBE extends BlockEntity {
    private static final Logger log = LoggerFactory.getLogger(HMIBE.class);

    public HMIBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.HMI_BE.get(), pPos, pBlockState);
    }

    public InteractionResult handleRightClickEvent(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {

            Main.log("a");
            return InteractionResult.SUCCESS; // 返回成功
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
