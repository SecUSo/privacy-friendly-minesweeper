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

package org.secuso.privacyfriendlyminesweeper.activities.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.GameActivity;

/**
 * @author I3ananas
 * @version 20180824
 * This class describes a DialogFragment handling an AlertDialog that is shown to set up a user defined game
 * It contains checks of the values entered in the dialog and corresponding actions
 */
public class UserDefinedGameModeDialogFragment extends DialogFragment {

    //create an instance of this dialog fragment
    public static UserDefinedGameModeDialogFragment newInstance(){
        UserDefinedGameModeDialogFragment dialogFragment = new UserDefinedGameModeDialogFragment();
        return dialogFragment;
    }

    /**
     * This method creates and sets up the dialog for setting up a user defined game
     * @param savedInstanceState
     * @return The dialog to show
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_user_defined_game_mode, null);
        builder.setView(view);

        //Action when clicking the "Continue"-Button (check entered parameters and trigger creation of corresponding game or notify if parameters are not valid)
        builder.setPositiveButton(R.string.startGame, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                int nrOfColumns = 0;
                int nrOfRows = 0;

                EditText nrOfColumns_editText = (EditText) view.findViewById(R.id.editTextNrOfColumns);
                EditText nrOfRows_editText = (EditText) view.findViewById(R.id.editTextNrOfRows);
                SeekBar seekbar = (SeekBar) view.findViewById(R.id.degreeOfDifficulty);

                //check if user entered value for number of columns
                if(!nrOfColumns_editText.getText().toString().equals("")){
                    nrOfColumns = Integer.valueOf(nrOfColumns_editText.getText().toString());
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_column), Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if user entered value for number of rows
                if(!nrOfRows_editText.getText().toString().equals("")){
                    nrOfRows = Integer.valueOf(nrOfRows_editText.getText().toString());
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_row), Toast.LENGTH_SHORT).show();
                    return;
                }

                int nrOfMines = 0;
                int nrOfCells = nrOfColumns * nrOfRows;

                //calculate number of mines or notify if there would be no mine because there are too few fields
                if(seekbar.getProgress() == 0){
                    //game mode easy requires at least 9 cells, otherwise there would be no mine
                    if(nrOfCells < 9){
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_easy), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        nrOfMines = (int) Math.round((double) nrOfCells * 0.12);
                    }
                }
                if(seekbar.getProgress() == 1){
                    //game mode medium requires at least 7 cells, otherwise there would be no mine
                    if(nrOfCells < 7){
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_medium), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        nrOfMines = (int) Math.round((double) nrOfCells * 0.15);
                    }
                }
                if(seekbar.getProgress() == 2){
                    //game mode difficult requires at least 5 cells, otherwise there would be no mine
                    if(nrOfCells < 5){
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_difficult), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        nrOfMines = (int) Math.round((double) nrOfCells * 0.20);
                    }
                }

                //trigger start of game
                ((GameActivity)getActivity()).userDefinedGameDialog_positiveClick(nrOfColumns, nrOfRows, nrOfMines);
            }
        });

        //Action when clicking the "Cancel"-Button (nothing)
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });

        return builder.create();
    }
}
