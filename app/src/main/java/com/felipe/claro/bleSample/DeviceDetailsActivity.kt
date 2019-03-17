package com.felipe.claro.bleSample

import android.bluetooth.*
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_device_details.*
import java.util.*

class DeviceDetailsActivity : AppCompatActivity() {

    lateinit var tag : String

    //instacia do servidorGatt e lista de servicos encontrados
    private lateinit var bluetoothGatt : BluetoothGatt
    var services = listOf<BluetoothGattService>()

    //Código UUID do serviço e da caracteristica desejada
    private val SERVICE : UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
    private val CHAR : UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")

    //Retorno da conexão com o servidor
    private val gattCallback = object : BluetoothGattCallback()
    {
        //acionada quando há mudança na conexão com o device
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

            when(newState)
            {
                //conexao bem sucedida
                BluetoothProfile.STATE_CONNECTED ->
                {
                    Log.d(tag, "CONECTADO")

                    gatt!!.discoverServices() //procura os serviços do dispositivo

                }

                BluetoothProfile.STATE_DISCONNECTED ->
                {
                    Log.d(tag, "DESCONECTADO")

                    bluetoothGatt.close()


                    //TRATAR PROBLEMA DE PERDA DE CONEXÃO
                    runOnUiThread {
                        displayNewValue("Falha ao conectar!!!")
                    }
//                    val detailIntent = Intent(applicationContext, DeviceDetailsActivity::class.java)
//                    detailIntent.putExtra("device", gatt!!.device)
//
//                    startActivity(detailIntent)
                }
            }
        }

        //acionado na chamada do gatt!!.discoverServices()
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            Log.d(tag, "Serviços Encontrados: ${gatt!!.services.size}")

            services = gatt.services



            //display dos servicos encontrados
            var i = 1
            services.forEach{service ->

                Log.d(tag, "SERVICE $i -> ${service.uuid}")

                val char = service.characteristics
                var j = 1

                char.forEach{c ->
                    Log.d(tag, "SERVICE $i / $j -> ${c.uuid}")
                    j++
                }
                i++
            }

            gatt.readCharacteristic(gatt.getService(SERVICE).getCharacteristic(CHAR))
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?,
                                          characteristic: BluetoothGattCharacteristic?,
                                          status: Int){

            val result = characteristic!!.value

            Log.d(tag, "VALUE: ${result[0]}")

            runOnUiThread {
                displayNewValue(result[0].toString())
            }

            gatt!!.setCharacteristicNotification(characteristic, true)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?,
                                             characteristic: BluetoothGattCharacteristic?){

            val result = characteristic!!.value

            Log.d(tag, "VALUE: ${result[0]}")

            runOnUiThread {
                displayNewValue(result[0].toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)

        tag = applicationContext.getString(R.string.app_name)

        val dados: Bundle = intent.extras!!
        val device = dados["device"]!! as BluetoothDevice


        //cria a conexao com o device selecionado
        bluetoothGatt = device.connectGatt(applicationContext, false, gattCallback)

        btDisc.setOnClickListener {
            bluetoothGatt.disconnect()
            finish()
        }
    }

    private fun displayNewValue(value : String)
    {
        textValue.text = value
    }
}
