package io.lemonjuice.flan_mai_plugin.utils;

import io.lemonjuice.flan_mai_plugin.model.PlayRecord;
import io.lemonjuice.flan_mai_plugin.utils.enums.Rank;
import io.lemonjuice.flan_mai_plugin.utils.enums.SongLevelLabel;
import org.json.JSONObject;

public class PlayRecordUtils {

    public static PlayRecord parsePlayRecord(JSONObject json) {
        PlayRecord result = new PlayRecord();

        result.achievements = json.getFloat("achievements");
        result.level = json.getString("level");
        result.dxScore = json.getInt("dxScore");
        result.levelLabel = SongLevelLabel.fromString(json.getString("level_label"));
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
