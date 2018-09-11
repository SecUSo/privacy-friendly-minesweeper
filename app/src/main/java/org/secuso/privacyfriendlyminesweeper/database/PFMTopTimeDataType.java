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
 * @version 20180524
 * This class represents the data type of top times that will be stored in a table
 * Each column of the table is a private variable in this class
 */
public class PFMTopTimeDataType {

    private int ID;
    private String GAME_MODE;
    private int TIME;
    private String DATE;

    public PFMTopTimeDataType() {    }

    /**
     * This constructor generates a single data set of a top playing time
     * @param ID The primary key for the table (automatically set by the DB)
     * @param GAME_MODE Game mode of the top playing time
     * @param TIME Playing time of a finished game
     * @param DATE Date and time the game was played
     */
    public PFMTopTimeDataType(int ID, String GAME_MODE, int TIME, String DATE) {
        this.ID = ID;
        this.GAME_MODE = GAME_MODE;
        this.TIME = TIME;
        this.DATE = DATE;
    }

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getGAME_MODE() { return GAME_MODE; }

    public void setGAME_MODE(String GAME_MODE) { this.GAME_MODE = GAME_MODE; }

    public int getTIME() { return TIME; }

    public void setTIME(int TIME) { this.TIME = TIME; }

    public String getDATE() { return DATE; }

    public void setDATE(String DATE) { this.DATE = DATE; }

}
