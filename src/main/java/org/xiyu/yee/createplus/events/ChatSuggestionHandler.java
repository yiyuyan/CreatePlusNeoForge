package org.xiyu.yee.createplus.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.xiyu.yee.createplus.Createplus;

@EventBusSubscriber(modid = Createplus.MODID)
public class ChatSuggestionHandler {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        // ע���������
        registerCommand(dispatcher, "hudcolor", "fps", "time", "pos", "biome", "localtime");
        registerCommand(dispatcher, "weather", "����", "����", "�ױ�");
        registerCommand(dispatcher, "time", "�ճ�", "����", "����", "ǰҹ", "��ҹ");
        registerCommand(dispatcher, "give", "@s", "@p", "@a");
        
        // ע��������
        registerSubCommand(dispatcher, "hudcolor fps", "text", "value");
        registerSubCommand(dispatcher, "hudcolor time", "text", "value");
        registerSubCommand(dispatcher, "hudcolor pos", "text", "value");
        registerSubCommand(dispatcher, "hudcolor biome", "text", "value");
        registerSubCommand(dispatcher, "hudcolor localtime", "text", "value");
    }

    private static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, String command, String... subCommands) {
        // ע���б�ܵ�����
        var slashBuilder = Commands.literal(command);
        // ע�����ŵ�����
        var dotBuilder = Commands.literal("." + command);
        
        for (String sub : subCommands) {
            var subCommand = Commands.literal(sub)
                .executes(context -> handleCommand(command, sub));
            slashBuilder.then(subCommand);
            dotBuilder.then(subCommand);
        }
        
        dispatcher.register(slashBuilder);
        dispatcher.register(dotBuilder);
    }

    private static void registerSubCommand(CommandDispatcher<CommandSourceStack> dispatcher, String parentCommand, String... subCommands) {
        String[] parts = parentCommand.split(" ");
        
        // Ϊ��б�ܺ͵�ŵĸ�����ֱ�ע��������
        for (String prefix : new String[]{"", "."}) {
            String fullCommand = prefix + parentCommand;
            LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(prefix + parts[0]);
            
            for (String sub : subCommands) {
                builder.then(Commands.literal(parts[1])
                    .then(Commands.literal(sub)
                        .executes(context -> handleSubCommand(parentCommand, sub))));
            }
            
            dispatcher.register(builder);
        }
    }

    private static int handleCommand(String command, String subCommand) {
        // ���������߼�
        return 1;
    }

    private static int handleSubCommand(String parentCommand, String subCommand) {
        // �����������߼�
        return 1;
    }
} 