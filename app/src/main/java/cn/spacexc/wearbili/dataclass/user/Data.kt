package cn.spacexc.wearbili.dataclass.user

data class Data(
    val api_host: String,
    val confirm_uri: String,
    val direct_login: Int,
    val has_login: Int,
    val user_info: UserInfo
)