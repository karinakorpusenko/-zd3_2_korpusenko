package com.example.mytv2

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class Registr : AppCompatActivity() {

    lateinit var login: EditText
    lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registr)

        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
    }

    fun onQui(view: View) {
        if (login.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
            if(login.text.toString() == "ects" && password.text.toString() == "ects2024") {
                val intent = Intent(this, QuestsActivity::class.java)
                startActivity(intent)
            }
            else {
                val alert = AlertDialog.Builder(this)
                    .setTitle("Ошибка")
                    .setMessage("Неверный логин или пароль!")
                    .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
                    .create()
                alert.show()
            }
        } else {
            val alert = AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage("Заполните оба поля!")
                .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
                .create()
            alert.show()
        }
    }
}