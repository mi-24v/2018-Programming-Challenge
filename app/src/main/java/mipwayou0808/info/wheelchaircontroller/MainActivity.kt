package mipwayou0808.info.wheelchaircontroller

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.disposables.Disposable
import kotterknife.bindView

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class MainActivity : AppCompatActivity() {
    private val LOCATION_SERVICE_REQUESTER = 256
    private val BLUETOOTH_SWITCH_REQUESTER = 512

    private val forwardButton: Button by bindView(R.id.button_forward)
    private val leftButton: Button by bindView(R.id.button_left)
    private val backButton: Button by bindView(R.id.button_back)
    private val rightButton: Button by bindView(R.id.button_right)
    private val stopButton: Button by bindView(R.id.button_stop)
    private val statusText: TextView by bindView(R.id.label_status)

//    private val mBluetoothAdapter: BluetoothAdapter by lazy { initBle() }

    private val deviceData = WheelChairDevice()
    private val bleClient: RxBleClient by lazy { RxBleClient.create(applicationContext) }
    private val bleDevice: RxBleDevice by lazy { bleClient.getBleDevice(deviceData.macAddress) }
    private lateinit var bluetoothConnections: List<Disposable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cvtest)

        forwardButton.setOnClickListener { sendCommand(WheelChairCommandType.FORWARD) }
        leftButton.setOnClickListener { sendCommand(WheelChairCommandType.LEFT) }
        backButton.setOnClickListener { sendCommand(WheelChairCommandType.BACK) }
        rightButton.setOnClickListener { sendCommand(WheelChairCommandType.RIGHT) }
        stopButton.setOnClickListener { sendCommand(WheelChairCommandType.STOP) }

        bluetoothConnections += bleClient.observeStateChanges().switchMap<RxBleConnection>({ state: RxBleClient.State ->
            when(state){
                RxBleClient.State.READY -> {
                    bleDevice.establishConnection(true)
                }
                RxBleClient.State.BLUETOOTH_NOT_AVAILABLE -> {
                    AlertDialog.Builder(applicationContext)
                            .setTitle("Error")
                            .setMessage("This device does not support Bluetooth Low Energy feature.")
                            .setPositiveButton("finish", {dialog, which -> finish() })
                            .show()
                    io.reactivex.Observable.empty<RxBleConnection>()
                }
                RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED -> {
                    checkLocationPermission()
                    io.reactivex.Observable.empty<RxBleConnection>()
                }
                RxBleClient.State.BLUETOOTH_NOT_ENABLED -> {
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLUETOOTH_SWITCH_REQUESTER)
                    io.reactivex.Observable.empty<RxBleConnection>()
                }
                else -> {
                    io.reactivex.Observable.empty<RxBleConnection>()
                }
            }
        } ).subscribe(
                {connection ->
                    connection.setupNotification(deviceData.notificationUUID)
                            .flatMap({notificationObservable -> notificationObservable})
                            .subscribe(
                                {value ->
                                when(getCommandTypeFromInt(Integer.parseInt(String(value)))){
                                    WheelChairCommandType.STOP -> {
                                        statusText.text = getString(R.string.label_button_stop)
                                    }
                                    WheelChairCommandType.FORWARD -> {
                                        statusText.text = getString(R.string.label_button_forward)
                                    }
                                    WheelChairCommandType.BACK -> {
                                        statusText.text = getString(R.string.label_button_back)
                                    }
                                    WheelChairCommandType.LEFT -> {
                                        statusText.text = getString(R.string.label_button_left)
                                    }
                                    WheelChairCommandType.RIGHT -> {
                                        statusText.text = getString(R.string.label_button_right)
                                    }
                                }
                            })
                },
                {throwable ->
                    throwable.printStackTrace()
                }
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothConnections.forEach{conn -> conn.dispose()}
    }

    private fun sendCommand(type: WheelChairCommandType){
        bluetoothConnections += bleDevice.establishConnection(true)
                .flatMapSingle({connection ->
                    connection.writeCharacteristic(deviceData.writeUUID, byteArrayOf(type.payload))
                    })
                .subscribe(
                        {characteristicValue ->
                            Log.i(this.packageName, "sent value: $characteristicValue")
                        },
                        {throwable ->
                            throwable.printStackTrace()
                        }
                )
    }

    private fun initBle(): BluetoothAdapter{
        //check location permission required by BLE
        checkLocationPermission()

        //check BT function
        val badapter = BluetoothAdapter.getDefaultAdapter()
        badapter?:AlertDialog.Builder(applicationContext)
                .setTitle("Error")
                .setMessage("This device does not support Bluetooth Low Energy feature.")
                .setPositiveButton("finish", {dialog, which -> finish() })
                .show()

        if (!badapter.isEnabled)startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLUETOOTH_SWITCH_REQUESTER)

        return badapter
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_SERVICE_REQUESTER)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            LOCATION_SERVICE_REQUESTER -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog.Builder(applicationContext)
                            .setTitle("Error")
                            .setMessage("This application requires Location permission for Bluetooth Low Energy feature.")
                            .setPositiveButton("finish", {dialog, which -> finish() })
                            .show()
                }else{
                    Toast.makeText(applicationContext, "permission accepted.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            BLUETOOTH_SWITCH_REQUESTER -> {
                if (resultCode != Activity.RESULT_OK){
                    AlertDialog.Builder(applicationContext)
                            .setTitle("Error")
                            .setMessage("this application requires Bluetooth ON.")
                            .setPositiveButton("finish", {dialog, which -> finish() })
                            .show()
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}
