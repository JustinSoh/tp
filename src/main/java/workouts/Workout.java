package workouts;
import java.time.LocalDate;

import constants.ErrorConstant;
import utility.Parser;

/**
 * Workout class is a parent class that is used in Gym and Run classes.
 * It contains the date of the workout and a parser object to parse the date.
 */
public class Workout {
    //@@author JustinSoh
    private LocalDate date = null;


    /**
     * Overloaded constructor that uses the optional date parameter from user input.
     *
     * @param stringDate String representing the date of the workout.
     */
    public Workout(String stringDate) {
        Parser parser = new Parser();
        this.date = parser.parseDate(stringDate);
    }

    /**
     * Constructor that builds a new Workout object.
     */
    public Workout() {
    }


    /**
     * Returns the date of the workout. If the date is not specified (null)
     * It will return {@code ErrorConstant.NO_DATE_SPECIFIED_ERROR} as the dateString.
     *
     * @return validatedDate as a string representing the date of the workout.
     */
    public String getDate() {
        String validatedDate = "";
        if(this.date == null){
            validatedDate = ErrorConstant.NO_DATE_SPECIFIED_ERROR;
        } else {
            validatedDate = this.date.toString();
        }

        return validatedDate;
    }

    public String getDateForFile(){
        Parser parser = new Parser();
        return parser.parseFormattedDate(this.date);
    }

    /**
     * Adds the workout object into the workout list.
     *
     * @param workout The workout object to be added.
     */
    private void addIntoWorkoutList(Workout workout) {
        if (workout instanceof Run) {
            workoutLists.addRun((Run) workout);
        } else if (workout instanceof Gym) {
            workoutLists.addGym((Gym) workout);
        }
    }

    /**
     * Retrieves the string representation of a Workout object.
     *
     * @return A formatted string representing a Workout object.
     */
    @Override
    public String toString(){
        return getDate().toString();
    }
}
