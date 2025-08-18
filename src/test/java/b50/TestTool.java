package b50;

import io.lemonjuice.flan_mai_plugin.MaiPluginInit;
import io.lemonjuice.flan_mai_plugin.image_gen.DivingFishB50Generator;
import io.lemonjuice.flan_mai_plugin.image_gen.SongInfoGenerator;
import org.junit.jupiter.api.Test;

public class TestTool {
    @Test
    public void generateB50() {
        MaiPluginInit.init();
        DivingFishB50Generator.generate(1582017385L);
    }

    @Test
    public void generateSongInfo() {
        MaiPluginInit.init();
        SongInfoGenerator.generate(834); //潘
        SongInfoGenerator.generate(11663); //系
    }
}
