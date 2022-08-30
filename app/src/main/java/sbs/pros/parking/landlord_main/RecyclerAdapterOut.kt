package sbs.pros.parking.landlord_main

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sbs.pros.parking.R

class RecyclerAdapterOut(var outParkers: ArrayList<Parker>) : RecyclerView.Adapter<RecyclerAdapterOut.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterOut.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tile_out, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return outParkers.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapterOut.ViewHolder, position: Int){
        holder.carNumber.text = outParkers[position].carNumber
        holder.time.text = outParkers[position].startTime
        holder.currentPay.text = outParkers[position].getPayment("20:10").toString() + "₽"
        holder.rating.rating = outParkers[position].rating

        holder.rating.setOnRatingBarChangeListener { ratingBar, rate, b ->
            outParkers[position].rating = rate
            Log.d(TAG, "Rating changed to $rate for parker $position")
        }
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var carNumber: TextView
        var time: TextView
        var currentPay: TextView
        var rating: RatingBar

        init {
            carNumber = itemView.findViewById(R.id.car_number)
            time = itemView.findViewById(R.id.time)
            currentPay = itemView.findViewById(R.id.current_pay)
            rating = itemView.findViewById(R.id.rating)
        }
    }
}
