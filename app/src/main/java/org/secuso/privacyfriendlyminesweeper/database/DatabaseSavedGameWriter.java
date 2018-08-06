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
 * This class writes a saved game in the database (in background / asynchronous)
 */
public class DatabaseSavedGameWriter extends AsyncTask<Object, Void, Void> {

    private final PFMSQLiteHelper helper;

    public DatabaseSavedGameWriter(PFMSQLiteHelper helper){
        this.helper = helper;
    }

    @Override
    protected Void doInBackground(Object[] params) {

        PFMSavedGameDataType dataSavedGame = new PFMSavedGameDataType();
        dataSavedGame.setGAME_MODE(String.valueOf(params[0]));
        dataSavedGame.setTIME(Integer.parseInt((String)params[1]));
        dataSavedGame.setDATE(String.valueOf(params[2]));
        dataSavedGame.setPROGRESS(String.valueOf(params[3]));
        dataSavedGame.setSAVED_GAME_CONTENT(String.valueOf(params[4]));
        dataSavedGame.setSAVED_GAME_STATUS(String.valueOf(params[5]));

        helper.addSavedGameData(dataSavedGame);

        helper.close();

        return null;
    }
}
