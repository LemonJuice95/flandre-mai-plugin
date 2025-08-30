package io.lemonjuice.flan_mai_plugin.utils;

import io.lemonjuice.flan_mai_plugin.model.Song;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Comparator;

public class SongUtils {
    public static final Comparator<String> levelComparator = (l1, l2) -> {
        String l1_ = l1.replace("+?", ".6").replace("?", ".4").replace("+", ".5");
        String l2_ = l2.replace("+?", ".6").replace("?", ".4").replace("+", ".5");
        return Float.compare(Float.parseFloat(l2_), Float.parseFloat(l1_));
    };

    public static Song parseSong(JSONObject songJson) {
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
            String levelI = jsonArray.getString(i);
            result.level.add(levelI);
            if(result.maxLevel != null) {
                result.maxLevel = levelComparator.compare(levelI, result.maxLevel) < 0 ? levelI : result.maxLevel;
            } else {
                result.maxLevel = levelI;
            }
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
