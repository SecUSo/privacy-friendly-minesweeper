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
 * @author I3ananas
 * @version 20180803
 * This class represents the data type of a saved game that will be stored in a table
 * Each column in the table is a private variable in this class
 */
public class PFMSavedGameDataType {

    private int ID;
    private String GAME_MODE;
    private int TIME;
    private String DATE;
    private String PROGRESS;
    private String SAVED_GAME_CONTENT;
    private String SAVED_GAME_STATUS;

    public PFMSavedGameDataType() {    }

    /**
     * This constructor generates a single data set of a saved game
     * @param ID The primary key for the table (automatically set by the DB)
     * @param GAME_MODE Game mode of the saved game
     * @param TIME Playing time so far
     * @param DATE Date and time the game was played the last time
     * @param PROGRESS Progress of the saved game (ratio of opened fields)
     * @param SAVED_GAME_CONTENT Concatenated String with content information about the cells of the playing field
     * @param SAVED_GAME_STATUS Concatenated String with status information about the cells of the playing field
     */
    public PFMSavedGameDataType(int ID, String GAME_MODE, int TIME, String DATE, String PROGRESS, String SAVED_GAME_CONTENT, String SAVED_GAME_STATUS){
        this.ID = ID;
        this.GAME_MODE = GAME_MODE;
        this.TIME = TIME;
        this.DATE = DATE;
        this.PROGRESS = PROGRESS;
        this.SAVED_GAME_CONTENT = SAVED_GAME_CONTENT;
        this.SAVED_GAME_STATUS = SAVED_GAME_STATUS;
    }

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getGAME_MODE() { return GAME_MODE; }

    public void setGAME_MODE(String GAME_MODE) { this.GAME_MODE = GAME_MODE; }

    public int getTIME() { return TIME; }

    public void setTIME(int TIME) { this.TIME = TIME; }

    public String getDATE() { return DATE; }

    public void setDATE(String DATE) { this.DATE = DATE; }

    public String getPROGRESS() { return PROGRESS; }

    public void setPROGRESS(String PROGRESS) { this.PROGRESS = PROGRESS; }

    public String getSAVED_GAME_CONTENT() { return SAVED_GAME_CONTENT; }

    public void setSAVED_GAME_CONTENT(String SAVED_GAME_CONTENT) { this.SAVED_GAME_CONTENT = SAVED_GAME_CONTENT; }

    public String getSAVED_GAME_STATUS() { return SAVED_GAME_STATUS; }

    public void setSAVED_GAME_STATUS(String SAVED_GAME_STATUS) { this.SAVED_GAME_STATUS = SAVED_GAME_STATUS; }

}
