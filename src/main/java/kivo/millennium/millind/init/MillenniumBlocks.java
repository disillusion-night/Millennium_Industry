package kivo.millennium.millind.init;

import kivo.millennium.millind.block.projector.ProjectorBL;
import kivo.millennium.millind.block.fluid.IcyWaterBlock;
import kivo.millennium.millind.block.fluidContainer.MetalTankBL;
import kivo.millennium.millind.block.multiblock.controller.HMIBL;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static kivo.millennium.millind.Main.MODID;


public class MillenniumBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> ICY_WATER_BL = BLOCKS.register("icy_water", IcyWaterBlock::new);

    public static final RegistryObject<MetalTankBL> METAL_TANK_BL = registerWithItem("metal_tank", MetalTankBL::new);
    public static final RegistryObject<HMIBL> HMI_BL = registerWithItem("hmi", HMIBL::new);
    public static final RegistryObject<ProjectorBL> PROJECTOR_BL = registerWithItem("projector", ProjectorBL::new);


    public static <I extends Block> RegistryObject<I> registerWithItem(String name, Supplier<I> supplier){
        RegistryObject<I> object = BLOCKS.register(name, supplier);
        RegistryObject<Item> blockItem = MillenniumItems.ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties()));
        MillenniumItems.ENGINEERING_PARTS.add(blockItem);
        return object;
    }
}


