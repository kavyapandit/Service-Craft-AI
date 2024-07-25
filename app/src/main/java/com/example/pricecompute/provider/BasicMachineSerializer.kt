package com.example.pricecompute.provider

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.pricecompute.gen.MachineList
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object MachineListSerializer:Serializer<MachineList> {
    override val defaultValue: MachineList
        get() = MachineList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): MachineList {
        try {
            return MachineList.parseFrom(input)
        }catch (e:InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: MachineList, output: OutputStream) {
        t.writeTo(output)
    }

    val Context.machineListStore: DataStore<MachineList> by dataStore(
        fileName = "machine_list.pb",
        serializer = MachineListSerializer
    )

}