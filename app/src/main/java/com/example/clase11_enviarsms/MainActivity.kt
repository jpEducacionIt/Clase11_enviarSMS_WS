package com.example.clase11_enviarsms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import org.jetbrains.annotations.Contract

class MainActivity : AppCompatActivity() {

    private lateinit var editTextMsj: EditText
    private lateinit var textContacto: TextView
    private lateinit var textNumero: TextView
    private lateinit var btnenv: Button
    private lateinit var btnBuscar: Button
    private lateinit var btnWS: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermisos()

        editTextMsj = findViewById(R.id.editTextMensaje)
        textContacto = findViewById(R.id.textViewContacto)
        textNumero = findViewById(R.id.textViewNumero)

        btnenv = findViewById(R.id.btnEnviar)
        btnBuscar= findViewById(R.id.btnBuscar)
        btnWS = findViewById(R.id.buttonWS)

        btnenv.setOnClickListener {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                editTextMsj.text.toString(),
                null,
                textNumero.text.toString(),
                null,
                null)

            Toast.makeText(this, "mensaje enviado", Toast.LENGTH_SHORT).show()
        }

        btnBuscar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, 1)
        }

        btnWS.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, editTextMsj.text.toString())
            intent.setPackage("com.whastsapp")
            intent.type = "text/plain"
            startActivity(intent)
        }
    }

    private fun checkPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 123)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null ) {
            val contacto: Uri? = data.data
            val cursor: Cursor? = contacto?.let { contentResolver.query(it,null,null,null,null) }

            if (cursor != null && cursor.moveToFirst()) {
                val indiceName: Int = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val indiceNumero: Int = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val nombre: String = cursor.getString(indiceName)
                var numero: String = cursor.getString(indiceNumero)

                numero = numero.replace("(","")
                    .replace(")", "")
                    .replace(" ", "")
                    .replace("-","")
                textContacto.text = nombre
                textNumero.text = numero
            }
        }
    }
}