package com.example.employeesapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.employee_item.view.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.lang.RuntimeException
import java.net.URL
import com.bumptech.glide.Glide


class EmployeesAdapter(private val employees: JSONArray)
    : RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeesAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.employee_item, parent, false)
        return ViewHolder(view)
    }
    // View Holder class to hold UI views
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.nameTextView
        val emailTextView: TextView = view.emailTextView
        val phoneTextView: TextView = view.phoneTextView
        val titleTextView: TextView = view.titleTextView
        val departmentTextView: TextView = view.departmentTextView
        val imageView: ImageView = view.imageView
        init {

            itemView.setOnClickListener {
                Toast.makeText(
                    view.context,
                    "Employee name is ${nameTextView.text}, adapter position = $adapterPosition",
                    Toast.LENGTH_LONG).show()
            }
        }
        // Add a item click listener
        init {
            itemView.setOnClickListener {
                // create an explicit intent
                val intent = Intent(view.context, EmployeeActivity::class.java)
                // add selected employee JSON as a string data
                intent.putExtra("employee",employees[adapterPosition].toString())
                // start a new activity
                view.context.startActivity(intent)

            }

        }

    }
    // Bind data to UI View Holder
    override fun onBindViewHolder(
        holder: EmployeesAdapter.ViewHolder,
        position: Int)
    {

        // employee to bind UI
        val employee: JSONObject = employees.getJSONObject(position)


        // employee lastname and firstname
        holder.nameTextView.text =
            employee["lastName"].toString()+ " " + employee["firstName"].toString()
        holder.emailTextView.text =
            "Email: " + employee["email"].toString()
        holder.phoneTextView.text =
            "Phone: " + employee["phone"].toString()
        holder.titleTextView.text =
            "Title: " + employee["title"].toString()
        holder.departmentTextView.text =
            "Department: " + employee["department"].toString()
        Glide.with(holder.imageView.context).load(employee["image"]).into(holder.imageView)



    }
    // Return item count in employees
    override fun getItemCount(): Int = employees.length()

}