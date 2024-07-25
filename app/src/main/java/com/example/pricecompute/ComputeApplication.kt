package com.example.pricecompute

import android.app.Application
import com.example.pricecompute.screens.ai.ChatMsg
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class ComputeApplication:Application() {
    lateinit var realm:Realm
    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    ChatMsg::class
                )
            )
        )

    }
}