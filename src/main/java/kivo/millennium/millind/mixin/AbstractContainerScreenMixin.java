package kivo.millennium.millind.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static kivo.millennium.client.render.gui.OopartBackGround.renderCustomBackground;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {

    protected AbstractContainerScreenMixin(Component titleIn) { super(titleIn); }

    @Inject(method = "renderSlot", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;III)V", shift = At.Shift.BEFORE))
    public void renderSlot(GuiGraphics graphics, Slot pSlot, CallbackInfo info) {
        renderCustomBackground(graphics, pSlot.getItem(), pSlot.x, pSlot.y);
    }

    @Inject(method = "renderFloatingItem", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;II)V", shift = At.Shift.BEFORE))
    private void renderFloatingItem(GuiGraphics pGuiGraphics, ItemStack pStack, int pX, int pY, String pText, CallbackInfo ci) {
        renderCustomBackground(pGuiGraphics, pStack, pX, pY);
    }
}