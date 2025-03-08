package org.xiyu.yee.createplus.features;

import cn.ksmcbrigade.el.events.block.ApplyBlockInternetRangeEvent;
import cn.ksmcbrigade.el.events.entity.ApplyEntityInternetRangeEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

public class Gibbon extends CreativePlusFeature {

    public Gibbon() {
        super("长臂猿", "增加交互距离");
    }
    @Override
    public void onEnable() {
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        NeoForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void block(ApplyBlockInternetRangeEvent event){
        event.value = 32767d;
    }

    @SubscribeEvent
    public void entity(ApplyEntityInternetRangeEvent event){
        event.value = 32767d;
    }

    @Override
    public void onTick() {

    }
} 