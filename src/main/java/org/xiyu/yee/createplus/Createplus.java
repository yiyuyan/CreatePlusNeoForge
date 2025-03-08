package org.xiyu.yee.createplus;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import net.minecraft.network.chat.Component;
import org.xiyu.yee.createplus.features.*;

import org.xiyu.yee.createplus.ui.FeatureScreen;

import org.xiyu.yee.createplus.commands.FeaturesCommand;

import org.xiyu.yee.createplus.utils.KeyBindings;
import org.xiyu.yee.createplus.tabs.SpawnEggsTab;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.HashSet;
import java.util.Set;

import org.xiyu.yee.createplus.events.ChatSuggestionHandler;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Createplus.MODID)
public class Createplus {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "createplus";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "createplus" namespace

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // ��������ģʽ��Ʒ��
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATEPLUS_TAB = CREATIVE_MODE_TABS.register("hidden_items", () ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.createplus.hidden_items"))
            .icon(() -> new ItemStack(Items.BARRIER))
            .displayItems((parameters, output) -> {
                // �ռ���������ʾ����Ʒ
                Set<Item> shownItems = new HashSet<>();
                
                // ��ȡ���б�׼��ǩҳ
                CreativeModeTabs.tabs().forEach(tab -> {
                    String tabId = tab.getDisplayName().getString();
                    if (!tabId.contains("hidden_items")) {
                        tab.getDisplayItems().forEach(stack -> shownItems.add(stack.getItem()));
                    }
                });

                // ���δ��ʾ����Ʒ
                for (Item item : BuiltInRegistries.ITEM) {
                    if (!shownItems.contains(item)) {
                        try {
                            ItemStack stack = new ItemStack(item);
                            if (!stack.isEmpty()) {
                                stack.setCount(1); // ȷ���ѵ�����Ϊ1
                                output.accept(stack);
                            }
                        } catch (Exception ignored) {
                            // �����޷���������Ʒ
                        }
                    }
                }
            })
            .build()
    );

    // ����ʵ��ˢ�ֵ���ǩҳ
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPAWN_EGGS_TAB = CREATIVE_MODE_TABS.register("spawn_eggs_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MODID + ".spawn_eggs"))
                    .icon(() -> new ItemStack(Items.SPAWNER))
                    .build());

    public static FeatureManager FEATURE_MANAGER;

    public static FeatureScreen featureScreen;

    public Createplus(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;

        // ֻע�ᴴ��ģʽ��ǩҳ
        CREATIVE_MODE_TABS.register(modEventBus);

        // ע��ͻ����¼�������
        modEventBus.addListener(this::addCreative);

        // ע�ᵽForge�¼�����
        forgeBus.register(this);
        
        // ע��ͻ���tick�¼�
        forgeBus.addListener(this::onClientTick);

        // ��ʼ��UI
        featureScreen = new FeatureScreen();
        
        // ע�ᰴ���¼�
        forgeBus.register(featureScreen);

        // ע������
        forgeBus.register(FeaturesCommand.class);

        // ��ʼ��FEATURE_MANAGER
        FEATURE_MANAGER = new FeatureManager();

        // ��FEATURE_MANAGER��ʼ��ʱ����¹���
        FEATURE_MANAGER.registerFeature(new Gibbon());
        FEATURE_MANAGER.registerFeature(new SpeedAdjust());
        FEATURE_MANAGER.registerFeature(new Freecam());
        FEATURE_MANAGER.registerFeature(new Zoom());
        FEATURE_MANAGER.registerFeature(new Performance());
        FEATURE_MANAGER.registerFeature(new TimeWeatherControl());
        FEATURE_MANAGER.registerFeature(new MiniHUD());

        // ע�����콨�鴦����
        NeoForge.EVENT_BUS.register(ChatSuggestionHandler.class);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == SPAWN_EGGS_TAB.getKey()) {
            SpawnEggsTab.buildContents(event);
        }
    }

    private void onClientTick(ClientTickEvent.Post event) {
        FEATURE_MANAGER.onTick();
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseButton.Post event) {
        // ֻ������갴���¼� (0���ͷţ�1�ǰ���)
        if (event.getAction() != 1) return;
        
        // �����������õĹ���
        for (CreativePlusFeature feature : FEATURE_MANAGER.getFeatures()) {
            if (feature.isEnabled()) {
                feature.handleClick(event.getButton() == 1); // 1���Ҽ���0�����
            }
        }
    }

    // �ͻ���ר�õ�����
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Create Plus client initialization...");
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.TOGGLE_HUD);
            event.register(KeyBindings.TOGGLE_FREECAM);
            event.register(KeyBindings.TOGGLE_ZOOM);
        }
    }

    // ����Ⱦ�¼��Ƶ�����������
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class RenderEvents {
        @SubscribeEvent
        public static void onRenderGui(RenderGuiLayerEvent.Post event) {
            if (featureScreen != null) {
                featureScreen.render(event.getGuiGraphics(), 0, 0, event.getPartialTick().getGameTimeDeltaTicks());
            }
            
            // ��ȾMiniHUD
            FEATURE_MANAGER.getFeatures().stream()
                .filter(feature -> feature instanceof MiniHUD)
                .map(feature -> (MiniHUD) feature)
                .findFirst()
                .ifPresent(miniHUD -> miniHUD.render(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaTicks()));
        }
    }
}
