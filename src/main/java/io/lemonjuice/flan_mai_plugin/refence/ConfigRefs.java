package io.lemonjuice.flan_mai_plugin.refence;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Properties;
import java.util.function.Supplier;

@Log4j2
public class ConfigRefs {
    private static final Properties properties = new Properties();
    private static final File cfgFile = new File("./config/mai_plugin.properties");

    public static final Supplier<String> BOT_NAME = () -> properties.getProperty("bot.name");
    public static final Supplier<String> DIVING_FISH_CLIENT_ID = () -> properties.getProperty("diving_fish.auth.client_id");
    public static final Supplier<String> DIVING_FISH_CLIENT_SECRET = () -> properties.getProperty("diving_fish.auth.client_secret");
    public static final Supplier<String> DIVING_FISH_SCOPES = () -> properties.getProperty("diving_fish.auth.scopes");
    public static final Supplier<Integer> DIVING_FISH_TOKEN_CLEAN_RATE = () -> {
        try {
            return Integer.parseInt(properties.getProperty("diving_fish.auth.cached_token_clean_rate"));
        } catch (NumberFormatException e) {
            log.warn("设置项diving_fish.auth.cached_token_clean_rate无效，将使用默认值300");
            return 300000;
        }
    };

    public static synchronized boolean check() {
        boolean result = true;

        if(!cfgFile.getParentFile().exists()) {
            cfgFile.getParentFile().mkdirs();
        }
        if(!cfgFile.exists()) {
            releaseConfigFile();
            result = false;
        }

        return result;
    }

    public static synchronized void init() {
        try (FileInputStream input = new FileInputStream(cfgFile)) {
            properties.load(input);
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
