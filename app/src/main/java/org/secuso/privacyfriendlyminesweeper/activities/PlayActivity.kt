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
package org.secuso.privacyfriendlyminesweeper.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.secuso.privacyfriendlyminesweeper.R
import org.secuso.privacyfriendlyminesweeper.activities.adapter.PlayRecyclerViewAdapter
import org.secuso.privacyfriendlyminesweeper.activities.model.Difficulty
import org.secuso.privacyfriendlyminesweeper.activities.model.GameState
import org.secuso.privacyfriendlyminesweeper.activities.model.Minesweeper
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.*
import org.secuso.privacyfriendlyminesweeper.activities.model.view.Orientation
import org.secuso.privacyfriendlyminesweeper.activities.viewmodel.PlayActivityViewModel
import org.secuso.privacyfriendlyminesweeper.activities.viewmodel.PlayActivityViewModelFactory
import org.secuso.privacyfriendlyminesweeper.database.*
import org.secuso.privacyfriendlyminesweeper.database.DatabaseBestTimeReader.BestTimeReaderReceiver
import java.text.DateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * This class is responsible for:
 * - Displaying the minesweeper game
 * - Handling user input
 * - updating statistics
 * - saving and loading games
 *
 * @author I3ananas, max-dreger, Patrick Schneider
 * @version 20221218
 */
class PlayActivity : AppCompatActivity(), PlayRecyclerViewAdapter.ItemClickListener,
    BestTimeReaderReceiver {
    private lateinit var parameter: Bundle
    private var bestTimeReader: DatabaseBestTimeReader? = null
    private var writer: DatabaseWriter? = null
    private var bestTime = 0

    private var newBestTime = false

    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar_play) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.playingfield) }
    private val timer: Chronometer by lazy { toolbar.findViewById(R.id.chronometer) }
    private val mines: TextView by lazy { toolbar.findViewById(R.id.mines) }
    private var cellSize: Int = 0

    private var timerIsRunning = false

    private lateinit var adapter: PlayRecyclerViewAdapter
    private lateinit var viewModel: PlayActivityViewModel
    private lateinit var game: Minesweeper
    private lateinit var difficulty: Difficulty
    private lateinit var orientation: Orientation

    private var colors = intArrayOf(
        R.color.black,
        R.color.darkblue,
        R.color.darkgreen,
        R.color.red,
        R.color.darkblue,
        R.color.brown,
        R.color.cyan,
        R.color.black,
        R.color.black
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_play)

        orientation = Orientation.from(resources.configuration)


        //check if this is loading a saved game
        parameter = this.intent.extras!!

        // if a saved game was loaded, rebuild the gamestate from the save data
        if (parameter.getBoolean("continue")) {
            val savedInfo: ArrayList<String> = parameter.getStringArrayList("information")!!
            val id = Integer.valueOf(savedInfo[0])
            val savedGameMode = savedInfo[1]
            val time = savedInfo[2]
            val savedContent = savedInfo[5]
            val savedStatus = savedInfo[6]

            if (savedGameMode.equals("easy", ignoreCase = true)) {
                difficulty = Difficulty.from(Difficulty.Type.EASY)
                parameter.putShortArray(
                    "info",
                    shortArrayOf(6.toShort(), 10.toShort(), 7.toShort())
                )
                parameter.putBoolean("continue", false)
            } else if (savedGameMode.equals("medium", ignoreCase = true)) {
                difficulty = Difficulty.from(Difficulty.Type.MEDIUM)
                parameter.putShortArray(
                    "info",
                    shortArrayOf(10.toShort(), 16.toShort(), 24.toShort())
                )
                parameter.putBoolean("continue", false)
            } else {
                difficulty = Difficulty.from(Difficulty.Type.DIFFICULT)
                parameter.putShortArray(
                    "info",
                    shortArrayOf(12.toShort(), 19.toShort(), 46.toShort())
                )
                parameter.putBoolean("continue", false)
            }

            //handle the saved time
            val units = time.split(":").toTypedArray()
            val minutes = units[0].toInt()
            val seconds = units[1].toInt()
            val provider = DatabaseSavedGameProvide(PFMSQLiteHelper(applicationContext))
            provider.execute(id)

            val grid = MinesweeperGridUtils.loadGridFromSave(
                savedContent,
                savedStatus,
                difficulty.rows,
                difficulty.cols
            )
            viewModel = ViewModelProvider(
                this,
                PlayActivityViewModelFactory(difficulty, grid)
            )[PlayActivityViewModel::class.java]
            viewModel.timerOffset = (60 * minutes + seconds) * 1000L
            timer.base = viewModel.clockBase
        } else {
            val test: ShortArray = parameter.getShortArray("info")!!

            // test[0..2] = [cols, rows, bombs]
            difficulty = Difficulty.from(test[1].toInt(), test[0].toInt(), test[2].toInt())
            viewModel = ViewModelProvider(
                this,
                PlayActivityViewModelFactory(difficulty)
            )[PlayActivityViewModel::class.java]
        }
        game = viewModel.getGame(orientation)

        game.gameState.observe(this) {
            if (it == GameState.RUNNING) {
                return@observe
            }

            val gametimeInMillis = SystemClock.elapsedRealtime() - timer.base
            val gametime = gametimeInMillis / 1000
            val time = gametime.toInt()

            timer.stop()

            if (bestTime > time && difficulty.type != Difficulty.Type.CUSTOM) {
                newBestTime = true
            }

            parameter.putBoolean("victory", it == GameState.WON)
            parameter.putInt("time", time)
            parameter.putString("gameMode", difficulty.desc)
            parameter.putBoolean("newBestTime", newBestTime)

            //start victory screen
            lockActivityOrientation()
            val intent = Intent(this, VictoryScreen::class.java)
            intent.putExtras(parameter)
            startActivityForResult(intent, 0)

            //update general statistics (not for user-defined game mode)

            //update general statistics (not for user-defined game mode)
            if (difficulty.type != Difficulty.Type.CUSTOM) {
                //first parameter: game mode
                //second parameter: 1 as one match was played
                //third parameter: 1 if game was won, 0 if game was lost
                //fourth parameter: number of uncovered fields
                //fifth parameter: playing time in seconds (for won games only)
                //sixth parameter: playing time in seconds
                //seventh parameter: actual date and time
                val resultParams = arrayOf<Any>(
                    difficulty.desc,
                    1,
                    if (it == GameState.WON) 1 else 0,
                    difficulty.cells - game.remainingCells,
                    time,
                    time,
                    DateFormat.getDateTimeInstance().format(Date())
                )
                writer!!.execute(*resultParams)
            }
        }

        //handle the custom toolbar
        if (supportActionBar == null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            val text = when (difficulty.type) {
                Difficulty.Type.EASY -> R.string.game_mode_easy
                Difficulty.Type.MEDIUM -> R.string.game_mode_medium
                Difficulty.Type.DIFFICULT -> R.string.game_mode_difficult
                Difficulty.Type.CUSTOM -> R.string.game_mode_user_defined_2lines
            }
            toolbar.findViewById<TextView>(R.id.game_mode).text = resources.getString(text)
        }

        // Calculate the actual possible size to display the whole grid on the device
        // Therefore after the playfield wrapper is rendered,
        // calculate the size, update the recyclerview and create the adapter
        //
        // This has to run on the UI Thread, no unnecessary work done
        val wrapper = findViewById<View>(R.id.playfield_wrapper)
        wrapper.post {
            val (height, width) = if (orientation == Orientation.LANDSCAPE) {
                recyclerView.height to (recyclerView.width - (25 * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt())
            } else {
                (recyclerView.height - (32 * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()) to recyclerView.width
            }
            val minimum = min(height / game.rows, width / game.cols)
            cellSize =
                minimum - (2 * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
            createAdapter()
            recyclerView.layoutParams =
                LinearLayout.LayoutParams(minimum * game.cols, minimum * game.rows)
            recyclerView.requestLayout()
        }

        recyclerView.layoutManager = GridLayoutManager(
            this,
            game.cols,
            LinearLayoutManager.VERTICAL,
            false
        )

        val buttons = mutableListOf<Button>(findViewById(R.id.toggle))
        if (orientation == Orientation.LANDSCAPE) {
            buttons.add(findViewById(R.id.toggle2))
        }
        //handling the Button that toggles between revealing cells and marking them as mines
        val buttonOnClick = { _: View ->
            game.clickMode = game.clickMode.toggle()
            buttons.forEach { button ->
                if (button.text == getString(R.string.toggled)) {
                    button.background =
                        ContextCompat.getDrawable(this, R.drawable.button_highlighted)
                    button.text = getString(R.string.untoggled)
                    button.setTextColor(resources.getColor(R.color.white))
                } else {
                    button.background =
                        ContextCompat.getDrawable(this, R.drawable.button_highlighted_clicked)
                    button.text = getString(R.string.toggled)
                    button.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
        buttons.forEach { it.setOnClickListener(buttonOnClick) }

        mines.text = game.remainingBombs.toString()
        toolbar.findViewById<ImageView>(R.id.mines_pic).setImageResource(R.drawable.mine)

        bestTimeReader = DatabaseBestTimeReader(PFMSQLiteHelper(applicationContext), this)
        bestTimeReader!!.execute(difficulty.desc)
        writer = DatabaseWriter(PFMSQLiteHelper(applicationContext))

        viewModel.revealedBomb.observe(this) {
            val (linIndex, _, cell) = it
            adapter.setItem(linIndex, cell)
            adapter.notifyItemChanged(linIndex)
        }
        viewModel.flaggedCell.observe(this) {
            val (linIndex, _, cell) = it
            adapter.setItem(linIndex, cell)
            adapter.notifyItemChanged(linIndex)
            mines.text = game.remainingBombs.toString()
        }
        viewModel.unflaggedCell.observe(this) {
            val (linIndex, _, cell) = it
            adapter.setItem(linIndex, cell)
            adapter.notifyItemChanged(linIndex)
            mines.text = game.remainingBombs.toString()
            mines.text = game.remainingBombs.toString()
        }
        viewModel.revealedCell.observe(this) {
            Log.e("Received", "${it.first}, ${it.second}")
            val (linIndex, _, cell) = it
            adapter.setItem(linIndex, cell)
            adapter.notifyItemChanged(linIndex)
        }
        createAdapter()
    }

    override fun onPostResume() {
        super.onPostResume()
        viewModel.playback()
    }

    /**
     * This method is used to set the best time for comparison
     */
    override fun setBestTime(bt: Int) {
        bestTime = bt
    }

    /**
     * This method overrides the onItemClick of the Playing Field cells.
     * @param view the View Containing the Cell where the event was triggered
     * @param position the position of the Cell that was clicked
     */
    override fun onItemClick(view: View, position: Int) {
        //on the first click the timer must be started
        game.clickCell(MinesweeperGridUtils.delinearizeIndex(position, game.cols))
        if (!timerIsRunning) {
            timer.base = viewModel.startTime
            timer.start()
            timerIsRunning = true
        }
    }

    /**
     * This method creates a new PlayRecyclerViewAdapter with the given parameters and connects it to the RecyclerView with the Playing Field
     */
    private fun createAdapter() {
        adapter = PlayRecyclerViewAdapter(
            this,
            game.grid.flatten().toTypedArray(),
            cellSize,
            cellSize,
            colors.map { color -> resources.getColor(color) }.toTypedArray().toIntArray(),
            ResourcesCompat.getColor(resources, R.color.middleblue, null)
        )
        adapter.setClickListener(this)
        recyclerView.adapter = adapter
    }

    /**
     * This method is used to close the PlayActivity when a button on the Victory Screen is pressed
     * @param requestCode the Code for the request, should be 0 if all went well
     * @param resultCode the Code for the result, should be RESULT_OK if nothing broke
     * @param data the Intent of the Activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                finish()
            }
        }
    }

    /**
     * This method is used to save the game if the PlayActivity is exited without winning or losing
     */
    override fun onStop() {

        //check if the game has not ended
        if (game.isRunning()) {
            //no saving of user defined mode
            if (difficulty.type != Difficulty.Type.CUSTOM) {
                //ready the save Data
                val time = viewModel.stopTime.toInt() / 1000
                timer.stop()

                //check if we need to save into database or not
                if (!isChangingConfigurations) {
                    val (bombs, states) = game.separateBombsAndStatus()
                    val bombString = bombs.flatten().joinToString("")
                    val stateString = states.flatten().map { it.code }.joinToString("")
                    val progress =
                        (difficulty.cells - game.remainingCells).toDouble() / difficulty.cells.toDouble()

                    //Save game
                    //first parameter: game mode
                    //second parameter: game time
                    //third parameter: date
                    //fourth parameter: progress
                    //fifth parameter: string coding the content of the playingfield
                    //sixth parameter: string coding the status of the playingfield
                    val writer = DatabaseSavedGameWriter(PFMSQLiteHelper(applicationContext), this)
                    val data = arrayOf<Any>(
                        difficulty.desc,
                        time,
                        DateFormat.getDateTimeInstance().format(Date()),
                        progress,
                        bombString,
                        stateString
                    )
                    writer.execute(*data)

                    //notify that game is saved
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.gameSaved),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
        super.onStop()
    }

    private fun lockActivityOrientation() {
        val display = this.windowManager.defaultDisplay
        val rotation = display.rotation
        val size = Point()
        display.getSize(size)
        val height = size.y
        val width = size.x
        when (rotation) {
            Surface.ROTATION_90 -> if (width > height) this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            Surface.ROTATION_180 -> if (height > width) this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT else this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            Surface.ROTATION_270 -> if (width > height) this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> if (height > width) this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else this.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
}