package com.vinayakstudios.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vinayakstudios.roomdemo.databinding.RecycleViewRowsLayoutBinding
import java.util.function.BinaryOperator

class ItemAdapter(private val items : ArrayList<EmployeeEntity>,
private val updateListener:(id:Int) -> Unit,
private val deleteListener:(id:Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

   class ViewHolder(binding : RecycleViewRowsLayoutBinding) : RecyclerView.ViewHolder(binding.root){
      val llMain = binding.llMain
      val tvName = binding.tvName
      val tvEmailId = binding.tvEmailId
      val ivEditBtn = binding.ivEditBtn
      val ivDeleteBtn = binding.ivDeleteButton
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(RecycleViewRowsLayoutBinding.inflate(LayoutInflater.from(parent.context),
         parent,false))
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val context = holder.itemView.context
      val item = items[position]

      holder.tvName.text = item.name
      holder.tvEmailId.text = item.email

      if(position % 2 == 0){
         holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.LightGray))
      }
      else{
         holder.llMain.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
      }

      holder.ivEditBtn.setOnClickListener{
         updateListener.invoke(item.id)
      }

      holder.ivDeleteBtn.setOnClickListener {
         deleteListener.invoke(item.id)
      }
   }

   override fun getItemCount(): Int {
      return items.size
   }


}