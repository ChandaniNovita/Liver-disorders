package com.example.chandaniliver

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "liver.tflite"

    private lateinit var resultText: TextView
    private lateinit var Age: EditText
    private lateinit var Gender: EditText
    private lateinit var TB: EditText
    private lateinit var DB: EditText
    private lateinit var Alkphos: EditText
    private lateinit var Sgpt: EditText
    private lateinit var Sgot: EditText
    private lateinit var TP: EditText
    private lateinit var ALB: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        Age = findViewById(R.id.Age)
        Gender = findViewById(R.id.Gender)
        TB = findViewById(R.id.TB)
        DB = findViewById(R.id.DB)
        Alkphos = findViewById(R.id.Alkphos)
        Sgpt = findViewById(R.id.Sgpt)
        Sgot = findViewById(R.id.Sgot)
        TP = findViewById(R.id.TP)
        ALB = findViewById(R.id.ALB)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Age.text.toString(),
                Gender.text.toString(),
                TB.text.toString(),
                DB.text.toString(),
                Alkphos.text.toString(),
                Sgpt.text.toString(),
                Sgot.text.toString(),
                TP.text.toString(),
                ALB.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Negatif"
                }else if (result == 1){
                    resultText.text = "Positif"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(10)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String, input9: String): Int{
        val inputVal = FloatArray(10)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        inputVal[8] = input9.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}