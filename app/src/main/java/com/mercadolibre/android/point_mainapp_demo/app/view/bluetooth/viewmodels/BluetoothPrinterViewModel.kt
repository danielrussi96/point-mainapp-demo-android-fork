package com.mercadolibre.android.point_mainapp_demo.app.view.bluetooth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothPrinterResult
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadolibre.android.point_mainapp_demo.app.view.bluetooth.contracts.PrinterEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BluetoothPrinterViewModel : ViewModel() {

    private val _printerEventLiveDataLiveData = MutableLiveData<PrinterEvents>()
    val printerEventLiveDataLiveData: LiveData<PrinterEvents> get() = _printerEventLiveDataLiveData

    var isPasteLargeText = false

    fun makePrint(stringToPrint: String, addressDevices: String? = null) {

        if (stringToPrint.isEmpty()) {
            _printerEventLiveDataLiveData.value = PrinterEvents.DataEmpty
            return
        }

        _printerEventLiveDataLiveData.value = PrinterEvents.IsLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            addressDevices?.let { address ->
                MPManager.bluetooth.printer.makePrint(stringToPrint, address) { response ->
                    response
                        .doIfSuccess { result ->
                            resultBehavior(result)
                        }.doIfError { exception ->
                            _printerEventLiveDataLiveData.value = PrinterEvents.Error(exception)
                        }
                }
            } ?: MPManager.bluetooth.printer.makePrint(stringToPrint) { response ->
                response
                    .doIfSuccess { result ->
                        resultBehavior(result)
                    }.doIfError { exception ->
                        _printerEventLiveDataLiveData.value = PrinterEvents.Error(exception)
                    }
            }
        }
    }

    private fun resultBehavior(makePrintResult: BluetoothPrinterResult) {
        viewModelScope.launch(Dispatchers.IO) {
            _printerEventLiveDataLiveData.postValue(PrinterEvents.IsLoading(false))
            if (makePrintResult == BluetoothPrinterResult.NEED_SELECTION_DEVICE) {
                MPManager.bluetooth.discover.getPairPrinterDevices { response ->
                    response
                        .doIfSuccess { listPrinter ->
                            _printerEventLiveDataLiveData.postValue(PrinterEvents.LaunchPrinterSelector(listPrinter))
                        }.doIfError { exception ->
                            _printerEventLiveDataLiveData.value = PrinterEvents.Error(exception)
                        }
                }
            } else {
                _printerEventLiveDataLiveData.postValue(PrinterEvents.OutputResult(makePrintResult.name))
            }
        }
    }
}
