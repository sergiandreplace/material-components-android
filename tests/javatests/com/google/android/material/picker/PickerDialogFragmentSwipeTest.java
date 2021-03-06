/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.picker;

import static org.junit.Assert.assertEquals;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PickerDialogFragmentSwipeTest {

  private static final Month start = Month.create(2000, Calendar.JANUARY);
  private static final Month end = Month.create(2000, Calendar.MAY);
  private static final Month opening = Month.create(2000, Calendar.FEBRUARY);

  @Rule
  public final ActivityTestRule<AppCompatActivity> activityTestRule =
      new ActivityTestRule<>(AppCompatActivity.class);

  private MaterialDatePicker<Long> dialogFragment;

  @Before
  public void setupDatePickerDialogForSwiping() {
    CalendarConstraints calendarConstraints =
        new CalendarConstraints.Builder().setStart(start).setEnd(end).setOpening(opening).build();
    FragmentManager fragmentManager = activityTestRule.getActivity().getSupportFragmentManager();
    String tag = "Date DialogFragment";

    dialogFragment =
        MaterialDatePicker.Builder.datePicker().setCalendarConstraints(calendarConstraints).build();
    dialogFragment.show(fragmentManager, tag);
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    IdlingRegistry.getInstance()
        .register(
            new RecyclerIdlingResource(
                dialogFragment.getView().findViewWithTag(MaterialCalendar.MONTHS_VIEW_GROUP_TAG)));
  }

  @Test
  public void calendarSwipeCappedAtStart() {
    PickerDialogFragmentTestUtils.swipeEarlier(dialogFragment);
    PickerDialogFragmentTestUtils.swipeEarlier(dialogFragment);
    PickerDialogFragmentTestUtils.swipeEarlier(dialogFragment);
    PickerDialogFragmentTestUtils.swipeEarlier(dialogFragment);

    assertEquals(start, findFirstVisibleItem());
  }

  @Test
  public void calendarSwipeCappedAtEnd() {
    PickerDialogFragmentTestUtils.swipeLater(dialogFragment);
    PickerDialogFragmentTestUtils.swipeLater(dialogFragment);
    PickerDialogFragmentTestUtils.swipeLater(dialogFragment);
    PickerDialogFragmentTestUtils.swipeLater(dialogFragment);
    assertEquals(end, findFirstVisibleItem());
  }

  @Test
  public void calendarOpensOnCurrent() {
    assertEquals(opening, findFirstVisibleItem());
  }

  private Month findFirstVisibleItem() {
    RecyclerView recyclerView =
        dialogFragment.getView().findViewWithTag(MaterialCalendar.MONTHS_VIEW_GROUP_TAG);
    MonthsPagerAdapter monthsPagerAdapter = (MonthsPagerAdapter) recyclerView.getAdapter();
    return monthsPagerAdapter.getPageMonth(
        ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
  }
}
