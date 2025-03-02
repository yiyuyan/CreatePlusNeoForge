package org.xiyu.yee.createplus.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.features.CreativePlusFeature;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ConfigManager {
    private static final String CONFIG_FILE = "createplus_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type CONFIG_TYPE = new TypeToken<HashMap<String, Boolean>>(){}.getType();

    public static void loadConfig() {
        File configFile = new File(Minecraft.getInstance().gameDirectory, CONFIG_FILE);
        Map<String, Boolean> config = new HashMap<>();

        // 如果配置文件存在，读取它
        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, CONFIG_TYPE);
            } catch (IOException e) {
              LOGGER.error("Failed to load config file", e);
            }
        }

        // 获取所有已注册的功能
        Map<String, CreativePlusFeature> registeredFeatures = new HashMap<>();
        for (CreativePlusFeature feature : Createplus.FEATURE_MANAGER.getFeatures()) {
            registeredFeatures.put(feature.getName(), feature);
        }

        // 同步配置和功能
        boolean needsSave = false;

        // 检查配置中的每个功能
        for (Map.Entry<String, Boolean> entry : new HashMap<>(config).entrySet()) {
            CreativePlusFeature feature = registeredFeatures.get(entry.getKey());
            if (feature != null) {
                // 如果功能存在，同步其状态
                feature.setEnabled(entry.getValue());
                registeredFeatures.remove(entry.getKey());
            } else {
                // 如果功能不存在于注册表中，从配置中移除
                config.remove(entry.getKey());
                needsSave = true;
            }
        }

        // 添加新功能到配置中
        for (CreativePlusFeature feature : registeredFeatures.values()) {
            config.put(feature.getName(), feature.isEnabled());
            needsSave = true;
        }

        // 如果有变化，保存配置
        if (needsSave) {
            saveConfig(config);
        }
    }

    public static void saveConfig() {
        Map<String, Boolean> config = new HashMap<>();
        
        // 保存所有功能的状态
        for (CreativePlusFeature feature : Createplus.FEATURE_MANAGER.getFeatures()) {
            config.put(feature.getName(), feature.isEnabled());
        }

        saveConfig(config);
    }

    private static void saveConfig(Map<String, Boolean> config) {
        File configFile = new File(Minecraft.getInstance().gameDirectory, CONFIG_FILE);
        try (Writer writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save config file", e);
        }
    }
} 