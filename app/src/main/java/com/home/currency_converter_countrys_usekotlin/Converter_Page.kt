package com.home.currency_converter_countrys_usekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.home.currency_converter_countrys_usekotlin.R
import org.json.JSONObject
import android.content.DialogInterface
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class Converter_Page : AppCompatActivity() {
    private lateinit var spnChoose: Spinner
    private lateinit var edWriteMoney: EditText
    private lateinit var spnConv1: Spinner
    private lateinit var spnConv2: Spinner
    private lateinit var spnConv3: Spinner
    private lateinit var btnResult: Button

    private lateinit var txtChoose1: TextView
    private lateinit var txtChoose2: TextView
    private lateinit var txtChoose3: TextView
    private lateinit var txtUpdate: TextView

    private lateinit var txtCountry1: TextView
    private lateinit var txtCountry2: TextView
    private lateinit var txtCountry3: TextView

    private var choose1: String = ""
    private var conv1: String = ""
    private var conv2: String = ""
    private var conv3: String = ""

    private var showChoose: String = ""
    private var showConv1: String = ""
    private var showConv2: String = ""
    private var showConv3: String = ""
    private var ifUSDUSD: Double = 0.0
    private var toUSD: Double = 0.0
    private var convTo1: Double = 0.0
    private var convTo2: Double = 0.0
    private var convTo3: Double = 0.0
    private var nowDate: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter_page)
        findView()
    }

    private fun catchData() {
        val catchData = "https://tw.rter.info/capi.php"
        Thread {
            try {
                val url = URL(catchData)
                val connection = url.openConnection() as HttpURLConnection
                val `is`: InputStream = connection.inputStream
                val inStream = BufferedReader(InputStreamReader(`is`))
                var line: String? = inStream.readLine()
                val json = StringBuffer()
                while (line != null) {
                    json.append(line)
                    line = inStream.readLine()
                }
                val jsonStr = json.toString()

                val jsonToUSD = JSONObject(jsonStr).getJSONObject(choose1)
                val jsonConv1 = JSONObject(jsonStr).getJSONObject(conv1)
                val jsonConv2 = JSONObject(jsonStr).getJSONObject(conv2)
                val jsonConv3 = JSONObject(jsonStr).getJSONObject(conv3)
                val jsonDate = JSONObject(jsonStr).getJSONObject("USDTWD")
                val jsonUSDUSD = JSONObject(jsonStr).getJSONObject("USDTWD")

                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.CEILING
                toUSD = jsonToUSD.getDouble("Exrate").roundTo(3)
                convTo1 = jsonConv1.getDouble("Exrate").roundTo(3)
                convTo2 = jsonConv2.getDouble("Exrate").roundTo(3)
                convTo3 = jsonConv3.getDouble("Exrate").roundTo(3)
                ifUSDUSD = jsonUSDUSD.getDouble("Exrate").roundTo(3)
                nowDate = jsonDate.getString("UTC")

                val edMoney = edWriteMoney.text.toString()
                val yourMoney = edMoney.toDouble()
                runOnUiThread {
                    if (yourMoney > 0) {
                        show_currency(choose1, conv1, txtCountry1, showConv1, convTo1)
                        show_currency(choose1, conv2, txtCountry2, showConv2, convTo2)
                        show_currency(choose1, conv3, txtCountry3, showConv3, convTo3)
                        txtUpdate.text = "匯率更新時間：$nowDate"
                        converting(choose1, conv1, yourMoney, txtChoose1, convTo1)
                        converting(choose1, conv2, yourMoney, txtChoose2, convTo2)
                        converting(choose1, conv3, yourMoney, txtChoose3, convTo3)
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("輸入錯誤")
                        builder.setMessage("數值不得少於0")
                        builder.setNegativeButton("確認") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                        builder.create().show()
                    }
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun show_currency(
        choosing: String,
        targetConvName: String,
        txtCountry: TextView,
        showConv: String,
        convTo: Double
    ) {
        txtCountry.text = when {
            choosing == "USDTWD" && targetConvName == "USDUSD" -> "$showConv,參考匯率:$toUSD"
            choosing == "USDTWD" && targetConvName == "USDTWD" -> "$showConv,參考匯率:1.0"
            choosing == "USDUSD" && targetConvName == "USDUSD" -> "$showConv,參考匯率:$ifUSDUSD"
            choosing == "USDUSD" && targetConvName == "USDTWD" -> "$showConv,參考匯率:1.0"
            else -> "$showConv,參考匯率:$convTo"
        }
    }

    private fun converting(
        choosing: String,
        targetConvName: String,
        yourMoney: Double,
        txtChoose: TextView,
        convTo: Double
    ) {
        val result: Double = when {
            choosing == targetConvName -> yourMoney
            choosing == "USDUSD" && targetConvName != "USDUSD" -> (yourMoney * convTo).roundTo(4)
            choosing != "USDUSD" && targetConvName == "USDUSD" -> (yourMoney / toUSD).roundTo(4)
            else -> {
                val toUSDfirst = yourMoney / toUSD
                (toUSDfirst * convTo).roundTo(4)
            }
        }
        txtChoose.text = result.toString()
    }

    private fun Double.roundTo(digits: Int): Double {
        val factor = 10.0.pow(digits)
        return (this * factor).roundToInt() / factor
    }

    private fun findView() {
        spnChoose = findViewById(R.id.spnChoose)
        edWriteMoney = findViewById(R.id.edWriteMoney)
        spnConv1 = findViewById(R.id.spnConv1)
        spnConv2 = findViewById(R.id.spnConv2)
        spnConv3 = findViewById(R.id.spnConv3)
        btnResult = findViewById(R.id.btnResult)
        txtChoose1 = findViewById(R.id.txtChoose1)
        txtChoose2 = findViewById(R.id.txtChoose2)
        txtChoose3 = findViewById(R.id.txtChoose3)
        txtUpdate = findViewById(R.id.txtUpdate)
        txtCountry1 = findViewById(R.id.txtCountry1)
        txtCountry2 = findViewById(R.id.txtCountry2)
        txtCountry3 = findViewById(R.id.txtCountry3)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.choose_countrys,
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnChoose.adapter = adapter
        spnConv1.adapter = adapter
        spnConv2.adapter = adapter
        spnConv3.adapter = adapter

        spnChoose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val result = parent?.getItemAtPosition(position).toString()
                showChoose = result
                choose1 = itemSelectedView(showChoose)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showChoose = "TWD(台幣)"
                choose1 = itemSelectedView(showChoose)
            }
        }

        spnConv1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val result1 = parent?.getItemAtPosition(position).toString()
                showConv1 = result1
                conv1 = itemSelectedView(showConv1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showConv1 = "TWD(台幣)"
                conv1 = itemSelectedView(showConv1)
            }
        }

        spnConv2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val result2 = parent?.getItemAtPosition(position).toString()
                showConv2 = result2
                conv2 = itemSelectedView(showConv2)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showConv2 = "TWD(台幣)"
                conv2 = itemSelectedView(showConv2)
            }
        }

        spnConv3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val result3 = parent?.getItemAtPosition(position).toString()
                showConv3 = result3
                conv3 = itemSelectedView(showConv3)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showConv3 = "TWD(台幣)"
                conv3 = itemSelectedView(showConv3)
            }
        }

        btnResult.setOnClickListener {
            if (edWriteMoney.text.toString().isNotEmpty()) {
                if (edWriteMoney.text.toString() != "0")
                    runOnUiThread {
                        catchData()
                    }
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("請輸入數字")
                builder.setMessage("請先輸入數字")
                builder.setNegativeButton("確認") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                builder.create().show()
            }
        }
    }

    private fun itemSelectedView(choosingItem: String): String {
        return if (choosingItem == "USD(美金)") {
            "USDUSD"
        } else {
            "USD${choosingItem.substring(0, 3)}"
        }
    }
}