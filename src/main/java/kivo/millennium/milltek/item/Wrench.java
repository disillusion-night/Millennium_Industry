package kivo.millennium.milltek.item;

import static kivo.millennium.milltek.machine.EIOState.*;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.machine.EIOState;
import kivo.millennium.milltek.pipe.client.AbstractPipeBL;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Wrench extends Item {

    public Wrench() {
        super(new Properties().durability(1024));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (level.isClientSide)
            return InteractionResult.PASS;
        BlockPos clickedPos = pContext.getClickedPos();
        BlockState blockState = level.getBlockState(clickedPos);
        if (blockState.getBlock() instanceof AbstractPipeBL abstractPipeBL) {
            return configurePipe(level, blockState, abstractPipeBL, pContext.getPlayer(), pContext.getClickLocation(),
                    clickedPos);
        }
        // Player player = pContext.getPlayer();
        // BlockState blockState = pContext.getPlayer().getBlockStateOn();
        return InteractionResult.SUCCESS;
    }

    public InteractionResult configurePipe(Level level, BlockState pipeBlock, AbstractPipeBL pipeBL, Player player,
            Vec3 hitLocation, BlockPos blockPos) {
        Direction direction = null;
        Vec3 blockCenter = Vec3.atCenterOf(blockPos);
        Vec3 relativeHit = hitLocation.subtract(blockCenter);

        double absX = Math.abs(relativeHit.x);
        double absY = Math.abs(relativeHit.y);
        double absZ = Math.abs(relativeHit.z);

        double threshold = pipeBL.getDefaultWidth() / 2;

        if (absX > threshold && absX > absY && absX > absZ) {
            direction = relativeHit.x > 0 ? Direction.EAST : Direction.WEST;
        } else if (absY > threshold && absY > absX && absY > absZ) {
            direction = relativeHit.y > 0 ? Direction.UP : Direction.DOWN;
        } else if (absZ > threshold && absZ > absX && absZ > absY) {
            direction = relativeHit.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }

        if (absX < threshold && absY < threshold && absZ == threshold) {
            direction = relativeHit.z > 0 ? Direction.SOUTH : Direction.NORTH;
        } else if (absX < threshold && absY == threshold && absZ < threshold) {
            direction = relativeHit.y > 0 ? Direction.UP : Direction.DOWN;
        } else if (absX == threshold && absY < threshold && absZ < threshold) {
            direction = relativeHit.x > 0 ? Direction.EAST : Direction.WEST;
        }

        Main.log(relativeHit.x + "," + relativeHit.y + "," + relativeHit.z);

        if (direction != null) {
            EIOState newState = getNewSideState(level, blockPos.relative(direction), pipeBlock,
                    level.getBlockState(blockPos.relative(direction)), pipeBL, direction);

            level.setBlock(blockPos, pipeBlock.setValue(getPropertyForDirection(direction), newState), 3); // 触发客户端更新
            // level.setBlockAndUpdate(pos,
            // state.setValue(getPropertyForDirection(direction), newState));
            player.sendSystemMessage(
                    Component.literal("Switch " + direction.getName() + " to " + newState.getSerializedName()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public EIOState getNewSideState(BlockGetter blockGetter, BlockPos neighborPos, BlockState state,
            BlockState neighborState, AbstractPipeBL pipeBL, Direction direction) {
        EIOState curr = state.getValue(getPropertyForDirection(direction));
        EIOState next = NONE;
        switch (curr) {
            case CONNECT -> {
                if (pipeBL.isSamePipe(neighborState.getBlock())) {
                    next = DISCONNECTED;
                    return next;
                } else {
                    next = PULL;
                    return next;
                }
            }
            case PULL -> {
                next = PUSH;
                return next;
            }
            case PUSH -> {
                next = DISCONNECTED;
                return next;
            }
            case DISCONNECTED -> {
                if (pipeBL.canConnectTo(blockGetter, neighborPos, neighborState, direction)) {
                    next = CONNECT;
                    return next;
                } else {
                    next = NONE;
                    return next;
                }
            }
        }
        return next;
    }
}
