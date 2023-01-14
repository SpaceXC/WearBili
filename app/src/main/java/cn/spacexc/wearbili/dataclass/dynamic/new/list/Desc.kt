package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Desc(
    @SerializedName("rich_text_nodes")
    val richTextNodes: List<ItemRichTextNode>,
    @SerializedName("text")
    val text: String
)