package com.flayone.taxcc.taxcomparecalculate.data

import com.dbflow5.annotation.Column
import com.dbflow5.annotation.Database
import com.dbflow5.annotation.PrimaryKey
import com.dbflow5.annotation.Table
import com.dbflow5.config.DBFlowDatabase
import com.dbflow5.structure.BaseModel
//
//@Database(version = AppDatabase.VERSION)
//private object AppDatabase : DBFlowDatabase(){
//    override val associatedDatabaseClassFile: Class<*>
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override val databaseVersion: Int
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override val isForeignKeysSupported: Boolean
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//
//    override fun areConsistencyChecksEnabled(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun backupEnabled(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    const val VERSION = 1
//}

@Database(version = 1)
abstract class MyDateBase : DBFlowDatabase()

@Table(database = MyDateBase::class, allFields = true)
class CalculateHistoryData : BaseModel() {
    @PrimaryKey
    var id: Int = 0

    @Column
    var baseSalary = "" //税前工资
    @Column
    var welfare = ""  //五险一金
    @Column
    var entend = ""  //附加扣除数
    @Column
    var afterTex = ""  //税后
    @Column
    var taxThreshold = ""  //自定义起征数
}

//class CustomFlowSQliteOpenHelper(databaseDefinition: DatabaseDefinition, listener: DatabaseHelperListener) : FlowSQLiteOpenHelper(databaseDefinition, listener)