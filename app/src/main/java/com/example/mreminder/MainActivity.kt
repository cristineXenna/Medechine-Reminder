package com.example.mreminder

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), Imedicine {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var addsBtn:FloatingActionButton
    private lateinit var recv: RecyclerView

    lateinit var medicineListener: Imedicine
    lateinit var recycleItem: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycleItem=findViewById(R.id.recycler_item)
        init()
        loadItemFromFirebase()

        drawerLayout=findViewById(R.id.drawer_layout)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView=findViewById(R.id.nav_view)

        toggle=ActionBarDrawerToggle(
            this,
            findViewById(R.id.toolbar),
            "Open navigation drawer",
            "Close navigation drawer"
        )
        drawerLayout.addDrawerListener(toggle)

        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnThisMonth.setOnClickListener{
            binding.btnThisMonth.setTextColor(Color.GRAY)
            binding.btnThisWeek.setTextColor(Color.WHITE)
            binding.month.visibility = View.VISIBLE
            binding.days.visibility = View.GONE
        }
        binding.btnThisWeek.setOnClickListener {
            binding.btnThisMonth.setTextColor(Color.WHITE)
            binding.btnThisWeek.setTextColor(Color.GRAY)
            binding.days.visibility = View.VISIBLE
            binding.month.visibility = View.GONE
        }
        week_process()
        addsBtn = binding.addingBtn
        recv = binding.mRecycler
        addsBtn.setOnClickListener { addInfo() }
        val navigationView=findViewById<NavigationView>(R.id.nav_view)
    }


    private fun week_process() {
        var mon = findViewById<TextView>(R.id.btn_mon)
        var tue = findViewById<TextView>(R.id.btn_tue)
        var wed = findViewById<TextView>(R.id.btn_wed)
        var thu = findViewById<TextView>(R.id.btn_thu)
        var fri = findViewById<TextView>(R.id.btn_fri)
        var sat = findViewById<TextView>(R.id.btn_sat)
        var sun = findViewById<TextView>(R.id.btn_sun)
    }

    private fun loadItemFromFirebase() {
        val medicineModels:MutableList<MedicineModel> = ArrayList()
        val userID = "123"
        FirebaseDatabase.getInstance()
            .getReference("medicine")
            .child(userID)
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
        Snackbar.make(findViewById(R.id.mainLayout), message!!, Snackbar.LENGTH_LONG).show()
    }


}