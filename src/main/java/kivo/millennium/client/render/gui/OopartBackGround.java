package kivo.millennium.client.render.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kivo.millennium.millind.item.Oopart.IBABackground;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class OopartBackGround {
        @OnlyIn(Dist.CLIENT)
        public static void renderCustomBackground(GuiGraphics graphics, ItemStack pStack, int px, int py) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }
            if (pStack.isEmpty()) {
                return;
            }
            if (pStack.getItem() instanceof IBABackground){
                renderBABackGround(graphics, px, py, ((IBABackground) pStack.getItem()).getBackGround());
            }
        }

        @OnlyIn(Dist.CLIENT)
        private static void renderBABackGround(GuiGraphics graphics, int x, int y, ResourceLocation background) {
            RenderSystem.disableDepthTest();
            graphics.blit(background,
                    x,
                    y,
                    16,
                    16,
                    0,
                    0,
                    64,
                    64,
                    64,
                    64
            );
        }
    }

