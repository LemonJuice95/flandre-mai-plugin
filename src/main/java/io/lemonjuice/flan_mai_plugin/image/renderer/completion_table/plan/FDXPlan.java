package io.lemonjuice.flan_mai_plugin.image.renderer.completion_table.plan;

import io.lemonjuice.flan_mai_plugin.model.PlayRecord;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;

public class FDXPlan extends Plan {
    @Override
    public boolean validateCompleted(PlayRecord playRecord) {
        return playRecord.syncStatus.equals("fsd") || playRecord.syncStatus.equals("fsdp");
    }

    @Override
    public String getIconPath(PlayRecord playRecord) {
        return FileRefs.playBonus(playRecord.syncStatus);
    }

    @Override
    public int getIconWidth() {
        return 75;
    }

    @Override
    public int getIconHeight() {
        return 75;
    }
}
