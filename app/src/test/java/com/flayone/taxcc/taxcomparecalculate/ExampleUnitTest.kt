package com.flayone.taxcc.taxcomparecalculate

import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        //速算扣除数计算公式验证
        val levelList =mutableListOf<Int>()
        val quickDeductionList =mutableListOf<Int>()
        val taxRateList =mutableListOf<String>()
//        levelList.addAll(arrayListOf(0,1500,4500,9000,35000,55000,80000))
        levelList.addAll(arrayListOf(0,3000,30000,32000,35000,55000,80000))
        taxRateList.addAll(arrayListOf("0.03","0.10","0.20","0.25","0.30","0.35","0.45"))
        for (i in 1..levelList.size){
            if (i == 1){
                quickDeductionList.add(0)
            }else{
                quickDeductionList.add((levelList[i-1]*(subtract(taxRateList[i-1],taxRateList[i-2]).toDouble())).toInt()+quickDeductionList[i-2])
            }
        }
        println("速算扣除数计算公式验证 =="+quickDeductionList)

        val jsonString = "{\"l1\":\"demo\",\"l2\":2}"
        val jsonArrayString = "[{\"l1\":\"demo\",\"l2\":1},{\"l1\":\"demo\",\"l2\":2}]"
//        println("fastJSON = " +JSON.toJSONString(jsonArrayString))

        var a:A1 = JSON.parseObject(jsonString,A1::class.java)
//        var b:MutableList<A1> = JSON.parseObject(jsonString,A1::class.java)
        val s = JSON.toJSONString(a)
        val re = jsonString.toObject<A1>()
        val rl = jsonArrayString.toList<A1>()

        val mNewsSortList = Gson().fromJson<MutableList<A1>>(jsonArrayString, object : TypeToken<List<A1>>() {}.type)
        println("a == ${a.l1} + ${a.l2}  s= $s re = $re rl = $rl  mNewsSortList = $mNewsSortList")

        val divA = "2000.00"
        val divB = "17"

        println("result === ${div(divA,divB,2)}")


        assertEquals(4, 2 + 2)
    }
    data class A1(
            val l1: String? ,
            val l2: Int?
    ):JSONConvertable

    class L1:JSONConvertable{
        val l1: List<A1> = arrayListOf()
    }
}
