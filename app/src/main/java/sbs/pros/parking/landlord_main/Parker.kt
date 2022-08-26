package sbs.pros.parking.landlord_main;

public class Parker {

    private String carNumber;
    private String startTime;
    private int rating;


    public Parker (String carNumber, String startTime, int rating){
        this.carNumber = carNumber;
        this.startTime = startTime;
        this.rating = rating;
    }

    public void setCarNumber(String carNumber) {this.carNumber = carNumber;}
    public void setStartTime(String startTime) {this.startTime = startTime;}
    public void setRating(int rating) {this.rating = rating;}
    public int getRating() {return rating;}
    public String getCarNumber() {return carNumber;}
    public String getStartTime() {return startTime;}


    public int getPrice(String finishTime){

        /*
        get lanlord's prices

        make an approprtiate time format

        return price*time

         */

        return 1;
    }


}
