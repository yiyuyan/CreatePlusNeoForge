package org.xiyu.yee.createplus.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.features.CreativePlusFeature;

import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Createplus.MODID)
public class FeaturesCommand {

    // 创建一个命令补全提供器
    private static final SuggestionProvider<CommandSourceStack> FEATURE_SUGGESTIONS = (context, builder) -> {
        // 获取所有功能名称
        var featureNames = Createplus.FEATURE_MANAGER.getFeatures()
            .stream()
            .map(CreativePlusFeature::getName)
            .collect(Collectors.toList());
        
        // 返回匹配当前输入的建议
        return SharedSuggestionProvider.suggest(featureNames, builder);
    };

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("features")
            .then(Commands.argument("feature", StringArgumentType.greedyString())
                .suggests(FEATURE_SUGGESTIONS) // 添加补全建议
                .executes(context -> {
                    String featureName = StringArgumentType.getString(context, "feature");
                    return toggleFeature(context.getSource(), featureName);
                }))
            .then(Commands.literal("help")
                .executes(context -> {
                    showHelp(context.getSource());
                    return 1;
                }))
            .executes(context -> listFeatures(context.getSource()))); // 不带参数时列出所有功能
    }

    private static int toggleFeature(CommandSourceStack source, String featureName) {
        var features = Createplus.FEATURE_MANAGER.getFeatures();
        
        for (CreativePlusFeature feature : features) {
            if (feature.getName().equals(featureName)) {
                feature.toggle();
                String status = feature.isEnabled() ? "启用" : "禁用";
                source.sendSuccess(() -> 
                    Component.literal("§b已" + status + "功能: §f" + feature.getName()), false);
                return 1;
            }
        }

        source.sendFailure(Component.literal("§c未找到功能: §f" + featureName));
        return 0;
    }

    private static int listFeatures(CommandSourceStack source) {
        var features = Createplus.FEATURE_MANAGER.getFeatures();
        
        source.sendSuccess(() -> Component.literal("§b可用功能列表:"), false);
        for (CreativePlusFeature feature : features) {
            String status = feature.isEnabled() ? "§a[已启用]" : "§7[已禁用]";
            source.sendSuccess(() -> 
                Component.literal(status + " §f" + feature.getName()), false);
        }
        
        return 1;
    }

    private static void showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("§6=== CreatePlus 功能帮助 ==="), false);
        
        // 作者信息
        source.sendSuccess(() -> Component.literal("\n§d作者信息:"), false);
        source.sendSuccess(() -> Component.literal("§7作者: §b饩雨(God_xiyu)"), false);
        source.sendSuccess(() -> Component.literal("§7交流群: §b691870136"), false);
        source.sendSuccess(() -> Component.literal("§c请勿使用本模组进行恶意破坏，违者将被云黑名单封禁！"), false);
        
        // 基础功能
        source.sendSuccess(() -> Component.literal("\n§e基础功能:"), false);
        source.sendSuccess(() -> Component.literal("§7/features <功能名> - 开启/关闭功能"), false);
        source.sendSuccess(() -> Component.literal("§7/features list - 显示所有功能"), false);
        
        // 建筑导出导入
        source.sendSuccess(() -> Component.literal("\n§e建筑导出导入:"), false);
        source.sendSuccess(() -> Component.literal("§b1. 导出建筑:"), false);
        source.sendSuccess(() -> Component.literal("§7- 使用木铲右键选择两个点"), false);
        source.sendSuccess(() -> Component.literal("§7- 输入 /exportbuilding <名称>"), false);
        source.sendSuccess(() -> Component.literal("§7- 建筑会保存到 buildings/<名称>.mcfunction"), false);
        
        source.sendSuccess(() -> Component.literal("\n§b2. 导入建筑:"), false);
        source.sendSuccess(() -> Component.literal("§7- 输入 /importbuilding <名称>"), false);
        source.sendSuccess(() -> Component.literal("§7- 会显示绿色预览框"), false);
        source.sendSuccess(() -> Component.literal("§7- 输入 /confirmimport 确认导入"), false);
        source.sendSuccess(() -> Component.literal("§7- 输入 /cancelimport 取消导入"), false);
        source.sendSuccess(() -> Component.literal("§c注意: 需要OP权限(2级)才能导入"), false);
        
        // 其他功能说明
        source.sendSuccess(() -> Component.literal("\n§e其他功能:"), false);
        source.sendSuccess(() -> Component.literal("§b范围放置:"), false);
        source.sendSuccess(() -> Component.literal("§7- 副手持方块时自动生效"), false);
        source.sendSuccess(() -> Component.literal("§7- 在目标位置周围形成球形放置区域"), false);
        
        source.sendSuccess(() -> Component.literal("\n§b方块变色:"), false);
        source.sendSuccess(() -> Component.literal("§7- 按住左CTRL并滚动鼠标滚轮"), false);
        source.sendSuccess(() -> Component.literal("§7- 可切换同类方块的颜色/材质"), false);
        source.sendSuccess(() -> Component.literal("§7- 支持: 羊毛、玻璃、地毯、陶瓦等"), false);
        
        source.sendSuccess(() -> Component.literal("\n§b镜像建造:"), false);
        source.sendSuccess(() -> Component.literal("§7- 使用木剑右键选择镜像点"), false);
        source.sendSuccess(() -> Component.literal("§7- Shift+右键切换镜像轴"), false);
        source.sendSuccess(() -> Component.literal("§7- 放置方块时会自动在对称位置放置"), false);
        
        source.sendSuccess(() -> Component.literal("\n§6=== 更多信息请你自己探索 ==="), false);
    }
} 