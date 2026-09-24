package com.spendora

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SpendoraRobolectricTest {

    @Test
    fun testAppNameString() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SPENDO₹A", appName)
    }

    @Test
    fun testApplicationPackage() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("com.spendora", context.packageName)
    }

    @Test
    fun testResourcesAvailable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val bgDrawable = context.getDrawable(R.drawable.ic_launcher_background)
        assertNotNull(bgDrawable)
        val fgDrawable = context.getDrawable(R.drawable.ic_launcher_foreground)
        assertNotNull(fgDrawable)
        val logoDrawable = context.getDrawable(R.drawable.ic_spendora_logo)
        assertNotNull(logoDrawable)
    }

    @Test
    fun testSplitMateConnectedSheetIntent() {
        val sheetUrl = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit"
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(sheetUrl))
        assertEquals(android.content.Intent.ACTION_VIEW, intent.action)
        assertEquals("docs.google.com", intent.data?.host)
        assertEquals("/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit", intent.data?.path)
    }
}
