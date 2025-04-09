package kivo.millennium.millind.cables.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;
import static kivo.millennium.millind.cables.BakedModelHelper.quad;
import static kivo.millennium.millind.cables.BakedModelHelper.v;
import static kivo.millennium.millind.cables.ConnectorType.*;
import static net.minecraft.core.Direction.*;

public class PipeBakedModel implements IDynamicBakedModel {
    private TextureAtlasSprite pipeTexture;

    public PipeBakedModel() {
        pipeTexture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(getRL("block/your_pipe_texture")); // 替换为你的纹理路径
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable net.minecraft.client.renderer.RenderType layer) {
        List<BakedQuad> quads = new ArrayList<>();
        if (state == null || side != null) {
            return quads; // 处理物品渲染或特定面渲染
        }

        float thickness = state.getValue(THICKNESS);
        double min = 0.5 - thickness;
        double max = 0.5 + thickness;

        ConnectorType north = state.getValue(NORTH);
        ConnectorType south = state.getValue(SOUTH);
        ConnectorType west = state.getValue(WEST);
        ConnectorType east = state.getValue(EAST);
        ConnectorType up = state.getValue(UP);
        ConnectorType down = state.getValue(DOWN);

        boolean isStraightNS = (north != NONE && south != NONE && east == NONE && west == NONE && up == NONE && down == NONE);
        boolean isStraightEW = (east != NONE && west != NONE && north == NONE && south == NONE && up == NONE && down == NONE);
        boolean isStraightUD = (up != NONE && down != NONE && north == NONE && south == NONE && east == NONE && west == NONE);
        boolean isStraight = isStraightNS || isStraightEW || isStraightUD;

        if (isStraight) {
            // 渲染为无缝连接的长方体
            double start = 0.0;
            double end = 1.0;
            if (isStraightNS) {
                quads.addAll(quad(v(min, max, start), v(max, max, start), v(max, min, start), v(min, min, start), pipeTexture, SOUTH));
                quads.addAll(quad(v(min, min, end), v(max, min, end), v(max, max, end), v(min, max, end), pipeTexture, NORTH));
                quads.addAll(quad(v(min, min, start), v(min, min, end), v(max, min, end), v(max, min, start), pipeTexture, DOWN));
                quads.addAll(quad(v(min, max, end), v(min, max, start), v(max, max, start), v(max, max, end), pipeTexture, UP));
                quads.addAll(quad(v(min, max, end), v(min, min, end), v(min, min, start), v(min, max, start), pipeTexture, WEST));
                quads.addAll(quad(v(max, max, start), v(max, min, start), v(max, min, end), v(max, max, end), pipeTexture, EAST));
            } else if (isStraightEW) {
                quads.addAll(quad(v(start, max, min), v(start, max, max), v(start, min, max), v(start, min, min), pipeTexture, WEST));
                quads.addAll(quad(v(end, min, min), v(end, min, max), v(end, max, max), v(end, max, min), pipeTexture, EAST));
                quads.addAll(quad(v(start, min, min), v(end, min, min), v(end, max, min), v(start, max, min), pipeTexture, DOWN));
                quads.addAll(quad(v(start, max, max), v(end, max, max), v(end, min, max), v(start, min, max), pipeTexture, UP));
                quads.addAll(quad(v(start, max, max), v(start, min, max), v(start, min, min), v(start, max, min), pipeTexture, NORTH));
                quads.addAll(quad(v(end, max, min), v(end, min, min), v(end, min, max), v(end, max, max), pipeTexture, SOUTH));
            } else if (isStraightUD) {
                quads.addAll(quad(v(min, start, max), v(max, start, max), v(max, start, min), v(min, start, min), pipeTexture, DOWN));
                quads.addAll(quad(v(min, end, min), v(max, end, min), v(max, end, max), v(min, end, max), pipeTexture, UP));
                quads.addAll(quad(v(min, start, min), v(min, end, min), v(max, end, min), v(max, start, min), pipeTexture, WEST));
                quads.addAll(quad(v(min, end, max), v(min, start, max), v(max, start, max), v(max, end, max), pipeTexture, EAST));
                quads.addAll(quad(v(min, end, max), v(min, start, max), v(min, start, min), v(min, end, min), pipeTexture, SOUTH));
                quads.addAll(quad(v(max, end, min), v(max, start, min), v(max, start, max), v(max, end, max), pipeTexture, NORTH));
            }
        } else {
            // 渲染中心连接处
            quads.addAll(quad(v(min, max, min), v(max, max, min), v(max, min, min), v(min, min, min), pipeTexture, SOUTH));
            quads.addAll(quad(v(min, min, max), v(max, min, max), v(max, max, max), v(min, max, max), pipeTexture, NORTH));
            quads.addAll(quad(v(min, min, min), v(min, min, max), v(max, min, max), v(max, min, min), pipeTexture, DOWN));
            quads.addAll(quad(v(min, max, max), v(min, max, min), v(max, max, min), v(max, max, max), pipeTexture, UP));
            quads.addAll(quad(v(min, max, max), v(min, min, max), v(min, min, min), v(min, max, min), pipeTexture, WEST));
            quads.addAll(quad(v(max, max, min), v(max, min, min), v(max, min, max), v(max, max, max), pipeTexture, EAST));

            // 渲染延伸部分
            double extensionLength = max; // 延伸长度，可以根据需要调整

            if (north != NONE) {
                quads.addAll(createExtensionQuads(min, max, min, max, 0, min, pipeTexture, NORTH));
            }
            if (south != NONE) {
                quads.addAll(createExtensionQuads(min, max, min, max, max, 1, pipeTexture, SOUTH));
            }
            if (west != NONE) {
                quads.addAll(createExtensionQuads(0, min, min, max, min, max, pipeTexture, WEST));
            }
            if (east != NONE) {
                quads.addAll(createExtensionQuads(max, 1, min, max, min, max, pipeTexture, EAST));
            }
            if (up != NONE) {
                quads.addAll(createExtensionQuads(min, max, max, 1, min, max, pipeTexture, UP));
            }
            if (down != NONE) {
                quads.addAll(createExtensionQuads(min, max, 0, min, min, max, pipeTexture, DOWN));
            }
        }

        return quads;
    }

    // Helper method to create quads for extensions
    private List<BakedQuad> createExtensionQuads(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax, TextureAtlasSprite sprite, Direction facing) {
        List<BakedQuad> quads = new ArrayList<>();
        switch (facing) {
            case NORTH -> quads.addAll(quad(v(xMin, yMax, zMax), v(xMax, yMax, zMax), v(xMax, yMin, zMax), v(xMin, yMin, zMax), sprite, facing));
            case SOUTH -> quads.addAll(quad(v(xMin, yMin, zMin), v(xMax, yMin, zMin), v(xMax, yMax, zMin), v(xMin, yMax, zMin), sprite, facing));
            case WEST -> quads.addAll(quad(v(xMin, yMax, zMin), v(xMin, yMax, zMax), v(xMin, yMin, zMax), v(xMin, yMin, zMin), sprite, facing));
            case EAST -> quads.addAll(quad(v(xMax, yMin, zMin), v(xMax, yMin, zMax), v(xMax, yMax, zMax), v(xMax, yMax, zMin), sprite, facing));
            case UP -> quads.addAll(quad(v(xMin, yMax, zMin), v(xMax, yMax, zMin), v(xMax, yMax, zMax), v(xMin, yMax, zMax), sprite, facing));
            case DOWN -> quads.addAll(quad(v(xMin, yMin, zMax), v(xMax, yMin, zMax), v(xMax, yMin, zMin), v(xMin, yMin, zMin), sprite, facing));
        }
        return quads;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return pipeTexture;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Nonnull
    @Override
    public net.minecraftforge.client.ChunkRenderTypeSet getRenderTypes(@Nonnull BlockState state, @Nonnull RandomSource rand, @Nonnull ModelData data) {
        return net.minecraftforge.client.ChunkRenderTypeSet.all();
    }

    @Nonnull
    @Override
    public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
        return net.minecraft.client.renderer.block.model.ItemTransforms.NO_TRANSFORMS;
    }

    @Nonnull
    @Override
    public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
        return net.minecraft.client.renderer.block.model.ItemOverrides.EMPTY;
    }
}