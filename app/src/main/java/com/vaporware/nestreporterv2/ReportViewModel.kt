package com.vaporware.nestreporterv2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn

class ReportViewModel(application: Application): AndroidViewModel(application) {
    private val email = GoogleSignIn.getLastSignedInAccount(application)?.email ?: "Default"
    private val fireStore = FireStoreRepository(email)
    private var appState = fireStore.getLiveState()
    private var currentId: String? = null

    fun getLiveNestId(): LiveData<String?> {

        return Transformations.map(appState) {
            Log.d("GetLiveNestId","performing transformation: $it")
            if (it == null) {
                val freshId = fireStore.createReport()
                val updatedState = NestAppState(currentId = freshId)
                Log.d("getLiveNestId","No state found, generating: $updatedState new reportID: $freshId")
                fireStore.putState(updatedState)
                currentId = freshId
                freshId
            } else {
                currentId = it.currentId
                it.currentId
            }
        }
    }

    fun getLiveReport(reportId: String): LiveData<Report> {
        val report = fireStore.getReport(reportId)
        currentId = reportId
        return report
        }

    fun deleteReport() {
        val next = fireStore.deleteReport(currentId!!)
        changeReport(next)
    }

    fun createAndSwitchToNest() {
        changeReport(fireStore.createReport())
    }

    private fun changeReport(next: String) {
        currentId = next
        fireStore.changeReport(next)
    }
    fun updateReport(location: String, vararg pairs: Pair<String, Any> ) {
        val freshId = getNestId()
        for (pair in pairs) {
            Log.d("updateReport", "Updating pair: $pair in $location at id: $freshId")
            fireStore.updateReport(freshId, "$location.${pair.first}" to pair.second)
        }
    }
    private fun getNestId(): String {
        if (currentId == null) {
            currentId = fireStore.getState().currentId
        }
        return currentId!!
    }
}