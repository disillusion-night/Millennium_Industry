package kivo.millennium.millind.pipe.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractPipeBE extends BlockEntity {
    private List<BlockPos> outputs = null;
    private List<Direction> outputsDir = null;

    protected AbstractPipeBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tickServer() {
        if (checkContent()) {
            checkOutputs();
            if (!outputs.isEmpty()) {
                int amount = ContentStored() / outputs.size();
                for (int i = 0; i < outputs.size(); i++) {
                    BlockEntity te = level.getBlockEntity(outputs.get(i));
                    if (te != null) {
                        check(te, outputsDir.get(i), amount);
                    }
                }
            }
        }
    }

    protected abstract boolean checkContent();

    protected abstract int ContentStored();

    private void checkOutputs() {
        if (outputs == null) {
            outputs = List.of();
            outputsDir = List.of();
            traverse(worldPosition, cable -> {
                for (Direction direction : Direction.values()) {
                    BlockPos p = cable.getBlockPos().relative(direction);
                    BlockEntity te = level.getBlockEntity(p);
                    if (te != null && !(te instanceof AbstractPipeBE)) {
                        check(te, outputs, outputsDir);
                    }
                }
            });
        }
    }

    protected abstract boolean check(BlockEntity te, Direction direction, int amount);

    protected abstract boolean check(BlockEntity te, List<BlockPos> outputs, List<Direction> outputsDir);

    public void markDirty() {
        traverse(worldPosition, cable -> cable.outputs = null);
    }

    private void traverse(BlockPos pos, Consumer<AbstractPipeBE> consumer) {
        Set<BlockPos> traversed = new HashSet<>();
        traversed.add(pos);
        consumer.accept(this);
        traverse(pos, traversed, consumer);
    }

    private void traverse(BlockPos pos, Set<BlockPos> traversed, Consumer<AbstractPipeBE> consumer) {
        for (Direction direction : Direction.values()) {
            BlockPos p = pos.relative(direction);
            if (!traversed.contains(p)) {
                traversed.add(p);
                if (level.getBlockEntity(p) instanceof AbstractPipeBE cable) {
                    consumer.accept(cable);
                    cable.traverse(p, traversed, consumer);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }
}

