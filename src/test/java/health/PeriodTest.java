package health;

import constants.UiConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import utility.CustomExceptions;
import constants.HealthConstant;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

class PeriodTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void cleanup() {
        System.setOut(originalOut);
        HealthList.clearHealthLists();
        outContent.reset();
    }

    /**
     * Tests the behaviour of toString in Period class.
     */
    @Test
    void calculatePeriodLength_printsCorrectPeriod() {
        Period period = new Period("09-03-2022", "16-03-2022");
        String expected = "Period Start: "
                + period.getStartDate()
                + " Period End: "
                + period.getEndDate()
                + System.lineSeparator()
                + "Period Length: "
                + period.getPeriodLength()
                + " days"
                + System.lineSeparator();

        System.out.println(period);
        assertEquals(expected, outContent.toString());
    }

    /**
     * Tests the behaviour of the showLatestPeriod method and whether it prints
     * the last Period object added.
     */
    @Test
    void showLatestPeriod_twoPeriodInputs_printCorrectPeriod() throws CustomExceptions.OutOfBounds {
        Period firstPeriod = new Period("09-02-2023", "16-02-2023");
        Period secondPeriod = new Period("09-03-2023", "16-03-2023");


        String expected = "Period Start: "
                + secondPeriod.getStartDate()
                + " Period End: "
                + secondPeriod.getEndDate()
                + System.lineSeparator()
                + "Period Length: "
                + secondPeriod.getPeriodLength()
                + " days"
                + System.lineSeparator();

        HealthList.showLatestPeriod();
        assertEquals(expected, outContent.toString());
    }

    /**
     * Tests the behaviour of the showPeriodHistory method and whether it prints
     * the period history correctly.
     */
    @Test
    void showPeriodHistory_twoInputs_printCorrectPeriodHistory() throws CustomExceptions.OutOfBounds {
        Period firstPeriod = new Period("10-04-2023", "16-04-2023");
        Period secondPeriod = new Period("09-05-2023", "16-05-2023");

        String expected = "Your Period history:"
                + System.lineSeparator()
                + "1. Period Start: "
                + secondPeriod.getStartDate()
                + " Period End: "
                + secondPeriod.getEndDate()
                + System.lineSeparator()
                + "Period Length: "
                + secondPeriod.getPeriodLength()
                + " days"
                + System.lineSeparator()
                + "2. Period Start: "
                + firstPeriod.getStartDate()
                + " Period End: "
                + firstPeriod.getEndDate()
                + System.lineSeparator()
                + "Period Length: "
                + firstPeriod.getPeriodLength()
                + " days"
                + System.lineSeparator()
                + "Cycle Length: "
                + firstPeriod.cycleLength
                + " days"
                + System.lineSeparator();

        HealthList.showPeriodHistory();
        assertEquals(expected, outContent.toString());
    }

    /**
     * Test deleting of periods with valid list and valid index.
     * Expected behaviour is to have one periods entry left in the list.
     *
     * @throws CustomExceptions.OutOfBounds If the index is invalid.
     */
    @Test
    void deletePeriod_properList_listOfSizeOne() throws CustomExceptions.OutOfBounds {
        new Period("10-04-2024", "16-04-2024");
        new Period("09-05-2024", "16-05-2024");

        int index = 1;
        HealthList.deletePeriod(index);
        assertEquals(1, HealthList.getPeriodsSize());
    }

    /**
     * Test deleting of period with empty list.
     * Expected behaviour is for an AssertionError to be thrown.
     */
    @Test
    void deletePeriod_emptyList_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                HealthList.deletePeriod(0));
    }

    /**
     * Test deleting of period with invalid index.
     * Expected behaviour is for an OutOfBounds error to be thrown.
     */
    @Test
    void deletePeriod_properListInvalidIndex_throwOutOfBoundsForBmi() {
        Period firstPeriod = new Period("10-04-2024", "16-04-2024");

        int invalidIndex = 5;
        assertThrows(CustomExceptions.OutOfBounds.class, () ->
                HealthList.deletePeriod(invalidIndex));
    }
    /**
     * Tests the behaviour of the predictNextPeriodStartDate function and whether it prints
     * correct predicted start date.
     */
    @Test
    void predictNextPeriodStartDate_sufficientInputs_printCorrectPredictedDate() throws CustomExceptions.OutOfBounds{
        HealthList healthList = new HealthList();
        Period firstPeriod = new Period("09-12-2023", "16-12-2023");
        Period secondPeriod = new Period("09-01-2024", "16-01-2024");
        Period thirdPeriod = new Period("10-02-2024", "16-02-2024");
        Period fourthPeriod = new Period("09-03-2024", "14-03-2024");

        long expectedCycleLength = (31+ 32 + 28) / HealthConstant.LATEST_THREE_CYCLE_LENGTHS;
        LocalDate expected = fourthPeriod.getStartDate().plusDays(expectedCycleLength);
        LocalDate result = HealthList.predictNextPeriodStartDate();
        assertEquals(expected, result);
    }

    /**
     * Tests the behaviour of the printNextCyclePrediction function and whether it prints
     * the predicted date with period is late message.
     */
    @Test
    void printNextCyclePrediction_afterToday_printPeriodIsLate() {
        LocalDate today = LocalDate.now();
        LocalDate predictedDate = today.minusDays(5);

        String expected = HealthConstant.PREDICTED_START_DATE_MESSAGE
                + predictedDate
                + HealthConstant.PERIOD_IS_LATE
                + "5 "
                + HealthConstant.DAYS_MESSAGE
                + UiConstant.FULL_STOP
                + System.lineSeparator()
                + UiConstant.PARTITION_LINE
                + System.lineSeparator();

        Period.printNextCyclePrediction(predictedDate);
        assertEquals(expected, outContent.toString());
    }

    /**
     * Tests the behaviour of the printNextCyclePrediction function and whether it prints
     * the predicted date and the number of days to predicted date.
     */
    @Test
    void printNextCyclePrediction_beforeToday_printNumberOfDaysToPredictedDate() {
        LocalDate today = LocalDate.now();
        LocalDate predictedDate = today.plusDays(10);

        String expected = HealthConstant.PREDICTED_START_DATE_MESSAGE
                + predictedDate
                + ", in 10 "
                + HealthConstant.DAYS_MESSAGE
                + UiConstant.FULL_STOP
                + System.lineSeparator()
                + UiConstant.PARTITION_LINE
                + System.lineSeparator();

        Period.printNextCyclePrediction(predictedDate);
        assertEquals(expected, outContent.toString());
    }

    /**
     * Tests the behaviour of the printLatestThreeCycles method and whether it prints
     * the latest three period objects only.
     */
    @Test
    void printLatestThreeCycles_fourInputs_printsThreePeriodObjectsOnly() {
        HealthList healthList = new HealthList();
        Period firstPeriod = new Period("09-01-2024", "16-01-2024");
        Period secondPeriod = new Period("10-02-2024", "16-02-2024");
        Period thirdPeriod = new Period("09-03-2024", "14-03-2024");
        Period fourthPeriod = new Period("09-04-2024", "16-04-2024");

        String expected = UiConstant.PARTITION_LINE
                + System.lineSeparator()
                + "Period Start: "
                + fourthPeriod.getStartDate()
                + " Period End: "
                + fourthPeriod.getEndDate()
                + System.lineSeparator()
                + "Period Length: "
                + fourthPeriod.getPeriodLength()
                + " days"
                + System.lineSeparator()
                + "Period Start: "
                + thirdPeriod.getStartDate()
                + " Period End: "
                + thirdPeriod.getEndDate()
                + System.lineSeparator()
                + "Period Length: "
                + thirdPeriod.getPeriodLength()
                + " days"
                + System.lineSeparator()
                + "Cycle Length: "
                + thirdPeriod.cycleLength
                + " days"
                + System.lineSeparator()
                + "Period Start: "
                + secondPeriod.getStartDate()
                + " Period End: "
                + secondPeriod.getEndDate()
                + System.lineSeparator()
                + "Period Length: "
                + secondPeriod.getPeriodLength()
                + " days"
                + System.lineSeparator()
                + "Cycle Length: "
                + secondPeriod.cycleLength
                + " days"
                + System.lineSeparator();

        HealthList.printLatestThreeCycles();
        assertEquals(expected, outContent.toString());
    }
}
