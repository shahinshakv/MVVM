package com.shahinsha.mvvm.view

import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.shahinsha.mvvm.R
import com.shahinsha.mvvm.viewmodel.MainViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var convertedAmountTextView: TextView
    private lateinit var viewModel: MainViewModel

    private var fromCurrency = "USD"
    private var toCurrency = "AED"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        fromCurrencySpinner = findViewById(R.id.from_currency_spinner)
        toCurrencySpinner = findViewById(R.id.to_currency_spinner)
        amountEditText = findViewById(R.id.amount_edit_text)
        convertedAmountTextView = findViewById(R.id.converted_amount_text_view)

        val currencyAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        )
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrencySpinner.adapter = currencyAdapter
        toCurrencySpinner.adapter = currencyAdapter

        fromCurrencySpinner.setSelection(currencyAdapter.getPosition(fromCurrency))
        toCurrencySpinner.setSelection(currencyAdapter.getPosition(toCurrency))

        amountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                convertCurrency()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fromCurrency = parent?.getItemAtPosition(position).toString()
                convertCurrency()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        toCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                toCurrency = parent?.getItemAtPosition(position).toString()
                convertCurrency()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        viewModel.appData.observe(this, { appData ->
            if (appData != null) {
                Log.d(TAG, "Received app data: $appData")

                val amount = amountEditText.text.toString().toDoubleOrNull()
                if (amount != null) {
                    Log.i(TAG, "onCreate: "+ fromCurrency)
                    val fromRate = appData.rates[fromCurrency]
                    val toRate = appData.rates[toCurrency]
                    if (fromRate != null && toRate != null) {
                        val convertedAmount = amount * (toRate / fromRate)
                        convertedAmountTextView.text = String.format("%.2f", convertedAmount)
                    } else {
                        Log.w(TAG, "Failed to find exchange rate for currencies $fromCurrency and $toCurrency")
                        convertedAmountTextView.text = ""
                    }
                } else {
                    convertedAmountTextView.text = ""
                }
            } else {
                Log.w(TAG, "Received null app data")
                convertedAmountTextView.text = ""
            }
        })


        viewModel.fetchData(toCurrency)
    }

    private fun convertCurrency() {
        val amount = amountEditText.text.toString().toDoubleOrNull()
        if (amount != null) {
            val toCurrency = toCurrencySpinner.selectedItem.toString()
            Log.i(TAG, "convertCurrency: "+ toCurrency)
            viewModel.fetchData(toCurrency)
        } else {
            convertedAmountTextView.text = ""
        }
    }





}