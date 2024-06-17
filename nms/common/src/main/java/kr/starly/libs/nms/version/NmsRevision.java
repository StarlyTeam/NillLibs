package kr.starly.libs.nms.version;

import lombok.Getter;

@Getter
public enum NmsRevision {

    v1_21_R1("1.21.0"),
    v1_20_R4("1.20.5"),
    v1_20_R3("1.20.3"),
    v1_20_R2("1.20.2"),
    v1_20_R1("1.20.0"),
    v1_19_R3("1.19.4"),
    v1_19_R2("1.19.3"),
    v1_19_R1("1.19.0"),
    v1_18_R2("1.18.2"),
    v1_18_R1("1.18.0");

    public static final NmsRevision REQUIRED_REVISION = getRequiredRevision();

    private final String packageName;
    private final int[] since;

    NmsRevision(String since) {
        this.packageName = name();
        this.since = VersionUtils.toMajorMinorPatch(since);
    }

    private static NmsRevision getRequiredRevision() {
        for (NmsRevision revision : values())
            if (VersionUtils.isServerHigherOrEqual(revision.getSince()))
                return revision;

        throw new UnsupportedOperationException("Your version of Minecraft is not supported by NmsMultiver");
    }
}