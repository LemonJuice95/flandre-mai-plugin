package io.lemonjuice.flan_mai_plugin;

import io.lemonjuice.flan_mai_plugin.console.ConsoleCommandInit;
import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.PluginRegisterEvent;
import io.lemonjuice.flandre_bot_framework.plugins.BotPlugin;

@EventSubscriber
public class FlandreMaiPlugin implements BotPlugin {
    @Override
    public void load() {
        ConfigRefs.init();
        ConsoleCommandInit.COMMANDS.load();
        Thread.startVirtualThread(SongManager::init);
    }

    @Override
    public String getName() {
        return "Flandre MaiMai Plugin";
    }

    @SubscribeEvent
    public void registerPlugin(PluginRegisterEvent event) {
        event.register(this);
    }
}
