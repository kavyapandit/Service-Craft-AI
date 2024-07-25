package com.example.pricecompute.provider

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pricecompute.ComputeApplication
import com.example.pricecompute.gen.BasicMachine
import com.example.pricecompute.gen.MachineList
import com.example.pricecompute.gen.MachinePrefs
import com.example.pricecompute.model.Machine
import com.example.pricecompute.model.MachineSpecs
import com.example.pricecompute.provider.MachineListSerializer.machineListStore
import com.example.pricecompute.provider.MachineSerializer.machinePrefsStore
import com.example.pricecompute.screens.ai.ChatMsg
import com.example.pricecompute.screens.ai.ChatUiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ComputeViewModel(
    private val realm:Realm,
    private val application:Application
):AndroidViewModel(ComputeApplication()){

    private var _currentMachine:MutableStateFlow<Machine> = MutableStateFlow(
        machineList[0]
    )
    val currentMachine =  _currentMachine.asStateFlow()

    private val jsonResponse = MutableStateFlow("")

    private val machinePrefsStore: DataStore<MachinePrefs>
        get() = application.machinePrefsStore

    private val machineListStore: DataStore<MachineList>
        get() = application.machineListStore

    init {
        saveMachineList()
    }

    val machines: List<Machine> = machineListStore.data.map {
        it.machinesList.map { machine->
            machine.toMachine()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), machineList).value

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        generationConfig = generationConfig { temperature = 0.7f }
    )

    val chats = realm.query<ChatMsg>().
        asFlow().map {
            it.list.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    private val chat = generativeModel.startChat()

    private val _uiState:MutableStateFlow<ChatUiState> = MutableStateFlow(
        ChatUiState(
            messages = listOf(
                ChatMsg().apply {
                    text = "Please enter the program you want to run"
                    participant = 1
                    isPending = false
                }
            ).plus(chats.value)
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()


    fun sendMessage(userMsg:String){
        var modifiedMsg = ""
        // add pending message
        _uiState.value.addMessage(
            ChatMsg().apply {
                text = userMsg
                participant = 0
                isPending = true
            }
        )
        realm.writeBlocking {
            copyToRealm(
                _uiState.value.messages.last().apply {
                    isPending = false
                }
            )
        }
        modifiedMsg = promptFilter(userMsg)

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(modifiedMsg)
                _uiState.value.replaceLastPendingMessage()

                response.text?.let {msg->
                    _uiState.value.addMessage(
                        ChatMsg().apply {
                            text = msg
                            participant = 1
                            isPending = false
                        }
                    )
                }
                realm.writeBlocking {
                    copyToRealm(
                        _uiState.value.messages.last().apply {
                            isPending =false
                        }
                    )
                }
                jsonResponse.value = response.text ?:
                """
{
  "RAM": 256,
  "GPU": 0,
  "SSD": 4000,
  "HDD": 0
}
"""
            }
            catch (e:Exception){
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMsg().apply {
                        text = e.message ?: "Unknown error has occurred"
                        participant = 2
                        isPending = false
                    }
                )
            }
        }
    }


    fun changeMachine(machine: Machine){
        viewModelScope.launch{
            _currentMachine.value = machine
                .also {
                    Log.d("TAG", "changeMachine: MachineChanged Successfully")
                }
        }
    }


    private fun provideDiscount(duration:Long):Double{
        when(duration){
            in 0L .. 6L -> {
                return 0.0
            }
            in 7L .. 13L -> {
                return 0.05
            }
            in 14L .. 29L -> {
                return 0.1
            }
            in 30 .. 179 -> {
                return 0.15
            }
            else -> {
                return 0.20
            }
        }
    }
    fun computePrice(ram:String, gpu:String, ssd:String, hdd:String, duration: Long):Double{
        val price = currentMachine.value.price
        val cpuPrice = priceMapRam[ram]?.times(24)?: 0.0
        val gpuPrice = priceMapGpu[gpu]?.times(24)?: 0.0
        val ssdPrice = priceMapSsd[ssd]?.times(24)?: 0.0
        val hddPrice = priceMapHdd[hdd]?.times(24)?: 0.0
        val result = price.plus(cpuPrice).plus(gpuPrice.plus(ssdPrice).plus(hddPrice)).times(duration)
        return result.minus(result*provideDiscount(duration))
    }

    fun clearChat(){
        viewModelScope.launch {
            realm.writeBlocking {
                for( i in 0..<chats.value.size){
                    findLatest(chats.value[i])?.also {
                        delete(it)
                    }
                }
                copyToRealm(
                    ChatMsg().apply {
                        text = "Hi, I am your assistant, tell me about your program"
                        participant = 1
                        isPending = false
                    }
                )
            }
        }
    }

    fun getFromDB(){
        viewModelScope.launch {
            val machine = machinePrefsStore.data.first().toMachine()
            changeMachine(
               machine
            ).also {
                Log.d("TAG","FETCHED SUCCESSFULLY")
            }
        }
    }
    fun saveToDB(){
        viewModelScope.launch {
            machinePrefsStore.updateData {
                it.toBuilder()
                    .setMachineName(currentMachine.value.machineName)
                    .setDesc(currentMachine.value.desc)
                    .setCpuLimit(currentMachine.value.plan.cpuLimit ?: 0)
                    .setGpuLimit(currentMachine.value.plan.gpuLimit ?: 0)
                    .setSsd(currentMachine.value.plan.ssd ?: 0)
                    .setHdd(currentMachine.value.plan.hdd ?: 0)
                    .setExpiryDate(currentMachine.value.plan.expiryDate.toString())
                    .build()
                    .also {
                        Log.d("TAG", "STORED SUCCESSFULLY" )
                    }
            }
        }
    }

    fun getNewMachine(){
        try{
            val machineSpecs = Json.decodeFromString<MachineSpecs>(jsonResponse.value)
            machineList.add(machineSpecs.toMachine())
            Toast.makeText(
                application,
                "Machine Added Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }catch (e:Exception){
            Toast.makeText(
                application,
                e.message?:"Unknown error has occurred",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun saveMachineList(){
        viewModelScope.launch {
            machineListStore.updateData {
                it.toBuilder()
                    .clearMachines()
                    .addAllMachines(
                        machineList.map { mac->
                            mac.toBasicMachine()
                        }
                    ).build()
            }.also {
                Log.d("TAG", "saveMachineList: UPDATED SUCCESSFULLY")
            }
        }
    }

    companion object{
        private const val API_KEY = "AIzaSyBC2NOarS10QxveyVYgAkhG4ESL5UfCaTA"
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ComputeApplication
                ComputeViewModel(app.realm,app)
            }
        }
        fun promptFilter(input:String) = "Given the following text:\n" +
                "$input\n" +
                "[detail the ram,gpu,ssd and hdd requirements for the project only in the form of json data includng fields like ram,gpu,ssd,hdd of the type integers in GB, also donot include any other textual outputs" +
                " and also compulsorily remove the backticks and the word json from the output]"
    }

}