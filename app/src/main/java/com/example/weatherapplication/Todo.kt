package com.example.weatherapplication

data class Todo (var userID:Int,var Id: Int,var title:String,var compelted:Boolean) {
    override fun toString(): String {
        return "Todo(userID=$userID, Id=$Id, title='$title', compelted=$compelted)"
    }
}