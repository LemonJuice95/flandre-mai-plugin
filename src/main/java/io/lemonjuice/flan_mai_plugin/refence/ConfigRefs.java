package io.lemonjuice.flan_mai_plugin.refence;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Properties;

@Log4j2
public class ConfigRefs {
    public static String BOT_NAME;
    public static String DIVING_FISH_TOKEN;

    public static void init() {
        File cfgFile = new File("./config/mai_plugin.properties");
        if(!cfgFile.getParentFile().exists()) {
            cfgFile.getParentFile().mkdirs();
        }
        if(!cfgFile.exists()) {
            releaseConfigFile();
        }
        try (FileInputStream input = new FileInputStream(cfgFile)) {
            Properties properties = new Properties();
            properties.load(input);

            BOT_NAME = properties.getProperty("bot.name");
            DIVING_FISH_TOKEN = properties.getProperty("diving_fish.dev_token");

        } catch (IOException e) {
            log.error("加载舞萌插件配置文件失败！", e);
        }
    }

    private static void releaseConfigFile() {
        try (InputStream input = ConfigRefs.class.getClassLoader().getResourceAsStream("export/config/mai_plugin.properties");
             FileOutputStream output = new FileOutputStream("./config/mai_plugin.properties")) {
            output.write(input.readAllBytes());
        } catch (IOException e) {
            log.error("释放舞萌插件配置文件失败！", e);
        }
    }
}
