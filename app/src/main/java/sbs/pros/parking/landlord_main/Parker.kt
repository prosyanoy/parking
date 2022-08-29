package sbs.pros.parking.landlord_main

class Parker(var carNumber: String, var startTime: String, var rating: Float) {

    private val startHour:Int = startTime.substring(0,2).toInt()
    private val startMinute:Int = startTime.substring(3,5).toInt()

    // assume the price is 100₽/hour
    private val price:Int = 100




    fun getPayment(finishTime: String?): Int {
        val finishHour: Int = finishTime!!.substring(0,2).toInt()
        val finishMinute: Int = finishTime!!.substring(3,5).toInt()

        val totalTime = (finishHour - startHour) * 60 + (finishMinute - startMinute)

        return (price * totalTime.div(60))
    }
}