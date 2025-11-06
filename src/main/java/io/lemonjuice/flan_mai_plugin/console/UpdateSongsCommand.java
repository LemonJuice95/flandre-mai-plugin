package io.lemonjuice.flan_mai_plugin.console;

import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;

import java.util.List;

public class UpdateSongsCommand extends ConsoleCommandRunner {
    public UpdateSongsCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        BotConsole.println("更新歌曲列表...");
        Thread.startVirtualThread(SongManager::init);
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("updmaisongs");
    }

    @Override
    public String getUsingFormat() {
        return "'updmaisongs'";
    }

    @Override
    public String getDescription() {
        return "更新舞萌歌曲列表";
    }
}
