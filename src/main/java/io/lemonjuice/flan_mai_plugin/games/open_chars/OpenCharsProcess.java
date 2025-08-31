package io.lemonjuice.flan_mai_plugin.games.open_chars;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class OpenCharsProcess {
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final List<Song> LEGAL_SONGS = new ArrayList<>();
    private static final Set<Character> legalChars = new HashSet<>();

    static {
        for(char i = 'a'; i <= 'z'; i++) {
            legalChars.add(i);
        }
        for(char i = 'A'; i <= 'Z'; i++) {
            legalChars.add(i);
        }
        for(char i = '0'; i <= '9'; i++) {
            legalChars.add(i);
        }
        String legalSymbols = "!?@#$%^&*().,-_+=[]{}~`<>:;\"'/|\\ ";
        for(char i : legalSymbols.toCharArray()) {
            legalChars.add(i);
        }
    }

    private final List<Song> songs = new ArrayList<>();
    private final Set<Character> openedChars = new HashSet<>();
    private final Set<Integer> failedIndexes = new HashSet<>();
    private final Set<Integer> completedIndexes = new HashSet<>();
    private int remaining;

    public OpenCharsProcess(int songNum) {
        checkInitializedOrThrow();

        this.remaining = Math.min(songNum, LEGAL_SONGS.size());
        ThreadLocalRandom.current().ints(0, LEGAL_SONGS.size())
                .distinct()
                .limit(Math.min(songNum, LEGAL_SONGS.size()))
                .forEach((i) -> songs.add(LEGAL_SONGS.get(i)));
    }

    public synchronized boolean openChar(char c) {
        boolean result = this.openedChars.add(c);
        if(result) {
            for(int i = 0; i < this.songs.size(); i++) {
                if(!this.isSongUnknown(i)) {
                    continue;
                }
                boolean flag = true;
                for(char ch : this.songs.get(i).title.toCharArray()) {
                    if(!this.openedChars.contains(ch) && ch != ' ') {
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    this.failedIndexes.add(i);
                    this.remaining--;
                }
            }
        }
        return result;
    }

    public String getSongsMessage() {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < this.songs.size(); i++) {
            if(this.failedIndexes.contains(i)) {
                result.append("[×] [");
                result.append(i);
                result.append("]  ");
                result.append(this.songs.get(i).title);
            } else if(this.completedIndexes.contains(i)) {
                result.append("[√] [");
                result.append(i);
                result.append("]  ");
                result.append(this.songs.get(i).title);
            } else {
                result.append("[?] [");
                result.append(i);
                result.append("]  ");
                for(char c : this.songs.get(i).title.toCharArray()) {
                    if(this.openedChars.contains(c) || c == ' ') {
                        result.append(c);
                    } else {
                        result.append("*");
                    }
                }
            }
            result.append("\n");
        }
        return result.toString().trim();
    }

    public boolean isSongUnknown(int index) throws IndexOutOfBoundsException {
        return !this.failedIndexes.contains(index) && !this.completedIndexes.contains(index);
    }

    public synchronized boolean guessSong(String name, int index) throws IndexOutOfBoundsException {
        List<Song> matchedSongs = SongManager.searchSong(name);
        if(matchedSongs.contains(this.songs.get(index)) && this.isSongUnknown(index)) {
            this.completedIndexes.add(index);
            this.remaining--;
            return true;
        }
        return false;
    }

    public String getOpenedCharsMessage() {
        return this.openedChars.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    public boolean checkAccomplished() {
        return this.remaining <= 0;
    }

    private static void checkInitializedOrThrow() {
        if(!initialized.get()) throw new NotInitializedException("开字母功能未初始化完成");
    }

    public static void init() {
        initialized.set(false);

        SongManager.getSongs().forEach((song) -> {
            int legalCount = 0;
            for(char ch : song.title.toCharArray()) {
                if(legalChars.contains(ch)) {
                    legalCount++;
                }
            }
            if((float) legalCount / song.title.length() >= 0.6F) {
                LEGAL_SONGS.add(song);
            }
        });

        initialized.set(true);
    }
}
