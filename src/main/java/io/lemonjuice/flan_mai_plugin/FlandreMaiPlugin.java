package io.lemonjuice.flan_mai_plugin;

import com.google.common.eventbus.Subscribe;
import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.meta.PluginLoadEvent;

@EventSubscriber
public class FlandreMaiPlugin {
    @Subscribe
    public void loadPlugin(PluginLoadEvent event) {
        ConfigRefs.init();
        Thread.startVirtualThread(SongManager::init);
    }
}
