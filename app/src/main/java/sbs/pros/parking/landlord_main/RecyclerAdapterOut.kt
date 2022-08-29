package sbs.pros.parking.landlord_main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sbs.pros.parking.R

class RecyclerAdapterOut(var parkedParkers: ArrayList<Parker>) : RecyclerView.Adapter<RecyclerAdapterOut.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterOut.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tile_parked, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parkedParkers.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapterOut.ViewHolder, position: Int){
        holder.carNumber.text = parkedParkers[position].carNumber
        holder.time.text = parkedParkers[position].startTime
        holder.currentPay.text = parkedParkers[position].getPayment("14:10").toString()
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var carNumber: TextView
        var time: TextView
        var currentPay: TextView

        init {
            carNumber = itemView.findViewById(R.id.car_number)
            time = itemView.findViewById(R.id.time)
            currentPay = itemView.findViewById(R.id.current_pay)
        }
    }
}
