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

package org.secuso.privacyfriendlyminesweeper.activities;

import android.os.Bundle;
import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;

/**
 * @author I3ananas
 * @version 20180430
 * This class implements functions required to handle the process of playing
 */
public class PlayActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_play;
    }
}
