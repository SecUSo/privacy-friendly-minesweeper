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
 * @version 20180813
 * This class resets all general statistics and top times in the database (in background / asynchronous)
 */
public class DatabaseReset extends AsyncTask<Void, Void, Void>{

    public interface DatabaseResetReceiver{
        void resetStatistics();
    }

    private final DatabaseResetReceiver databaseResetReceiver;
    private final PFMSQLiteHelper helper;

    public DatabaseReset(PFMSQLiteHelper helper, DatabaseResetReceiver databaseResetReceiver){
        this.helper = helper;
        this.databaseResetReceiver = databaseResetReceiver;
    }

    @Override
    protected Void doInBackground(Void... parameters) {
        helper.deleteAllGeneralStatisticsData();
        helper.deleteAllTopTimeData();
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        databaseResetReceiver.resetStatistics();
    }
}