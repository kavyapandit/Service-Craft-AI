package com.example.pricecompute.provider

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.pricecompute.gen.MachinePrefs
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream


object MachineSerializer : Serializer<MachinePrefs>{
    override val defaultValue: MachinePrefs
        get() = machineList[0].toMachinePrefs()

    override suspend fun readFrom(input: InputStream): MachinePrefs {
        try {
            return MachinePrefs.parseFrom(input)
        }catch (e: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: MachinePrefs, output: OutputStream) {
        t.writeTo(output)
    }
    val Context.machinePrefsStore: DataStore<MachinePrefs> by dataStore(
        fileName = "machine_prefs.pb",
        serializer = MachineSerializer
    )
}