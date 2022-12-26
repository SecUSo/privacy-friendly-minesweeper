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

/**
 * This class is responsible for providing the common functionality:
 * - De-/linearising 2D coordinates
 * - Separating and combining the bombs and status info of the grid
 * - Assembling the grid from a save state
 *
 * @author Patrick Schneider
 * @version 20221218
 */
class MinesweeperGridUtils {
    companion object {
        fun separateBombsAndStatus(grid: Array<Array<MinesweeperCell>>): Pair<Array<Array<Int>>, Array<Array<MinesweeperCell.State>>> {
            val data = grid.fold(
                Pair<MutableList<Array<Int>>, MutableList<Array<MinesweeperCell.State>>>(
                    mutableListOf(),
                    mutableListOf()
                )
            ) { accRow, row ->
                val pair = row.fold(
                    Pair<MutableList<Int>, MutableList<MinesweeperCell.State>>(
                        mutableListOf(),
                        mutableListOf()
                    )
                ) { acc, cell ->
                    acc.apply {
                        first += cell.bombs
                        second += cell.state
                    }
                }
                accRow.apply {
                    first += pair.first.toTypedArray()
                    second += pair.second.toTypedArray()
                }
            }
            return Pair(data.first.toTypedArray(), data.second.toTypedArray())
        }

        fun combineBombsAndStatus(
            bombs: Array<Array<Int>>,
            status: Array<Array<MinesweeperCell.State>>
        ): Array<Array<MinesweeperCell>> {
            return bombs.zip(status).map { row ->
                row.first.zip(row.second).map { cell -> MinesweeperCell(cell.first, cell.second) }
                    .toTypedArray()
            }.toTypedArray()
        }

        inline fun <reified T> delinearizeData(
            data: Array<T>,
            numberOfRows: Int,
            numberOfColumns: Int
        ): Array<Array<T>> {
            val grid = mutableListOf<Array<T>>()
            for (row in 0 until numberOfRows) {
                val list = mutableListOf<T>()
                for (col in 0 until numberOfColumns) {
                    list += data[linearizeIndex(row, col, numberOfColumns)]
                }
                grid += list.toTypedArray()
            }
            return grid.toTypedArray()
        }

        inline fun <reified T> linearizeData(
            data: Array<Array<T>>,
            numberOfRows: Int,
            numberOfColumns: Int
        ): Array<T> {
            val grid = mutableListOf<T>()
            for (row in 0 until numberOfRows) {
                for (col in 0 until numberOfColumns) {
                    grid += data[row][col]
                }
            }
            return grid.toTypedArray<T>()
        }

        fun delinearizeIndex(index: Int, numberOfColumns: Int): Pair<Int, Int> {
            return Pair(index / numberOfColumns, index % numberOfColumns)
        }

        fun linearizeIndex(index: Pair<Int, Int>, numberOfColumns: Int): Int {
            return linearizeIndex(index.first, index.second, numberOfColumns)
        }

        fun linearizeIndex(row: Int, col: Int, numberOfColumns: Int): Int {
            return row * numberOfColumns + col
        }

        /**
         * This method fills the playing Field with the data from the saved game and alters the PlayingField until it is in the same state as the Saved Game and ready to be continued
         * @param savedContent A String coding the Content of each Cell (if there is a Bomb there and how many neighboring Bombs)
         * @param savedStatus A string coding the status of each Cell (if it is untouched, revealed or marked)
         */
        fun loadGridFromSave(
            savedContent: String,
            savedStatus: String,
            numberOfRows: Int,
            numberOfColumns: Int
        ): Array<Array<MinesweeperCell>> {
            val bombsLinear =
                savedContent.map { bomb -> Character.getNumericValue(bomb) }.toTypedArray()
            val statusLinear = savedStatus.map { state ->
                MinesweeperCell.State.from(
                    Character.getNumericValue(
                        state
                    )
                )
            }.toTypedArray()
            return combineBombsAndStatus(
                delinearizeData(
                    bombsLinear,
                    numberOfRows,
                    numberOfColumns
                ), delinearizeData(statusLinear, numberOfRows, numberOfColumns)
            )
        }
    }
}