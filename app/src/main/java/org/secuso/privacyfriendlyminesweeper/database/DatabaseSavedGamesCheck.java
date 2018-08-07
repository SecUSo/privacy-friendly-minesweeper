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

/**
 * @author I3ananas
 * @version 20180806
 * This class checks if there are any saved games in the database (in background / asynchronous)
 */
public class DatabaseSavedGamesCheck extends AsyncTask<Void, Void, Boolean> {

    public interface DatabaseSavedGamesCheckReceiver{
        void updateContinueButton(boolean savedGamesExist);
    }

    private final DatabaseSavedGamesCheck.DatabaseSavedGamesCheckReceiver databaseSavedGamesCheckReceiver;
    private final PFMSQLiteHelper helper;

    public DatabaseSavedGamesCheck(PFMSQLiteHelper helper, DatabaseSavedGamesCheck.DatabaseSavedGamesCheckReceiver databaseSavedGamesCheckReceiver){
        this.helper = helper;
        this.databaseSavedGamesCheckReceiver = databaseSavedGamesCheckReceiver;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Boolean savedGamesExist = false;

        if(helper.checkForSavedGames()){
            savedGamesExist = true;
        }

        helper.close();
        return savedGamesExist;
    }

    @Override
    protected void onPostExecute(Boolean savedGamesExist){
        databaseSavedGamesCheckReceiver.updateContinueButton(savedGamesExist);
    }
}
