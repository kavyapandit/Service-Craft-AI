package com.example.pricecompute.screens.ai

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId



class ChatMsg :RealmObject{
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var text: String = ""
    var participant:Int = 0
    var isPending: Boolean = false
}