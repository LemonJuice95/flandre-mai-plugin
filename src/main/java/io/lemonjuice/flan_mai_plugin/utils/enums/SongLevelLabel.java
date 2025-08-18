package io.lemonjuice.flan_mai_plugin.utils.enums;

import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import lombok.Getter;

public enum SongLevelLabel {
    BASIC(FileRefs.B50_LEVEL_LABEL_BASIC),
    ADVANCED(FileRefs.B50_LEVEL_LABEL_ADVANCED),
    EXPERT(FileRefs.B50_LEVEL_LABEL_EXPERT),
    MASTER(FileRefs.B50_LEVEL_LABEL_MASTER),
    REMASTER(FileRefs.B50_LEVEL_LABEL_REMASTER);

    @Getter
    private final String bgPic;

    private SongLevelLabel(String bgPic) {
        this.bgPic = bgPic;
    }

    public static SongLevelLabel fromString(String name) {
        return SongLevelLabel.valueOf(name.replace(":", "").toUpperCase());
    }
}
