package seedu.flirtfork.commands;

import seedu.flirtfork.ActivityList;
import seedu.flirtfork.Command;
import seedu.flirtfork.FavouritesList;
import seedu.flirtfork.FoodList;
import seedu.flirtfork.GiftList;
import seedu.flirtfork.Storage;
import seedu.flirtfork.Ui;
import seedu.flirtfork.UserDetails;
import seedu.flirtfork.exceptions.FlirtForkException;

import java.io.IOException;

public class ExitCommand extends Command {
    @Override
    public void execute(FavouritesList favourites, FoodList foods, ActivityList activities, Ui ui,
                        Storage storage, UserDetails userDetails, GiftList gifts) throws FlirtForkException {

        try {
            storage.saveFavourites(favourites.getFavourites());
            storage.saveActivity(activities);
            storage.saveFood(foods);
            storage.saveGift(gifts);
            ui.exitMessage();
        } catch (IOException e) {
            ui.errorMessage("Yikes! Our love potion spilled and couldn't save your data! " + e.getMessage());
        }
    }
}