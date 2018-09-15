package com.vaporware.nestreporterv2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn

class RVM(private val app: Application): AndroidViewModel(app) {
    private val email = GoogleSignIn.getLastSignedInAccount(app)?.email ?: "Default"
    private val fireStore = FireStoreRepository(email)
    private var appState: MutableLiveData<NestAppState>? = null
    private var reportsList: LiveData<List<Report>> = fireStore.getAllReports()

    fun getLiveReport(reportId: String): LiveData<Report> {

        return Transformations.map(reportsList) {
            it.find { report ->
                report.infoTab.reportId == reportId
            }
        }
    }

    fun saveReport(report: Report) {
        fireStore.saveReport(report)
    }

    fun saveAppState() {
        val foo = PreferenceManager.getDefaultSharedPreferences(app.applicationContext).edit()
        val state = appState?.value
        foo.putString("currentId", state?.currentId)
        foo.putInt("highestNest", state?.highestNest?:0)
        foo.putInt("highestFC", state?.highestFalseCrawl?:0)
        foo.apply()
    }

    private fun loadAppState() {
        if (appState == null) {
            val foo = PreferenceManager.getDefaultSharedPreferences(app.applicationContext).all
            val updatedState = NestAppState(foo["currentId"] as String, foo["highestFC"] as Int, foo["highestNest"] as Int)
            val updatedLD = MutableLiveData<NestAppState>()
            updatedLD.value = updatedState
            appState = updatedLD
        }
    }

    fun getLiveCurrentId(): LiveData<String> {
        if (appState == null) loadAppState()
        return Transformations.map(appState!!) {
            it.currentId
        }
    }

    fun setCurrentId(reportId: String) {
        if (appState == null) loadAppState()
        appState?.value = appState?.value!!.copy(currentId = reportId)
    }
}




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
        val report = fireStore.getReportLiveData(reportId)
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

     fun changeReport(next: String) {
        currentId = next
        fireStore.changeReport(next)
    }

    fun updateReport(location: String, vararg pairs: Pair<String, Any> ) {
        val additionalPairs = applyUIUpdateRules(pairs)
        fireStore.updateReport(getNestId(), additionalPairs.map {
            val foo = it.first
            "$location.$foo" to it.second
        }.toTypedArray())
    }

    fun getNameAndIdList(): LiveData<List<Pair<String, String>>> {
        return fireStore.getNameAndIdList()
    }


    private fun applyUIUpdateRules(pairs: Array<out Pair<String, Any>>): Array<out Pair<String, Any>> {
        val newPairs = mutableListOf<Pair<String,Any>>()
        for (pair in pairs) {
            val foo = checkPair(pair)
            Log.d("applyUpUpdateRules","rule: $pair got $foo")
            newPairs.addAll(foo)
        }
        newPairs.addAll(pairs)
        Log.d("ApplyUpdateRules", "total changes to be applied: $newPairs")
        return newPairs.toTypedArray()
    }
    private fun checkPair(pair: Pair<String, Any>): Collection<Pair<String, Any>> {
        return ruleMap[pair] ?: listOf()
    }

   fun getReport(): Report {
       return fireStore.getReport(getNestId())
   }

    fun getNextValue(label: String): Int {
        val state = fireStore.getState()
        return if (label == "nest") {
            val next = state.highestNest +1
            fireStore.putState(state.copy(highestNest = next))
            next
        } else {
            val next = state.highestFalseCrawl +1
            fireStore.putState(state.copy(highestFalseCrawl = next))
            next
        }
    }


    )
    fun getNestId(): String {
        if (currentId == null) {
            currentId = fireStore.getState().currentId
        }
        return currentId!!
    }
}