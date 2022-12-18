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
package org.secuso.privacyfriendlyminesweeper.activities.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperGrid
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperCell as Cell

/**
 * This enum represents all possible game states.
 *
 * @author Patrick Schneider
 * @version 20221218
 */
enum class GameState {
    RUNNING,
    WON,
    LOST
}

/**
 * This enum represents how a click should be interpreted.
 *
 * @author Patrick Schneider
 * @version 20221218
 */
enum class ClickMode {
    REVEALING,
    FLAGGING;

    fun toggle(): ClickMode {
        return if (this == REVEALING) FLAGGING else REVEALING
    }
}

/**
 * This class is responsible for managing the minesweeper game:
 * - clicking a cell
 * - determining the game state
 * - emitting events
 * - switching the [ClickMode]
 *
 * @author Patrick Schneider
 * @version 20221218
 */
open class Minesweeper(var _grid: MinesweeperGrid) {
    private var firstClick: Boolean = true
    var clickMode = ClickMode.REVEALING

    var gameState = MutableLiveData<GameState>().apply { value = GameState.RUNNING }
        protected set

    private var _remainingFields = _grid.difficulty.cells
    private var _remainingBombs = _grid.difficulty.bombs
    val remainingBombs: Int
        get() = _remainingBombs
    val remainingCells: Int
        get() = _remainingFields - _remainingBombs

    open val revealedBomb = _grid.revealedBomb.onEach {
        if (gameState.value == GameState.RUNNING) {
            gameState.value = GameState.LOST
        }
    }

    open val revealedCell = _grid.revealedCell.onEach {
        _remainingFields--
        if (_remainingFields == 0) {
            gameState.value = GameState.WON
        }
    }
    open val flaggedCell = _grid.flaggedCell.onEach {
        Log.e("flow", "not only here!")
        _remainingBombs--
        _remainingFields--
        if (_remainingFields == 0) {
            gameState.value = GameState.WON
        }
    }
    open val unflaggedCell = _grid.unflaggedCell.onEach {
        _remainingBombs++
        _remainingFields++
    }
    val difficulty = _grid.difficulty
    open val grid = _grid.grid
    open val rows = difficulty.rows
    open val cols = difficulty.cols

    suspend fun playback() {
        _remainingBombs = difficulty.bombs
        _remainingFields = difficulty.cells
        _grid.playback()
    }

    fun getBombs() = _grid.getBombs()
    fun getStatus() = _grid.getStatus()
    fun separateBombsAndStatus() = _grid.separateBombsAndStatus()
    fun combineBombsAndStatus(bombs: Array<Array<Int>>, status: Array<Array<Cell.State>>) =
        _grid.combineBombsAndStatus(bombs, status)

    fun isRunning() = gameState.value == GameState.RUNNING

    open fun clickCell(index: Pair<Int, Int>) {
        if (gameState.value != GameState.RUNNING) {
            return
        }
        if (firstClick) {
            if (!_grid.isInitialized()) {
                _grid.fillPlayingField(index)
            }
            firstClick = false
        }
        runBlocking {
            if (clickMode == ClickMode.FLAGGING) {
                _grid.flagClick(index)
            } else {
                _grid.revealClick(index)
            }
        }
    }
}