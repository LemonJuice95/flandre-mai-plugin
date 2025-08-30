package io.lemonjuice.flan_mai_plugin.utils;

import io.lemonjuice.flan_mai_plugin.model.PlayRecord;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import io.lemonjuice.flan_mai_plugin.utils.enums.Rank;
import io.lemonjuice.flan_mai_plugin.utils.enums.SongLevelLabel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecordUtils {
    public static List<PlayRecord> searchRecordBySongId(long qq, int songId) {
        List<PlayRecord> result = new ArrayList<>();

        try {
            JSONArray records = MaiMaiProberService.requestPlayerRecords(qq);
            for (int i = 0; i < records.length(); i++) {
                JSONObject recordI = records.getJSONObject(i);
                if(recordI.optInt("song_id", -1) == songId) {
                    result.add(RecordUtils.parsePlayRecord(recordI));
                }
            }
        } catch (NullPointerException ignored) {
            //直接返回空列表
        }

        return result;
    }

    public static PlayRecord parsePlateRecord(JSONObject json) {
        PlayRecord result = new PlayRecord();

        result.achievements = json.getFloat("achievements");
        result.fcStatus = json.getString("fc");
        result.syncStatus = json.getString("fs");
        result.songId = json.getInt("id");
        result.level = json.getString("level");
        result.levelIndex = json.getInt("level_index");
        result.title = json.getString("title");
        result.type = json.getString("type");
        result.rank = Rank.fromAchievement(result.achievements);

        return result;
    }

    public static PlayRecord parsePlayRecord(JSONObject json) {
        PlayRecord result = new PlayRecord();

        result.achievements = json.getFloat("achievements");
        result.level = json.getString("level");
        result.dxScore = json.optInt("dxScore", 0);
        try {
            result.levelLabel = SongLevelLabel.fromString(json.getString("level_label"));
        } catch (IllegalArgumentException | JSONException e) {
            result.levelLabel = SongLevelLabel.BASIC;
        }
        result.title = json.getString("title");
        result.type = json.getString("type");
        result.syncStatus = json.getString("fs");
        result.ds = json.getFloat("ds");
        result.rating = json.getInt("ra");
        result.songId = json.getInt("song_id");
        result.rank = Rank.fromString(json.getString("rate"));
        result.levelIndex = json.getInt("level_index");
        result.fcStatus = json.getString("fc");

        return result;
    }
}
