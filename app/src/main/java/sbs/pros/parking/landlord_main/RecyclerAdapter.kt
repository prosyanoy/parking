package sbs.pros.parking.landlord_main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sbs.pros.parking.R

class RecyclerAdapter(var inParkers: ArrayList<Parker>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val carNumbers = arrayOf("B 222 BB 22", "A 111 AA 11", "C 333 CC 33")
    private val startTimes = arrayOf("11:11", "14:15", "00:03")



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tile_in, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {

        return inParkers.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int){
        holder.carNumber.text = inParkers[position].carNumber
        holder.startTime.text = inParkers[position].startTime
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var carNumber: TextView
        var startTime: TextView

        init {
            carNumber = itemView.findViewById(R.id.car_number)
            startTime = itemView.findViewById(R.id.time)
        }
    }
}
