package ru.anura.workwithpermissions

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //проверка, выдано ли разрешение
        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        //если разрешение выдано, то запрашиваем контакты, если нет, то запрашиваем разрешение
        if (permissionGranted) {
            requestContacts()
        } else {
            requestPermission()
        }
    }

    //запрашиваем разрешение
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            //массив разрешений
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            //код разрешения
            READ_CONTACTS_RC
        )
    }

    //вызывается при получении разрешения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //проверка кода разрешения
        if (requestCode == READ_CONTACTS_RC && grantResults.isNotEmpty()) {
            //проверка, выдано ли разрешение
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                requestContacts()
            } else {
                Log.d("MainActivity", "Permission Denied")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestContacts() {
        thread {
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                )
                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                )
                val contact = Contact(id, name)
                Log.d("MainActivity", contact.toString())
            }
            cursor?.close()
        }
    }

    companion object {

        private const val READ_CONTACTS_RC = 100
    }
}