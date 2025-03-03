package com.example.firebasecourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen() {
    val database = Firebase.database
    val myRef = database.getReference("courses")

    var courseList by remember { mutableStateOf(listOf<Course>()) }
    var searchQuery by remember { mutableStateOf("") }

    // Fetch data from Firebase
    LaunchedEffect(Unit) {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = snapshot.children.mapNotNull { it.getValue(Course::class.java) }
                courseList = courses
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Danh sách khóa học") }) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search course") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(courseList.filter { it.name.contains(searchQuery, ignoreCase = true) }) { course ->
                    CourseItem(course)
                }
            }
        }
    }
}

@Composable
fun CourseItem(course: Course) {
    val database = Firebase.database.getReference("courses")
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(course.name) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                TextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Edit course name") }
                )
                Button(
                    onClick = {
                        database.child(course.id).updateChildren(mapOf("name" to editedName))
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            } else {
                Text(text = "Course Name: ${course.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Duration: ${course.duration}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Description: ${course.description}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        database.child(course.id).removeValue()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}

