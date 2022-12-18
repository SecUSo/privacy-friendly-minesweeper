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
package org.secuso.privacyfriendlyminesweeper.activities.viewmodel

import android.os.SystemClock
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.secuso.privacyfriendlyminesweeper.activities.model.Difficulty
import org.secuso.privacyfriendlyminesweeper.activities.model.Minesweeper
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.*
import org.secuso.privacyfriendlyminesweeper.activities.model.view.MinesweeperView
import org.secuso.privacyfriendlyminesweeper.activities.model.view.Orientation

/**
 * This class creates the [PlayActivityViewModel] with the supplied data.
 *
 * @author Patrick Schneider
 * @version 20221218
 */
class PlayActivityViewModelFactory(
    private val difficulty: Difficulty,
    private val grid: Array<Array<MinesweeperCell>>? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayActivityViewModel(difficulty, grid) as T
    }
}

/**
 * This class is responsible for:
 * - Managing and keeping the game data alive
 * - Translating events of the game to observable events for the view
 * - Measuring the time
 *
 * @author Patrick Schneider
 * @version 20221218
 */
class PlayActivityViewModel(difficulty: Difficulty, grid: Array<Array<MinesweeperCell>>?) : ViewModel() {

    val game = MinesweeperView(
        if (grid == null) MinesweeperGrid(difficulty) else MinesweeperGrid(difficulty, grid),
        Orientation.PORTRAIT
    )
    var timerOffset: Long = 0
    val clockBase: Long
        get() = SystemClock.elapsedRealtime() - timerOffset
    var startTime: Long = -1
        private set
        get() {
            if (field == -1L) {
                field = clockBase
            }
            return field
        }
    var stopTime: Long = -1
        private set
        get() {
            if (startTime == -1L) {
                field = timerOffset
            } else if (field == -1L) {
                field = SystemClock.elapsedRealtime() - startTime
            }
            return field
        }
    val revealedBomb = game.revealedBomb.asLiveData()
    val revealedCell = game.revealedCell.asLiveData()
    val flaggedCell = game.flaggedCell.asLiveData()
    val unflaggedCell = game.unflaggedCell.asLiveData()

    fun getGame(orientation: Orientation): Minesweeper {
        game.orientation = orientation
        return game
    }

    fun playback() {
        viewModelScope.launch { game.playback() }
    }
}