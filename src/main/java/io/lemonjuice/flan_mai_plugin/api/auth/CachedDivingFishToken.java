package io.lemonjuice.flan_mai_plugin.api.auth;

import lombok.Getter;

@Getter
public class CachedDivingFishToken {
    private final String token;
    private final long qq;
    private final long validUntilMillis;

    public CachedDivingFishToken(String token, long qq, long validUntilMillis) {
        this.token = token;
        this.qq = qq;
        this.validUntilMillis = validUntilMillis;
    }
}
