package cn.spacexc.wearbili.dataclass.search


import com.google.gson.annotations.SerializedName

data class AppDisplayOption(
    @SerializedName("is_search_page_grayed")
    val isSearchPageGrayed: Int
)