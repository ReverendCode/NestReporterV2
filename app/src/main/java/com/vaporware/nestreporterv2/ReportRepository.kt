package com.vaporware.nestreporterv2

import android.arch.lifecycle.LiveData
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.kiwimob.firestore.livedata.livedata
import kotlinx.coroutines.experimental.async


class FireStoreRepository {
    private val fireDb = FirebaseFirestore.getInstance()
            .collection("Sections")
            .document("Section 1")
            .collection("Reports")
    private var vals = fireDb.document("Values")

    fun getReport(id: String): LiveData<Report> {
        val document = fireDb.document(id)
        Log.d("fireReport",document.toString())
        return document.livedata(Report::class.java)
    }

    suspend fun getStaticReport(id: String): Report {
        val documents = fireDb.document(id)
        if (documents.get().isSuccessful) {
            Log.d("staticReport","Success: ${documents.get().result.toObject(Report::class.java)}")
        }
        val deferred = async{
             Tasks.await(documents.get()).toObject(Report::class.java)
        }

            return deferred.await() ?: Report()
    }


    fun getAllReports(): LiveData<List<Report>> {
        val foo = fireDb.livedata(Report::class.java)
        Log.d("getAllReports", foo.value.toString())
        return foo
    }

    fun updateReport(report: Report) {
        var updatedReport = report
        Log.d("updatedReport",updatedReport.toString())
        if (updatedReport.reportId == "0") {
            val ref = fireDb.document()
            updatedReport = updatedReport.copy(reportId = ref.id)
            ref.set(updatedReport)
        } else fireDb.document(updatedReport.reportId).set(updatedReport)
        vals.update("current", updatedReport.reportId)
    }

    fun deleteReport(report: Report) {
        if (report.reportId != "") fireDb.document(report.reportId.toString()).delete()
    }

    fun getState(): Values {
        val document = vals
        var returnValues: Values? = null
        document.get().addOnCompleteListener {
            returnValues = if (it.isSuccessful) {
                val foo = it.result
                if (foo.exists()) {
                    foo.toObject(Values::class.java)!!
                } else Values()
            } else Values()
        }
        return returnValues ?:Values()
    }

    fun getLiveState(): LiveData<Values> {
        return fireDb.document("Values").livedata(Values::class.java)
    }

    fun putState(state: Values) {
        fireDb.document("Values").set(state)
    }
}