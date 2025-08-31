package io.lemonjuice.flan_mai_plugin.games.condition;

import io.lemonjuice.flan_mai_plugin.model.Song;

import java.util.Comparator;

public class LevelCondition extends SongFilterCondition {
    public static final Comparator<String> levelComparator = (l1, l2) -> {
        String l1_ = l1.replace("+?", ".6").replace("?", ".4").replace("+", ".5");
        String l2_ = l2.replace("+?", ".6").replace("?", ".4").replace("+", ".5");
        return Float.compare(Float.parseFloat(l1_), Float.parseFloat(l2_));
    };

    private final Mode mode;
    private final String level;

    public LevelCondition(Mode mode, String level) {
        this.mode = mode;
        this.level = level;
    }

    @Override
    public boolean matches(Song song) {
        return this.mode.matches(song.maxLevel, this.level);
    }

    public enum Mode {
        LESS {
            @Override
            public boolean matches(String songLevel, String stdLevel) {
                return levelComparator.compare(songLevel, stdLevel) < 0;
            }
        },
        LESS_OR_EQUALS {
            @Override
            public boolean matches(String songLevel, String stdLevel) {
                return levelComparator.compare(songLevel, stdLevel) <= 0;
            }
        },
        EQUALS {
            @Override
            public boolean matches(String songLevel, String stdLevel) {
                return levelComparator.compare(songLevel, stdLevel) == 0;
            }
        },
        GREATER_OR_EQUALS {
            @Override
            public boolean matches(String songLevel, String stdLevel) {
                return levelComparator.compare(songLevel, stdLevel) >= 0;
            }
        },
        GREATER {
            @Override
            public boolean matches(String songLevel, String stdLevel) {
                return levelComparator.compare(songLevel, stdLevel) > 0;
            }
        };

        public abstract boolean matches(String songLevel, String stdLevel);
    }
}
