package com.vaporware.nestreporterv2

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_info.*
import java.util.*


class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val date = Calendar.getInstance()
        date.set(year,month,day)
        when (this.tag) {
            "crawl_found" -> {
                viewModel.updateReport(viewModel.getCurrentReport()
                        .copy(dateCrawlFound = date.time))
            }
            else -> {}
        }
        Log.d("onDateSet", this.tag)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        Log.d("year",year.toString())
        return DatePickerDialog(activity,
                this, year, month, day)
    }
}
class EditWatcher(private val tag: Field): TextWatcher {
    override fun afterTextChanged(change: Editable?) {
        var updatedReport = viewModel.getCurrentReport()
        updatedReport = when (tag) {
            Field.OBSERVERS -> updatedReport.copy(observers = change.toString())
            Field.OTHER_SPECIES -> updatedReport.copy(speciesOther = change.toString())
        }
        viewModel.updateReport(updatedReport)
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

}

fun add55Days(date: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DAY_OF_MONTH,55)
    return cal.time
}
enum class Field {
    OBSERVERS, OTHER_SPECIES
}
