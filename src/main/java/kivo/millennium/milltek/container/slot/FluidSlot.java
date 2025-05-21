package kivo.millennium.milltek.container;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

public class FluidSlot {
  private final IFluidHandler fluidHandler;
  private final int tankIndex;
  private final int x;
  private final int y;
  private final int width;
  private final int height;

  public FluidSlot(IFluidHandler fluidHandler, int tankIndex, int x, int y) {
    this(fluidHandler, tankIndex, x, y, 16, 57); // 默认宽高
  }

  public FluidSlot(IFluidHandler fluidHandler, int tankIndex, int x, int y, int width, int height) {
    this.fluidHandler = fluidHandler;
    this.tankIndex = tankIndex;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public int getTankIndex() {
    return tankIndex;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getFluidAmount() {
    FluidStack stack = fluidHandler.getFluidInTank(tankIndex);
    return stack != null ? stack.getAmount() : 0;
  }

  public int getFluidCapacity() {
    return fluidHandler.getTankCapacity(tankIndex);
  }

  public FluidStack getFluidStack() {
    return fluidHandler.getFluidInTank(tankIndex);
  }

  public void setFluidStack(FluidStack stack) {
    // 直接设置流体内容（需你的存储类支持）
    if (fluidHandler instanceof kivo.millennium.milltek.storage.MillenniumFluidStorage storage) {
      storage.setFluidInTank(tankIndex, stack);
    }
  }

  public IFluidHandler getFluidHandler() {
    return fluidHandler;
  }
}
