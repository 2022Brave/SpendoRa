package com.spendora.data.cycle

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CycleDateRange(
    val startMillis: Long,
    val endMillis: Long,
    val label: String,
    val startDay: Int,
    val endDay: Int,
    val monthName: String,
    val year: Int,
    val daysInCycle: Int
)

class FinancialCycleCalculator(
    var cycleStartDay: Int = 1 // 1..31, configurable
) {
    fun calculateCurrentCycle(referenceTimeMillis: Long = System.currentTimeMillis()): CycleDateRange {
        val cal = Calendar.getInstance().apply { timeInMillis = referenceTimeMillis }
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDaysCurrentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val effectiveStartDay = cycleStartDay.coerceIn(1, maxDaysCurrentMonth)

        val startCal = Calendar.getInstance().apply {
            timeInMillis = referenceTimeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endCal = Calendar.getInstance().apply {
            timeInMillis = referenceTimeMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        if (effectiveStartDay == 1) {
            // Standard 1st -> End of Month calendar cycle
            startCal.set(Calendar.DAY_OF_MONTH, 1)
            endCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        } else {
            if (currentDay >= effectiveStartDay) {
                // Cycle started in current month, ends next month on (effectiveStartDay - 1)
                startCal.set(Calendar.DAY_OF_MONTH, effectiveStartDay)
                endCal.add(Calendar.MONTH, 1)
                val maxNextMonth = endCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val endDay = (effectiveStartDay - 1).coerceAtMost(maxNextMonth)
                endCal.set(Calendar.DAY_OF_MONTH, endDay)
            } else {
                // Cycle started in previous month, ends current month on (effectiveStartDay - 1)
                startCal.add(Calendar.MONTH, -1)
                val maxPrevMonth = startCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val sDay = effectiveStartDay.coerceAtMost(maxPrevMonth)
                startCal.set(Calendar.DAY_OF_MONTH, sDay)
                endCal.set(Calendar.DAY_OF_MONTH, effectiveStartDay - 1)
            }
        }

        val monthFormat = SimpleDateFormat("MMMM", Locale.US)
        val monthName = monthFormat.format(startCal.time)
        val year = startCal.get(Calendar.YEAR)
        val label = if (effectiveStartDay == 1) {
            "Current Month Cycle ($monthName $year)"
        } else {
            "Cycle ${startCal.get(Calendar.DAY_OF_MONTH)} $monthName - ${endCal.get(Calendar.DAY_OF_MONTH)} ${monthFormat.format(endCal.time)}"
        }

        val daysInCycle = ((endCal.timeInMillis - startCal.timeInMillis) / (1000L * 60 * 60 * 24)).toInt() + 1

        return CycleDateRange(
            startMillis = startCal.timeInMillis,
            endMillis = endCal.timeInMillis,
            label = label,
            startDay = startCal.get(Calendar.DAY_OF_MONTH),
            endDay = endCal.get(Calendar.DAY_OF_MONTH),
            monthName = monthName,
            year = year,
            daysInCycle = daysInCycle
        )
    }
}
