package com.embag.tdatabasebatime.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embag.tdatabasebatime.Model.Entity.RepeatType
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Repository.AlgorithmRepository
import com.embag.tdatabasebatime.Repository.AlgorithmResult
import com.embag.tdatabasebatime.Repository.ScheduleWithPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AlgorithmViewModel(
    private val algorithmRepository: AlgorithmRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _algorithmResult = MutableStateFlow<AlgorithmResult?>(null)
    val algorithmResult: StateFlow<AlgorithmResult?> = _algorithmResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _freeSlots = MutableStateFlow<List<Pair<LocalTime, LocalTime>>>(emptyList())
    val freeSlots: StateFlow<List<Pair<LocalTime, LocalTime>>> = _freeSlots

    private val _estimatedSchedules = MutableStateFlow<List<ScheduleWithPriority>>(emptyList())
    val estimatedSchedules: StateFlow<List<ScheduleWithPriority>> = _estimatedSchedules

    // اضافه کردن StateFlow جدید برای همه زمان‌بندی‌های روز
    private val _allSchedulesForDate = MutableStateFlow<List<Schedule>>(emptyList())
    val allSchedulesForDate: StateFlow<List<Schedule>> = _allSchedulesForDate

    init {
        viewModelScope.launch {
            loadDataForDate(_selectedDate.value)
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        viewModelScope.launch {
            loadDataForDate(date)
        }
    }

    private fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _freeSlots.value = algorithmRepository.calculateFreeSlots(date)
                _estimatedSchedules.value = algorithmRepository.getEstimatedSchedulesForDate(date)

                // اضافه کردن بارگیری همه زمان‌بندی‌های روز
                _allSchedulesForDate.value = algorithmRepository.getAllSchedulesForDate(date)
            } catch (e: Exception) {
                e.printStackTrace()
                // در صورت خطا، لیست‌های خالی برگردان
                _freeSlots.value = emptyList()
                _estimatedSchedules.value = emptyList()
                _allSchedulesForDate.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun runAlgorithm() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = algorithmRepository.runSchedulingAlgorithm(_selectedDate.value)
                _algorithmResult.value = result

                loadDataForDate(_selectedDate.value)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // تابع کمکی برای گرفتن متن نوع تکرار
    fun getRepeatTypeText(repeatType: RepeatType): String {
        return when (repeatType) {
            RepeatType.DAILY -> "روز"
            RepeatType.WEEKLY -> "هفته"
            RepeatType.MONTHLY -> "ماه"
            RepeatType.YEARLY -> "سال"
            RepeatType.CUSTOM_DAYS -> "روزهای مشخص"
            else -> "بدون تکرار"
        }
    }
}