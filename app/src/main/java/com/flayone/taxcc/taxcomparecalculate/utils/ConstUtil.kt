package com.flayone.taxcc.taxcomparecalculate.utils


val yearLevelList = arrayListOf(0, 36000, 144000, 300000, 420000, 660000, 960000)
val taxRateList = arrayListOf("0.03", "0.10", "0.20", "0.25", "0.30", "0.35", "0.45")

val levelListOld = arrayListOf(0, 1500, 4500, 9000, 35000, 55000, 80000)
val taxRateListOld = arrayListOf("0.03", "0.10", "0.20", "0.25", "0.30", "0.35", "0.45")

val levelListNew = arrayListOf(0, 3000, 12000, 25000, 35000, 55000, 80000)
val taxRateListNew = arrayListOf("0.03", "0.10", "0.20", "0.25", "0.30", "0.35", "0.45")

const val HISTORY_TAG = "historyList"
const val HISTORY_TAG_A = "historyListA"//月均算法历史数据
const val HISTORY_TAG_YEAR = "historyListYear"//年累计算法历史数据
const val LOCAL_Data = "LocalData"
const val hisLimit = 20 //历史记录列表个数限制
const val threshold = "5000" //起征点


const val USER_URL = "https://flayone.github.io/public/user.html"
const val PRIVACY_URL = "https://flayone.github.io/public/index.html"

const val sp_setting = "tax_setting"
const val sp_user_privacy = "tax_user_privacy"
const val sp_user_first_start_time = "sp_user_first_start_time"