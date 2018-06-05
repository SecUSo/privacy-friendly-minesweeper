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

package org.secuso.privacyfriendlyminesweeper.activities;

import android.os.Bundle;
import android.widget.ExpandableListView;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.HelpExpandableListAdapter;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Karola Marky, Christopher Beckmann, I3ananas
 * @version 20180604
 * Class structure taken from tutorial at http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * last access 27th October 2016
 */
public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        LinkedHashMap<String, List<String>> expandableListDetail = buildData();

        ExpandableListView generalExpandableListView = findViewById(R.id.generalExpandableListView);
        generalExpandableListView.setAdapter(new HelpExpandableListAdapter(this, new ArrayList<>(expandableListDetail.keySet()), expandableListDetail));

        overridePendingTransition(0, 0);
    }

    private LinkedHashMap<String, List<String>> buildData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

        expandableListDetail.put(getString(R.string.help_whatis), Collections.singletonList(getString(R.string.help_whatis_answer)));
        expandableListDetail.put(getString(R.string.help_feature_one), Collections.singletonList(getString(R.string.help_feature_one_answer)));
        expandableListDetail.put(getString(R.string.help_feature_two), Collections.singletonList(getString(R.string.help_feature_two_answer)));
        expandableListDetail.put(getString(R.string.help_feature_three), Collections.singletonList(getString(R.string.help_feature_three_answer)));
        expandableListDetail.put(getString(R.string.help_feature_four), Collections.singletonList(getString(R.string.help_feature_four_answer)));
        expandableListDetail.put(getString(R.string.help_feature_five), Collections.singletonList(getString(R.string.help_feature_five_answer)));
        expandableListDetail.put(getString(R.string.help_feature_six), Collections.singletonList(getString(R.string.help_feature_six_answer)));
        expandableListDetail.put(getString(R.string.help_privacy), Collections.singletonList(getString(R.string.help_privacy_answer)));
        expandableListDetail.put(getString(R.string.help_permission), Collections.singletonList(getString(R.string.help_permission_answer)));

        return expandableListDetail;
    }

    protected int getNavigationDrawerID() {
        return R.id.nav_help;
    }

}
