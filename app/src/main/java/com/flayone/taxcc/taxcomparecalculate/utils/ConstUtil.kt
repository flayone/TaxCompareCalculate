package com.flayone.taxcc.taxcomparecalculate.utils


val yearLevelList = arrayListOf(0, 36000, 144000, 300000, 420000, 660000, 960000)
val taxRateList = arrayListOf("0.03", "0.10", "0.20", "0.25", "0.30", "0.35", "0.45")

const val HISTORY_TAG = "historyList"
const val HISTORY_TAG_A = "historyListA"//月均算法历史数据
const val HISTORY_TAG_YEAR = "historyListYear"//年累计算法历史数据
const val LOCAL_Data = "LocalData"
const val hisLimit = 20 //历史记录列表个数限制
const val threshold = "5000" //起征点
