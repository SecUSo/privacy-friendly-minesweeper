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

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author I3ananas
 * @version 20180806
 * This class reads all saved games from the database (in background / asynchronous)
 */
public class DatabaseSavedGamesReader extends AsyncTask<String, Void, JSONObject> {

    public interface DatabaseSavedGamesReaderReceiver{
        void displaySavedGames(JSONObject savedGames);
    }

    private final DatabaseSavedGamesReader.DatabaseSavedGamesReaderReceiver databaseSavedGamesReaderReceiver;
    private final PFMSQLiteHelper helper;

    public DatabaseSavedGamesReader(PFMSQLiteHelper helper, DatabaseSavedGamesReader.DatabaseSavedGamesReaderReceiver databaseSavedGamesReaderReceiver){
        this.helper = helper;
        this.databaseSavedGamesReaderReceiver = databaseSavedGamesReaderReceiver;
    }

    @Override
    protected JSONObject doInBackground(String[] params) {

        JSONObject savedGames = null;

        try{
            savedGames = new DatabaseExporter(String.valueOf(params[0]), "PF_MINESWEEPER_DB").dbToJSON();
        }
        catch(JSONException e){

        }

        helper.close();
        return savedGames;
    }

    @Override
    protected void onPostExecute(JSONObject savedGames){
        databaseSavedGamesReaderReceiver.displaySavedGames(savedGames);
    }
}
