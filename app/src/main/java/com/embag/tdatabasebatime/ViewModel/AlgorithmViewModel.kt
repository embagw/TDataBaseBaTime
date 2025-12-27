package com.embag.tdatabasebatime.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // 🆕 بارگیری خودکار در ابتدا
    init {
        viewModelScope.launch {
            loadDataForDate(_selectedDate.value)
        }
    }

    // تغییر تاریخ
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        viewModelScope.launch {
            loadDataForDate(date)
        }
    }

    // بارگیری داده‌ها برای تاریخ انتخاب شده
    private fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // بارگیری زمان‌های خالی
                _freeSlots.value = algorithmRepository.calculateFreeSlots(date)

                // بارگیری زمان‌بندی‌های ESTIMATED
                _estimatedSchedules.value = algorithmRepository.getEstimatedSchedulesForDate(date)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // اجرای الگوریتم
    fun runAlgorithm() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = algorithmRepository.runSchedulingAlgorithm(_selectedDate.value)
                _algorithmResult.value = result

                // بارگیری مجدد داده‌ها
                loadDataForDate(_selectedDate.value)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}