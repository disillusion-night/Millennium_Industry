package kivo.millennium.milltek.pipe.client;

import kivo.millennium.milltek.pipe.network.EPipeState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.NotNull;

import kivo.millennium.milltek.pipe.network.AbstractPipeBL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import static kivo.millennium.milltek.pipe.client.PipePatterns.SpriteIdx.*;
import static kivo.millennium.milltek.pipe.network.AbstractPipeBL.*;
import static kivo.millennium.milltek.util.BakedModelHelper.quad;
import static kivo.millennium.milltek.util.BakedModelHelper.v;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PipeBakedModel implements IDynamicBakedModel {

    private final IGeometryBakingContext context;

    private final ResourceLocation normal;
    private final ResourceLocation none;
    private final ResourceLocation cross;
    private final ResourceLocation three;
    private final ResourceLocation corner;

    public PipeBakedModel(IGeometryBakingContext context, ResourceLocation corner, ResourceLocation normal,
            ResourceLocation none, ResourceLocation three, ResourceLocation cross) {
        this.context = context;
        this.corner = corner;
        this.normal = normal;
        this.none = none;
        this.three = three;
        this.cross = cross;
    }

    private TextureAtlasSprite spriteNoneCable;
    private TextureAtlasSprite spriteNormalCable;
    private TextureAtlasSprite spriteCornerCable;
    private TextureAtlasSprite spriteThreeCable;
    private TextureAtlasSprite spriteCrossCable;

    private TextureAtlasSprite getSpriteNormal(PipePatterns.SpriteIdx idx) {
        initTextures();
        return switch (idx) {
            case SPRITE_NONE -> spriteNoneCable;
            case SPRITE_STRAIGHT -> spriteNormalCable;
            case SPRITE_CORNER -> spriteCornerCable;
            case SPRITE_THREE -> spriteThreeCable;
            case SPRITE_CROSS -> spriteCrossCable;
        };
    }

    static {
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, false, false, false),
                PipePatterns.QuadSetting.of(SPRITE_NONE, 0));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, true, false, false),
                PipePatterns.QuadSetting.of(SPRITE_CORNER, 0));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, true, true, false),
                PipePatterns.QuadSetting.of(SPRITE_CORNER, 1));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, false, true, true),
                PipePatterns.QuadSetting.of(SPRITE_CORNER, 2));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, false, false, true),
                PipePatterns.QuadSetting.of(SPRITE_CORNER, 3));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, false, false, false),
                PipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 3));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, true, false, false),
                PipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 0));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, false, true, false),
                PipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 1));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, false, false, true),
                PipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 2));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, true, false, true),
                PipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 0));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, false, true, false),
                PipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 1));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, true, true, false),
                PipePatterns.QuadSetting.of(SPRITE_THREE, 0));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(false, true, true, true),
                PipePatterns.QuadSetting.of(SPRITE_THREE, 1));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, false, true, true),
                PipePatterns.QuadSetting.of(SPRITE_THREE, 2));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, true, false, true),
                PipePatterns.QuadSetting.of(SPRITE_THREE, 3));
        PipePatterns.PATTERNS.put(PipePatterns.Pattern.of(true, true, true, true),
                PipePatterns.QuadSetting.of(SPRITE_CROSS, 0));
    }

    private void initTextures() {
        if (spriteNormalCable == null) {
            spriteNormalCable = getTexture(normal);
            spriteNoneCable = getTexture(none);
            spriteCornerCable = getTexture(corner);
            spriteThreeCable = getTexture(three);
            spriteCrossCable = getTexture(cross);
        }
    }

    private TextureAtlasSprite getTexture(ResourceLocation location) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(location);
    }

    Function<PipePatterns.SpriteIdx, TextureAtlasSprite> spriteGetter = this::getSpriteNormal;

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand,
            @NotNull ModelData extraData, @Nullable RenderType layer) {
        initTextures();
        List<BakedQuad> quads = new ArrayList<>();

        TextureAtlasSprite spriteCable = spriteNormalCable;
        if (state == null) {
            double width = 0.5;

            quads.add(quad(v(0, 1, 1), v(1, 1, 1), v(1, 1, 0), v(0, 1, 0), spriteNoneCable));
            quads.add(quad(v(0, 0, 0), v(1, 0, 0), v(1, 0, 1), v(0, 0, 1), spriteNoneCable));
            quads.add(quad(v(1, 0, 0), v(1, 1, 0), v(1, 1, 1), v(1, 0, 1), spriteNoneCable));
            quads.add(quad(v(0, 0, 1), v(0, 1, 1), v(0, 1, 0), v(0, 0, 0), spriteNoneCable));
            quads.add(quad(v(0, 1, 0), v(1, 1, 0), v(1, 0, 0), v(0, 0, 0), spriteNoneCable));
            quads.add(quad(v(0, 0, 1), v(1, 0, 1), v(1, 1, 1), v(0, 1, 1), spriteNoneCable));

            return quads;

        }
        /*
         * if (state == null || side != null || (layer != null &&
         * !layer.equals(RenderType.solid()))) {
         * return quads;
         * }
         */

        if (!(state.getBlock() instanceof AbstractPipeBL AbstractPipeBL)) {
            return quads;
        }

        double m = (1 - AbstractPipeBL.getDefaultWidth()) / 2;
        double n = 1 - m;

        EPipeState north = state.getValue(NORTH);
        EPipeState east = state.getValue(EAST);
        EPipeState south = state.getValue(SOUTH);
        EPipeState west = state.getValue(WEST);
        EPipeState up = state.getValue(UP);
        EPipeState down = state.getValue(DOWN);

        if (up.isNormal()) {
            quads.add(quad(v(n, 1, m), v(n, 1, n), v(n, n, n), v(n, n, m), spriteCable));
            quads.add(quad(v(m, 1, n), v(m, 1, m), v(m, n, m), v(m, n, n), spriteCable));
            quads.add(quad(v(m, 1, m), v(n, 1, m), v(n, n, m), v(m, n, m), spriteCable));
            quads.add(quad(v(m, n, n), v(n, n, n), v(n, 1, n), v(m, 1, n), spriteCable));
        } else {
            PipePatterns.QuadSetting pattern = PipePatterns.findPattern(west, south, east, north);
            quads.add(quad(v(m, n, n), v(n, n, n), v(n, n, m), v(m, n, m), spriteGetter.apply(pattern.sprite()),
                    pattern.rotation()));
        }

        if (down.isNormal()) {
            quads.add(quad(v(n, m, m), v(n, m, n), v(n, 0, n), v(n, 0, m), spriteCable));
            quads.add(quad(v(m, m, n), v(m, m, m), v(m, 0, m), v(m, 0, n), spriteCable));
            quads.add(quad(v(m, m, m), v(n, m, m), v(n, 0, m), v(m, 0, m), spriteCable));
            quads.add(quad(v(m, 0, n), v(n, 0, n), v(n, m, n), v(m, m, n), spriteCable));
        } else {
            PipePatterns.QuadSetting pattern = PipePatterns.findPattern(west, north, east, south);
            quads.add(quad(v(m, m, m), v(n, m, m), v(n, m, n), v(m, m, n), spriteGetter.apply(pattern.sprite()),
                    pattern.rotation()));
        }

        if (east.isNormal()) {
            quads.add(quad(v(1, n, n), v(1, n, m), v(n, n, m), v(n, n, n), spriteCable));
            quads.add(quad(v(1, m, m), v(1, m, n), v(n, m, n), v(n, m, m), spriteCable));
            quads.add(quad(v(1, n, m), v(1, m, m), v(n, m, m), v(n, n, m), spriteCable));
            quads.add(quad(v(1, m, n), v(1, n, n), v(n, n, n), v(n, m, n), spriteCable));
        } else {
            PipePatterns.QuadSetting pattern = PipePatterns.findPattern(down, north, up, south);
            quads.add(quad(v(n, m, m), v(n, n, m), v(n, n, n), v(n, m, n), spriteGetter.apply(pattern.sprite()),
                    pattern.rotation()));
        }

        if (west.isNormal()) {
            quads.add(quad(v(m, n, n), v(m, n, m), v(0, n, m), v(0, n, n), spriteCable));
            quads.add(quad(v(m, m, m), v(m, m, n), v(0, m, n), v(0, m, m), spriteCable));
            quads.add(quad(v(m, n, m), v(m, m, m), v(0, m, m), v(0, n, m), spriteCable));
            quads.add(quad(v(m, m, n), v(m, n, n), v(0, n, n), v(0, m, n), spriteCable));
        } else {
            PipePatterns.QuadSetting pattern = PipePatterns.findPattern(down, south, up, north);
            quads.add(quad(v(m, m, n), v(m, n, n), v(m, n, m), v(m, m, m), spriteGetter.apply(pattern.sprite()),
                    pattern.rotation()));
        }

        if (north.isNormal()) {
            quads.add(quad(v(m, n, m), v(n, n, m), v(n, n, 0), v(m, n, 0), spriteCable));
            quads.add(quad(v(m, m, 0), v(n, m, 0), v(n, m, m), v(m, m, m), spriteCable));
            quads.add(quad(v(n, m, 0), v(n, n, 0), v(n, n, m), v(n, m, m), spriteCable));
            quads.add(quad(v(m, m, m), v(m, n, m), v(m, n, 0), v(m, m, 0), spriteCable));
        } else {
            PipePatterns.QuadSetting pattern = PipePatterns.findPattern(west, up, east, down);
            quads.add(quad(v(m, n, m), v(n, n, m), v(n, m, m), v(m, m, m), spriteGetter.apply(pattern.sprite()),
                    pattern.rotation()));
        }

        if (south.isNormal()) {
            quads.add(quad(v(m, n, 1), v(n, n, 1), v(n, n, n), v(m, n, n), spriteCable));
            quads.add(quad(v(m, m, n), v(n, m, n), v(n, m, 1), v(m, m, 1), spriteCable));
            quads.add(quad(v(n, m, n), v(n, n, n), v(n, n, 1), v(n, m, 1), spriteCable));
            quads.add(quad(v(m, m, 1), v(m, n, 1), v(m, n, n), v(m, m, n), spriteCable));
        } else {
            PipePatterns.QuadSetting pattern = PipePatterns.findPattern(west, down, east, up);
            quads.add(quad(v(m, m, n), v(n, m, n), v(n, n, n), v(m, n, n), spriteGetter.apply(pattern.sprite()),
                    pattern.rotation()));
        }

        return quads;
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
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    @Nonnull
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
            @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.translucent());
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return spriteNormalCable == null
                ? Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply((new ResourceLocation("minecraft", "missingno")))
                : spriteNormalCable;
    }

    @Nonnull
    @Override
    public ItemTransforms getTransforms() {
        return context.getTransforms();
    }

    @Nonnull
    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}