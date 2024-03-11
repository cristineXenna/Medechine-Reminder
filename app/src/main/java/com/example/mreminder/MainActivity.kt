package com.example.mreminder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputBinding
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mreminder.adapter.MedicineAdapter
import com.example.mreminder.listener.Imedicine
import com.example.mreminder.model.MedicineModel
import com.example.mreminder.utils.SpaceItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), Imedicine {

    lateinit var medicineListener: Imedicine
    lateinit var recycleItem: RecyclerView

    lateinit var addsBtn: FloatingActionButton
    lateinit var btn_this_month: TextView
    lateinit var btn_this_week: TextView
    lateinit var btn_today: TextView

    lateinit var btn_open_menu: ImageButton
    lateinit var btn_close_menu: ImageButton
    lateinit var menu: RelativeLayout
    lateinit var btn_home: CardView
    lateinit var btn_profile: CardView
    lateinit var btn_history: CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycleItem=findViewById(R.id.recycler_item)
        init()
        loadItemFromFirebase()

        btn_this_month = findViewById(R.id.btn_this_month)
        btn_this_week = findViewById(R.id.btn_this_week)
        btn_today = findViewById(R.id.btn_today)

        btn_open_menu = findViewById(R.id.btn_open_menu)
        btn_close_menu = findViewById(R.id.btn_menu_close)
        menu = findViewById(R.id.menu)
        menu.visibility = View.GONE
        btn_today.setTextColor(Color.GRAY)

        btn_this_month.setOnClickListener{
            btn_this_month.setTextColor(Color.GRAY)
            btn_this_week.setTextColor(Color.WHITE)
            btn_today.setTextColor(Color.WHITE)
        }

        btn_this_week.setOnClickListener {
            btn_this_month.setTextColor(Color.WHITE)
            btn_this_week.setTextColor(Color.GRAY)
            btn_today.setTextColor(Color.WHITE)
        }

        btn_today.setOnClickListener {
            btn_this_month.setTextColor(Color.WHITE)
            btn_this_week.setTextColor(Color.WHITE)
            btn_today.setTextColor(Color.GRAY)
        }

        btn_open_menu.setOnClickListener{
            menu.visibility = View.VISIBLE
        }

        btn_close_menu.setOnClickListener {
            menu.visibility = View.GONE
        }

        addsBtn = findViewById(R.id.addingBtn)
        addsBtn.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadItemFromFirebase() {
        val medicineModels:MutableList<MedicineModel> = ArrayList()
        val UID = FirebaseAuth.getInstance().uid.toString()
        FirebaseDatabase.getInstance()
            .getReference(UID)
            .child("medicines")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (medicineSnapshot in snapshot.children){
                            val medicineModel=medicineSnapshot.getValue(MedicineModel::class.java)
                            medicineModel!!.key=medicineSnapshot.key
                            medicineModels.add(medicineModel)
                        }
                        medicineListener.onLoadSucces(medicineModels)
                    }
                    else medicineListener.onLoadFail("Item not found")
                }

                override fun onCancelled(error: DatabaseError) {
                    medicineListener.onLoadFail(error.message)
                }

            })
    }

    private fun init() {
        medicineListener=this
        val gridLayoutManager=GridLayoutManager(this, 2)
        recycleItem.layoutManager=gridLayoutManager
        recycleItem.addItemDecoration(SpaceItemDecoration())
    }

    override fun onLoadSucces(medecineModelList: List<MedicineModel>?) {
        val adapter = MedicineAdapter(this, medecineModelList!!, this)
        recycleItem.adapter = adapter
    }

    override fun onLoadFail(message: String?) {
//        Snackbar.make(this, message!!, Snackbar.LENGTH_LONG).show()
    }


}