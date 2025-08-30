package io.lemonjuice.flan_mai_plugin.image.renderer.completion_table.plan;

import io.lemonjuice.flan_mai_plugin.model.PlayRecord;

public class NormalPlan extends Plan {
    @Override
    public boolean validateCompleted(PlayRecord playRecord) {
        return playRecord.achievements >= 100.0F;
    }

    @Override
    public String getIconPath(PlayRecord playRecord) {
        return playRecord.rank.getPicPath();
    }

    @Override
    public int getIconWidth() {
        return 102;
    }

    @Override
    public int getIconHeight() {
        return 46;
    }
}
