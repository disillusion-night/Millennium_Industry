package kivo.millennium.millind.multiblock;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

public class BlockTypeValidator {
    private final Set<BlockState> validStates = new HashSet<>();
    private final Set<TagKey<Block>> validTags = new HashSet<>();

    /**
     * 添加一个具体的BlockState到允许列表中。
     */
    public void addValidState(BlockState state) {
        this.validStates.add(state);
    }

    /**
     * 添加一个标签到允许的标签列表中。
     */
    public void addValidTag(TagKey<Block> tag) {
        this.validTags.add(tag);
    }

    /**
     * 检查给定的BlockState是否合法。
     */
    public boolean isValid(BlockState state) {
        // 首先检查是否是允许的具体状态之一
        if (validStates.contains(state)) {
            return true;
        }
        // 然后检查是否匹配任何标签
        for (TagKey<Block> tag : validTags) {
            if (state.is(tag)) {
                return true;
            }
        }
        return false;
    }
}