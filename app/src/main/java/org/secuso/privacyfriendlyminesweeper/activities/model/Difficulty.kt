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

class Difficulty(val type: Type, val rows: Int, val cols: Int, val bombs: Int) {
    enum class Type(
        val desc: String,
        val rows: Int? = null,
        val cols: Int? = null,
        val bombs: Int? = null
    ) {
        EASY("easy", 10, 6, 7),
        MEDIUM("medium", 16, 10, 24),
        DIFFICULT("difficult", 19, 12, 46),
        CUSTOM("user-defined");

        companion object {
            fun from(rows: Int?, cols: Int?, bombs: Int?): Type {
                return if (cols == EASY.cols && rows == EASY.rows && bombs == EASY.bombs) {
                    EASY
                } else if (cols == MEDIUM.cols && rows == MEDIUM.rows && bombs == MEDIUM.bombs) {
                    MEDIUM
                } else if (cols == DIFFICULT.cols && rows == DIFFICULT.rows && bombs == DIFFICULT.bombs) {
                    DIFFICULT
                } else {
                    CUSTOM
                }
            }
        }
    }

    val cells = rows * cols
    val desc = type.desc

    companion object {

        fun from(rows: Int? = null, cols: Int? = null, bombs: Int? = null) =
            from(null, rows, cols, bombs)

        fun from(
            type: Type?,
            rows: Int? = null,
            cols: Int? = null,
            bombs: Int? = null
        ): Difficulty {
            val _type = type ?: Type.from(rows, cols, bombs)
            return if (_type == Type.CUSTOM) {
                Difficulty(_type, rows!!, cols!!, bombs!!)
            } else {
                Difficulty(_type, _type.rows!!, _type.cols!!, _type.bombs!!)
            }
        }
    }
}