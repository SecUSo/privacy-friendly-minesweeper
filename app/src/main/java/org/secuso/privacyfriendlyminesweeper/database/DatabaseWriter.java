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

import java.util.List;

/**
 * @author I3ananas
 * @version 20180524
 * This class writes data in the database (in background / asynchronous)
 */
public class DatabaseWriter extends AsyncTask<Object, Void, Void> {

    private final PFMSQLiteHelper helper;

    public DatabaseWriter(PFMSQLiteHelper helper){
        this.helper = helper;
    }

    @Override
    protected Void doInBackground(Object[] params) {

        String game_mode = String.valueOf(params[0]);
        int played = (Integer)params[1];
        int won = (Integer)params[2];
        int uncovered_fields = (Integer)params[3];
        int playing_time = (Integer)params[4];
        String date = String.valueOf(params[5]);

        //general statistics are updated in any case
        PFMGeneralStatisticsDataType data_gs;

        //if there is no data set for game_mode add one, otherwise update the existing one
        if(helper.checkIfGeneralStatsContainedInDatabase(game_mode) == 0){
            //id is set automatically in the database but constructor requires a parameter, used 0 randomly
            data_gs = new PFMGeneralStatisticsDataType(0, game_mode, played, won, uncovered_fields, playing_time);
            helper.addGeneralStatisticsData(data_gs);
        }
        else{
            data_gs = helper.getGeneralStatisticsData(helper.checkIfGeneralStatsContainedInDatabase(game_mode));
            data_gs.setNR_OF_PLAYED_GAMES(data_gs.getNR_OF_PLAYED_GAMES() + played);
            data_gs.setNR_OF_WON_GAMES(data_gs.getNR_OF_WON_GAMES() + won);
            data_gs.setNR_OF_UNCOVERED_FIELDS(data_gs.getNR_OF_UNCOVERED_FIELDS() + uncovered_fields);
            data_gs.setTOTAL_PLAYING_TIME(data_gs.getTOTAL_PLAYING_TIME() + playing_time);
            helper.updateGeneralStatisticsData(data_gs);
        }

        //if game was won (String date is not empty)
        if(!date.equals("lost")){

            PFMTopTimeDataType data_tt = null;

            String[] gm = {game_mode};
            List<PFMTopTimeDataType> list = helper.getAllTopTimeData(gm);

            if(list.size() < 10){
                //id is set automatically in the database but constructor requires a parameter, used 0 randomly
                data_tt = new PFMTopTimeDataType(0, game_mode, playing_time, date);
                helper.addTopTimeData(data_tt);
            }
            if(list.size() >= 10){
                int maxTime = playing_time;
                for(int i = 0; i < list.size(); i++){
                    if(maxTime < list.get(i).getTIME()){
                        maxTime = list.get(i).getTIME();
                        data_tt = list.get(i);
                    }
                }
                if(maxTime > playing_time){
                    data_tt.setGAME_MODE(game_mode);
                    data_tt.setTIME(playing_time);
                    data_tt.setDATE(date);
                    helper.updateTopTimeData(data_tt);
                }
            }
        }

        helper.close();

        return null;
    }
}
