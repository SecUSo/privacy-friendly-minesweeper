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
package org.secuso.privacyfriendlyminesweeper.activities.model.grid

import kotlinx.coroutines.flow.MutableSharedFlow
import org.secuso.privacyfriendlyminesweeper.activities.model.Difficulty

/**
 * This interface describes the needed behaviour to being a minesweeper grid:
 * - flagging a cell
 * - revealing a cell
 * - revealing surrounding cells
 * - constructing the grid and placing bombs
 * - providing access to the grid
 * - emitting events
 *
 * @author Patrick Schneider
 * @version 20221218
*/
interface IMinesweeperGrid {
    val difficulty: Difficulty
    val revealedBomb: MutableSharedFlow<Triple<Int, Pair<Int, Int>, MinesweeperCell>>
    val revealedCell: MutableSharedFlow<Triple<Int, Pair<Int, Int>, MinesweeperCell>>
    val flaggedCell: MutableSharedFlow<Triple<Int, Pair<Int, Int>, MinesweeperCell>>
    val unflaggedCell: MutableSharedFlow<Triple<Int, Pair<Int, Int>, MinesweeperCell>>
    val grid: Array<Array<MinesweeperCell>>
    val indexedGrid: Array<Array<Pair<Pair<Int, Int>, MinesweeperCell>>>

    fun isInitialized(): Boolean
    suspend fun playback()
    suspend fun flagClick(index: Pair<Int, Int>): MinesweeperCell
    suspend fun revealClick(index: Pair<Int, Int>): MinesweeperCell

    /**
     * This method fills the playing Field with data. First it puts the needed amount of bombs in random Cells, then Calculates the Number of Neighboring Bomb for each Cell
     * @param click the position of the Cell where the user clicked first. This one can not have a Bomb in it
     */
    fun fillPlayingField(click: Pair<Int, Int>)

    fun separateBombsAndStatus(): Pair<Array<Array<Int>>, Array<Array<MinesweeperCell.State>>>
    fun combineBombsAndStatus(
        bombs: Array<Array<Int>>,
        status: Array<Array<MinesweeperCell.State>>
    ) = MinesweeperGridUtils.combineBombsAndStatus(bombs, status)

    fun getBombs(): Array<Array<Int>>
    fun getStatus(): Array<Array<MinesweeperCell.State>>

    fun delinearizeIndex(index: Int): Pair<Int, Int> {
        return MinesweeperGridUtils.delinearizeIndex(index, difficulty.cols)
    }

    fun linearizeIndex(index: Pair<Int, Int>): Int {
        return MinesweeperGridUtils.linearizeIndex(index, difficulty.cols)
    }

    fun linearizeIndex(row: Int, col: Int): Int {
        return MinesweeperGridUtils.linearizeIndex(row, col, difficulty.cols)
    }
}