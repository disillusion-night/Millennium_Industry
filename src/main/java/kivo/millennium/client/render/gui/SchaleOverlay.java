package kivo.millennium.client.render.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kivo.millennium.millind.Main;
import kivo.millennium.millind.init.MillenniumCreativeTab;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.CreativeModeTab;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getKey;

public class SchaleOverlay {
    public static void renderCustomOverlayer(GuiGraphics pGuiGraphics, int leftpos, int toppos, int width, int height, CreativeModeTab selectedTab){
        if(selectedTab == MillenniumCreativeTab.OOPARTS.get() || selectedTab == MillenniumCreativeTab.ENGINEERING_PARTS.get()){
            renderSchaleOverlay(pGuiGraphics, leftpos, toppos, width, height);
        }
    }

    public static void renderSchaleOverlay(GuiGraphics graphics, int leftpos, int toppos, int width, int height){
        RenderSystem.disableDepthTest();

        Vector2i middle = getMiddlePoint(leftpos, toppos, width, height);
        Vector2i resource = new Vector2i(1000, 1000);
        Vector2i overlay = new Vector2i(100, 100);
        RenderSystem.enableBlend();
        graphics.blit(Main.getRL("textures/gui/layer/schale_logo.png"),
                middle.x - 8 - (overlay.x / 2),
                middle.y - (overlay.y / 2),
                overlay.x,
                overlay.y,
                0,
                0,
                resource.x,
                resource.y,
                resource.x,
                resource.y);
        RenderSystem.disableBlend();
    }

    public static Vector2i getMiddlePoint(int leftpos, int toppos, int width, int height){
        return new Vector2i((leftpos + width) / 2, (toppos + height) / 2);
    }
}
