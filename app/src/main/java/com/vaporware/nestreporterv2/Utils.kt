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
import kotlinx.coroutines.experimental.launch
import java.util.*


class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val date = Calendar.getInstance()
        date.set(year,month,day)
        when (view) {
            button_crawl_found_date -> {
                val data = date.time
                viewModel.updateReport("infoTab","date_crawl_found" to data)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity,
                this, year, month, day)
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
