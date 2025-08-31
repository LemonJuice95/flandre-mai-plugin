package io.lemonjuice.flan_mai_plugin.games.condition;

import io.lemonjuice.flan_mai_plugin.model.Song;

public abstract class SongFilterCondition {
    public abstract boolean matches(Song song);
}
