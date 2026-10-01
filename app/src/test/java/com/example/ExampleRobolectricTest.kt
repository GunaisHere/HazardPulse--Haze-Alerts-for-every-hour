package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DndSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HazardPulse", appName)
    }

    @Test
    fun `dnd active test during night`() {
        val dnd = DndSettings(
            enabled = true,
            startHour = 22,
            startMinute = 0,
            endHour = 7,
            endMinute = 0,
            allowExtremeOverride = true
        )

        val nightCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 30)
        }
        assertTrue("DND should be active at 23:30", dnd.isCurrentlyActive(nightCal))

        val dayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
        }
        assertFalse("DND should be inactive at 14:00", dnd.isCurrentlyActive(dayCal))
    }

    @Test
    fun `voice persona defaults test`() {
        val jarvis = com.example.data.model.VoicePersona.JARVIS
        assertEquals("Sir", jarvis.defaultCallsign)
        assertEquals("J.A.R.V.I.S.", jarvis.displayName)

        val friday = com.example.data.model.VoicePersona.FRIDAY
        assertEquals("Boss", friday.defaultCallsign)
        assertEquals("F.R.I.D.A.Y.", friday.displayName)
    }
}
