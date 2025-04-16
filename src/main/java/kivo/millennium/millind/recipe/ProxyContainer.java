package kivo.millennium.millind.recipe;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.capability.MillenniumItemStorage;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProxyContainer implements Container {
    private ArrayList<ISlotProxy> slotProxies;

    public ProxyContainer(ISlotProxy...slotProxies){
        this.slotProxies = new ArrayList();
        this.slotProxies.addAll(Arrays.asList(slotProxies));
    }

    public ProxyContainer(List<ISlotProxy> slotProxies){
        this.slotProxies = new ArrayList();
        this.slotProxies.addAll(slotProxies);
    }

    public ArrayList<ISlotProxy> getSlotProxies(){
        return this.slotProxies;
    }

    @Override
    public int getContainerSize() {
        return slotProxies.size();
    }

    @Override
    public boolean isEmpty() {
        if (slotProxies.isEmpty()) {
            return true;
        }
        AtomicBoolean isEmp = new AtomicBoolean(true);
        slotProxies.forEach(slotProxy ->{
            if (!slotProxy.isEmpty()) isEmp.set(false);
        });
        return isEmp.get();
    }

    public ItemStack getFirstItem(){
        for (int i = 0; i < slotProxies.size(); i++){
            if(slotProxies.get(i) instanceof ItemProxy itemProxy) return itemProxy.get();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        if(slotProxies.get(pSlot) instanceof ItemProxy itemProxy){
            return itemProxy.get();
        }else {
            return ItemStack.EMPTY;
        }
    }

    public FluidStack getFluid(int pSlot) {
        if(slotProxies.get(pSlot) instanceof FluidProxy fluidProxy){
            return fluidProxy.get();
        }else {
            return null;
        }
    }

    public int getAmount(int pSlot){
        return slotProxies.get(pSlot).getAmount();
    }

    public ISlotProxy getProxyInSlot(int slot){
        return slotProxies.get(slot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        if(slotProxies.get(pSlot) instanceof ItemProxy itemProxy){
            return itemProxy.shrink(pAmount);
        }else {
            return null;
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        if(slotProxies.get(pSlot) instanceof ItemProxy itemProxy){
            return null;
        }else {
            return null;
        }
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        if(slotProxies.get(pSlot) instanceof ItemProxy itemProxy){
            itemProxy.set(pStack);
        }else {

        }
    }

    @Override
    public void setChanged() {

    }

    public ProxyContainer addProxy(ItemProxy itemProxy){
        this.slotProxies.add(itemProxy);
        return this;
    }

    public ProxyContainer addProxy(MillenniumItemStorage itemStorage, int index){
        this.slotProxies.add(new ItemProxy(itemStorage, index));
        return this;
    }

    public ProxyContainer addProxy(FluidProxy fluidProxy){
        this.slotProxies.add(fluidProxy);
        return this;
    }

    public ProxyContainer addProxy(MillenniumFluidStorage fluidStorage, int index){
        this.slotProxies.add(new FluidProxy(fluidStorage, index));
        return this;
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {

    }

    public boolean isContain(List<RecipeComponent> recipeComponentList){
        if(recipeComponentList.size() != this.getContainerSize()){
            return false;
        }
        for (int i = 0; i < getContainerSize(); i++){
            RecipeComponent component = recipeComponentList.get(i);
            if (this.getProxyInSlot(i).getType() != component.getType()){
                return false;
            }
            if(!this.getProxyInSlot(i).contains(component)){
                return false;
            }
        }
        return true;
    }

    public boolean hasPlaceFor(List<RecipeComponent> recipeComponentList){
        if(recipeComponentList.size() != this.getContainerSize()){
            return false;
        }
        for (int i = 0; i < getContainerSize(); i++){
            RecipeComponent component = recipeComponentList.get(i);
            if (this.getProxyInSlot(i).getType() != component.getType()){

                return false;
            }
            if(!this.getProxyInSlot(i).hasPlaceFor(component)){
                return false;
            }
        }
        return true;
    }

    public boolean tryRemove(List<RecipeComponent> recipeComponentList){
        if(this.isContain(recipeComponentList)){
            for (int i = 0; i < getContainerSize(); i++){
                this.slotProxies.get(i).remove(recipeComponentList.get(i));
            }
            return true;
        }
        return false;

    }

    public boolean tryAdd(List<RecipeComponent> recipeComponentList){
        if(this.hasPlaceFor(recipeComponentList)){
            for (int i = 0; i < getContainerSize(); i++){
                this.slotProxies.get(i).add(recipeComponentList.get(i));
            }
            return true;
        }
        return false;

    }

    //public void setSlotProxies(NonNullList<ISlotProxy> slotProxies) {
        //this.slotProxies = slotProxies;
    //}
}
