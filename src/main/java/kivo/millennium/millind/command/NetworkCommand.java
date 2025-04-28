package kivo.millennium.millind.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import kivo.millennium.millind.pipe.client.network.AbstractLevelNetwork; // 导入网络相关类
import kivo.millennium.millind.pipe.client.network.LevelNetworkManagerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument; // 导入 UuidArgument
import net.minecraft.network.chat.Component; // 用于发送消息
import net.minecraft.server.level.ServerLevel; // 需要 ServerLevel
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod; // 用于 @Mod.EventBusSubscriber

import java.util.UUID; // 用于 UUID

import static kivo.millennium.millind.Main.MODID; // 假设 Main.MODID 是可访问的

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NetworkCommand {

    // 定义一个自定义的异常类型，用于网络未找到的情况
    // 使用 translatable component 方便本地化
    private static final SimpleCommandExceptionType ERROR_NETWORK_NOT_FOUND = new SimpleCommandExceptionType(
            Component.translatable("commands.networktest.network_not_found")); // 使用 %s 作为 UUID 的占位符

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        register(dispatcher);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 定义基础指令： /networktest
        // 要求权限等级 2 (通常是 OP 或拥有相应权限的玩家)
        LiteralArgumentBuilder<CommandSourceStack> baseCommand = Commands.literal("networktest")
                .requires(source -> source.hasPermission(2));

        // 定义子命令： info
        LiteralArgumentBuilder<CommandSourceStack> infoCommand = Commands.literal("info");

        // 定义参数： network_uuid，使用 UuidArgument 类型
        RequiredArgumentBuilder<CommandSourceStack, UUID> uuidArgument = Commands.argument("network_uuid", UuidArgument.uuid());

        // 构建指令结构： /networktest info <network_uuid>
        // 当输入完整的指令并提供 UUID 参数时，执行 executeInfo 方法
        uuidArgument.executes(context -> executeInfo(context)); // 设置执行逻辑

        // 将 uuid 参数添加到 info 子命令下
        infoCommand.then(uuidArgument);
        // 将 info 子命令添加到基础指令下
        baseCommand.then(infoCommand);

        // 注册指令
        dispatcher.register(baseCommand);

        // 注册别名 /nt 指向 /networktest (可选，但方便测试)
        dispatcher.register(Commands.literal("nt").redirect(baseCommand.build()));
    }

    // 指令执行逻辑： /networktest info <network_uuid>
    private static int executeInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        // 获取指令源
        CommandSourceStack source = context.getSource();

        // 获取指令执行所在的 ServerLevel
        ServerLevel level = source.getLevel();

        // 从指令参数中获取 UUID
        UUID networkUuid = UuidArgument.getUuid(context, "network_uuid");

        // 获取当前维度的 LevelNetworkManagerData 实例
        LevelNetworkManagerData manager = LevelNetworkManagerData.get(level);

        // 通过 UUID 查找网络实例
        AbstractLevelNetwork network = manager.getNetworkByUuid(networkUuid);

        if (network == null) {
            // 如果网络未找到，抛出自定义异常
            // 使用 .create(args...) 方法可以为 TranslatableComponent 提供参数
            throw ERROR_NETWORK_NOT_FOUND.create();
        } else {
            // 如果网络找到，向指令源发送网络信息
            source.sendSuccess(() -> Component.literal("--- Network Info ---"), false); // false 表示消息不会出现在聊天历史记录中
            source.sendSuccess(() -> Component.literal("  UUID: " + network.getUuid()), false);
            source.sendSuccess(() -> Component.literal("  Type: " + network.getLevelNetworkType().getName()), false);
            source.sendSuccess(() -> Component.literal("  Input Nodes (Block): " + network.getAllBlockInputNodes().size()), false); // 使用新的方法获取所有方块输入节点数
            source.sendSuccess(() -> Component.literal("  Output Nodes (Block): " + network.getAllBlockOutputNodes().size()), false); // 使用新的方法获取所有方块输出节点数
            source.sendSuccess(() -> Component.literal("  Input Nodes (Entity): " + network.getEntityInputNodes().size()), false);
            source.sendSuccess(() -> Component.literal("  Output Nodes (Entity): " + network.getEntityOutputNodes().size()), false);

            // 如果需要，可以在这里添加更多特定于网络类型的信息
            // if (network instanceof FluidLevelNetwork fluidNetwork) { ... }

            source.sendSuccess(() -> Component.literal("-------------------"), false);

            // 返回 1 表示指令成功执行
            return 1;
        }
    }

    // TODO: 以后可以添加更多子命令 (例如，/networktest list [type], /networktest debug <uuid> ...)

}