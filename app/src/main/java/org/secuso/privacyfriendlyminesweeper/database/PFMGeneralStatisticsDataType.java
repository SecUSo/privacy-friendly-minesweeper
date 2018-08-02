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

/**
 * @author Karola Marky, I3ananas
 * @version 20180614
 * This class represents the "data type" of general statistics that will be stored in a table
 * Each column of the table is a private variable in this class
 */
public class PFMGeneralStatisticsDataType {

    private int ID;
    private String GAME_MODE;
    private int NR_OF_PLAYED_GAMES;
    private int NR_OF_WON_GAMES;
    private int NR_OF_UNCOVERED_FIELDS;
    private int WINS_PLAYING_TIME;
    private int TOTAL_PLAYING_TIME;

    public PFMGeneralStatisticsDataType() {    }

    /**
     * This constructor generates a single data set of general statistics
     * @param ID The primary key for the database (automatically set by the DB)
     * @param GAME_MODE Game mode of the data set
     * @param NR_OF_PLAYED_GAMES Number of played games
     * @param NR_OF_WON_GAMES Number of won games
     * @param NR_OF_UNCOVERED_FIELDS Number of uncovered fields
     * @param WINS_PLAYING_TIME Total playing time of won games
     * @param TOTAL_PLAYING_TIME Total playing time
     */
    public PFMGeneralStatisticsDataType(int ID, String GAME_MODE, int NR_OF_PLAYED_GAMES, int NR_OF_WON_GAMES, int NR_OF_UNCOVERED_FIELDS, int WINS_PLAYING_TIME, int TOTAL_PLAYING_TIME) {

        this.ID = ID;
        this.GAME_MODE = GAME_MODE;
        this.NR_OF_PLAYED_GAMES = NR_OF_PLAYED_GAMES;
        this.NR_OF_WON_GAMES = NR_OF_WON_GAMES;
        this.NR_OF_UNCOVERED_FIELDS = NR_OF_UNCOVERED_FIELDS;
        this.WINS_PLAYING_TIME = WINS_PLAYING_TIME;
        this.TOTAL_PLAYING_TIME = TOTAL_PLAYING_TIME;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getGAME_MODE() {
        return GAME_MODE;
    }

    public void setGAME_MODE(String GAME_MODE) {
        this.GAME_MODE = GAME_MODE;
    }

    public int getNR_OF_PLAYED_GAMES() {
        return NR_OF_PLAYED_GAMES;
    }

    public void setNR_OF_PLAYED_GAMES(int NR_OF_PLAYED_GAMES) {
        this.NR_OF_PLAYED_GAMES = NR_OF_PLAYED_GAMES;
    }

    public int getNR_OF_WON_GAMES() {
        return NR_OF_WON_GAMES;
    }

    public void setNR_OF_WON_GAMES(int NR_OF_WON_GAMES) {
        this.NR_OF_WON_GAMES = NR_OF_WON_GAMES;
    }

    public int getNR_OF_UNCOVERED_FIELDS() {
        return NR_OF_UNCOVERED_FIELDS;
    }

    public void setNR_OF_UNCOVERED_FIELDS(int NR_OF_UNCOVERED_FIELDS) {
        this.NR_OF_UNCOVERED_FIELDS = NR_OF_UNCOVERED_FIELDS;
    }

    public int getWINS_PLAYING_TIME() { return WINS_PLAYING_TIME; }

    public void setWINS_PLAYING_TIME(int WINS_PLAYING_TIME) { this.WINS_PLAYING_TIME = WINS_PLAYING_TIME; }

    public int getTOTAL_PLAYING_TIME() { return TOTAL_PLAYING_TIME; }

    public void setTOTAL_PLAYING_TIME(int TOTAL_PLAYING_TIME) { this.TOTAL_PLAYING_TIME = TOTAL_PLAYING_TIME; }

}
