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

/**
 * This interface resembles a reversible Transformation.
 * Mathematically interpreted an isomorphism.
 *
 * @author Patrick Schneider
 * @version 20221218
 */
interface Transformation<T, R> {
    fun apply(data: T): R
    fun revert(data: R): T
}

/**
 * This interface resembles a factory for a [Transformation].
 *
 * @author Patrick Schneider
 * @version 20221218
 */
interface TransformationFactory<A, B, C> {
    fun create(): (A) -> Transformation<B, C>
}