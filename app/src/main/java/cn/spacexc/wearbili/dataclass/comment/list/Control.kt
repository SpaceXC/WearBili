package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Control(
    @SerializedName("answer_guide_android_url")
    val answerGuideAndroidUrl: String,
    @SerializedName("answer_guide_icon_url")
    val answerGuideIconUrl: String,
    @SerializedName("answer_guide_ios_url")
    val answerGuideIosUrl: String,
    @SerializedName("answer_guide_text")
    val answerGuideText: String,
    @SerializedName("bg_text")
    val bgText: String,
    @SerializedName("child_input_text")
    val childInputText: String,
    @SerializedName("disable_jump_emote")
    val disableJumpEmote: Boolean,
    @SerializedName("giveup_input_text")
    val giveupInputText: String,
    @SerializedName("input_disable")
    val inputDisable: Boolean,
    @SerializedName("root_input_text")
    val rootInputText: String,
    @SerializedName("screenshot_icon_state")
    val screenshotIconState: Int,
    @SerializedName("show_text")
    val showText: String,
    @SerializedName("show_type")
    val showType: Int,
    @SerializedName("upload_picture_icon_state")
    val uploadPictureIconState: Int,
    @SerializedName("web_selection")
    val webSelection: Boolean
)