package AddMedFragments;

public class AlarmDates {

    int year;
    int month;
    int day;
    int hour;
    int minute;
    String medId;
    String medFrequency;

    public AlarmDates(int year, int month, int day, int hour, int minute, String medId, String medFrequency) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.medId = medId;
        this.medFrequency = medFrequency;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public void setMedFrequency(String medFrequency) {
        this.medFrequency = medFrequency;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getMedId() {
        return medId;
    }

    public String getMedFrequency() {
        return medFrequency;
    }
}
