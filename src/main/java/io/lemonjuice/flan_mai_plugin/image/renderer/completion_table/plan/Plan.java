package io.lemonjuice.flan_mai_plugin.image.renderer.completion_table.plan;

import io.lemonjuice.flan_mai_plugin.model.PlayRecord;

public abstract class Plan {
    public abstract boolean validateCompleted(PlayRecord playRecord);

    public abstract String getIconPath(PlayRecord playRecord);

    public abstract int getIconWidth();

    public abstract int getIconHeight();
}
