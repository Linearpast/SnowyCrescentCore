package io.zershyan.sccore.common.datagen.init;

import io.zershyan.sccore.SCCore;

import java.util.List;

public enum SCCConfigLang {
    //server
    inviteValidTime(
            "inviteValidTime",
            "邀请过期时间(秒)",
            "Invite Valid Time (Seconds)"
    ),
    inviteValidDistance(
            "inviteValidDistance",
            "邀请有效距离(格)",
            "Invite Valid Distance (Blocks)"
    ),
    inviteCooldown(
            "inviteCooldown",
            "邀请冷却时间(秒)",
            "Invite Cooldown (Seconds)"
    ),
    applyValidTime(
            "applyValidTime",
            "申请过期时间(秒)",
            "Apply Valid Time (Seconds)"
    ),
    applyValidDistance(
            "applyValidDistance",
            "申请有效距离(格)",
            "Apply Valid Distance (Blocks)"
    ),
    applyCooldown(
            "applyCooldown",
            "申请冷却时间(秒)",
            "Apply Cooldown (Seconds)"
    ),
    requestValidTime(
            "requestValidTime",
            "请求过期时间(秒)",
            "Request Valid Time (Seconds)"
    ),
    requestCooldown(
            "requestCooldown",
            "请求冷却时间(秒)",
            "Request Cooldown (Seconds)"
    ),

    //client

    //common

    //startup
    enableExample(
            "enableExample",
            "开启SCCore的代码示例",
            "Enable SCCore Code Example"
    ),

    //type
    developmentConfig(
            "developmentConfig",
            "开发环境配置",
            "Development Config"
    ),

    ;

    private final String name;
    private final SCCoreLang.Lang lang;

    SCCConfigLang(String name, String zhCn, String enUs) {
        this.name = name;
        this.lang = new SCCoreLang.Lang(zhCn, enUs);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return SCCore.MODID + ".configuration." + name;
    }

    public static void initLang(List<SCCoreLang.LangEntity<?>> langList) {
        for (SCCConfigLang value : SCCConfigLang.values()) {
            langList.add(new SCCoreLang.LangEntity<>(value.getKey(), value.lang));
        }
    }
}
