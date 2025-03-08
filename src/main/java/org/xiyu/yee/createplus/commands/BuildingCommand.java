package org.xiyu.yee.createplus.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.utils.BuildingManager;
import org.xiyu.yee.createplus.utils.FileUtils;
import org.xiyu.yee.createplus.features.BuildingExport;
import org.xiyu.yee.createplus.features.CreativePlusFeature;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

@EventBusSubscriber(modid = Createplus.MODID)
public class BuildingCommand {
    
    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // ������������
        dispatcher.register(Commands.literal("exportbuilding")
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    return exportBuilding(context.getSource(), name);
                }))
        );

        // ���뽨������
        dispatcher.register(Commands.literal("importbuilding")
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    return importBuilding(context.getSource(), name);
                }))
        );

        // ȷ�ϵ�������
        dispatcher.register(Commands.literal("confirmimport")
            .requires(source -> source.hasPermission(2))
            .executes(context -> confirmImport(context.getSource()))
        );

        // ȡ����������
        dispatcher.register(Commands.literal("cancelimport")
            .executes(context -> cancelImport(context.getSource()))
        );
    }

    private static int exportBuilding(CommandSourceStack source, String name) {
        BuildingExport exportFeature = null;
        
        // ����BuildingExport����
        for (CreativePlusFeature feature : Createplus.FEATURE_MANAGER.getFeatures()) {
            if (feature instanceof BuildingExport) {
                exportFeature = (BuildingExport) feature;
                break;
            }
        }

        if (exportFeature == null || !exportFeature.isEnabled()) {
            source.sendFailure(Component.literal("��c�������ý����������ܣ�ʹ�� /features ��������"));
            return 0;
        }

        if (!exportFeature.hasSelection()) {
            source.sendFailure(Component.literal("��c����ʹ��ľ��ѡ������"));
            return 0;
        }

        try {
            Path buildingsDir = FileUtils.getBuildingsDirectory();
            Path filePath = buildingsDir.resolve(name + ".mcfunction");

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                Map<BlockPos, BlockState> blocks = exportFeature.getSelectedBlocks();
                BlockPos reference = exportFeature.getFirstPos();

                for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
                    BlockPos relativePos = entry.getKey().subtract(reference);
                    BlockState state = entry.getValue();
                    
                    // ��ȡ�����ע������״̬
                    String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                    StringBuilder blockStateString = new StringBuilder();
                    
                    state.getProperties().forEach(property -> {
                        if (blockStateString.length() > 0) {
                            blockStateString.append(",");
                        } else {
                            blockStateString.append("[");
                        }
                        String propertyName = property.getName();
                        String propertyValue = state.getValue(property).toString().toLowerCase();
                        blockStateString.append(propertyName).append("=").append(propertyValue);
                    });
                    
                    if (blockStateString.length() > 0) {
                        blockStateString.append("]");
                    }

                    // д��setblock����
                    writer.write(String.format("setblock ~%d ~%d ~%d %s%s\n",
                        relativePos.getX(), relativePos.getY(), relativePos.getZ(),
                        blockId, blockStateString.toString()));
                }
            }

            source.sendSuccess(() -> 
                Component.literal("��a�ѽ�����������: createplus/buildings/" + name + ".mcfunction"), false);
            return 1;
        } catch (IOException e) {
            source.sendFailure(Component.literal("��c����ʧ��: " + e.getMessage()));
            return 0;
        }
    }

    private static int importBuilding(CommandSourceStack source, String name) {
        try {
            Path filePath = FileUtils.getBuildingsDirectory().resolve(name + ".mcfunction");
            if (!Files.exists(filePath)) {
                source.sendFailure(Component.literal("��c�Ҳ��������ļ�: createplus/buildings/" + name + ".mcfunction"));
                return 0;
            }

            // ���Ȩ��
            if (!source.hasPermission(2)) {
                source.sendFailure(Component.literal("��c��Ҫ2��Ȩ�޲��ܵ��뽨��"));
                return 0;
            }

            // ����Ԥ����Ⱦ
            BuildingManager.getInstance().startPreview(filePath, source);
            source.sendSuccess(() -> 
                Component.literal("��a����Ԥ������������ /confirmimport ȷ�ϵ��룬���� /cancelimport ȡ��"), false);
            return 1;
        } catch (IOException e) {
            source.sendFailure(Component.literal("��c����ʧ��: " + e.getMessage()));
            return 0;
        }
    }

    private static int confirmImport(CommandSourceStack source) {
        BuildingManager manager = BuildingManager.getInstance();
        if (!manager.isPreviewActive()) {
            source.sendFailure(Component.literal("��cû������Ԥ���Ľ���������ʹ�� /importbuilding <����> ѡ����"));
            return 0;
        }

        manager.confirmImport();
        return 1;
    }

    private static int cancelImport(CommandSourceStack source) {
        BuildingManager manager = BuildingManager.getInstance();
        if (!manager.isPreviewActive()) {
            source.sendFailure(Component.literal("��cû������Ԥ���Ľ�����"));
            return 0;
        }

        manager.cancelImport();
        return 1;
    }
} 