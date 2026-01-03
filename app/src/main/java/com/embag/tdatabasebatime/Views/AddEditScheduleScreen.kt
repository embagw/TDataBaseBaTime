package com.embag.tdatabasebatime.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.Entity.RepeatType
import com.embag.tdatabasebatime.Model.Entity.Schedule
import com.embag.tdatabasebatime.Model.Entity.ScheduleType
import com.embag.tdatabasebatime.ViewModel.TaskViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onLinkTasks: () -> Unit
) {
    val currentSchedule by viewModel.currentSchedule.collectAsState()
    val isEditMode = currentSchedule != null

    val initialRepeatDaysOfWeek = currentSchedule?.repeatDaysOfWeek?.split(",")?.map {
        try {
            DayOfWeek.of(it.toInt())
        } catch (e: Exception) {
            null
        }
    }?.filterNotNull()?.toSet() ?: emptySet()

    // متغیرهای جدید برای تکرار
    var repeatType by rememberSaveable {
        mutableStateOf(currentSchedule?.repeatType ?: RepeatType.NONE)
    }
    var repeatInterval by rememberSaveable {
        mutableStateOf(currentSchedule?.repeatInterval?.toString() ?: "1")
    }
    /*var selectedDaysOfWeek by rememberSaveable {
        mutableStateOf<Set<DayOfWeek>>(
            currentSchedule?.repeatDaysOfWeek?.split(",")?.map {
                DayOfWeek.of(it.toInt())
            }?.toSet() ?: emptySet()
        )
    }*/
    var selectedDaysOfWeek by rememberSaveable {
        mutableStateOf<Set<DayOfWeek>>(initialRepeatDaysOfWeek)
    }

    var repeatDayOfMonth by rememberSaveable {
        mutableStateOf(currentSchedule?.repeatDayOfMonth?.toString() ?: "1")
    }
    var repeatMonthOfYear by rememberSaveable {
        mutableStateOf(currentSchedule?.repeatMonthOfYear?.toString() ?: "1")
    }
    var hasRepeatEndDate by rememberSaveable {
        mutableStateOf(currentSchedule?.repeatEndDate != null)
    }
    var repeatEndDate by rememberSaveable {
        mutableStateOf(currentSchedule?.repeatEndDate ?: LocalDate.now().plusMonths(1))
    }
    var repeatCount by rememberSaveable {
        mutableStateOf(currentSchedule?.repeatCount?.toString() ?: "")
    }
    val categories by viewModel.categories.collectAsState()

    // State variables - استفاده از rememberSaveable برای حفظ داده‌ها
    var selectedCategoryId by rememberSaveable {
        mutableStateOf(currentSchedule?.categoryId)
    }
    var scheduleType by rememberSaveable {
        mutableStateOf(currentSchedule?.type ?: ScheduleType.SCHEDULED)
    }
    var title by rememberSaveable { mutableStateOf(currentSchedule?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(currentSchedule?.description ?: "") }

    // تاریخ زمان‌بندی (برای همه انواع)
    var scheduleDate by rememberSaveable {
        mutableStateOf(currentSchedule?.scheduleDate ?: LocalDate.now())
    }

    // ساعت‌ها (فقط برای SCHEDULED)
    var startTime by rememberSaveable {
        mutableStateOf(currentSchedule?.startTime ?: LocalTime.of(9, 0))
    }
    var endTime by rememberSaveable {
        mutableStateOf(currentSchedule?.endTime ?: LocalTime.of(10, 0))
    }

    // برای انواع دیگر
    var estimatedMinutes by rememberSaveable {
        mutableStateOf(currentSchedule?.estimatedMinutes?.toString() ?: "60")
    }
    var count by rememberSaveable {
        mutableStateOf(currentSchedule?.count?.toString() ?: "5")
    }

    // State برای مدیریت Pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showTimePickerChain by remember { mutableStateOf(false) } // برای زنجیره‌ای کردن

    var showRepeatEndDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "ویرایش زمان‌بندی" else "ایجاد زمان‌بندی جدید") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // بخش اصلی
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // عنوان
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = title.isEmpty()
                    )

                    // دسته‌بندی
                    var categoryExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                value = categories.find { it.id == selectedCategoryId }?.name ?: "بدون دسته‌بندی",
                                onValueChange = {},
                                label = { Text("دسته‌بندی") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("بدون دسته‌بندی") },
                                    onClick = {
                                        selectedCategoryId = null
                                        categoryExpanded = false
                                    }
                                )
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.name) },
                                        onClick = {
                                            selectedCategoryId = category.id
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // توضیحات
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        maxLines = 3
                    )

                    // نوع زمان‌بندی
                    var typeExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                value = when (scheduleType) {
                                    ScheduleType.SCHEDULED -> "برنامه‌ریزی شده"
                                    ScheduleType.ESTIMATED -> "تخمین زمانی"
                                    ScheduleType.COUNT -> "تعداد دفعات"
                                    ScheduleType.EVENT -> "رویداد"
                                },
                                onValueChange = {},
                                label = { Text("نوع زمان‌بندی *") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("برنامه‌ریزی شده") },
                                    onClick = {
                                        scheduleType = ScheduleType.SCHEDULED
                                        typeExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("تخمین زمانی") },
                                    onClick = {
                                        scheduleType = ScheduleType.ESTIMATED
                                        typeExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("تعداد دفعات") },
                                    onClick = {
                                        scheduleType = ScheduleType.COUNT
                                        typeExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("رویداد") },
                                    onClick = {
                                        scheduleType = ScheduleType.EVENT
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تکرار",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Switch(
                            checked = repeatType != RepeatType.NONE,
                            onCheckedChange = { checked ->
                                repeatType = if (checked) RepeatType.DAILY else RepeatType.NONE
                            }
                        )
                    }

                    if (repeatType != RepeatType.NONE) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // نوع تکرار
                        var repeatTypeExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ExposedDropdownMenuBox(
                                expanded = repeatTypeExpanded,
                                onExpandedChange = { repeatTypeExpanded = !repeatTypeExpanded }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    readOnly = true,
                                    value = when (repeatType) {
                                        RepeatType.DAILY -> "روزانه"
                                        RepeatType.WEEKLY -> "هفتگی"
                                        RepeatType.MONTHLY -> "ماهانه"
                                        RepeatType.YEARLY -> "سالانه"
                                        RepeatType.CUSTOM_DAYS -> "روزهای خاص هفته"
                                        else -> "بدون تکرار"
                                    },
                                    onValueChange = {},
                                    label = { Text("نوع تکرار") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = repeatTypeExpanded)
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = repeatTypeExpanded,
                                    onDismissRequest = { repeatTypeExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("بدون تکرار") },
                                        onClick = {
                                            repeatType = RepeatType.NONE
                                            repeatTypeExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("روزانه") },
                                        onClick = {
                                            repeatType = RepeatType.DAILY
                                            repeatTypeExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("هفتگی") },
                                        onClick = {
                                            repeatType = RepeatType.WEEKLY
                                            repeatTypeExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("ماهانه") },
                                        onClick = {
                                            repeatType = RepeatType.MONTHLY
                                            repeatTypeExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("سالانه") },
                                        onClick = {
                                            repeatType = RepeatType.YEARLY
                                            repeatTypeExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("روزهای خاص هفته") },
                                        onClick = {
                                            repeatType = RepeatType.CUSTOM_DAYS
                                            repeatTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // فاصله تکرار
                        OutlinedTextField(
                            value = repeatInterval,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                    val value = it.toInt()
                                    if (value >= 1) {
                                        repeatInterval = it
                                    }
                                } else if (it.isEmpty()) {
                                    repeatInterval = ""
                                }
                            },
                            label = { Text("هر چند ${getRepeatIntervalText(repeatType)}") },
                            modifier = Modifier.fillMaxWidth(),
                            suffix = { Text(getRepeatIntervalSuffix(repeatType)) },
                            isError = repeatInterval.isEmpty() || repeatInterval.toIntOrNull() == null
                        )

                        // نمایش فیلدهای مربوط به هر نوع تکرار
                        when (repeatType) {
                            RepeatType.WEEKLY, RepeatType.CUSTOM_DAYS -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("روزهای هفته:", style = MaterialTheme.typography.labelMedium)

                                val daysOfWeek = listOf(
                                    DayOfWeek.SATURDAY to "شنبه",
                                    DayOfWeek.SUNDAY to "یکشنبه",
                                    DayOfWeek.MONDAY to "دوشنبه",
                                    DayOfWeek.TUESDAY to "سه‌شنبه",
                                    DayOfWeek.WEDNESDAY to "چهارشنبه",
                                    DayOfWeek.THURSDAY to "پنجشنبه",
                                    DayOfWeek.FRIDAY to "جمعه"
                                )

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    daysOfWeek.forEach { (day, name) ->
                                        FilterChip(
                                            selected = selectedDaysOfWeek.contains(day),
                                            onClick = {
                                                val newSet = selectedDaysOfWeek.toMutableSet()
                                                if (newSet.contains(day)) {
                                                    newSet.remove(day)
                                                } else {
                                                    newSet.add(day)
                                                }
                                                selectedDaysOfWeek = newSet
                                            },
                                            label = { Text(name) }
                                        )
                                    }
                                }
                            }

                            RepeatType.MONTHLY -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = repeatDayOfMonth,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                            val value = it.toInt()
                                            if (value in 1..31) {
                                                repeatDayOfMonth = it
                                            }
                                        } else if (it.isEmpty()) {
                                            repeatDayOfMonth = ""
                                        }
                                    },
                                    label = { Text("روز ماه (1-31)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = repeatDayOfMonth.isEmpty() || repeatDayOfMonth.toIntOrNull() !in 1..31
                                )
                            }

                            RepeatType.YEARLY -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // روز ماه
                                    OutlinedTextField(
                                        value = repeatDayOfMonth,
                                        onValueChange = {
                                            if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                                val value = it.toInt()
                                                if (value in 1..31) {
                                                    repeatDayOfMonth = it
                                                }
                                            } else if (it.isEmpty()) {
                                                repeatDayOfMonth = ""
                                            }
                                        },
                                        label = { Text("روز") },
                                        modifier = Modifier.weight(1f),
                                        isError = repeatDayOfMonth.isEmpty() || repeatDayOfMonth.toIntOrNull() !in 1..31
                                    )

                                    // ماه سال
                                    var monthExpanded by remember { mutableStateOf(false) }
                                    val months = listOf(
                                        "فروردین" to 1, "اردیبهشت" to 2, "خرداد" to 3,
                                        "تیر" to 4, "مرداد" to 5, "شهریور" to 6,
                                        "مهر" to 7, "آبان" to 8, "آذر" to 9,
                                        "دی" to 10, "بهمن" to 11, "اسفند" to 12
                                    )

                                    Box(modifier = Modifier.weight(1f)) {
                                        ExposedDropdownMenuBox(
                                            expanded = monthExpanded,
                                            onExpandedChange = { monthExpanded = !monthExpanded }
                                        ) {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .menuAnchor(),
                                                readOnly = true,
                                                value = months.find { it.second == repeatMonthOfYear.toIntOrNull() }?.first ?: "فروردین",
                                                onValueChange = {},
                                                label = { Text("ماه") },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded)
                                                }
                                            )

                                            ExposedDropdownMenu(
                                                expanded = monthExpanded,
                                                onDismissRequest = { monthExpanded = false }
                                            ) {
                                                months.forEach { (name, value) ->
                                                    DropdownMenuItem(
                                                        text = { Text(name) },
                                                        onClick = {
                                                            repeatMonthOfYear = value.toString()
                                                            monthExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // پایان تکرار
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "پایان تکرار:",
                                style = MaterialTheme.typography.labelMedium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "تا تعداد مشخص",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                RadioButton(
                                    selected = !hasRepeatEndDate && repeatCount.isNotEmpty(),
                                    onClick = {
                                        hasRepeatEndDate = false
                                        repeatEndDate = null
                                    }
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "تا تاریخ مشخص",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                RadioButton(
                                    selected = hasRepeatEndDate,
                                    onClick = {
                                        hasRepeatEndDate = true
                                        repeatCount = ""
                                    }
                                )
                            }
                        }

                        if (!hasRepeatEndDate) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = repeatCount,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                        repeatCount = it
                                    } else if (it.isEmpty()) {
                                        repeatCount = ""
                                    }
                                },
                                label = { Text("تعداد دفعات تکرار") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("مثال: 10") },
                                isError = repeatCount.isNotEmpty() && repeatCount.toIntOrNull() == null
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = repeatEndDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Button(onClick = { showRepeatEndDatePicker = true }) {
                                    Text("انتخاب تاریخ پایان")
                                }
                            }
                        }
                    }
                }
            }



            // بخش تاریخ (برای همه انواع)
            Card(elevation = CardDefaults.cardElevation(4.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "تاریخ زمان‌بندی:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = scheduleDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Button(onClick = { showDatePicker = true }) {
                            Text("انتخاب تاریخ")
                        }
                    }
                }
            }

            // بخش زمان‌ها (فقط برای SCHEDULED)
            if (scheduleType == ScheduleType.SCHEDULED) {
                Card(elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "زمان فعالیت:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // زمان شروع
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "شروع: ${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Button(onClick = {
                                showStartTimePicker = true
                                showTimePickerChain = true
                            }) {
                                Text("انتخاب ساعت شروع")
                            }
                        }

                        // زمان پایان
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "پایان: ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Button(onClick = { showEndTimePicker = true }) {
                                Text("انتخاب ساعت پایان")
                            }
                        }

                        // نمایش مدت
                        val duration = ChronoUnit.MINUTES.between(startTime, endTime)
                        Text(
                            text = "مدت زمان: $duration دقیقه",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (duration > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // بخش‌های دیگر بر اساس نوع
            when (scheduleType) {
                ScheduleType.ESTIMATED -> {
                    Card(elevation = CardDefaults.cardElevation(4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = estimatedMinutes,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                        estimatedMinutes = it
                                    } else if (it.isEmpty()) {
                                        estimatedMinutes = ""
                                    }
                                },
                                label = { Text("مدت زمان تخمینی (دقیقه) *") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = estimatedMinutes.isEmpty() || estimatedMinutes.toLongOrNull() == null
                            )
                        }
                    }
                }
                ScheduleType.COUNT -> {
                    Card(elevation = CardDefaults.cardElevation(4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = count,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.isNotEmpty()) {
                                        count = it
                                    } else if (it.isEmpty()) {
                                        count = ""
                                    }
                                },
                                label = { Text("تعداد دفعات *") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = count.isEmpty() || count.toIntOrNull() == null
                            )
                        }
                    }
                }
                else -> {}
            }

            // دکمه ذخیره
            Button(
                onClick = {
                    when (scheduleType) {
                        ScheduleType.SCHEDULED -> {
                            if (title.isNotEmpty() && endTime.isAfter(startTime)) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            categoryId = selectedCategoryId,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            scheduleDate = scheduleDate,
                                            startTime = startTime, // فقط برای SCHEDULED
                                            endTime = endTime, // فقط برای SCHEDULED
                                            estimatedMinutes = estimatedMinutes.toLongOrNull(), // برای ESTIMATED
                                            count = count.toIntOrNull(), // برای COUNT
                                            currentCount = currentSchedule?.currentCount ?: 0, // برای COUNT
                                            // فیلدهای تکرار
                                            repeatType = repeatType,
                                            repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                            repeatDaysOfWeek = if (selectedDaysOfWeek.isNotEmpty()) {
                                                selectedDaysOfWeek.joinToString(",") { it.value.toString() }
                                            } else null,
                                            repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                            repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                            repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                            repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null,
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        categoryId = selectedCategoryId,
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        scheduleDate = scheduleDate,
                                        startTime = startTime,
                                        endTime = endTime,
                                        // پارامترهای تکرار
                                        repeatType = repeatType,
                                        repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                        repeatDaysOfWeek = selectedDaysOfWeek.takeIf { it.isNotEmpty() },
                                        repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                        repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                        repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                        repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null
                                    )
                                }
                                onBack()
                            }
                        }
                        ScheduleType.EVENT -> {
                            if (title.isNotEmpty()) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            categoryId = selectedCategoryId,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            scheduleDate = scheduleDate,
                                            startTime = startTime, // فقط برای SCHEDULED
                                            endTime = endTime, // فقط برای SCHEDULED
                                            estimatedMinutes = estimatedMinutes.toLongOrNull(), // برای ESTIMATED
                                            count = count.toIntOrNull(), // برای COUNT
                                            currentCount = currentSchedule?.currentCount ?: 0, // برای COUNT
                                            // فیلدهای تکرار
                                            repeatType = repeatType,
                                            repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                            repeatDaysOfWeek = if (selectedDaysOfWeek.isNotEmpty()) {
                                                selectedDaysOfWeek.joinToString(",") { it.value.toString() }
                                            } else null,
                                            repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                            repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                            repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                            repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null,
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        categoryId = selectedCategoryId,
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        scheduleDate = scheduleDate,
                                        // پارامترهای تکرار
                                        repeatType = repeatType,
                                        repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                        repeatDaysOfWeek = selectedDaysOfWeek.takeIf { it.isNotEmpty() },
                                        repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                        repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                        repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                        repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null
                                    )
                                }
                                onBack()

                            }
                        }
                        ScheduleType.ESTIMATED -> {
                            if (title.isNotEmpty() && estimatedMinutes.isNotEmpty() && estimatedMinutes.toLongOrNull() != null) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            categoryId = selectedCategoryId,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            scheduleDate = scheduleDate,
                                            startTime = startTime, // فقط برای SCHEDULED
                                            endTime = endTime, // فقط برای SCHEDULED
                                            estimatedMinutes = estimatedMinutes.toLongOrNull(), // برای ESTIMATED
                                            count = count.toIntOrNull(), // برای COUNT
                                            currentCount = currentSchedule?.currentCount ?: 0, // برای COUNT
                                            // فیلدهای تکرار
                                            repeatType = repeatType,
                                            repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                            repeatDaysOfWeek = if (selectedDaysOfWeek.isNotEmpty()) {
                                                selectedDaysOfWeek.joinToString(",") { it.value.toString() }
                                            } else null,
                                            repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                            repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                            repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                            repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null,
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        categoryId = selectedCategoryId,
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        scheduleDate = scheduleDate,
                                        estimatedMinutes = estimatedMinutes.toLong(),
                                        // پارامترهای تکرار
                                        repeatType = repeatType,
                                        repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                        repeatDaysOfWeek = selectedDaysOfWeek.takeIf { it.isNotEmpty() },
                                        repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                        repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                        repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                        repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null
                                    )
                                }
                                onBack()
                            }
                        }
                        ScheduleType.COUNT -> {
                            if (title.isNotEmpty() && count.isNotEmpty() && count.toIntOrNull() != null) {
                                if (isEditMode) {
                                    viewModel.updateSchedule(
                                        Schedule(
                                            id = currentSchedule!!.id,
                                            categoryId = selectedCategoryId,
                                            type = scheduleType,
                                            title = title,
                                            description = description.ifBlank { null },
                                            scheduleDate = scheduleDate,
                                            startTime = startTime, // فقط برای SCHEDULED
                                            endTime = endTime, // فقط برای SCHEDULED
                                            estimatedMinutes = estimatedMinutes.toLongOrNull(), // برای ESTIMATED
                                            count = count.toIntOrNull(), // برای COUNT
                                            currentCount = currentSchedule?.currentCount ?: 0, // برای COUNT
                                            // فیلدهای تکرار
                                            repeatType = repeatType,
                                            repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                            repeatDaysOfWeek = if (selectedDaysOfWeek.isNotEmpty()) {
                                                selectedDaysOfWeek.joinToString(",") { it.value.toString() }
                                            } else null,
                                            repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                            repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                            repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                            repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null,
                                            isActive = currentSchedule?.isActive ?: true
                                        )
                                    )
                                } else {
                                    viewModel.createSchedule(
                                        categoryId = selectedCategoryId,
                                        type = scheduleType,
                                        title = title,
                                        description = description.ifBlank { null },
                                        count = count.toInt(),
                                        // پارامترهای تکرار
                                        repeatType = repeatType,
                                        repeatInterval = repeatInterval.toIntOrNull() ?: 1,
                                        repeatDaysOfWeek = selectedDaysOfWeek.takeIf { it.isNotEmpty() },
                                        repeatDayOfMonth = repeatDayOfMonth.toIntOrNull(),
                                        repeatMonthOfYear = repeatMonthOfYear.toIntOrNull(),
                                        repeatEndDate = if (hasRepeatEndDate) repeatEndDate else null,
                                        repeatCount = if (!hasRepeatEndDate) repeatCount.toIntOrNull() else null
                                    )
                                }
                                onBack()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = when (scheduleType) {
                    ScheduleType.SCHEDULED -> title.isNotEmpty() && endTime.isAfter(startTime)
                    ScheduleType.EVENT -> title.isNotEmpty()
                    ScheduleType.ESTIMATED -> title.isNotEmpty() && estimatedMinutes.isNotEmpty() && estimatedMinutes.toLongOrNull() != null
                    ScheduleType.COUNT -> title.isNotEmpty() && count.isNotEmpty() && count.toIntOrNull() != null
                }
            ) {
                Text(if (isEditMode) "ذخیره تغییرات" else "ایجاد زمان‌بندی")
            }
        }
    }

    // Pickerها
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date -> scheduleDate = date },
            onDismiss = { showDatePicker = false },
            initialDate = scheduleDate
        )
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                startTime = time
                // خودکار زمان پایان را تنظیم کن
                endTime = time.plusHours(1)
                if (showTimePickerChain) {
                    // پس از انتخاب زمان شروع، زمان پایان را نشان بده
                    showEndTimePicker = true
                }
            },
            onDismiss = {
                showStartTimePicker = false
                showTimePickerChain = false
            },
            initialTime = startTime
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                endTime = time
            },
            onDismiss = { showEndTimePicker = false },
            initialTime = endTime
        )
    }
    if (showRepeatEndDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                repeatEndDate = date
                showRepeatEndDatePicker = false
            },
            onDismiss = { showRepeatEndDatePicker = false },
            initialDate = repeatEndDate
        )
    }
}

@Composable
fun getRepeatIntervalText(repeatType: RepeatType): String {
    return when (repeatType) {
        RepeatType.DAILY -> "روز"
        RepeatType.WEEKLY -> "هفته"
        RepeatType.MONTHLY -> "ماه"
        RepeatType.YEARLY -> "سال"
        else -> ""
    }
}

@Composable
fun getRepeatIntervalSuffix(repeatType: RepeatType): String {
    return when (repeatType) {
        RepeatType.DAILY -> "روز"
        RepeatType.WEEKLY -> "هفته"
        RepeatType.MONTHLY -> "ماه"
        RepeatType.YEARLY -> "سال"
        else -> ""
    }
}