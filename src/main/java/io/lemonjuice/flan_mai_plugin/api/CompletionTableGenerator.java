package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.completion_table.PlateCompletionTableRenderer;
import io.lemonjuice.flan_mai_plugin.model.PlayRecord;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import io.lemonjuice.flan_mai_plugin.utils.RecordUtils;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flan_mai_plugin.utils.enums.MaiVersion;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO 完成api方法
@Log4j2
public class CompletionTableGenerator {
    public static final String PLATE_NOT_FOUND = "Plate Not Found";

    public static String generateWithPlates(long qq, String plateName) {
        try {
            File output = new File("./cache/mai_completion_table/" + plateName + "_" + qq + ".png");

            List<MaiVersion> versions = MaiVersion.matchVersion(String.valueOf(plateName.charAt(0)));
            List<Integer> requirement = new ArrayList<>();

            plateName = plateName.replace("极", "極");
            if(!versions.isEmpty())
                plateName = versions.getFirst().getMappingName() + plateName.substring(1);

            if(!new File(FileRefs.PLATE_DIR + plateName + ".png").exists()) {
                return PLATE_NOT_FOUND;
            }

            for (MaiVersion ver : versions) {
                String verName = ver.getMappingName();
                requirement.addAll(SongManager.getPlateRequirement(verName));
            }

            List<String> versionNames = versions.stream().map(MaiVersion::getEnglishName).toList();
            JSONArray recordsJson = MaiMaiProberService.requestPlateProgress(qq, versionNames);
            List<PlayRecord> records = new ArrayList<>();
            for(int i = 0; i < recordsJson.length(); i++) {
                records.add(RecordUtils.parsePlateRecord(recordsJson.getJSONObject(i)));
            }

            PlateCompletionTableRenderer renderer = new PlateCompletionTableRenderer(plateName, requirement, records, output, ImageFormat.PNG);
            if(!renderer.renderAndOutput()) {
                return "";
            }

            return output.getPath();
        } catch (Exception e) {
            if(e instanceof NotInitializedException) {
                throw e;
            }
            log.error("牌子完成表生成失败！", e);
        }
        return "";
    }

    public static String generateWithLevel(long qq, int level) {
        return "";
    }
}
