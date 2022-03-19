package com.vinayakstudios.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vinayakstudios.roomdemo.databinding.ActivityMainBinding
import com.vinayakstudios.roomdemo.databinding.UpdateRecordDialogLayoutBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

   private var binding : ActivityMainBinding? = null
   private var employeeDao : EmployeeDAO? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding?.root)
      employeeDao = (application as EmployeeApp).db?.employeeDao()

      btnAddRecordOnClick()

      lifecycleScope.launch{
         employeeDao?.fetchALLEmployees()?.collect(){
            val list = ArrayList(it)
            setUpDataInRecyclerView(list,employeeDao!!)
         }
      }
   }

   private fun btnAddRecordOnClick(){
      binding?.btnAddRecord?.setOnClickListener{
         addRecord(employeeDao!!)
      }
   }

   fun addRecord(employeeDao : EmployeeDAO){
      val name = binding?.etName?.text.toString()
      val emailId = binding?.etEmailId?.text.toString()

      if(name.isNotEmpty() && emailId.isNotEmpty()){
         lifecycleScope.launch{
            employeeDao.insert(EmployeeEntity(name = name, email = emailId))
            Toast.makeText(applicationContext,"Record Saved",Toast.LENGTH_LONG).show()

            binding?.etName?.text?.clear()
            binding?.etEmailId?.text?.clear()
         }
      }
      else{
         Toast.makeText(this,
         "Name or Email Id Cannot be blank",
         Toast.LENGTH_LONG).show()
      }
   }

   private fun setUpDataInRecyclerView(employeeList : ArrayList<EmployeeEntity>,
   employeeDao : EmployeeDAO){
      if(employeeList.isNotEmpty()){
         val itemAdapter = ItemAdapter(employeeList,
            {
               updateId ->
               updateRecordDialog(updateId,employeeDao)
            },
            {
               deleteId ->
               deleteRecordDialog(deleteId,employeeDao)
            })
         binding?.rvRecordsView?.layoutManager = LinearLayoutManager(this)
         binding?.rvRecordsView?.adapter = itemAdapter
         binding?.rvRecordsView?.visibility = View.VISIBLE
         binding?.tvNoRecordsAvailable?.visibility = View.GONE
      }
      else{
         binding?.rvRecordsView?.visibility = View.GONE
         binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
      }
   }

   private fun updateRecordDialog(id : Int, employeeDao : EmployeeDAO){
      val updateDialog = Dialog(this, R.style.Theme_Dialog)
      updateDialog.setCancelable(false)
      val binding = UpdateRecordDialogLayoutBinding.inflate(layoutInflater)
      updateDialog.setContentView(binding.root)

      lifecycleScope.launch{
         employeeDao.fetchEmployeeById(id).collect {
            if(it!=null){
               binding.etUpdatedName.setText(it.name)
               binding.etUpdatedEmailId.setText(it.email)
            }
         }
      }

      updateDialog.show()

      binding.btnUpdate.setOnClickListener {
         var name = ""
         var email = ""

         name = binding.etUpdatedName.text.toString()
         email = binding.etUpdatedEmailId.text.toString()


         if(name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch{
               employeeDao.update(EmployeeEntity(id,name,email))
               Toast.makeText(this@MainActivity,
                  "Record Updated",
                  Toast.LENGTH_LONG).show()
               updateDialog.dismiss()
            }
         }
         else{
            Toast.makeText(this,
               "Name or Email Id cannot be Empty",
               Toast.LENGTH_LONG).show()
         }
      }

      binding.btnCancel.setOnClickListener {
         updateDialog.dismiss()
      }
   }

   private fun deleteRecordDialog(id : Int,employeeDao: EmployeeDAO){
      var name = ""
      var emailId = ""
      lifecycleScope.launch {
         employeeDao.fetchEmployeeById(id).collect {
            if(it != null){
               name = it.name
               emailId = it.name
            }
         }
      }
      val builder = AlertDialog.Builder(this)
      builder.setTitle("Delete Record")
      builder.setPositiveButton("Yes"){dialogInterface,_ ->
         lifecycleScope.launch{
            employeeDao.delete(EmployeeEntity(id,name,emailId))
            Toast.makeText(this@MainActivity,
               "Record Deleted",
               Toast.LENGTH_LONG).show()
            dialogInterface.dismiss()
         }
      }
      builder.setNegativeButton("No"){dialogInterface,_ ->
         dialogInterface.dismiss()
      }

      val alertDialog : AlertDialog = builder.create()
      alertDialog.setCancelable(false)
      alertDialog.show()
   }
}