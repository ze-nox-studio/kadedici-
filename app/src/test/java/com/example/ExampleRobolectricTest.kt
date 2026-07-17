package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Study Schedule & Tracker", appName)
  }

  @Test
  fun `verify day of week mapping calculations`() {
    // 2026-07-17 is Friday
    assertEquals(5, getDayOfWeekForDate("2026-07-17"))

    // 2026-07-18 is Saturday
    assertEquals(6, getDayOfWeekForDate("2026-07-18"))

    // 2026-07-19 is Sunday
    assertEquals(7, getDayOfWeekForDate("2026-07-19"))

    // 2026-07-20 is Monday
    assertEquals(1, getDayOfWeekForDate("2026-07-20"))
  }

  private fun getDayOfWeekForDate(date: String): Int {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val d = sdf.parse(date) ?: return 0
    cal.time = d
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 0
    }
  }
}
