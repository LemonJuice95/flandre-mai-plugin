package io.lemonjuice.flan_mai_plugin.image;

import lombok.Getter;

public enum ImageFormat {
    PNG("PNG");

    @Getter
    private final String formatName;

    private ImageFormat(String formatName) {
        this.formatName = formatName;
    }
}
