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
 * @version 20180530
 * This class reads the best saved playing time for a given game mode (in background / asynchronous)
 */
public class DatabaseBestTimeReader extends AsyncTask<String, Void, Integer> {

    public interface BestTimeReaderReceiver{
        void setBestTime(int bestTime);
    }

    private final BestTimeReaderReceiver bestTimeReaderReceiver;
    private final PFMSQLiteHelper helper;

    public DatabaseBestTimeReader(PFMSQLiteHelper helper, BestTimeReaderReceiver bestTimeReaderReceiver){
        this.helper = helper;
        this.bestTimeReaderReceiver = bestTimeReaderReceiver;
    }

    @Override
    protected Integer doInBackground(String[] params) {

        int bestTime = helper.readBestTime(params[0]);

        helper.close();

        return bestTime;

    }

    @Override
    protected void onPostExecute(Integer bestTime){

        bestTimeReaderReceiver.setBestTime(bestTime);

    }
}
