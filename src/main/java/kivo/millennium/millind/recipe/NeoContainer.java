package kivo.millennium.millind.recipe;

import kivo.millennium.millind.Main;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NeoContainer implements Container {
    private ArrayList<ISlotProxy> slotProxies;

    public NeoContainer (ISlotProxy...slotProxies){
        this.slotProxies = new ArrayList();
        this.slotProxies.addAll(Arrays.asList(slotProxies));
    }

    public NeoContainer (List<ISlotProxy> slotProxies){
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

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {

    }

    public NeoContainer addStack(FluidStack fluidStack){
        slotProxies.add(new FluidProxy().of(fluidStack));
        return this;
    }

    public NeoContainer addStack(ItemStack itemStack){
        slotProxies.add(new ItemProxy().of(itemStack));
        return this;
    }

    public boolean isContain(NeoContainer container){
        if(container.getContainerSize() != this.getContainerSize()){
            return false;
        }
        for (int i = 0; i < getContainerSize(); i++){
            if(!this.getProxyInSlot(i).contains(container.getProxyInSlot(i))){
                return false;
            }
        }
        return true;
    }

    public boolean hasPlaceFor(NeoContainer container){
        if(container.getContainerSize() != this.getContainerSize()){
            return false;
        }
        for (int i = 0; i < getContainerSize(); i++){
            if(!this.getProxyInSlot(i).hasPlaceFor(container.getProxyInSlot(i))){
                return false;
            }
        }
        return true;
    }

    public boolean tryRemove(NeoContainer container){
        if(this.isContain(container)){
            for (int i = 0; i < getContainerSize(); i++){
                this.slotProxies.get(i).remove(container.getProxyInSlot(i));
            }
            return true;
        }
        return false;

    }

    public boolean tryAdd(NeoContainer container){
        if(this.hasPlaceFor(container)){
            for (int i = 0; i < getContainerSize(); i++){
                this.slotProxies.get(i).add(container.getProxyInSlot(i));
            }
            return true;
        }
        return false;

    }

    //public void setSlotProxies(NonNullList<ISlotProxy> slotProxies) {
        //this.slotProxies = slotProxies;
    //}
}
