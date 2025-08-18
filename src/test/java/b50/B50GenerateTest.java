package b50;

import io.lemonjuice.flan_mai_plugin.MaiPluginInit;
import io.lemonjuice.flan_mai_plugin.b50.DivingFishB50Generator;
import org.junit.jupiter.api.Test;

public class B50GenerateTest {
    @Test
    public void generateB50() {
        MaiPluginInit.init();
        DivingFishB50Generator.generate(1582017385L);
    }
}
