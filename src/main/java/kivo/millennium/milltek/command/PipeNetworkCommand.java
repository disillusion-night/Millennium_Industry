package kivo.millennium.milltek.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Collectors;

public class PipeNetworkCommand {

    private static final SuggestionProvider<CommandSourceStack> NETWORK_TYPE_SUGGESTIONS = (context, builder) -> {
        return net.minecraft.commands.SharedSuggestionProvider.suggest(
                MillenniumLevelNetworkType.LEVEL_NETWORK_TYPES.getEntries().stream()
                        .map(entry -> entry.getId().getPath())
                        .collect(Collectors.toList()),
                builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("list_pipe")
                .requires(source -> source.hasPermission(2)) // 需要操作权限
                .then(Commands.argument("networkType", StringArgumentType.string())
                        .suggests(NETWORK_TYPE_SUGGESTIONS)
                        .executes(PipeNetworkCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String networkType = StringArgumentType.getString(context, "networkType");

        // 获取当前维度的网络数据
        LevelNetworkSavedData networkData = LevelNetworkSavedData.getInstance();
        if (networkData == null) {
            source.sendSuccess(() -> Component.literal("No network data found for this dimension."), false);
            return 0;
        }

        // 获取指定类型的网络
        AbstractLevelNetwork[] networks = networkData.getNetworksByType(networkType).values()
                .toArray(new AbstractLevelNetwork[0]);
        if (networks == null || networks.length == 0) {
            source.sendSuccess(() -> Component.literal("No networks found for type: " + networkType), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Network Type: " + networkType), false);

        // 遍历该类型的所有网络
        for (int i = 0; i < networks.length; ++i) {
            AbstractLevelNetwork network = networks[i];
            if (network != null) {
                source.sendSuccess(() -> Component.literal(" - Network ID: "), false);
            }
        }

        return 1;
    }
}