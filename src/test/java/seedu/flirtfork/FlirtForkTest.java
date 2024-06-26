package seedu.flirtfork;

import org.junit.jupiter.api.Test;
import seedu.flirtfork.commands.GenerateIdeaCommand;
import seedu.flirtfork.commands.GenerateItineraryCommand;
import seedu.flirtfork.commands.GenerateSmartItineraryCommand;
import seedu.flirtfork.commands.HelpCommand;
import seedu.flirtfork.exceptions.FlirtForkException;
import java.util.NoSuchElementException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlirtForkTest {
    private static final String FILE_PATH = "./data/FlirtFork.txt";
    private Ui ui = new Ui();
    private Storage storage = new Storage(FILE_PATH);
    private FavouritesList favourites;
    private FoodList foods;
    private ActivityList activities;
    private UserDetails userDetails;
    private GiftList gifts;

    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void testGenerateIdeaCommand() {
        try {
            favourites = storage.loadFavourites();
            foods = new FoodList(storage.loadFood());
            activities = new ActivityList(storage.loadActivity());
            userDetails = storage.loadUserDetails();
        } catch (FileNotFoundException e) {
            ui.errorMessage("File not found. Starting with an empty task list :)");
            favourites = new FavouritesList(new ArrayList<>());
        }
        GenerateIdeaCommand generateIdeaCommand = new GenerateIdeaCommand();

        // Backup original system
        InputStream sysInBackup = System.in;
        PrintStream sysOutBackup = System.out;

        // Provide the simulated input
        String inputData = "Yes";
        ByteArrayInputStream in = new ByteArrayInputStream(inputData.getBytes());

        // Capture System.out output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        try {
            generateIdeaCommand.execute(favourites, foods, activities, ui, storage, userDetails, gifts);
        } catch (NoSuchElementException e) {
            System.setIn(in);
            String output = outputStream.toString();
            assertTrue(output.contains("You can do"));
            assertTrue(output.contains("and have a nice meal at"));
            assertTrue(output.length() > 36);
        } finally {
            System.setIn(sysInBackup);
            System.setOut(sysOutBackup);
        }
    }

    @Test
    public void generateItineraryCommand_validInputs_success() {
        try {
            favourites = storage.loadFavourites();
            foods = new FoodList(storage.loadFood());
            activities = new ActivityList(storage.loadActivity());
        } catch (FileNotFoundException e) {
            ui.errorMessage("File not found. Starting with an empty task list :)");
            favourites = new FavouritesList(new ArrayList<>());
        }
        GenerateItineraryCommand generateItineraryCommand = new GenerateItineraryCommand("C C");

        // Backup original system
        InputStream sysInBackup = System.in;
        PrintStream sysOutBackup = System.out;

        // Provide the simulated input
        String inputData = "Yes";
        ByteArrayInputStream in = new ByteArrayInputStream(inputData.getBytes());

        // Capture System.out output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        try {
            generateItineraryCommand.execute(favourites, foods, activities, ui, storage, userDetails, gifts);
        } catch (NoSuchElementException e) {
            System.setIn(in);
            String output = outputStream.toString();
            assertTrue(output.contains("Here is a rough itinerary for your date:"));
        } catch (FlirtForkException e) {
            ui.errorMessage(e.getMessage());
        } finally {
            System.setIn(sysInBackup);
            System.setOut(sysOutBackup);
        }
    }

    @Test
    public void generateItineraryCommand_invalidInputs_errorMessagePrinted() {
        try {
            favourites = storage.loadFavourites();
            foods = new FoodList(storage.loadFood());
            activities = new ActivityList(storage.loadActivity());
        } catch (FileNotFoundException e) {
            ui.errorMessage("File not found. Starting with an empty task list :)");
            favourites = new FavouritesList(new ArrayList<>());
        }
        GenerateItineraryCommand generateItineraryCommand = new GenerateItineraryCommand("THW GDBE");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            System.setOut(printStream);
            generateItineraryCommand.execute(favourites, foods, activities, ui, storage, userDetails, gifts);
            String output = outputStream.toString();
            assertTrue(output.contains("Follow this format: 'itinerary LOCATION, PRICE'"));
            System.setOut(System.out);
        } catch (FlirtForkException e) {
            ui.errorMessage(e.getMessage());
        }
    }

    @Test
    public void helpCommand_uiIsNull_throwsFlirtForkException() {
        // Arrange
        HelpCommand helpCommand = new HelpCommand();
        Ui ui = null; // Simulating the UI being null to trigger the error condition.

        // Act and Assert
        FlirtForkException thrown = assertThrows(FlirtForkException.class, () -> {
            helpCommand.execute(null, null, null, ui, null, null, null);
        }, "UI component must not be null.");

        assertTrue(thrown.getMessage().contains("UI component must not be null."));
    }

    @Test
    void userDetailsSettingInvalidAgeThrowsException() {
        UserDetails userDetails = new UserDetails();
        Exception exceptionNegative = assertThrows(IllegalArgumentException.class, () -> {
            userDetails.setAge("-1");
        });
        assertEquals("Age cannot be negative.", exceptionNegative.getMessage());

        Exception exceptionNotInteger = assertThrows(IllegalArgumentException.class, () -> {
            userDetails.setAge("twenty");
        });
        assertEquals("Age must be a valid integer.", exceptionNotInteger.getMessage());
    }

    @Test
    void userDetailsSettingValidAgeUpdatesValue() {
        UserDetails userDetails = new UserDetails();
        String validAge = "25";
        userDetails.setAge(validAge);
        assertEquals(validAge, userDetails.getAge());
    }

    @Test
    void userDetailsSettingInvalidGenderThrowsException() {
        UserDetails userDetails = new UserDetails();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userDetails.setGender("Alien");
        });
        assertEquals("Gender must be 'Male', 'Female', or 'Other'.", exception.getMessage());
    }

    @Test
    void userDetailsSettingValidGenderUpdatesValue() {
        UserDetails userDetails = new UserDetails();
        String validGender = "Male";
        userDetails.setGender(validGender);
        assertEquals(validGender, userDetails.getGender());

        validGender = "Female";
        userDetails.setGender(validGender);
        assertEquals(validGender, userDetails.getGender());

        validGender = "Other";
        userDetails.setGender(validGender);
        assertEquals(validGender, userDetails.getGender());
    }

    @Test
    public void generateSmartItineraryCommand_nullUserDetails_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GenerateSmartItineraryCommand(null);
        });
        assertTrue(exception.getMessage().contains("User details are required"));
    }
    


}
