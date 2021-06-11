package com.example.compl.util
import java.util.HashMap

object Constants {

    val pieDataTypeToPosition:HashMap<String,Int> = hashMapOf()
    val pieDataTypes = mutableListOf<String>()
    val pieDataColour = mutableListOf<String>()

    init {
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