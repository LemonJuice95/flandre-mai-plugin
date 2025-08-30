package io.lemonjuice.flan_mai_plugin.utils;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.games.open_chars.OpenCharsProcess;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class SongManager {
    private static final ConcurrentHashMap<Integer, Song> ID_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<Song>> TITLE_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<Integer>> PLATE_REQUIREMENTS = new ConcurrentHashMap<>(); //使用歌曲id

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    public static List<Integer> getPlateRequirement(String version) {
        checkInitializedOrThrow();
        return PLATE_REQUIREMENTS.get(version);
    }

    public static List<Song> getSongs() {
        checkInitializedOrThrow();
        return ID_MAP.values().stream().toList();
    }

    public static List<Song> searchSong(String name) {
        checkInitializedOrThrow();
        List<Song> result = new ArrayList<>();
        Pattern idQuery = Pattern.compile("^(id)?(\\d+)$");
        if(!name.isEmpty()) {
            Matcher idMatcher = idQuery.matcher(name);
            if(idMatcher.find()) {
                int songId = Integer.parseInt(idMatcher.group(2));
                if(isSongIdExists(songId)) {
                    result.add(getSongById(songId));
                }
            }
            if(result.isEmpty()) {
                if(isSongTitleExists(name)) {
                    result = getSongByTitle(name);
                } else {
                    result = getSongByAlias(name);
                }
            }
        }
        return result;
    }

    public static Song getSongById(int id) {
        checkInitializedOrThrow();
        return ID_MAP.get(id);
    }

    public static List<Song> getSongByTitle(String title) {
        checkInitializedOrThrow();
        return TITLE_MAP.get(title);
    }

    public static boolean isSongIdExists(int id) {
        checkInitializedOrThrow();
        return ID_MAP.containsKey(id);
    }

    public static boolean isSongTitleExists(String title) {
        checkInitializedOrThrow();
        return TITLE_MAP.containsKey(title);
    }

    public static List<Song> getSongByAlias(String alias) {
        checkInitializedOrThrow();
        return ID_MAP.values()
                .stream()
                .filter(s -> s.alias.contains(alias))
                .collect(Collectors.toList());
    }

    public static boolean isInitialized() {
        return initialized.get();
    }

    private static void checkInitializedOrThrow() throws NotInitializedException {
        if(!initialized.get()) throw new NotInitializedException("歌曲信息未初始化完成");
    }

    public static synchronized void init() {
        initialized.set(false);
        ID_MAP.clear();
        TITLE_MAP.clear();
        PLATE_REQUIREMENTS.clear();

        initMusicData();
        initPlateRequirement();

        initialized.set(true);

        onInitFinish();
    }

    private static void onInitFinish() {
        OpenCharsProcess.init();
    }

    private static void initPlateRequirement() {
        JSONObject requirementJson = MaiMaiProberService.requestPlateRequirement();
        for(String ver : requirementJson.keySet()) {
            JSONArray jsonArray = requirementJson.getJSONArray(ver);
            List<Integer> songIds = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++) {
                songIds.add(jsonArray.getInt(i));
            }
            if(ver.contains("&")) {
                for(String eachVer : ver.split("&")) {
                    if(eachVer.equals("华")) {
                        eachVer = "華";
                    }
                    PLATE_REQUIREMENTS.put(eachVer, songIds);
                }
            } else {
                PLATE_REQUIREMENTS.put(ver, songIds);
            }
        }
    }

    private static void initMusicData() {
        JSONArray songsJson = MaiMaiProberService.requestSongListRaw();
        JSONObject chartStats = MaiMaiProberService.requestChartStats();
        for(int i = 0; i < songsJson.length(); i++) {
            JSONObject songJson = songsJson.getJSONObject(i);
            Song song = SongUtils.parseSong(songJson);
            if(chartStats.has(String.valueOf(song.id))) {
                JSONArray charts = chartStats.getJSONArray(String.valueOf(song.id));
                for (int j = 0; j < charts.length() && j < song.charts.size(); j++) {
                    if(charts.getJSONObject(j).has("fit_diff")) {
                        song.charts.get(j).fitDIff = charts.getJSONObject(j).getFloat("fit_diff");
                    }
                }
            }
            ID_MAP.put(song.id, song);
            if(!TITLE_MAP.containsKey(song.title)) {
                TITLE_MAP.put(song.title, new ArrayList<>());
            }
            TITLE_MAP.get(song.title).add(song);
        }

        JSONArray aliasJson = MaiMaiProberService.requestSongAlias();
        for(int i = 0; i < aliasJson.length(); i++) {
            JSONObject json = aliasJson.getJSONObject(i);
            int songId = json.getInt("SongID");
            JSONArray songAlias = json.getJSONArray("Alias");
            Song song = ID_MAP.get(songId);
            for(int j = 0; j < songAlias.length(); j++) {
                song.alias.add(songAlias.getString(j));
            }
        }
    }
}