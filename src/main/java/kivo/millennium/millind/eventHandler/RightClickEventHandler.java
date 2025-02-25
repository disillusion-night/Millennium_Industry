package kivo.millennium.millind.eventHandler;


import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

import static kivo.millennium.millind.Main.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class RightClickEventHandler {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event){
        ItemStack itemStack = event.getItemStack();
        Player player = event.getEntity();

    }

}
