package org.aussiebox.starexpress.util;

public final class RoleInfo {
    private final String roleNamespace;
    private final RoleType roleType;
    private final int roleColor;
    private final GuidebookEntry guidebookEntry;

    public RoleInfo(String namespace, RoleType type, int color, GuidebookEntry entry) {
        this.roleNamespace = namespace;
        this.roleType = type;
        this.roleColor = color;
        this.guidebookEntry = entry;
    }

    public String namespace() {
        return this.roleNamespace;
    }

    public RoleType type() {
        return this.roleType;
    }

    public int color() {
        return this.roleColor;
    }

    public GuidebookEntry guidebookEntry() {
        return this.guidebookEntry;
    }

    public enum RoleType {
        ROLE,
        MODIFIER;

        RoleType() {
        }
    }

    public enum GuidebookEntry {
        /**
         * 指南书左侧的职业阵营分类。
         *
         * <p>这里单独保留 VIGILANTE，而不是继续把义警并入 GOOD，
         * 这样 GuidebookScreen 可以直接映射 Wathe 的正式 Faction，
         * 原版义警和扩展显式注册的义警职业也就会进入同一个独立列表。</p>
         */
        GOOD,
        VIGILANTE,
        NEUTRAL,
        EVIL,
        NONE;

        GuidebookEntry() {
        }
    }
}
