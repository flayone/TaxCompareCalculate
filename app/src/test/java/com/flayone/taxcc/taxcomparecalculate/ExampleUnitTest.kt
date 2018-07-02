package com.flayone.taxcc.taxcomparecalculate

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
        assertEquals(4, 2 + 2)
    }
}
