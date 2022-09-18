package cn.spacexc.wearbili.dataclass.history

data class Data(
    val cursor: Cursor,
    val list: List<HistoryObject>,
    val tab: List<Tab>
)