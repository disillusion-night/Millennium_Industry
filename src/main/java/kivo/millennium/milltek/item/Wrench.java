package kivo.millennium.milltek.item;

import static kivo.millennium.milltek.pipe.network.AbstractPipeBL.getPropertyForDirection;
import static kivo.millennium.milltek.pipe.network.EPipeState.*;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.pipe.network.AbstractPipeBL;
import kivo.millennium.milltek.pipe.network.EPipeState;
import kivo.millennium.milltek.pipe.network.PipeBE;
import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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
            if (!(level instanceof ServerLevel)) {
                return InteractionResult.SUCCESS; // 仅在服务器端处理
            }
            ServerLevel serverLevel = (ServerLevel) level;
            PipeBE<?> pipeBE = (PipeBE<?>) serverLevel.getBlockEntity(blockPos);
            EPipeState newState = setNewSideState(serverLevel, blockPos.relative(direction), pipeBlock,
                    level.getBlockState(blockPos.relative(direction)), pipeBE, direction);
            //level.setBlock(blockPos, pipeBlock.setValue(getPropertyForDirection(direction), newState), 3); // 触发客户端更新
            player.sendSystemMessage(
                    Component.literal("Switch " + direction.getName() + " to " + newState.toString()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public EPipeState setNewSideState(ServerLevel level, BlockPos neighborPos, BlockState state, BlockState neighborState, PipeBE<?> pipeBE, Direction direction) {
        EPipeState curr = state.getValue(getPropertyForDirection(direction));
        switch (curr) {
            case CONNECT -> {
                pipeBE.setDirectionState(direction, PULL, 3);
                return  PULL;
            }
            case PULL -> {
                pipeBE.setDirectionState(direction, PUSH, 3);
                return  PUSH;
            }
            case PUSH -> {
                pipeBE.setDirectionState(direction, DISCONNECT, 3);
                return DISCONNECT;
            }
            case DISCONNECT -> {
                if (neighborState.isAir() || !neighborState.hasBlockEntity()) {
                    pipeBE.setDirectionState(direction, NONE, 3);
                    return NONE;
                } else if (neighborState.getBlock() instanceof AbstractPipeBL) {
                    if (neighborState.getValue(getPropertyForDirection(direction)) == DISCONNECT) {
                        pipeBE.setDirectionState(direction, NONE, 3);
                        return NONE;
                    } else {
                        if (pipeBE.getCapabilityType().equals(((PipeBE<?>) level.getBlockEntity(neighborPos)).getCapabilityType())) {
                            pipeBE.setDirectionState(direction, CONNECT, 3);
                            return CONNECT;
                        } else {
                            pipeBE.setDirectionState(direction, NONE, 3);
                            return NONE;
                        }
                    }
                } else {
                    pipeBE.setDirectionState(direction, CONNECT, 3);
                    return CONNECT;
                }
            }
            case PIPE -> {
                pipeBE.setDirectionState(direction, DISCONNECT, 3);
                pipeBE.recalculateNetwork();
                return DISCONNECT;
            }
            case NONE -> {
                pipeBE.setDirectionState(direction, DISCONNECT, 3);
                return DISCONNECT;
            }
            default -> {
                pipeBE.setDirectionState(direction, NONE, 3);
                return NONE;
            }
        }
    }
}
