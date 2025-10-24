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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//TODO 完成api方法
@Log4j2
public class CompletionTableGenerator {

    public static BufferedImage generateWithPlates(long qq, String plateName) {
        try {

            List<MaiVersion> versions = MaiVersion.matchVersion(String.valueOf(plateName.charAt(0)));
            Set<Integer> requirementRaw = new HashSet<>();

            plateName = plateName.replace("极", "極");
            if(!versions.isEmpty())
                plateName = versions.getFirst().getMappingName() + plateName.substring(1);

            if(!new File(FileRefs.PLATE_DIR + plateName + ".png").exists()) {
                throw new IllegalArgumentException("不存在的牌子");
            }

            for (MaiVersion ver : versions) {
                String verName = ver.getMappingName();
                requirementRaw.addAll(SongManager.getPlateRequirement(verName));
            }

            List<String> versionNames = versions.stream().map(MaiVersion::getEnglishName).toList();
            JSONArray recordsJson = MaiMaiProberService.requestPlateProgress(qq, versionNames);
            List<PlayRecord> records = new ArrayList<>();
            for(int i = 0; i < recordsJson.length(); i++) {
                records.add(RecordUtils.parsePlateRecord(recordsJson.getJSONObject(i)));
            }

            List<Integer> requirement = new ArrayList<>(requirementRaw);

            PlateCompletionTableRenderer renderer = new PlateCompletionTableRenderer(plateName, requirement, records);

            return renderer.render();
        } catch (Exception e) {
            if(e instanceof NotInitializedException) {
                throw e;
            }
            log.error("牌子完成表生成失败！", e);
        }
        return null;
    }

    public static BufferedImage generateWithLevel(long qq, int level) {
        return null;
    }
}
