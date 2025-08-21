package b50;

import io.lemonjuice.flan_mai_plugin.MaiPluginInit;
import io.lemonjuice.flan_mai_plugin.api.DivingFishB50Generator;
import io.lemonjuice.flan_mai_plugin.api.SongInfoGenerator;
import io.lemonjuice.flan_mai_plugin.api.SongPlayDataGenerator;
import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import io.lemonjuice.flan_mai_plugin.utils.RecordUtils;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestTool {
    @Test
    public void generateB50() {
        ConfigRefs.init();
        SongManager.init();
        DivingFishB50Generator.generate(1582017385L);
    }

    @Test
    public void generateSongInfo() {
        ConfigRefs.init();
        SongManager.init();
        SongInfoGenerator.generate(834); //潘
        SongInfoGenerator.generate(11663); //系
    }

    @Test
    public void generateSongPlayData() {
        ConfigRefs.init();
        SongManager.init();
        SongPlayDataGenerator.generate(1582017385, 417);
        SongPlayDataGenerator.generate(1582017385, 110793);
    }
}
