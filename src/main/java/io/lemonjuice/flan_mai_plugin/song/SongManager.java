package io.lemonjuice.flan_mai_plugin.song;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class SongManager {
    private static final ConcurrentHashMap<Integer, Song> ID_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<Song>> TITLE_MAP = new ConcurrentHashMap<>();

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

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
        List<Song> result = new ArrayList<>();
        for(Song s : ID_MAP.values()) {
            if(s.alias.contains(alias)) {
                result.add(s);
            }
        }
        return result;
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

        JSONArray songsJson = MaiMaiProberService.requestSongListRaw();
        JSONObject chartStats = MaiMaiProberService.requestChartStats();
        for(int i = 0; i < songsJson.length(); i++) {
            JSONObject songJson = songsJson.getJSONObject(i);
            Song song = parseSong(songJson);
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

        initialized.set(true);
    }



    private static Song parseSong(JSONObject songJson) {
        Song result = new Song();

        result.id = Integer.parseInt(songJson.getString("id"));
        result.title = songJson.getString("title");
        result.type = songJson.getString("type");
        JSONArray jsonArray = songJson.getJSONArray("ds");
        for (int i = 0; i < jsonArray.length(); i++) {
            result.ds.add(jsonArray.getFloat(i));
        }
        jsonArray = songJson.getJSONArray("level");
        for (int i = 0; i < jsonArray.length(); i++) {
            result.level.add(jsonArray.getString(i));
        }
        jsonArray = songJson.getJSONArray("cids");
        for(int i = 0; i < jsonArray.length(); i++) {
            result.cids.add(jsonArray.getInt(i));
        }
        jsonArray = songJson.getJSONArray("charts");
        for(int i = 0; i < jsonArray.length(); i++) {
            Song.Chart chart = new Song.Chart();
            JSONObject chartJson = jsonArray.getJSONObject(i);
            JSONArray notesJson = chartJson.getJSONArray("notes");
            for(int j = 0; j < notesJson.length(); j++) {
                if(result.type.equals("SD") && j == 3) {
                    chart.notes.add(0);
                }
                chart.notes.add(notesJson.getInt(j));
            }
            chart.author = chartJson.getString("charter");
            result.charts.add(chart);
        }
        JSONObject basicInfo = songJson.getJSONObject("basic_info");
        Song.Info info = new Song.Info();
        info.title = basicInfo.getString("title");
        info.artist = basicInfo.getString("artist");
        info.category = basicInfo.getString("genre");
        info.bpm = basicInfo.getInt("bpm");
        info.releaseDate = basicInfo.getString("release_date");
        info.from = basicInfo.getString("from");
        info.isNew = basicInfo.getBoolean("is_new");
        result.info = info;
        return result;
    }
}