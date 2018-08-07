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
 * This class exports and provides a saved game and deletes it from the database (in background / asynchronous)
 */
public class DatabaseSavedGameProvide extends AsyncTask<Integer, Void, String[]> {

    public interface DatabaseSavedGameProvideReceiver{
        void restartSavedGame(String[] savedGameData);
    }

    private final DatabaseSavedGameProvide.DatabaseSavedGameProvideReceiver databaseSavedGameProvideReceiver;
    private final PFMSQLiteHelper helper;

    public DatabaseSavedGameProvide(PFMSQLiteHelper helper, DatabaseSavedGameProvide.DatabaseSavedGameProvideReceiver databaseSavedGameProvideReceiver){
        this.helper = helper;
        this.databaseSavedGameProvideReceiver = databaseSavedGameProvideReceiver;
    }

    @Override
    protected String[] doInBackground(Integer[] position) {
        String[] savedGameData = new String[6];

        PFMSavedGameDataType savedGame = helper.getSavedGameData(position[0]);
        savedGameData[0] = savedGame.getGAME_MODE();
        savedGameData[1] = String.valueOf(savedGame.getTIME());
        savedGameData[2] = savedGame.getDATE();
        savedGameData[3] = savedGame.getPROGRESS();
        savedGameData[4] = savedGame.getSAVED_GAME_CONTENT();
        savedGameData[5] = savedGame.getSAVED_GAME_STATUS();

        helper.deleteSavedGameData(position[0]);

        helper.close();

        return savedGameData;
    }

    @Override
    protected void onPostExecute(String[] savedGameData){
        databaseSavedGameProvideReceiver.restartSavedGame(savedGameData);
    }
}
