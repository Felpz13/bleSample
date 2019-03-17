package com.felipe.claro.bleSample

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.felipe.claro.bleSample.Auxiliares.ListScanAdapter
import kotlinx.android.synthetic.main.activity_scan.*
import android.content.Intent


class ScanActivity : AppCompatActivity() {

    private val bleScanner : BluetoothLeScanner
        get() {
            val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            return bluetoothAdapter.bluetoothLeScanner
        }

    //callback resultado da pesquisa de dispositivos
    private val leScanCallback = object : ScanCallback()
    {
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            updateList(result)
        }
    }

    private val devicesList = arrayListOf<BluetoothDevice>()
    private lateinit var listAdapter : ListScanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val tag = applicationContext.getString(R.string.app_name)
        listAdapter = ListScanAdapter(this, devicesList)

        if(!bleSupported())
        {
            Log.d(tag , textLog(R.string.ble_not_supported))
        }

        if(!hasPermissions(this))
        {
            getPermissions()
        }

        lista.adapter = listAdapter

        btStart.setOnClickListener {
            startScan()
            Log.d(tag, textLog(R.string.scan_on))
        }

        btStop.setOnClickListener {
            stopScan()
            Log.d(tag, textLog(R.string.scan_off))
        }

        lista.setOnItemClickListener { _, _, position, _ ->

            val selectedDevice = devicesList[position]

            stopScan()
            Log.d(tag, textLog(R.string.scan_off))

            val detailIntent = Intent(applicationContext, DeviceDetailsActivity::class.java)
            detailIntent.putExtra("device", selectedDevice)

            startActivity(detailIntent)
        }

    }

    //Verificar se o aparelho suporta BLE
    private fun bleSupported() : Boolean
    {
        val packageManager = packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    //checar se tem permissões
    private fun hasPermissions(context: Context, permissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN)
    ) : Boolean
    {
        permissions.forEach {
            if(ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED)
            {
                return false
            }
        }
        return true
    }

    //Pedir permissões (Localização e BLE)
    private fun getPermissions(permissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION))
    {
        val PERMISSION_ALL = 1

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL)

        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, PERMISSION_ALL)
    }

    //Procura dispositivos bluetooth
    private fun startScan()
    {
        //exemplo de filtro
        val beaconFilter = ScanFilter.Builder()
            //.setServiceUuid(TIPO de SERVICO BUSCADO)
            .build()

        //adcionar todos os filtros feitos na lista de filtros
        val filter = mutableListOf<ScanFilter>()
        filter.add(beaconFilter)

        val settings = ScanSettings.Builder()
            //.setScanMode(TIPO DE FREQUENCIA)
            .build()

        bleScanner.startScan(filter, settings, leScanCallback)
    }

    //Para de procurar dispostivos bluetooth
    private fun stopScan()
    {
        bleScanner.stopScan(leScanCallback)
        devicesList.clear()
        listAdapter.notifyDataSetChanged()
    }

    //atualiza list view com dispositivos encontrados
    private fun updateList(result: ScanResult) {
        val device = result.device

        if (!devicesList.contains(device)) {
            devicesList.add(device)
            listAdapter.notifyDataSetChanged()
        }
    }

    fun textLog(id : Int) : String = applicationContext.getString(id)
}
