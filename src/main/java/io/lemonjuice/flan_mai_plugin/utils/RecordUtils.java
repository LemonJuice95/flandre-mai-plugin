package io.lemonjuice.flan_mai_plugin.utils;

import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecordUtils {
    public static List<JSONObject> searchRecordBySongId(long qq, int songId) {
        List<JSONObject> result = new ArrayList<>();

        try {
            JSONArray records = MaiMaiProberService.requestPlayerRecords(qq);
            for (int i = 0; i < records.length(); i++) {
                JSONObject recordI = records.getJSONObject(i);
                if(recordI.optInt("song_id", -1) == songId) {
                    result.add(recordI);
                }
            }
        } catch (NullPointerException ignored) {
            //直接返回空列表
        }

        return result;
    }
}
