package com.example.firebasecourse

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val database = Firebase.database
    val myRef = database.getReference("courses")

    var courseName by remember { mutableStateOf("") }
    var courseDuration by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(title = { Text("KHÓA HỌC") })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = { Text("Enter your course name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = courseDuration,
                onValueChange = { courseDuration = it },
                label = { Text("Enter your course duration") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = courseDescription,
                onValueChange = { courseDescription = it },
                label = { Text("Enter your course description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val courseId = myRef.push().key
                    val course = Course(courseId!!, courseName, courseDuration, courseDescription)
                    myRef.child(courseId).setValue(course)
                    Toast.makeText(context, "Data Added", Toast.LENGTH_SHORT).show()

                    // Reset input fields
                    courseName = ""
                    courseDuration = ""
                    courseDescription = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Data")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("course_list") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Courses")
            }
        }
    }
}

// Data class for Course
data class Course(
    val id: String = "",
    val name: String = "",
    val duration: String = "",
    val description: String = ""
)
