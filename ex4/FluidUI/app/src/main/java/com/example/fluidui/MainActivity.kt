package com.example.fluidui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showEmployeeData(0)
    }
    val firstnames = arrayOf("Renato", "Rosangela", "Tim", "Bartol", "Jeannette")
    val lastnames = arrayOf("Ksenia", "Metzli", "Asuncion", "Zemfina", "Giang")
    val jobtitles = arrayOf("District Quality Coordinator","International Intranet Representative","District Intranet Administrator","Dynamic Research Manager","Central Infrastructure Consultant")

    // function displays employees data in UI
    fun showEmployeeData(index: Int) {
        // show data to UI
        firstname.text = firstnames[index]
        lastname.text = lastnames[index]
        jobtitle.text = jobtitles[index]
        infoText.text = lastnames[index] + " " + firstnames[index] + " is ...." + getString(R.string.basic_text)
        // load and show image from resources
        var id = 0
        if (index == 0) id = R.drawable.employee1
        else if (index == 1) id = R.drawable.employee2
        else if (index == 2) id = R.drawable.employee3
        else if (index == 3) id = R.drawable.employee4
        else if (index == 4) id = R.drawable.employee5
        imageView.setImageResource(id)
    }
    fun numberClicked(view: View?) {
        // get text show in TextView
        val text = (view as TextView).text.toString()
        // convert text to integer, array is starting from zero position
        val int = text.toInt() - 1
        // call function to show selected employees data
        showEmployeeData(int)
    }


}
