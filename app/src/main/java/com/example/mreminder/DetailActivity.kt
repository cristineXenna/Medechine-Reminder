package com.example.mreminder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.mreminder.model.Detail
import com.example.mreminder.model.MedicineModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailActivity : AppCompatActivity() {

    lateinit var btn_back : ImageButton
    lateinit var image : ImageView
    lateinit var tv_name : TextView
    lateinit var tv_dose : TextView
    lateinit var tv_next_dose : TextView
    lateinit var tv_detail_dose : TextView
    lateinit var tv_program : TextView
    lateinit var tv_quanitty : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        btn_back = findViewById(R.id.back_button)
        image = findViewById(R.id.detail_image)
        tv_name = findViewById(R.id.detail_name)
        tv_dose = findViewById(R.id.detail_dose)
        tv_next_dose = findViewById(R.id.detail_next_dose)
        tv_detail_dose = findViewById(R.id.detail_dose_time)
        tv_program = findViewById(R.id.detail_program)
        tv_quanitty = findViewById(R.id.detail_quantity)

        btn_back.setOnClickListener {
            var inten = Intent(this, MainActivity::class.java)
            startActivity(inten)
        }

        getDataFromDatabase()
    }

    private fun getDataFromDatabase() {
        val UID = "123"
        val child = Detail.key
        val medicineModels:MutableList<MedicineModel> = ArrayList()

        FirebaseDatabase.getInstance()
            .getReference("medicine")
            .child(UID)
            .child(child)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (data in snapshot.children){
                            var datas = snapshot.getValue(MedicineModel::class.java)
                            datas!!.key = data.key
                            medicineModels.add(datas)
                        }
                        Glide.with(this@DetailActivity)
                            .load(medicineModels[0].image)
                            .into(image)

                        tv_name.text = medicineModels[0].name.toString()
                        tv_dose.text = medicineModels[0].dose.toString()
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}