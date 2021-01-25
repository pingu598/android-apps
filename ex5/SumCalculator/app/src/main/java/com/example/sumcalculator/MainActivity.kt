package com.example.sumcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    var sumin = 0
    var pressedPlus = false
    var click1string = ""
    var click2string = ""
    fun numberInput(view: View) {
        // view is a button (pressed one) get text and convert to Int
        var digit = (view as Button).text.toString().toInt()
        // append a new string to textView
        sum.append(digit.toString())

        // clicked number, like 55
        if(!pressedPlus) {
            click1string += digit.toString()
        } else {
            click2string += digit.toString()
        }

    }
    fun sumInput(view: View){
        pressedPlus = true
        sum.append("+")
    }
    fun equals(view: View){
        sumin = click1string.toInt() + click2string.toInt()
        sum.append("="+sumin)

    }
    fun delete(view: View){
        pressedPlus = false
        click1string = ""
        click2string = ""
        sum.text = ""
    }
}
