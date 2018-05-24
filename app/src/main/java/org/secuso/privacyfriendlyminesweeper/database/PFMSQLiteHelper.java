/*
 This file is part of Privacy Friendly Minesweeper.

 Privacy Friendly Minesweeper is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Minesweeper is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Minesweeper. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlyminesweeper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Karola Marky, I3ananas
 * @version 20180518
 * Structure based on http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 * accessed at 16th June 2016
 * This class defines structure and methods of the database
 */
public class PFMSQLiteHelperGeneralStats extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    //Name of the database
    private static final String DATABASE_NAME = "PF_MINESWEEPER_DB";

    //Names of tables in the database
    private static final String TABLE_GENERAL_STATISTICS = "GENERAL_STATISTICS";
    private static final String TABLE_TOP_TIMES = "TOP_TIMES";

    //Names of columns in the tables
    private static final String KEY_ID = "id";
    private static final String KEY_GAME_MODE = "game_mode";
    private static final String KEY_NR_OF_PLAYED_GAMES = "nr_of_played_games";
    private static final String KEY_NR_OF_WON_GAMES = "nr_of_won_games";
    private static final String KEY_NR_OF_UNCOVERED_FIELDS = "nr_of_uncovered_fields";
    private static final String KEY_TOTAL_PLAYING_TIME = "total_playing_time";
    private static final String KEY_PLAYING_TIME = "playing_time";
    private static final String KEY_DATE = "date";

    public PFMSQLiteHelperGeneralStats(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_GENERAL_STATISTICS_TABLE = "CREATE TABLE " + TABLE_GENERAL_STATISTICS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_GAME_MODE + " INTEGER," +
                KEY_NR_OF_PLAYED_GAMES + " INTEGER," +
                KEY_NR_OF_WON_GAMES + " INTEGER," +
                KEY_NR_OF_UNCOVERED_FIELDS + " INTEGER," +
                KEY_TOTAL_PLAYING_TIME + " INTEGER);";

        sqLiteDatabase.execSQL(CREATE_GENERAL_STATISTICS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERAL_STATISTICS);

        onCreate(sqLiteDatabase);
    }

    /**
     * Adds a single data set of general statistics to the table
     * As no ID is provided and KEY_ID is autoincremented
     * the last available key of the table is taken and incremented by 1
     * @param generalStats data set of general statistics that is added
     */
    public void addGeneralStatisticsData(PFMGeneralStatisticsDataType generalStats) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_MODE, generalStats.getGAME_MODE());
        values.put(KEY_NR_OF_PLAYED_GAMES, generalStats.getNR_OF_PLAYED_GAMES());
        values.put(KEY_NR_OF_WON_GAMES, generalStats.getNR_OF_WON_GAMES());
        values.put(KEY_NR_OF_UNCOVERED_FIELDS, generalStats.getNR_OF_UNCOVERED_FIELDS());
        values.put(KEY_TOTAL_PLAYING_TIME, generalStats.getTOTAL_PLAYING_TIME());

        database.insert(TABLE_GENERAL_STATISTICS, null, values);
        database.close();
    }

    /**
     * Adds a single data set of general statistics to the table
     * This method can be used for re-insertion for example an undo-action
     * Therefore, the key of the data set will also be written into the database
     * @param generalStats data set of general statistics that is added
     */
    public void addGeneralStatisticsDataWithID(PFMGeneralStatisticsDataType generalStats) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, generalStats.getID());
        values.put(KEY_GAME_MODE, generalStats.getGAME_MODE());
        values.put(KEY_NR_OF_PLAYED_GAMES, generalStats.getNR_OF_PLAYED_GAMES());
        values.put(KEY_NR_OF_WON_GAMES, generalStats.getNR_OF_WON_GAMES());
        values.put(KEY_NR_OF_UNCOVERED_FIELDS, generalStats.getNR_OF_UNCOVERED_FIELDS());
        values.put(KEY_TOTAL_PLAYING_TIME, generalStats.getTOTAL_PLAYING_TIME());

        database.insert(TABLE_GENERAL_STATISTICS, null, values);
        database.close();
    }

    /**
     * This method gets a single data set of general statistics based on its ID
     * @param id of the data set that is requested, could be get by the get-method
     * @return the data set of general statistics that is requested
     */
    public PFMGeneralStatisticsDataType getGeneralStatisticsData(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        Log.d("DATABASE", Integer.toString(id));

        Cursor cursor = database.query(TABLE_GENERAL_STATISTICS, new String[]{KEY_ID, KEY_GAME_MODE,
                        KEY_NR_OF_PLAYED_GAMES, KEY_NR_OF_WON_GAMES, KEY_NR_OF_UNCOVERED_FIELDS, KEY_TOTAL_PLAYING_TIME},
                        KEY_ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null, null);

        PFMGeneralStatisticsDataType dataSetGeneralStats = new PFMGeneralStatisticsDataType();

        if( cursor != null && cursor.moveToFirst() ){
            dataSetGeneralStats.setID(Integer.parseInt(cursor.getString(0)));
            dataSetGeneralStats.setGAME_MODE(cursor.getString(1));
            dataSetGeneralStats.setNR_OF_PLAYED_GAMES(Integer.parseInt(cursor.getString(2)));
            dataSetGeneralStats.setNR_OF_WON_GAMES(Integer.parseInt(cursor.getString(3)));
            dataSetGeneralStats.setNR_OF_UNCOVERED_FIELDS(Integer.parseInt(cursor.getString(4)));
            dataSetGeneralStats.setTOTAL_PLAYING_TIME(Integer.parseInt(cursor.getString(5)));

            Log.d("DATABASE", "Read " + cursor.getString(1) + " from DB");

            cursor.close();
            database.close();
        }
        return dataSetGeneralStats;
    }

    /**
     * This method returns all data sets of general statistics from the DB as a list
     * @return A list of all available data sets of general statistics in the database
     */
    public List<PFMGeneralStatisticsDataType> getAllGeneralStatisticsData() {
        List<PFMGeneralStatisticsDataType> generalStatsDataList = new ArrayList<PFMGeneralStatisticsDataType>();

        String selectQuery = "SELECT  * FROM " + TABLE_GENERAL_STATISTICS;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        PFMGeneralStatisticsDataType dataSetGeneralStats = null;

        if (cursor.moveToFirst()) {
            do {
                dataSetGeneralStats = new PFMGeneralStatisticsDataType();
                dataSetGeneralStats.setID(Integer.parseInt(cursor.getString(0)));
                dataSetGeneralStats.setGAME_MODE(cursor.getString(1));
                dataSetGeneralStats.setNR_OF_PLAYED_GAMES(Integer.parseInt(cursor.getString(2)));
                dataSetGeneralStats.setNR_OF_WON_GAMES(Integer.parseInt(cursor.getString(3)));
                dataSetGeneralStats.setNR_OF_UNCOVERED_FIELDS(Integer.parseInt(cursor.getString(4)));
                dataSetGeneralStats.setTOTAL_PLAYING_TIME(Integer.parseInt(cursor.getString(5)));
                generalStatsDataList.add(dataSetGeneralStats);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return generalStatsDataList;
    }

    /**
     * Updates a single data set of general statistics
     * @param dataSetGeneralStats data set of (new) values to replace another in the database
     * @return actually makes the update
     */
    public int updateGeneralStatisticsData(PFMGeneralStatisticsDataType dataSetGeneralStats) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_MODE, dataSetGeneralStats.getGAME_MODE());
        values.put(KEY_NR_OF_PLAYED_GAMES, dataSetGeneralStats.getNR_OF_PLAYED_GAMES());
        values.put(KEY_NR_OF_WON_GAMES, dataSetGeneralStats.getNR_OF_WON_GAMES());
        values.put(KEY_NR_OF_UNCOVERED_FIELDS, dataSetGeneralStats.getNR_OF_UNCOVERED_FIELDS());
        values.put(KEY_TOTAL_PLAYING_TIME, dataSetGeneralStats.getTOTAL_PLAYING_TIME());

        return database.update(TABLE_GENERAL_STATISTICS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(dataSetGeneralStats.getID()) });
    }

    /**
     * Deletes a single data set of general statistics from the DB
     * This method takes the data set and extracts its key to build the delete-query
     * @param dataSetGeneralStats data set that will be deleted
     */
    public void deleteGeneralStatisticsData(PFMGeneralStatisticsDataType dataSetGeneralStats) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_GENERAL_STATISTICS, KEY_ID + " = ?",
                new String[] { Integer.toString(dataSetGeneralStats.getID()) });
        database.close();
    }

    /**
     * Deletes all data sets of general statistics from the table
     */
    public void deleteAllGeneralStatisticsData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_GENERAL_STATISTICS);
        database.close();
    }

    /**
     * Checks if a data set with general statistics about a certain game mode is contained in the database
     * @param game_mode game mode for which is checked if a data set describing corresponding statistics is contained in database
     * @return id of the data set describing general statistics of the game mode
     */
    public int checkIfGeneralStatsContainedInDatabase(String game_mode){
        int id = 0;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_GENERAL_STATISTICS, new String[]{KEY_ID, KEY_GAME_MODE,
                        KEY_NR_OF_PLAYED_GAMES, KEY_NR_OF_WON_GAMES, KEY_NR_OF_UNCOVERED_FIELDS, KEY_TOTAL_PLAYING_TIME},
                        KEY_GAME_MODE + "=?",
                        new String[]{game_mode}, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                if (cursor.getString(1).equals(game_mode)) {
                    id = cursor.getInt(0);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return id;
    }
}
