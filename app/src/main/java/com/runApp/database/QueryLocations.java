package com.runApp.database;

/**
 * Created by Rares on 21/02/15.
 */
public interface QueryLocations {

    String[] PROJECTION_SIMPLE = {
            GymDatabase.Tables.LOCATIONS + "." + GymDBContract.LocationsColumns.ID,
            GymDatabase.Tables.LOCATIONS + "." + GymDBContract.LocationsColumns.LATITUDE,
            GymDatabase.Tables.LOCATIONS + "." + GymDBContract.LocationsColumns.LONGITUDE,
            GymDatabase.Tables.LOCATIONS + "." + GymDBContract.LocationsColumns.SPEED,
            GymDatabase.Tables.LOCATIONS + "." + GymDBContract.LocationsColumns.NUMBER,
            GymDatabase.Tables.LOCATIONS + "." + GymDBContract.LocationsColumns.GOOGLE_RESPONSE};

    int ID = 0;
    int LATITUDE = ID + 1;
    int LONGITUDE = LATITUDE + 1;
    int SPEED = LONGITUDE + 1;
    int NUMBER = SPEED + 1;
    int GOOGLE_LOCATIONS = NUMBER + 1;
}
