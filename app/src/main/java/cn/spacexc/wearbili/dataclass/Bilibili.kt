package cn.spacexc.wearbili.dataclass

import com.google.gson.annotations.SerializedName

/**
 * Created by XC-Qan on 2022/6/8.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

data class SpaceProfileResult(
    val code: Int,
    val data : User
)

data class User(
    val mid : Long,
    val name : String,
    val sex : String,
    val face : String,
    val sign : String,
    val level : Int,
    val silence : Int,
    val coins : Float,
    val vip: VIP,
    val follower : Int
)

data class VIP(
    val type : Int,
    val status : Int,
    val due_date : Long,
    val label : VIPLabel,
    val avatar_subscript : Int,
    val nickname_color : String
)

data class VIPLabel(
    val text : String,
    val bg_color : String,
)

data class LoginQrCode(
    val code : Int,
    val data: QrCodeData
)

data class QrCodeData(
    val url : String,
    val oauthKey : String
)

data class QrCodeLoginStats(
    val code : Int,
    val status : Boolean,
    @SerializedName("data") val data : Any
)

/*
data class VideoRecommend(
    val code : Int,
    val massage : String,
    val data : VideoCardData
)

data class VideoCard(
    @SerializedName("goto") val videoIdType : String,
    @SerializedName("param") val videoId : String,
    val cover : String,
    val title : String,
    val args : VideoArgs,
    val player_args : PlayerArgs?,
    @SerializedName("cover_left_1_content_description") val playTimes : String,
    @SerializedName("cover_left_2_content_description") val danmakuCount : String,
    @SerializedName("cover_right_text") val duration : String
)
data class VideoArgs(
    val up_id : Long,
    val up_name : String,
    val aid: Long?,
    val rname : String,
    val tname : String
)
data class PlayerArgs(
    val duration : Long?
)
data class VideoCardData(
    val items : Array<VideoCard>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VideoCardData

        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int {
        return items.contentHashCode()
    }
}
*/

data class VideoInfo(
    val code : Int,
    val massage: String,
    val data : VideoInfoData
)

data class VideoInfoData(
    val bvid : String,
    val aid : Long,
    val cid : Long,
    val videos : Int,
    val pic : String,
    val tid : Int,
    val tname : String,
    val title : String,
    val pubdate : Long,
    val duration : Long,
    val ctime : Long,
    val desc : String,
    val owner: VideoOwner,
    val stat: VideoStat,
)
data class VideoOwner(
    val mid : Long,
    val name: String,
    val face: String
)
data class VideoStat(
    val danmaku : Int,
    val reply : Int,
    val favorite : Int,
    val coin : Int,
    val share : Int,
    val like : Int,
    val dislike : Int,
    val view : Long
)

data class VideoStreams(
    val data : VideoStreamData
)
data class VideoStreamData(
    val durl : Array<VideoStreamUrl>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VideoStreamData

        if (!durl.contentEquals(other.durl)) return false

        return true
    }

    override fun hashCode(): Int {
        return durl.contentHashCode()
    }
}

data class VideoStreamUrl(
    val url : String
)

data class VideoSearch(
    var code: Int,
    var message: String,
    var ttl: Int,
    var data: SearchData
) {
    data class SearchData(
        var seid: String,
        var page: Int,
        var pagesize: Int,
        var numResults: Int,
        var numPages: Int,
        var suggest_keyword: String,
        var rqt_type: String,
        var cost_time: CostTime,
        var exp_list: Any,
        var egg_hit: Int,
        var show_column: Int,
        var in_black_key: Int,
        var in_white_key: Int,
        var result: List<VideoSearchResult>
    ) {
        data class CostTime(
            var params_check: String,
            var illegal_handler: String,
            var as_response_format: String,
            var as_request: String,
            var save_cache: String,
            var deserialize_response: String,
            var as_request_format: String,
            var total: String,
            var main_handler: String
        )
        data class VideoSearchResult(
            var type: String,
            var id: Int,
            var author: String,
            var mid: Int,
            var typeid: String,
            var typename: String,
            var arcurl: String,
            var aid: Long,
            var bvid: String,
            var title: String,
            var description: String,
            var arcrank: String,
            var pic: String,
            var play: Int,
            var video_review: Int,
            var favorites: Int,
            var tag: String,
            var review: Int,
            var pubdate: Int,
            var senddate: Int,
            var duration: String,
            var badgepay: Boolean,
            var view_type: String,
            var is_pay: Int,
            var is_union_video: Int,
            var rec_tags: Any,
            var rank_score: Int,
            var like: Int,
            var upic: String,
            var corner: String,
            var cover: String,
            var desc: String,
            var url: String,
            var rec_reason: String,
            var danmaku: Int,
            var hit_columns: List<String>,
            var new_rec_tags: List<*>
        )
    }
}


data class VideoComment(
    val code : Int,
    val message: String,
    val ttl : Int,
    val data : CommentData
)
data class CommentData(
    val cursor : CommentCursor,
    val hots : List<CommentContentData>,
    val notice : CommentNotice,
    val replies : List<CommentContentData>,
    val top : CommentContentData,
    val upper : UpperInfo
) {

}

data class UpperInfo(
    val mid : Long
)
data class CommentCursor(
    val all_count : Int,
    val is_begin : Boolean,
    val is_end : Boolean,
    val name : String
)
data class CommentNotice(
    val content : String,
    val link : String,
    val id : Int,
    val title: String
)
data class CommentContentData(
    var rpid: Long,
    var oid: Int,
    var type: Int,
    var mid: Int,
    var root: Int,
    var parent: Int,
    var dialog: Int,
    var count: Int,
    var rcount: Int,
    var state: Int,
    var fansgrade: Int,
    var attr: Int,
    var ctime: Int,
    var rpid_str: String,
    var root_str: String,
    var parent_str: String,
    var like: Int,
    var action: Int,
    var member: Member,
    var content: Content,
    var assist: Int,
    var folder: Folder,
    var up_action: UpAction,
    var show_follow: Boolean,
    var invisible: Boolean,
    var reply_control: ReplyControl,
    var replies: Array<Replies>,
    var card_label: Array<CardLabel>,
    var is_top : Boolean = false
) {
    data class CardLabel(
        var rpid: Long,
        var text_content: String,
        var text_color_day: String,
        var text_color_night: String,
        var label_color_day: String,
        var label_color_night: String,
        var image: String,
        var type: Int,
        var background: String,
        var background_width: Int,
        var background_height: Int,
        var jump_url: String
    )
    data class Member(
        var mid: String,
        var uname: String,
        var sex: String,
        var sign: String,
        var avatar: String,
        var rank: String,
        var DisplayRank: String,
        var face_nft_new: Int,
        var is_senior_member: Int,
        var level_info: LevelInfo,
        var pendant: Pendant,
        var nameplate: Nameplate,
        var official_verify: OfficialVerify,
        var vip: Vip,
        var fans_detail: Any,
        var following: Int,
        var is_followed: Int,
        var user_sailing: UserSailing,
        var is_contractor: Boolean,
        var contract_desc: String,
        var nft_interaction: Any
    ) {
        data class LevelInfo(
            var current_level: Int,
            var current_min: Int,
            var current_exp: Int,
            var next_exp: Int
        )

        data class Pendant(
            var pid: Int,
            var name: String,
            var image: String,
            var expire: Int,
            var image_enhance: String,
            var image_enhance_frame: String
        )

        data class Nameplate(
            var nid: Int,
            var name: String,
            var image: String,
            var image_small: String,
            var level: String,
            var condition: String
        )

        data class OfficialVerify(var type: Int, var desc: String)
        data class Vip(
            var vipType: Int,
            var vipDueDate: Long,
            var dueRemark: String,
            var accessStatus: Int,
            var vipStatus: Int,
            var vipStatusWarn: String,
            var themeType: Int,
            var label: Label,
            var avatar_subscript: Int,
            var nickname_color: String
        ) {
            data class Label(
                var path: String,
                var text: String,
                var label_theme: String,
                var text_color: String,
                var bg_style: Int,
                var bg_color: String,
                var border_color: String
            )
        }

        data class UserSailing(var pendant: Any, var cardbg: Any, var cardbg_with_focus: Any)
    }

    data class Content(
        var message: String,
        var plat: Int,
        var device: String,
        var max_line: Int
    )

    data class Folder(var has_folded: Boolean, var is_folded: Boolean, var rule: String)
    data class UpAction(var like: Boolean, var reply: Boolean)
    data class ReplyControl(
        var up_reply: Boolean,
        var sub_reply_entry_text: String,
        var sub_reply_title_text: String,
        var time_desc: String
    )

    data class Replies(
        var rpid: Long,
        var oid: Int,
        var type: Int,
        var mid: Int,
        var root: Long,
        var parent: Long,
        var dialog: Long,
        var count: Int,
        var rcount: Int,
        var state: Int,
        var fansgrade: Int,
        var attr: Int,
        var ctime: Int,
        var rpid_str: String,
        var root_str: String,
        var parent_str: String,
        var like: Int,
        var action: Int,
        var member: Member,
        var content: Content,
        var replies: Any,
        var assist: Int,
        var folder: Folder,
        var up_action: UpAction,
        var show_follow: Boolean,
        var invisible: Boolean,
        var reply_control: ReplyControl
    ) {
        data class Member(
            var mid: String,
            var uname: String,
            var sex: String,
            var sign: String,
            var avatar: String,
            var rank: String,
            var DisplayRank: String,
            var face_nft_new: Int,
            var is_senior_member: Int,
            var level_info: LevelInfo,
            var pendant: Pendant,
            var nameplate: Nameplate,
            var official_verify: OfficialVerify,
            var vip: Vip,
            var fans_detail: Any,
            var following: Int,
            var is_followed: Int,
            var user_sailing: UserSailing,
            var is_contractor: Boolean,
            var contract_desc: String,
            var nft_interaction: Any
        ) {
            data class LevelInfo(
                var current_level: Int,
                var current_min: Int,
                var current_exp: Int,
                var next_exp: Int
            )

            data class Pendant(
                var pid: Int,
                var name: String,
                var image: String,
                var expire: Int,
                var image_enhance: String,
                var image_enhance_frame: String
            )

            data class Nameplate(
                var nid: Int,
                var name: String,
                var image: String,
                var image_small: String,
                var level: String,
                var condition: String
            )

            data class OfficialVerify(var type: Int, var desc: String)
            data class Vip(
                var vipType: Int,
                var vipDueDate: Long,
                var dueRemark: String,
                var accessStatus: Int,
                var vipStatus: Int,
                var vipStatusWarn: String,
                var themeType: Int,
                var label: Label,
                var avatar_subscript: Int,
                var nickname_color: String
            ) {
                data class Label(
                    var path: String,
                    var text: String,
                    var label_theme: String,
                    var text_color: String,
                    var bg_style: Int,
                    var bg_color: String,
                    var border_color: String
                )
            }

            data class UserSailing(
                var pendant: Pendant,
                var cardbg: Any,
                var cardbg_with_focus: Any
            ) {
                data class Pendant(
                    var id: Int,
                    var name: String,
                    var image: String,
                    var jump_url: String,
                    var type: String,
                    var image_enhance: String,
                    var image_enhance_frame: String
                )
            }
        }

        data class Content(
            var message: String,
            var plat: Int,
            var device: String,
            var max_line: Int,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentContentData

        if (rpid != other.rpid) return false
        if (oid != other.oid) return false
        if (type != other.type) return false
        if (mid != other.mid) return false
        if (root != other.root) return false
        if (parent != other.parent) return false
        if (dialog != other.dialog) return false
        if (count != other.count) return false
        if (rcount != other.rcount) return false
        if (state != other.state) return false
        if (fansgrade != other.fansgrade) return false
        if (attr != other.attr) return false
        if (ctime != other.ctime) return false
        if (rpid_str != other.rpid_str) return false
        if (root_str != other.root_str) return false
        if (parent_str != other.parent_str) return false
        if (like != other.like) return false
        if (action != other.action) return false
        if (member != other.member) return false
        if (content != other.content) return false
        if (assist != other.assist) return false
        if (folder != other.folder) return false
        if (up_action != other.up_action) return false
        if (show_follow != other.show_follow) return false
        if (invisible != other.invisible) return false
        if (reply_control != other.reply_control) return false
        if (!replies.contentEquals(other.replies)) return false
        if (!card_label.contentEquals(other.card_label)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rpid.hashCode()
        result = 31 * result + oid
        result = 31 * result + type
        result = 31 * result + mid
        result = 31 * result + root
        result = 31 * result + parent
        result = 31 * result + dialog
        result = 31 * result + count
        result = 31 * result + rcount
        result = 31 * result + state
        result = 31 * result + fansgrade
        result = 31 * result + attr
        result = 31 * result + ctime
        result = 31 * result + rpid_str.hashCode()
        result = 31 * result + root_str.hashCode()
        result = 31 * result + parent_str.hashCode()
        result = 31 * result + like
        result = 31 * result + action
        result = 31 * result + member.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + assist
        result = 31 * result + folder.hashCode()
        result = 31 * result + up_action.hashCode()
        result = 31 * result + show_follow.hashCode()
        result = 31 * result + invisible.hashCode()
        result = 31 * result + reply_control.hashCode()
        result = 31 * result + replies.contentHashCode()
        result = 31 * result + card_label.contentHashCode()
        return result
    }
}


data class VideoRecommendItem(
    val av_feature: String,
    val bvid: String,
    val cid: Int,
    val duration: Int,
    val goto: String,
    val id: Int,
    val is_followed: Int,
    val ogv_info: Any?,
    val owner: VideoRecommendOwner,
    val pic: String,
    val pos: Int,
    val pubdate: Int,
    val rcmd_reason: RcmdReason,
    val room_info: Any?,
    val show_info: Int,
    val stat: VideoRecommendStat,
    val title: String,
    val track_id: String,
    val uri: String
)



data class VideoRecommendOwner(
    val face: String,
    val mid: Int,
    val name: String
)

data class VideoRecommendStat(
    val danmaku: Int,
    val like: Int,
    val view: Int
)

data class VideoRecommendData(
    val business_card: Any?,
    val floor_info: Any?,
    val item: List<VideoRecommendItem>,
    val user_feature: String
)

data class RcmdReason(
    val content: String?,
    val reason_type: Int
)

data class VideoRecommend(
    val code: Int,
    val `data`: VideoRecommendData,
    val message: String,
    val ttl: Int
)