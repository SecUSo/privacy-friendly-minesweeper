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
 * This class is responsible for managing a minesweeper cell:
 * - flagging
 * - revealing
 * - being a bomb
 * - having a state
 * - saving the numbers of surrounding bombs
 *
 * @author Patrick Schneider
 * @version 20221218
 */
class MinesweeperCell(bombs: Int = 0, state: State = State.COVERED) {

    enum class State(val code: Int) {
        COVERED(0),
        REVEALED(1),
        FLAGGED(2);

        companion object {
            fun from(code: Int): State {
                return when (code) {
                    0 -> COVERED
                    1 -> REVEALED
                    else -> FLAGGED
                }
            }
        }
    }

    var bombs = bombs
        internal set
    var state = state
        internal set

    val isFlagged get() = state == State.FLAGGED
    val isRevealed get() = state == State.REVEALED
    val isCovered get() = state == State.COVERED
    val isBomb get() = bombs == 9
    fun toggleFlag() = if (isFlagged) state = State.COVERED else state = State.FLAGGED
    fun reveal() {
        state = State.REVEALED
    }
}