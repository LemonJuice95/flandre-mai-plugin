package io.lemonjuice.flan_mai_plugin;

import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.song.SongManager;

public class MaiPluginInit {
    public static void init() {
        SongManager.init();
        ConfigRefs.init();
    }
}
