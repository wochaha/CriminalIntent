package com.example.criminalintent.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.criminalintent.R
import kotlinx.android.synthetic.main.dialog_data.view.*
import java.util.*

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calender = Calendar.getInstance()
        calender.time = date
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_data,null)

        view.dialog_data_picker.init(year,month,day,null)

        return AlertDialog.Builder(activity)
            .setTitle(R.string.data_picker_title)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                val mYear = view.dialog_data_picker.year
                val mMonth = view.dialog_data_picker.month
                val mDay = view.dialog_data_picker.dayOfMonth
                val mDate = GregorianCalendar(mYear,mMonth,mDay).time
                sendResult(Activity.RESULT_OK,mDate)
            }
            .create()
    }

    companion object{
        private const val ARG_DATE = "date"
        const val EXTRA_DATE =
            "com.bignerdranch.android.criminalintent.date"

        fun newInstance(date: Date) : DatePickerFragment{
            val args = Bundle()
            args.putSerializable(ARG_DATE,date)

            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun sendResult(resultCode:Int, date: Date){
        if (targetFragment == null){
            return
        }else{
            val intent = Intent()
            intent.putExtra(EXTRA_DATE,date)
            targetFragment!!.onActivityResult(targetRequestCode,resultCode,intent)
        }
    }
}