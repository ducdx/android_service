package com.example.cattracking.worker

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class CatSuitUpWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    companion object {
        const val INPUT_DATA_CAT_AGENT_ID = "inId"
        const val OUTPUT_DATA_CAT_AGEND_ID = "outId"
    }

    override fun doWork(): Result {
        val catAgentId = inputData.getString(INPUT_DATA_CAT_AGENT_ID)
        Thread.sleep(3000L)
        val outputData = Data.Builder().putString(OUTPUT_DATA_CAT_AGEND_ID, catAgentId).build()
        return Result.success(outputData)
    }
}