package io.lemonjuice.flan_mai_plugin.console;

import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRegister;

public class ConsoleCommandInit {
    public static final ConsoleCommandRegister COMMANDS = new ConsoleCommandRegister();

    static {
        COMMANDS.register(UpdateSongsCommand::new);
    }
}
