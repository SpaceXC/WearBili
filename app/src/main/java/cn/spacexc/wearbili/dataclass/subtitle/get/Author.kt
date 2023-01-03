package cn.spacexc.wearbili.dataclass.subtitle.get


import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("birthday")
    val birthday: Int,
    @SerializedName("face")
    val face: String,
    @SerializedName("in_reg_audit")
    val inRegAudit: Int,
    @SerializedName("is_deleted")
    val isDeleted: Int,
    @SerializedName("is_fake_account")
    val isFakeAccount: Int,
    @SerializedName("is_senior_member")
    val isSeniorMember: Int,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("sign")
    val sign: String
)