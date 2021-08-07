package com.example.compl.util
import java.util.HashMap

object Constants {

    val pieDataTypeToPosition:HashMap<String,Int> = hashMapOf()
    val pieDataTypes = mutableListOf<String>()
    val pieDataColour = mutableListOf<String>()
    val pieDataTypeToPosition2:HashMap<String,Int> = hashMapOf()
    val pieDataTypes2 = mutableListOf<String>()
    val pieDataColour2 = mutableListOf<String>()

    init {

        //PIE CHART 2

        pieDataTypeToPosition2["completed"] = 0
        pieDataTypes2.add("completed")
        pieDataColour2.add("#4662FB")

        pieDataTypeToPosition2["close"] = 1
        pieDataTypes2.add("close")
        pieDataColour2.add("#3b4d61")

        pieDataTypeToPosition2["under process"] = 2
        pieDataTypes2.add("under process")
        pieDataColour2.add("#6b7b8c")

        //PIE CHART 1

        pieDataTypeToPosition["Type A"] = 0
        pieDataTypes.add("Type A")
        pieDataColour.add("#2085ec")

        pieDataTypeToPosition["Type B"] = 1
        pieDataTypes.add("Type B")
        pieDataColour.add("#72b4eb")

        pieDataTypeToPosition["Type C"] = 2
        pieDataTypes.add("Type C")
        pieDataColour.add("#0a417a")

        pieDataTypeToPosition["Type D"] = 3
        pieDataTypes.add("Type D")
        pieDataColour.add("#8464a0")

        pieDataTypeToPosition["Type E"] = 4
        pieDataTypes.add("Type E")
        pieDataColour.add("#cea9bc")

        pieDataTypeToPosition["Type F"] = 5
        pieDataTypes.add("Type F")
        pieDataColour.add("#323232")
    }

}