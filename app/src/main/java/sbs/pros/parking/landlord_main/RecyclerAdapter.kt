package sbs.pros.parking.landlord_main

import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sbs.pros.parking.R

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val inParkers: ArrayList<Parker> = ArrayList(0)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tile_in, parent, false)
        val parker: Parker = Parker("A 123 BC 45", "12:30", 0)
        inParkers.add(parker)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return inParkers.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int){
        holder.carNummber.text = inParkers[position].carNumber
        holder.startTime.text = inParkers[position].startTime
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var carNummber: TextView
        var startTime: TextView

        init {
            carNummber = itemView.findViewById(R.id.car_number)
            startTime = itemView.findViewById(R.id.time)
        }
    }
}
