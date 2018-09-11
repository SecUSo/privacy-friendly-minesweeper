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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Karola Marky, I3ananas
 * @version 20180803
 * Structure based on http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 * accessed at 16th June 2016
 * This class defines structure and methods of the database
 * It contains all methods that are used to work with the database (store data, delete data, update data, ...)
 */
public class PFMSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    //Name of the database
    private static final String DATABASE_NAME = "PF_MINESWEEPER_DB";

    //Names of tables in the database
    private static final String TABLE_GENERAL_STATISTICS = "GENERAL_STATISTICS";
    private static final String TABLE_TOP_TIMES = "TOP_TIMES";
    private static final String TABLE_SAVED_GAMES = "SAVED_GAMES";

    //Names of columns in the tables
    private static final String KEY_ID = "id";

    private static final String KEY_GAME_MODE = "game_mode";
    private static final String KEY_NR_OF_PLAYED_GAMES = "nr_of_played_games";
    private static final String KEY_NR_OF_WON_GAMES = "nr_of_won_games";
    private static final String KEY_NR_OF_UNCOVERED_FIELDS = "nr_of_uncovered_fields";
    private static final String KEY_WINS_PLAYING_TIME = "wins_playing_time";
    private static final String KEY_TOTAL_PLAYING_TIME = "total_playing_time";

    private static final String KEY_GAME_MODE_TOP_TIME = "game_mode";
    private static final String KEY_PLAYING_TIME = "playing_time";
    private static final String KEY_DATE = "date";

    private static final String KEY_GAME_MODE_SAVED = "game_mode";
    private static final String KEY_PLAYING_TIME_SAVED = "playing_time";
    private static final String KEY_DATE_SAVED = "date";
    private static final String KEY_PROGRESS = "progress";
    private static final String KEY_SAVED_GAME_CONTENT = "saved_game_content";
    private static final String KEY_SAVED_GAME_STATUS = "saved_game_status";

    public PFMSQLiteHelper(Context context) {
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
                KEY_WINS_PLAYING_TIME + " INTEGER," +
                KEY_TOTAL_PLAYING_TIME + " INTEGER);";

        String CREATE_TOP_TIMES_TABLE = "CREATE TABLE " + TABLE_TOP_TIMES +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_GAME_MODE_TOP_TIME + " INTEGER," +
                KEY_PLAYING_TIME + " INTEGER," +
                KEY_DATE + " TEXT);";

        String CREATE_SAVED_GAMES_TABLE = "CREATE TABLE " + TABLE_SAVED_GAMES +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_GAME_MODE + " INTEGER," +
                KEY_PLAYING_TIME + " INTEGER," +
                KEY_DATE + " TEXT," +
                KEY_PROGRESS + " TEXT," +
                KEY_SAVED_GAME_CONTENT + " TEXT," +
                KEY_SAVED_GAME_STATUS + " TEXT);";

        sqLiteDatabase.execSQL(CREATE_GENERAL_STATISTICS_TABLE);
        sqLiteDatabase.execSQL(CREATE_TOP_TIMES_TABLE);
        sqLiteDatabase.execSQL(CREATE_SAVED_GAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERAL_STATISTICS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_TIMES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_GAMES);

        onCreate(sqLiteDatabase);
    }

    /**
     * Adds a single data set of general statistics to the table
     * As no ID is provided and KEY_ID is autoincremented
     * the last available key of the table is taken and incremented by 1
     * @param generalStats Data set of general statistics that is added
     */
    public void addGeneralStatisticsData(PFMGeneralStatisticsDataType generalStats) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_MODE, generalStats.getGAME_MODE());
        values.put(KEY_NR_OF_PLAYED_GAMES, generalStats.getNR_OF_PLAYED_GAMES());
        values.put(KEY_NR_OF_WON_GAMES, generalStats.getNR_OF_WON_GAMES());
        values.put(KEY_NR_OF_UNCOVERED_FIELDS, generalStats.getNR_OF_UNCOVERED_FIELDS());
        values.put(KEY_WINS_PLAYING_TIME, generalStats.getWINS_PLAYING_TIME());
        values.put(KEY_TOTAL_PLAYING_TIME, generalStats.getTOTAL_PLAYING_TIME());

        database.insert(TABLE_GENERAL_STATISTICS, null, values);
        database.close();
    }

    /**
     * Adds a single data set of general statistics to the table
     * This method can be used for re-insertion for example an undo-action
     * Therefore, the key of the data set will also be written into the database
     * @param generalStats Data set of general statistics that is added
     */
    public void addGeneralStatisticsDataWithID(PFMGeneralStatisticsDataType generalStats) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, generalStats.getID());
        values.put(KEY_GAME_MODE, generalStats.getGAME_MODE());
        values.put(KEY_NR_OF_PLAYED_GAMES, generalStats.getNR_OF_PLAYED_GAMES());
        values.put(KEY_NR_OF_WON_GAMES, generalStats.getNR_OF_WON_GAMES());
        values.put(KEY_NR_OF_UNCOVERED_FIELDS, generalStats.getNR_OF_UNCOVERED_FIELDS());
        values.put(KEY_WINS_PLAYING_TIME, generalStats.getWINS_PLAYING_TIME());
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

        Cursor cursor = database.query(TABLE_GENERAL_STATISTICS, new String[]{KEY_ID, KEY_GAME_MODE,
                        KEY_NR_OF_PLAYED_GAMES, KEY_NR_OF_WON_GAMES, KEY_NR_OF_UNCOVERED_FIELDS, KEY_WINS_PLAYING_TIME,
                        KEY_TOTAL_PLAYING_TIME}, KEY_ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null, null);

        PFMGeneralStatisticsDataType dataSetGeneralStats = new PFMGeneralStatisticsDataType();

        if( cursor != null && cursor.moveToFirst() ){
            dataSetGeneralStats.setID(Integer.parseInt(cursor.getString(0)));
            dataSetGeneralStats.setGAME_MODE(cursor.getString(1));
            dataSetGeneralStats.setNR_OF_PLAYED_GAMES(Integer.parseInt(cursor.getString(2)));
            dataSetGeneralStats.setNR_OF_WON_GAMES(Integer.parseInt(cursor.getString(3)));
            dataSetGeneralStats.setNR_OF_UNCOVERED_FIELDS(Integer.parseInt(cursor.getString(4)));
            dataSetGeneralStats.setWINS_PLAYING_TIME(Integer.parseInt(cursor.getString(5)));
            dataSetGeneralStats.setTOTAL_PLAYING_TIME(Integer.parseInt(cursor.getString(6)));

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
                dataSetGeneralStats.setWINS_PLAYING_TIME(Integer.parseInt(cursor.getString(5)));
                dataSetGeneralStats.setTOTAL_PLAYING_TIME(Integer.parseInt(cursor.getString(6)));
                generalStatsDataList.add(dataSetGeneralStats);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return generalStatsDataList;
    }

    /**
     * Updates a single data set of general statistics
     * @param dataSetGeneralStats Data set that is updated
     * @return actually makes the update
     */
    public int updateGeneralStatisticsData(PFMGeneralStatisticsDataType dataSetGeneralStats) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_MODE, dataSetGeneralStats.getGAME_MODE());
        values.put(KEY_NR_OF_PLAYED_GAMES, dataSetGeneralStats.getNR_OF_PLAYED_GAMES());
        values.put(KEY_NR_OF_WON_GAMES, dataSetGeneralStats.getNR_OF_WON_GAMES());
        values.put(KEY_NR_OF_UNCOVERED_FIELDS, dataSetGeneralStats.getNR_OF_UNCOVERED_FIELDS());
        values.put(KEY_WINS_PLAYING_TIME, dataSetGeneralStats.getWINS_PLAYING_TIME());
        values.put(KEY_TOTAL_PLAYING_TIME, dataSetGeneralStats.getTOTAL_PLAYING_TIME());

        return database.update(TABLE_GENERAL_STATISTICS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(dataSetGeneralStats.getID()) });
    }

    /**
     * Deletes a single data set of general statistics from the DB
     * This method takes the data set and extracts its key to build the delete-query
     * @param dataSetGeneralStats Data set that will be deleted
     */
    public void deleteGeneralStatisticsData(PFMGeneralStatisticsDataType dataSetGeneralStats) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_GENERAL_STATISTICS, KEY_ID + " = ?",
                new String[] { Integer.toString(dataSetGeneralStats.getID()) });
        database.close();
    }

    /**
     * Deletes all data sets of general statistics
     */
    public void deleteAllGeneralStatisticsData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_GENERAL_STATISTICS);
        database.close();
    }

    /**
     * Checks if a data set with general statistics about a certain game mode is contained in the database
     * @param game_mode Game mode for which is checked if a data set describing corresponding statistics is contained in database
     * @return id of the data set describing general statistics of the game mode
     */
    public int checkIfGeneralStatsContainedInDatabase(String game_mode){
        int id = 0;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_GENERAL_STATISTICS, new String[]{KEY_ID, KEY_GAME_MODE,
                        KEY_NR_OF_PLAYED_GAMES, KEY_NR_OF_WON_GAMES, KEY_NR_OF_UNCOVERED_FIELDS, KEY_WINS_PLAYING_TIME,
                        KEY_TOTAL_PLAYING_TIME}, KEY_GAME_MODE + "=?",
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

    /**
     * Adds a single top time data set to the table
     * As no ID is provided and KEY_ID is autoincremented
     * the last available key of the table is taken and incremented by 1
     * @param topTime Data set of top time that is added
     */
    public void addTopTimeData(PFMTopTimeDataType topTime) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_MODE_TOP_TIME, topTime.getGAME_MODE());
        values.put(KEY_PLAYING_TIME, topTime.getTIME());
        values.put(KEY_DATE, topTime.getDATE());

        database.insert(TABLE_TOP_TIMES, null, values);
        database.close();
    }

    /**
     * This method returns all top time data sets of a game mode from the DB as a list
     * @param game_mode Game mode for which top time data sets are searched
     * @return A list of all available top time data sets of a game mode in the database
     */
    public List<PFMTopTimeDataType> getAllTopTimeData(String[] game_mode) {
        List<PFMTopTimeDataType> topTimeDataList = new ArrayList<PFMTopTimeDataType>();

        String selectQuery = "SELECT  * FROM " + TABLE_TOP_TIMES + " WHERE " + KEY_GAME_MODE_TOP_TIME  + "=?";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, game_mode);

        PFMTopTimeDataType dataSetTopTime;

        if (cursor.moveToFirst()) {
            do {
                dataSetTopTime = new PFMTopTimeDataType();
                dataSetTopTime.setID(Integer.parseInt(cursor.getString(0)));
                dataSetTopTime.setGAME_MODE(cursor.getString(1));
                dataSetTopTime.setTIME(Integer.parseInt(cursor.getString(2)));
                dataSetTopTime.setDATE(cursor.getString(3));
                topTimeDataList.add(dataSetTopTime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return topTimeDataList;
    }

    /**
     * This method returns all top time data sets from the DB as a list
     * @return A list of all available top time data sets in the database
     */
    public List<PFMTopTimeDataType> getAllTopTimeData() {
        List<PFMTopTimeDataType> topTimeDataList = new ArrayList<PFMTopTimeDataType>();

        String selectQuery = "SELECT  * FROM " + TABLE_TOP_TIMES;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        PFMTopTimeDataType dataSetTopTime;

        if (cursor.moveToFirst()) {
            do {
                dataSetTopTime = new PFMTopTimeDataType();
                dataSetTopTime.setID(Integer.parseInt(cursor.getString(0)));
                dataSetTopTime.setGAME_MODE(cursor.getString(1));
                dataSetTopTime.setTIME(Integer.parseInt(cursor.getString(2)));
                dataSetTopTime.setDATE(cursor.getString(3));
                topTimeDataList.add(dataSetTopTime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return topTimeDataList;
    }

    /**
     * Deletes a single top time data set from the DB
     * This method takes the data set and extracts its key to build the delete-query
     * @param dataSetTopTime Data set that will be deleted
     */
    public void deleteTopTimeData(PFMTopTimeDataType dataSetTopTime) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_TOP_TIMES, KEY_ID + " = ?",
                new String[] { Integer.toString(dataSetTopTime.getID()) });
        database.close();
    }

    /**
     * Deletes all top time data sets
     */
    public void deleteAllTopTimeData(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_TOP_TIMES);
        database.close();
    }

    /**
     * Updates a single top time data set
     * @param dataSetTopTime Top time data set that is updated
     * @return actually makes the update
     */
    public int updateTopTimeData(PFMTopTimeDataType dataSetTopTime) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_MODE_TOP_TIME, dataSetTopTime.getGAME_MODE());
        values.put(KEY_PLAYING_TIME, dataSetTopTime.getTIME());
        values.put(KEY_DATE, dataSetTopTime.getDATE());

        return database.update(TABLE_TOP_TIMES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(dataSetTopTime.getID()) });
    }

    /**
     * Reads the best saved playing time
     * @param game_mode Game_mode, for which the best saved playing time is read
     */
    public int readBestTime(String game_mode){
        int bestTime = Integer.MAX_VALUE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_TOP_TIMES, new String[]{KEY_ID, KEY_GAME_MODE_TOP_TIME,
                        KEY_PLAYING_TIME, KEY_DATE}, KEY_GAME_MODE_TOP_TIME + "=?",
                        new String[]{game_mode}, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                if (cursor.getString(1).equals(game_mode)) {
                    if(cursor.getInt(2) < bestTime){
                        bestTime = cursor.getInt(2);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return bestTime;
    }

    /**
     * Adds a single data set of a saved game to the table
     * If there are more than 10 saved games in the table, the oldest one is deleted
     * As no ID is provided and KEY_ID is autoincremented
     * the last available key of the table is taken and incremented by 1
     * @param savedGame Data set of a saved game that is added
     */
    public void addSavedGameData(PFMSavedGameDataType savedGame) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_MODE_SAVED, savedGame.getGAME_MODE());
        values.put(KEY_PLAYING_TIME_SAVED, savedGame.getTIME());
        values.put(KEY_DATE_SAVED, savedGame.getDATE());
        values.put(KEY_PROGRESS, savedGame.getPROGRESS());
        values.put(KEY_SAVED_GAME_CONTENT, savedGame.getSAVED_GAME_CONTENT());
        values.put(KEY_SAVED_GAME_STATUS, savedGame.getSAVED_GAME_STATUS());

        //if there are 10 or more saved games, the oldest one is deleted (first data set in the table)
        if(DatabaseUtils.queryNumEntries(database, TABLE_SAVED_GAMES) >= 10){
            String selectQuery = "SELECT  * FROM " + TABLE_SAVED_GAMES;
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()){
                database.delete(TABLE_SAVED_GAMES, KEY_ID + " = ?", new String[] { cursor.getString(0) });
            }
            cursor.close();
        }

        database.insert(TABLE_SAVED_GAMES, null, values);

        database.close();
    }

    /**
     * This method gets a single data set of a saved game based on its ID
     * @param id of the data set that is requested, could be get by the get-method
     * @return the data set of the saved game that is requested
     */
    public PFMSavedGameDataType getSavedGameData(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.query(TABLE_SAVED_GAMES, new String[]{KEY_ID, KEY_GAME_MODE_SAVED, KEY_PLAYING_TIME_SAVED, KEY_DATE_SAVED,
                        KEY_PROGRESS, KEY_SAVED_GAME_CONTENT, KEY_SAVED_GAME_STATUS},KEY_ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null, null);

        PFMSavedGameDataType dataSetSavedGame = new PFMSavedGameDataType();

        if( cursor != null && cursor.moveToFirst() ){
            dataSetSavedGame.setID(Integer.parseInt(cursor.getString(0)));
            dataSetSavedGame.setGAME_MODE(cursor.getString(1));
            dataSetSavedGame.setTIME(Integer.parseInt(cursor.getString(2)));
            dataSetSavedGame.setDATE(cursor.getString(3));
            dataSetSavedGame.setPROGRESS(cursor.getString(4));
            dataSetSavedGame.setSAVED_GAME_CONTENT(cursor.getString(5));
            dataSetSavedGame.setSAVED_GAME_STATUS(cursor.getString(6));

            cursor.close();
            database.close();
        }
        return dataSetSavedGame;
    }

    /**
     * Deletes a single saved game data set from the DB
     * This method takes the data set and extracts its key to build the delete-query
     * @param id ID of the data set that will be deleted
     */
    public void deleteSavedGameData(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_SAVED_GAMES, KEY_ID + " = ?",
                new String[] { Integer.toString(id)});
        database.close();
    }

    /**
     * This method checks if there are any saved games in the database
     * @return true if there are any saved games in the database, false otherwise
     */
    public boolean checkForSavedGames (){
        boolean savedGamesExist = false;
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SAVED_GAMES;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            savedGamesExist = true;
        }

        return savedGamesExist;
    }
}
