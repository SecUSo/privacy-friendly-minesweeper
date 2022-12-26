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
package org.secuso.privacyfriendlyminesweeper.activities.model.view

import android.content.res.Configuration
import kotlinx.coroutines.flow.map
import org.secuso.privacyfriendlyminesweeper.activities.model.Minesweeper
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperCell
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperGrid
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperGridUtils

/**
 * This enum resembles all possible and implemented orientations.
 *
 * @author Patrick Schneider
 * @version 20221218
 */
enum class Orientation {
    LANDSCAPE,
    PORTRAIT;

    companion object {
        fun from(configuration: Configuration): Orientation {
            return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE
            else PORTRAIT
        }
    }
}

/**
 * This enum resembles all possible and implemented transformations applicable to coordinates
 *
 * @author Patrick Schneider
 * @version 20221218
 */
enum class CoordinateTransformation(
    private val _apply: (Pair<Int, Int>, Int, Int) -> Pair<Int, Int>,
    private val _revert: (Pair<Int, Int>, Int, Int) -> Pair<Int, Int>
) : TransformationFactory<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>> {

    CCW({ (x, y), _, cols -> Pair(cols - y - 1, x) }, { (x, y), _, cols -> Pair(y, cols - x - 1) }),
    ID({ p, _, _ -> p }, { p, _, _ -> p });

    override fun create(): (Pair<Int, Int>) -> Transformation<Pair<Int, Int>, Pair<Int, Int>> {
        return { (rows, cols): Pair<Int, Int> ->
            object : Transformation<Pair<Int, Int>, Pair<Int, Int>> {
                override fun apply(data: Pair<Int, Int>) = _apply(data, rows, cols)
                override fun revert(data: Pair<Int, Int>) = _revert(data, rows, cols)
            }
        }
    }
}

/**
 * This class is responsible for:
 * - Translating view coordinates to model coordinates
 * - Translating model coordinates to view coordinates
 * - Implementing the [Minesweeper] Interface to provide a different view on the game,
 *   e.g. rotated display but same game orientation
 *
 * @author Patrick Schneider
 * @version 20221218
 */
class MinesweeperView(val gameGrid: MinesweeperGrid, orientation: Orientation) :
    Minesweeper(gameGrid) {

    var orientation = orientation
        set(value) {
            field = value
            transformation = onOrientation(
                CoordinateTransformation.ID,
                CoordinateTransformation.CCW
            ).create()(difficulty.rows to difficulty.cols)
        }
    override val revealedBomb = super.revealedBomb.map { it.transformIndex() }
    override val revealedCell = super.revealedCell.map { it.transformIndex() }
    override val flaggedCell = super.flaggedCell.map { it.transformIndex() }
    override val unflaggedCell = super.unflaggedCell.map { it.transformIndex() }

    private var transformation = onOrientation(
        CoordinateTransformation.ID,
        CoordinateTransformation.CCW
    ).create()(difficulty.rows to difficulty.cols)
    override val grid
        get() = onOrientation(gameGrid.grid, gridToView(gameGrid.grid))
    override val rows
        get() = onOrientation(difficulty.rows, difficulty.cols)
    override val cols
        get() = onOrientation(difficulty.cols, difficulty.rows)

    override fun clickCell(index: Pair<Int, Int>) = super.clickCell(fromViewToModel(index))

    private inline fun <reified T> onOrientation(portrait: T, landscape: T): T =
        if (orientation == Orientation.PORTRAIT) portrait else landscape

    private fun fromViewToModel(index: Pair<Int, Int>): Pair<Int, Int> =
        transformation.revert(index)

    private fun fromModelToView(index: Pair<Int, Int>): Pair<Int, Int> = transformation.apply(index)

    private fun fromModelToView(index: Int): Int {
        return MinesweeperGridUtils.linearizeIndex(
            fromModelToView(MinesweeperGridUtils.delinearizeIndex(index, difficulty.cols)),
            numberOfColumns = cols
        )
    }

    private fun gridToView(grid: Array<Array<MinesweeperCell>>): Array<Array<MinesweeperCell>> {
        val correct = Array(rows) { Array(cols) { MinesweeperCell() } }
        for (row in 0 until difficulty.rows) {
            for (col in 0 until difficulty.cols) {
                val (x, y) = fromModelToView(Pair(row, col))
                correct[x][y] = grid[row][col]
            }
        }
        return correct
    }

    private fun Triple<Int, Pair<Int, Int>, MinesweeperCell>.transformIndex(): Triple<Int, Pair<Int, Int>, MinesweeperCell> =
        Triple(fromModelToView(this.first), fromModelToView(this.second), this.third)
}
