package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("contract_desc")
    val contractDesc: String,
    @SerializedName("face_nft_new")
    val faceNftNew: Int,
    @SerializedName("fans_detail")
    val fansDetail: FansDetail,
    @SerializedName("is_contractor")
    val isContractor: Boolean,
    @SerializedName("is_senior_member")
    val isSeniorMember: Int,
    @SerializedName("level_info")
    val levelInfo: LevelInfo,
    @SerializedName("mid")
    val mid: String,
    @SerializedName("nameplate")
    val nameplate: Nameplate,
    @SerializedName("nft_interaction")
    val nftInteraction: Any?,
    @SerializedName("official_verify")
    val officialVerify: OfficialVerify,
    @SerializedName("pendant")
    val pendant: Pendant,
    @SerializedName("rank")
    val rank: String,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("sign")
    val sign: String,
    @SerializedName("uname")
    val uname: String,
    @SerializedName("vip")
    val vip: Vip
)