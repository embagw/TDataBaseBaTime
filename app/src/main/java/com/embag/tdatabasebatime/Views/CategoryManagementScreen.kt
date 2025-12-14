package com.embag.tdatabasebatime.Views


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.embag.tdatabasebatime.Model.Entity.Category
import com.embag.tdatabasebatime.Model.Entity.CategoryWithTaskCount
import com.embag.tdatabasebatime.ViewModel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val categories by viewModel.categoriesWithTaskCount.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    // دیالوگ اضافه/ویرایش دسته‌بندی
    if (showAddDialog) {
        AddEditCategoryDialog(
            category = editingCategory,
            onSave = { category ->
                if (category.id == 0L) {
                    viewModel.createCategory(category.name, category.color)
                } else {
                    viewModel.updateCategory(category)
                }
                editingCategory = null
                showAddDialog = false
            },
            onDismiss = {
                editingCategory = null
                showAddDialog = false
            }
        )
    }

    // دیالوگ حذف
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف دسته‌بندی") },
            text = {
                Text("آیا از حذف دسته‌بندی '${categoryToDelete?.name}' اطمینان دارید؟")
            },
            confirmButton = {
                TextButton (
                    onClick = {
                        categoryToDelete?.let { viewModel.deleteCategory(it) }
                        categoryToDelete = null
                        showDeleteDialog = false
                    }
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        categoryToDelete = null
                        showDeleteDialog = false
                    }
                ) {
                    Text("انصراف")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مدیریت دسته‌بندی‌ها") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            editingCategory = null
                            showAddDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Category")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("هیچ دسته‌بندی‌ای وجود ندارد")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "برای شروع، یک دسته‌بندی جدید ایجاد کنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(categories) { categoryWithCount ->
                    CategoryItem(
                        categoryWithCount = categoryWithCount,
                        onEdit = {
                            editingCategory = categoryWithCount.category
                            showAddDialog = true
                        },
                        onDelete = {
                            categoryToDelete = categoryWithCount.category
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    categoryWithCount: CategoryWithTaskCount,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val category = categoryWithCount.category
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // رنگ دسته‌بندی
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color(android.graphics.Color.parseColor(category.color ?: "#9E9E9E")))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${categoryWithCount.taskCount} تسک",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryDialog(
    category: Category?,
    onSave: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var color by remember { mutableStateOf(category?.color ?: "#2196F3") }

    val colors = listOf(
        "#2196F3", // آبی
        "#4CAF50", // سبز
        "#9C27B0", // بنفش
        "#FF9800", // نارنجی
        "#F44336", // قرمز
        "#607D8B", // آبی خاکستری
        "#795548", // قهوه‌ای
        "#E91E63"  // صورتی
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "افزودن دسته‌بندی" else "ویرایش دسته‌بندی") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام دسته‌بندی") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isEmpty()
                )

                Text(
                    text = "انتخاب رنگ:",
                    style = MaterialTheme.typography.titleSmall
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colors) { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .border(
                                    width = if (color == colorHex) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable() { color = colorHex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotEmpty()) {
                        val updatedCategory = Category(
                            id = category?.id ?: 0,
                            name = name,
                            color = color
                        )
                        onSave(updatedCategory)
                    }
                },
                enabled = name.isNotEmpty()
            ) {
                Text("ذخیره")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}