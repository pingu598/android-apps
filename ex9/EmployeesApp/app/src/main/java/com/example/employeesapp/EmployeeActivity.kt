package com.example.employeesapp

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.activity_employee.*
import kotlinx.android.synthetic.main.content_employee.*
import kotlinx.android.synthetic.main.employee_item.*
import org.json.JSONObject

class EmployeeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        // get data from intent
        val bundle: Bundle? = intent.extras;
        if (bundle != null) {
            val employeeString = bundle!!.getString("employee")
            val employee = JSONObject(employeeString)
            val name = employee["firstName"]
            // modify here to display other employee's data too!
            Toast.makeText(this, "Name is $name", Toast.LENGTH_LONG).show()
            fullName.text = "Name: " + employee["firstName"] + " " + employee["lastName"]
            email.text = "Email: " + employee["email"]
            phone.text = "Phone: " + employee["phone"]
            titleView.text = "Title: " + employee["title"]
            department.text = "Department: " + employee["department"]
            Glide.with(this).load(employee["image"]).into(imageView2)
        }
    }

}
