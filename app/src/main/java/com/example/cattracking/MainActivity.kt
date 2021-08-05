package com.example.cattracking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.cattracking.worker.CatFurGroomingWorker
import com.example.cattracking.worker.CatLitterBoxSittingWorker
import com.example.cattracking.worker.CatStretchingWorker
import com.example.cattracking.worker.CatSuitUpWorker

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SECRET_CAT_AGENT_ID = "scaId"
    }

    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkConstraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val catAgentId = "CatAgent1"
        val catStretchingRequest = OneTimeWorkRequest.Builder(CatStretchingWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getCatAgentIdInputData(CatStretchingWorker.INPUT_DATA_CAT_AGENT_ID,catAgentId))
            .build()
        val catFurGroomingRequest = OneTimeWorkRequest.Builder(CatFurGroomingWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getCatAgentIdInputData(CatFurGroomingWorker.INPUT_DATA_CAT_AGENT_ID,catAgentId))
            .build()
        val catLitterBoxRequest = OneTimeWorkRequest.Builder(CatLitterBoxSittingWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getCatAgentIdInputData(CatLitterBoxSittingWorker.INPUT_DATA_CAT_AGENT_ID,catAgentId))
            .build()
        val catSuitUpRequest = OneTimeWorkRequest.Builder(CatSuitUpWorker::class.java)
            .setConstraints(networkConstraints)
            .setInputData(getCatAgentIdInputData(CatSuitUpWorker.INPUT_DATA_CAT_AGENT_ID,catAgentId))
            .build()
        workManager.beginWith(catStretchingRequest)
            .then(catFurGroomingRequest)
            .then(catLitterBoxRequest)
            .then(catSuitUpRequest)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(catStretchingRequest.id).observe(this,{info ->
            if(info.state.isFinished){
                showResult("Agent done stretching")
            }
        })
        workManager.getWorkInfoByIdLiveData(catFurGroomingRequest.id).observe(this, {info->
            if(info.state.isFinished){
                showResult("Agent done Grooming its fur")
            }
        })
        workManager.getWorkInfoByIdLiveData(catLitterBoxRequest.id).observe(this,{info->
            if(info.state.isFinished){
                showResult("Agent done sitting in litter box")
            }
        })
        workManager.getWorkInfoByIdLiveData(catSuitUpRequest.id).observe(this, {info->
            if(info.state.isFinished){
                showResult("Agent done suiting up. Ready to go!!!")
                launchTrackingRoute()
            }
        })
    }

    private fun getCatAgentIdInputData(catAgentIdKey: String, catAgentIdValue: String) =
        Data.Builder().putString(catAgentIdKey,catAgentIdValue).build()

    private fun launchTrackingRoute() {
        RouteTrackingService.trackingCompletion.observe(this, { agentId ->
            showResult("Agent $agentId has arrived")
        })
        val serviceIntent = Intent(this, RouteTrackingService::class.java).apply {
            putExtra(EXTRA_SECRET_CAT_AGENT_ID, "007")
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun showResult(mes: String) {
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show()
    }
}