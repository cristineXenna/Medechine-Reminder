package com.example.mreminder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mreminder.adapter.MedicineAdapter
import com.example.mreminder.listener.Imedicine
import com.example.mreminder.model.MedicineModel
import com.example.mreminder.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), Imedicine {

    lateinit var medicineListener: Imedicine
    lateinit var recycleItem: RecyclerView
    lateinit var detail: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycleItem=findViewById(R.id.recycler_item)
        init()
        loadItemFromFirebase()

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