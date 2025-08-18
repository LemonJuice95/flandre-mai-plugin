package io.lemonjuice.flan_mai_plugin.song;

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
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class SongManager {
    public static final Map<Integer, Song> ID_MAP = new HashMap<>();
    public static final Map<String, Song> TITLE_MAP = new HashMap<>();

    public static Song getSongById(int id) {
        return ID_MAP.get(id);
    }

    public static Song getSongByTitle(String title) {
        return TITLE_MAP.get(title);
    }

    public static void init() {
        JSONArray songsJson = requestSongListRaw();
        for(int i = 0; i < songsJson.length(); i++) {
            JSONObject songJson = songsJson.getJSONObject(i);
            Song song = parseSong(songJson);
            ID_MAP.put(song.id, song);
            TITLE_MAP.put(song.title, song);
        }
    }

    @Nullable
    private static JSONArray requestSongListRaw() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("https://www.diving-fish.com/api/maimaidxprober/music_data");
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("获取歌曲列表失败！");
                return null;
            }
            return new JSONArray(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error("获取歌曲列表失败！", e);
        }
        return null;
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