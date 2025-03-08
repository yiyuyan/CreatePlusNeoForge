package org.xiyu.yee.createplus.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.features.CreativePlusFeature;
import org.xiyu.yee.createplus.utils.HelpManager;

import java.util.stream.Collectors;

@EventBusSubscriber(modid = Createplus.MODID)
public class FeaturesCommand {

    // ����һ�����ȫ�ṩ��
    private static final SuggestionProvider<CommandSourceStack> FEATURE_SUGGESTIONS = (context, builder) -> {
        // ��ȡ���й�������
        var featureNames = Createplus.FEATURE_MANAGER.getFeatures()
            .stream()
            .map(CreativePlusFeature::getName)
            .collect(Collectors.toList());
        
        // ����ƥ�䵱ǰ����Ľ���
        return SharedSuggestionProvider.suggest(featureNames, builder);
    };

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("features")
            .then(Commands.argument("feature", StringArgumentType.greedyString())
                .suggests(FEATURE_SUGGESTIONS) // ��Ӳ�ȫ����
                .executes(context -> {
                    String featureName = StringArgumentType.getString(context, "feature");
                    return toggleFeature(context.getSource(), featureName);
                }))
            .then(Commands.literal("help")
                .executes(context -> {
                    showHelp(context.getSource(), null);
                    return 1;
                })
                .then(Commands.argument("feature", StringArgumentType.greedyString())
                    .suggests(FEATURE_SUGGESTIONS)
                    .executes(context -> {
                        String featureName = StringArgumentType.getString(context, "feature");
                        showHelp(context.getSource(), featureName);
                        return 1;
                    })))
            .executes(context -> listFeatures(context.getSource()))); // ��������ʱ�г����й���
    }

    private static int toggleFeature(CommandSourceStack source, String featureName) {
        var features = Createplus.FEATURE_MANAGER.getFeatures();
        
        for (CreativePlusFeature feature : features) {
            if (feature.getName().equals(featureName)) {
                feature.toggle();
                String status = feature.isEnabled() ? "����" : "����";
                source.sendSuccess(() -> 
                    Component.literal("��b��" + status + "����: ��f" + feature.getName()), false);
                return 1;
            }
        }

        source.sendFailure(Component.literal("��cδ�ҵ�����: ��f" + featureName));
        return 0;
    }

    private static int listFeatures(CommandSourceStack source) {
        var features = Createplus.FEATURE_MANAGER.getFeatures();
        
        source.sendSuccess(() -> Component.literal("��b���ù����б�:"), false);
        for (CreativePlusFeature feature : features) {
            String status = feature.isEnabled() ? "��a[������]" : "��7[�ѽ���]";
            source.sendSuccess(() -> 
                Component.literal(status + " ��f" + feature.getName() + " ��7- " + feature.getDescription()), false);
        }
        
        source.sendSuccess(() -> Component.literal("\n��eʹ�� ��6/features help <������> ��e�鿴��ϸ����"), false);
        return 1;
    }

    private static void showHelp(CommandSourceStack source, String featureName) {
        if (featureName == null) {
            HelpManager.showGeneralHelp(source);
        } else {
            HelpManager.showFeatureHelp(source, featureName);
        }
    }
} 