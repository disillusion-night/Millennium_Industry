package kivo.millennium.milltek.gas;

import org.jetbrains.annotations.NotNull;

/**
 * 气体能力接口，方法命名与IFluidHandler保持一致
 */
public interface IGasHandler {
  /**
   * 获取气体槽数量
   */
  int getTanks();

  /**
   * 获取指定槽的气体堆栈
   */
  @NotNull
  GasStack getGasInTank(int tank);

  /**
   * 获取指定槽的容量
   */
  int getTankCapacity(int tank);

  /**
   * 判断指定槽是否允许存入该气体
   */
  boolean isGasValid(int tank, @NotNull GasStack stack);

  /**
   * 填充气体，返回实际填充量
   * 
   * @param resource 气体堆栈
   * @param action   执行/模拟
   */
  int fill(GasStack resource, GasAction action);

  /**
   * 按气体堆栈抽取，返回实际抽取的气体
   * 
   * @param resource 气体堆栈
   * @param action   执行/模拟
   */
  @NotNull
  GasStack drain(GasStack resource, GasAction action);

  /**
   * 按数量抽取，返回实际抽取的气体
   * 
   * @param maxDrain 最大抽取量
   * @param action   执行/模拟
   */
  @NotNull
  GasStack drain(int maxDrain, GasAction action);

  /**
   * 填充/抽取行为枚举，仿照FluidAction
   */
  enum GasAction {
    EXECUTE, SIMULATE;

    public boolean execute() {
      return this == EXECUTE;
    }
  }
}
