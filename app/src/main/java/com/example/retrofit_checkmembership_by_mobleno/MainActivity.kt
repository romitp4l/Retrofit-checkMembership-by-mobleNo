package com.example.retrofit_checkmembership_by_mobleno

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.retrofit_checkmembership_by_mobleno.ui.theme.RetrofitcheckMembershipbymobleNoTheme
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class MainActivity : ComponentActivity() {
    private val apiService by lazy { ApiService.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetrofitcheckMembershipbymobleNoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(apiService = apiService)
                }
            }
        }
    }
}

data class UserData(
    val data: User
)

data class User(
    val sn: String,
    val mobileNo: String,
    val name: String,
    val password: String
)


@Composable
fun MainScreen(apiService: ApiService) {
    var mobileNo by remember {
        mutableStateOf(TextFieldValue())
    }
    var responseData by remember {
        mutableStateOf<UserData?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = mobileNo,
            onValueChange = { mobileNo = it },
            label = { Text(text = "Enter your Mobile Number ") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Using lifecycleScope directly within the composable
                // Use a separate thread for network operations
                Thread {
                    fetchUserData(apiService, mobileNo.text)
                }.start()
            }
        ) {
            Text("Fetch User Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        responseData?.let { userData ->
            Column {
                Text("Serial Number: ${userData.data.sn}")
                Text("Mobile Number: ${userData.data.mobileNo}")
                Text("Name: ${userData.data.name}")
                Text("Password: ${userData.data.password}")
            }
        }
    }
}

 fun fetchUserData(apiService: ApiService, mobileNo: String) {
    try {
        val response = apiService.getUserData(mobileNo)
        if (response.isSuccessful) {
            Log.i("dataFetchSuccessful","fetchSuccessFull")

            // Handle the successful response
            // You can update responseData state here
        } else {
            // Handle error response
            // Log or display an error message
        }
    } catch (e: Exception) {
        // Handle network or other exceptions
        // Log or display an error message
    }
}

interface ApiService {
    @FormUrlEncoded
    @POST("/checkMembership.php")
     fun getUserData(
        @Field("mobileNo") mobileNo: String
    ): Response<UserData>

    companion object {
        private const val BASE_URL = "https://wellknown-computers.000webhostapp.com"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
