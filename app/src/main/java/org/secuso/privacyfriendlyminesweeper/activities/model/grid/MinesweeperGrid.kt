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
import java.util.*

import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperCell as Cell

/**
 * This class is responsible for managing the minesweeper grid:
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
class MinesweeperGrid(
    override val difficulty: Difficulty,
    override val grid: Array<Array<Cell>> = Array(difficulty.rows) { Array(difficulty.cols) { Cell() } }
) : IMinesweeperGrid {

    override val indexedGrid: Array<Array<Pair<Pair<Int, Int>, Cell>>>
        get() = grid.mapIndexed { row, cells ->
            cells.mapIndexed { col, cell -> (row to col) to cell }.toTypedArray()
        }.toTypedArray()

    private var initialized = false
    private var remainingBombs: Int = difficulty.bombs
    private var remainingFields: Int = difficulty.cells

    override val revealedBomb = MutableSharedFlow<Triple<Int, Pair<Int, Int>, Cell>>()
    override val revealedCell = MutableSharedFlow<Triple<Int, Pair<Int, Int>, Cell>>()
    override val flaggedCell = MutableSharedFlow<Triple<Int, Pair<Int, Int>, Cell>>()
    override val unflaggedCell = MutableSharedFlow<Triple<Int, Pair<Int, Int>, Cell>>()

    private val neighbourhood = arrayOf(
        Pair(-1, -1), Pair(0, -1), Pair(1, -1),
        Pair(-1, 0), Pair(1, 0),
        Pair(-1, 1), Pair(0, 1), Pair(1, 1)
    )

    override fun isInitialized(): Boolean {
        if (!initialized) {
            initialized = grid.flatten().any { it.isBomb }
        }
        return initialized
    }

    override suspend fun playback() {
        grid.forEachIndexed { row, gridRow ->
            gridRow.forEachIndexed { col, cell ->
                if (cell.isRevealed) {
                    revealedCell.emit(Triple(linearizeIndex(row, col), Pair(row, col), cell))
                } else if (cell.isFlagged) {
                    flaggedCell.emit(Triple(linearizeIndex(row, col), Pair(row, col), cell))
                }
            }
        }
    }

    override suspend fun flagClick(index: Pair<Int, Int>): Cell {
        val (x, y) = index
        if (grid[x][y].isRevealed) {
            revealCells(index)
        } else {
            if (grid[x][y].isFlagged) {
                remainingBombs++
                remainingFields++
                unflaggedCell.emit(Triple(linearizeIndex(index), index, grid[x][y]))
                grid[x][y].toggleFlag()
            } else if (remainingBombs > 0) {
                remainingBombs--
                remainingFields--
                flaggedCell.emit(Triple(linearizeIndex(index), index, grid[x][y]))
                grid[x][y].toggleFlag()
            }
        }
        return grid[x][y]
    }

    override suspend fun revealClick(index: Pair<Int, Int>): Cell {
        val (x, y) = index
        if (grid[x][y].isRevealed) {
            revealCells(index)
        } else {
            revealCell(index)
        }
        return grid[x][y]
    }

    /**
     * This method fills the playing Field with data. First it puts the needed amount of bombs in random Cells, then Calculates the Number of Neighboring Bomb for each Cell
     * @param click the position of the Cell where the user clicked first. This one can not have a Bomb in it
     */
    override fun fillPlayingField(click: Pair<Int, Int>) {

        //put bombs at random positions
        for (i in 0 until remainingBombs) {
            var row: Int
            var col: Int
            val randomGen = Random()

            // Generate random position until we have a valid bomb position.
            // A position is valid iff:
            //  * it is different from the position the user clicked on
            //  * it is not already occupied by a bomb
            //  * placing a bomb in this position does not create a cluster of bombs of size 4 or greater.
            //
            // A cluster of bombs of size n are n bombs which are horizontally and vertically connected.
            // We want to prevent the following arrangements:
            // 1) XX  2) XX   3) XXXX  4) XXX  5) XXX
            //    XX      XX              X        X
            do {
                row = randomGen.nextInt(difficulty.rows)
                col = randomGen.nextInt(difficulty.cols)
            } while (row == click.first && col == click.second
                || grid[row][col].isBomb
                || countClusterSize(android.util.Pair(row, col)) >= 4
            )
            grid[row][col].bombs = 9
        }

        // Fill the playing field with numbers depending on bomb position
        // As we do not want to handle each case (aka borders of the grid) extra,
        // we loop over all 8 neighbours of every cell and determine if the index is valid.
        for (row in 0 until difficulty.rows) {
            for (col in 0 until difficulty.cols) {
                if (grid[row][col].isBomb) {
                    continue
                }
                grid[row][col].bombs = 0
                for (neighbour in neighbourhood) {
                    val x = row + neighbour.first
                    val y = col + neighbour.second
                    if (x in 0 until difficulty.rows && y in 0 until difficulty.cols && grid[x][y].isBomb) {
                        grid[row][col].bombs++
                    }
                }
            }
        }
    }

    /**
     * Count the size of the bomb cluster which contains the given cell.
     * A cluster is the biggest set of bombs which are orthogonally connected.
     * @param pos the index of the cell to determine the cluster size.
     * @return int >= 0
     */
    private fun countClusterSize(pos: android.util.Pair<Int, Int>): Int {
        return if (grid[pos.first][pos.second].bombs != 9) 0 else countClusterSize(
            pos,
            ArrayList()
        )
    }

    /**
     * Recursively counts the size of the bomb cluster which contains the given cell.
     * A cluster is the biggest set of bombs which are orthogonally connected.
     * @param pos the index of the cell to determine the cluster size.
     * @param cluster the current cluster set.
     * @return int >= 0
     */
    private fun countClusterSize(
        pos: android.util.Pair<Int, Int>,
        cluster: MutableList<android.util.Pair<Int, Int>>
    ): Int {
        val row = pos.first
        val col = pos.second

        // Add all bombs around this bomb, if they weren't already in the cluster.
        // As we do not want to handle each case (aka borders of the grid) extra,
        // we loop over all 8 neighbours of every cell and determine if the index is valid.
        for (neighbour in neighbourhood) {
            val x = row + neighbour.first
            val y = col + neighbour.second
            val cell = android.util.Pair(x, y)
            if (x in 0 until difficulty.rows && y >= 0 && y < difficulty.cols && grid[x][y].isBomb && !cluster.contains(
                    cell
                )
            ) {
                cluster.add(cell)
                countClusterSize(cell, cluster)
            }
        }
        return cluster.size
    }

    /**
     * Recursively reveal all cells which are somehow attached to the given cell, if possible.
     * A cell may only be revealed iff count of marked cells neighbouring the current cell is geq than the bomb hint of the cell.
     * @param position
     */
    private suspend fun revealCells(position: Pair<Int, Int>) {
        val (row, col) = position
        var tagged = 0

        // Count all marked neighbours.
        // As we do not want to handle each case (aka borders of the grid) extra,
        // we loop over all 8 neighbours of every cell and determine if the index is valid.
        for (neighbour in neighbourhood) {
            val x = row + neighbour.first
            val y = col + neighbour.second
            if (x in 0 until difficulty.rows && y in 0 until difficulty.cols && grid[x][y].isFlagged) {
                tagged += 1
            }
        }
        if (tagged >= grid[row][col].bombs) {
            for (neighbour in neighbourhood) {
                val x = row + neighbour.first
                val y = col + neighbour.second

                if (x in 0 until difficulty.rows && y in 0 until difficulty.cols && grid[x][y].isCovered) {
                    revealCell(Pair(x, y))
                }
            }
        }
    }

    /**
     * This method handles the revealing of a Cell at a specific position
     * @param position position of the cell on the playing field
     */
    private suspend fun revealCell(position: Pair<Int, Int>) {

        val (x, y) = position
        if (!grid[x][y].isCovered) {
            return
        }

        grid[x][y].reveal()
        if (grid[x][y].isBomb) {
            revealedBomb.emit(Triple(linearizeIndex(position), position, grid[x][y]))
            return
        }

        revealedCell.emit(Triple(linearizeIndex(position), position, grid[x][y]))
        remainingFields--
        if (grid[x][y].bombs == 0) {
            revealCells(position)
        }

        return
    }

    override fun separateBombsAndStatus(): Pair<Array<Array<Int>>, Array<Array<Cell.State>>> {
        return MinesweeperGridUtils.separateBombsAndStatus(grid)
    }

    override fun getBombs(): Array<Array<Int>> {
        return MinesweeperGridUtils.separateBombsAndStatus(grid).first
    }

    override fun getStatus(): Array<Array<Cell.State>> {
        return MinesweeperGridUtils.separateBombsAndStatus(grid).second
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MinesweeperGrid

        if (difficulty != other.difficulty) return false
        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = difficulty.hashCode()
        result = 31 * result + grid.contentDeepHashCode()
        return result
    }
}