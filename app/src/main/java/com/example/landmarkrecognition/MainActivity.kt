package com.example.landmarkrecognition

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.landmarkrecognition.domain.Classification
import com.example.landmarkrecognition.domain.LandmarkClassifier
import com.example.landmarkrecognition.presentation.CameraPreview
import com.example.landmarkrecognition.presentation.LandmarkImageAnalyzer
import com.example.landmarkrecognition.ui.theme.LandmarkRecognitionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!hasPermission()){
            ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.CAMERA) , 0)
        }
        setContent {
            LandmarkRecognitionTheme {

                var classifications by remember{
                    mutableStateOf(emptyList<Classification>())
                }

                val analyzer = remember {
                    LandmarkImageAnalyzer(
                        classifier = LandmarkClassifier(context = applicationContext),
                        onResults = {
                            classifications = it
                        }
                    )
                }
                val controller = remember{
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(
                            ContextCompat.getMainExecutor(applicationContext),
                            analyzer
                        )
                    }
                }


                Box(
                    modifier = Modifier.fillMaxSize()
                ){
                    CameraPreview(
                        controller = controller,
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .align(Alignment.TopCenter)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        classifications.forEach {
                            Text(
                                text = it.name, modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center ,
                                fontSize = 16.sp ,
                                fontWeight = FontWeight.W500,
                                color = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    private fun hasPermission() = ContextCompat.checkSelfPermission(
        this , android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
