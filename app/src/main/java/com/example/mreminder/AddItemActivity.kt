package com.example.mreminder

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddItemActivity : AppCompatActivity() {

    lateinit var notifBtn : ImageButton
    lateinit var cancle_Btn : Button
    lateinit var oke_btn : Button
    lateinit var radioGroup: RadioGroup
    lateinit var unit : RadioGroup
    lateinit var DateRange: EditText
    lateinit var DateRangeBtn: Button
    var IDArray = arrayOf<Int>()
    var timeArray = ArrayList<String>()
    var ids = 0
    var image = ""
    var units = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        notifBtn = findViewById(R.id.notifBtn)
        notifBtn.setOnClickListener {
            createNewButton()
        }

        cancle_Btn = findViewById(R.id.cancleBtn)
        cancle_Btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        oke_btn = findViewById(R.id.okBtn)
        oke_btn.setOnClickListener {
            createNewItem()
        }

        radioGroup = findViewById(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            radioProcess(checkedId)
        }

        DateRange = findViewById(R.id.add_date)
        DateRangeBtn = findViewById(R.id.add_date_btn)
        DateRangeBtn.setOnClickListener{
            datePickerDialog()
        }

        unit = findViewById(R.id.unit)
        unit.setOnCheckedChangeListener { group, checkedId ->
            unitProcess(checkedId)
        }
    }

    private fun unitProcess(checkedId: Int) {
        val unit = findViewById<RadioButton>(checkedId).text
        units = unit.toString()
//        Toast.makeText(this, "$unit", Toast.LENGTH_SHORT).show()
    }

    private fun radioProcess(checkedId: Int) {
        val radio = findViewById<RadioButton>(checkedId)
        if (radio == findViewById(R.id.radio1)){
            image = "https://r2.easyimg.io/e2wshqbun/img_1.png"
        }

        if (radio == findViewById(R.id.radio2)){
            image = "https://r2.easyimg.io/e2wshqbun/img_2.png"
        }

        if (radio == findViewById(R.id.radio3)){
            image = "https://r2.easyimg.io/e2wshqbun/img_3.png"
        }

        if (radio == findViewById(R.id.radio4)){
            image = "https://r2.easyimg.io/e2wshqbun/img_4.png"
        }

        if (radio == findViewById(R.id.radio5)){
            image = "https://r2.easyimg.io/e2wshqbun/img_5.png"
        }
    }

    private fun datePickerDialog() {
        val builder: MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select a date range")

        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->

            val startDate = selection.first
            val endDate = selection.second

            val sdf =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDateString: String = sdf.format(Date(startDate))
            val endDateString: String = sdf.format(Date(endDate))

            val selectedDateRange = "$startDateString - $endDateString"

            DateRange.setText(selectedDateRange)
        }
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun createNewItem() {
        val uid = FirebaseAuth.getInstance().uid
        val name = findViewById<EditText>(R.id.add_name).text.toString()
        val dose = findViewById<EditText>(R.id.add_dose).text.toString()
        val dateRange = findViewById<EditText>(R.id.add_date).text.toString()
        val totalOfMedicine = findViewById<EditText>(R.id.total_of_medicine).text.toString().toInt()

        val data = hashMapOf(
            "name" to name,
            "dose" to dose,
            "dateRange" to dateRange,
            "image" to image,
            "unit" to units,
            "totalOfMedicine" to totalOfMedicine,
            "ussedMedicine" to 0
        )

        FirebaseDatabase.getInstance().getReference(uid.toString())
            .child("medicines")
            .child(name).setValue(data)
            .addOnSuccessListener {
                for (i in 0 until IDArray.size){
                    val id = IDArray[i]
                    val Data = findViewById<Button>(IDArray[i]).text.toString()
                    FirebaseDatabase.getInstance().getReference(uid.toString())
                        .child("medicines")
                        .child(name).child("time")
                        .child(i.toString())
                        .setValue(Data)
                }

                Toast.makeText(applicationContext,"New Medicine Is Added",
                Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

    }

    private fun createNewButton() {
        val layout = findViewById(R.id.layoutNotif) as LinearLayout
        val layout2 = findViewById<LinearLayout>(R.id.layoutNotif2)
        val form = Button(this)
        val id = View.generateViewId()

        form.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        form.text = "12:00"
        form.id = id
        form.setOnClickListener{
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                form.setText("$hourOfDay:$minute")
            }, hour, minute, true)

            timePickerDialog.show()
        }
        IDArray += arrayOf(id)

        if (ids < 4){
            layout.addView(form)
        }
        else{
            layout2.addView(form)
        }

        ids += 1
//        println("ID = ${IDArray.contentToString()}")
    }
}