package io.zershyan.sccore.common.datagen.init;

import io.zershyan.sccore.SCCore;

public enum SCCTranslatableLang {
    RESOURCES(new SCCoreLang.LangEntity<>(
            SCCore.MODID + ".resources",
            "Resources for " + SCCore.class.getSimpleName(),
            "Resources for " + SCCore.class.getSimpleName()
    )),
    COMMAND_RUN_FAIL(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".command_run_fail",
            "命令执行失败。",
            "Command run fail."
    )),
    COMMAND_RUN_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".command_run_success",
            "命令执行成功。",
            "Command run success."
    )),
    PLAY_ANIMATION_FAIL(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".play_animation_fail",
            "在这些玩家上播放动画失败：%s",
            "Fail to play animation with: %s"
    )),
    PLAY_ANIMATION_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".play_animation_success",
            "在%s个玩家上播放动画成功。",
            "Successfully played animation on %s player(s)."
    )),
    REMOVE_ANIMATION_FAIL(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".remove_animation_fail",
            "在这些玩家上移除动画失败：%s",
            "Fail to remove animation with: %s"
    )),
    REMOVE_ANIMATION_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".remove_animation_success",
            "在%s个玩家上移除动画成功。",
            "Successfully removed animation on %s player(s)."
    )),
    CLEAR_ANIMATIONS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".clear_animations",
            "动画已清除。",
            "Animation cleared."
    )),
    REFRESH_ANIMATIONS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".refresh_animations",
            "动画同步状态已刷新。",
            "Animation refreshed."
    )),
    ACCEPT_MESSAGE_CLICK(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".accept_message_click",
            "单击此处同意。",
            "Click here to accept."
    )),
    INVITE_MESSAGE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".invite_message",
            "已发送邀请。",
            "Invitation sent."
    )),
    INVITED_MESSAGE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".invited_message",
            "%s§c§l 邀请§r你进行动画：%s。",
            "%s§c§l invites§r you to animation: %s. "
    )),
    ACCEPT_INVITE_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".accept_invite_success",
            "已接受邀请。",
            "Invitation accepted."
    )),
    INVITE_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".invite_success",
            "%s 接受了你的动画邀请。",
            "%s has accepted your animation invitation."
    )),
    REQUEST_MESSAGE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".request_message",
            "已发送请求。",
            "Request sent."
    )),
    REQUESTED_MESSAGE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".requested_message",
            "%s§d§l 请求§r你进行动画：%s。",
            "%s§d§l requests§r you to animation: %s. "
    )),
    ACCEPT_REQUEST_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".accept_request_success",
            "已接受请求。",
            "Request accepted."
    )),
    REQUEST_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".request_success",
            "%s 接受了你的动画请求。",
            "%s has accepted your animation request."
    )),
    APPLY_JOIN_MESSAGE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".apply_join_message",
            "已发送申请。",
            "Application sent."
    )),
    APPLIED_JOIN_MESSAGE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".applied_join_message",
            "%s§b§l 申请§r加入动画。",
            "%S§b§l Apply for §r to join your animation. "
    )),
    ACCEPT_APPLY_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".accept_apply_success",
            "%s 接受了 %s 的申请。",
            "%s has accepted the application of %s."
    )),
    APPLY_SUCCESS(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".apply_success",
            "%s 接受了你的动画申请。",
            "%s has accepted your animation application."
    )),
    ANIMATION_TO_JSON(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_to_json",
            "动画%s已经存储到%s路径：",
            "The animation %s has been stored in the path on %s:"
    )),
    ANIMATION_JSON_PATH(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_json_path",
            "%s",
            "%s"
    )),
    LIST_ANIMATION_RESOURCE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".list_animation_resource",
            "%s侧的%s有：%s",
            "The %2$s on %1$s has : %s"
    )),
    ANIMATION_EXPIRE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_expire",
            "你不能执行该操作: 已过期。",
            "You cannot perform this operation: It has expired."
    )),
    ANIMATION_OUT_RANGE(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_out_range",
            "你不能执行该操作: 距离不在%s格以内。",
            "You cannot perform this operation: The distance is not within %s blocks."
    )),
    ANIMATION_OPERATION_UNSUPPORTED(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_operation_unsupported",
            "错误: 不支持这样做。",
            "Error: Unsupported operation."
    )),
    ANIMATION_COOLDOWN(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_cooldown",
            "你不能执行该操作: 冷却中(%s秒)。",
            "You cannot perform this operation: Cooling down (%s second(s))."
    )),
    ANIMATION_RESOURCE_NOT_FOUND(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_resource_not_found",
            "错误: 资源未找到，请检查资源或操作是否有误: %s",
            "Error: Resource not found, please check if there are any errors in the resource or operation : %s"
    )),
    ANIMATION_OPERATION_CANCELLED(new SCCoreLang.LangEntity<>(
            SCCoreLang.translationString + SCCoreLang.command + SCCoreLang.animation + ".animation_operation_cancelled",
            "异常: 操作被取消。",
            "Exception: Operation cancelled."
    )),
    ;

    public final SCCoreLang.LangEntity<String> langEntity;

    SCCTranslatableLang(SCCoreLang.LangEntity<String> lang) {
        this.langEntity = lang;
    }

    public String getKey() {
        return langEntity.key();
    }
}
