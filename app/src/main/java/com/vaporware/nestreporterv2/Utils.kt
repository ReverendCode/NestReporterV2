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
import kotlinx.android.synthetic.main.fragment_info.*
import java.util.*


class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val date = Calendar.getInstance()
        date.set(year,month,day)
        when (this.tag) {
            "crawl_found" -> {
                viewModel.updateReport(viewModel.getCurrentReport().value!!
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
class EditWatcher(val tag: Field): TextWatcher {
    override fun afterTextChanged(change: Editable?) {
        when (tag) {
            Field.OBSERVERS -> viewModel.updateReport(reports.value?.get(currentReportIndex)!!.copy(
                    observers = change.toString()
            ))
        }
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

}

fun updateReportFromUi(view: MainActivity): Report {
    return viewModel.getCurrentReport().value?.copy(
            abandonedEggCavities = view.bool_abandoned_egg_cavities.isChecked,
            abandonedBodyPits = view.bool_abandoned_body_pits.isChecked,
            noDigging = view.bool_no_digging.isChecked,
            nestType = when {
                view. bool_possible_false_crawl.isChecked -> NestType.PossibleFalseCrawl
                view. bool_nest_verified.isChecked -> NestType.Verified
                view.bool_nest_not_verified.isChecked -> NestType.Unverified
                view.bool_false_crawl.isChecked -> NestType.FalseCrawl
                else -> NestType.None
            },
            nestRelocated = view.bool_nest_relocated.isChecked
    )!!
}

fun add55Days(date: Date): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DAY_OF_MONTH,55)
    return cal.time
}
enum class Field {
    OBSERVERS
}
