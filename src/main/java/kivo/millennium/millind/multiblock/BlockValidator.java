package kivo.millennium.millind.multiblock;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockValidator {

    private final Set<TagKey<Block>> validTags;
    private final Set<Block> validBlocks;

    /**
     * 构造方法，用于创建 BlockValidator 对象。
     *
     * @param tags   有效的方块标签数组。
     * @param blocks 有效的方块数组。
     */
    public BlockValidator(TagKey<Block>[] tags, Block[] blocks) {
        this.validTags = new HashSet<>(Arrays.asList(tags)); // 将数组转换为 HashSet 以提高查找效率和去重
        this.validBlocks = new HashSet<>(Arrays.asList(blocks)); // 同样将数组转换为 HashSet
    }

    /**
     * 验证给定的方块是否有效。
     * 方块被认为是有效的条件是：
     * 1. 方块本身存在于 validBlocks 列表中。
     * 2. 方块拥有 validTags 列表中的任何一个标签。
     *
     * @param blockState 需要验证的方块。
     * @return 如果方块有效则返回 true，否则返回 false。
     */
    public boolean isValid(BlockState blockState) {
        // 检查方块是否直接存在于 validBlocks 集合中
        if (validBlocks.contains(blockState.getBlock())) {
            return true;
        }

        // 检查方块是否拥有任何 validTags 中的标签
        for (TagKey<Block> tag : validTags) {
            if (blockState.is(tag)) {
                return true;
            }
        }

        // 如果以上条件都不满足，则方块无效
        return false;
    }
}