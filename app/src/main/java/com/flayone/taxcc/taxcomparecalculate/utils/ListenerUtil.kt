package com.flayone.taxcc.taxcomparecalculate.utils

interface BaseEnsureListener{
    fun ensure(s:String)
}
interface BasePositionListener{
    fun onClick(i :Int)
}

interface CustomSalaryListener{
    fun ensure(da:BaseCalculateModel)
}

interface BaseListener{
    fun call()
}