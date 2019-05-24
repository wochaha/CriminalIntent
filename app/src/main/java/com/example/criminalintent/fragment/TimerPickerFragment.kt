package com.example.criminalintent.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.criminalintent.R
import kotlinx.android.synthetic.main.dialog_time.view.*
import java.util.*

class TimerPickerFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_TIME) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date

        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var mMinute = calendar.get(Calendar.MINUTE)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_time, null)
        view.dialog_time_picker.setIs24HourView(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.dialog_time_picker.hour = hour
            view.dialog_time_picker.minute = mMinute
        }

        view.dialog_time_picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            hour = hourOfDay
            mMinute = minute
        }

        return AlertDialog.Builder(activity)
            .setTitle(R.string.time_picker_title)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                val mDate = GregorianCalendar(year, month, day, hour, mMinute).time
                sendResult(Activity.RESULT_OK,mDate)
            }
            .create()
    }


    companion object {
        private const val ARG_TIME = "time"
        const val EXTRA_TIME =
            "com.bignerdranch.android.criminalintent.time"

        fun newInstance(time: Date): TimerPickerFragment {
            val args = Bundle()
            args.putSerializable(ARG_TIME, time)

            val fragment = TimerPickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun sendResult(resultCode:Int, date: Date){
        if (targetFragment == null){
            return
        }else{
            val intent = Intent()
            intent.putExtra(EXTRA_TIME,date)
            targetFragment!!.onActivityResult(targetRequestCode,resultCode,intent)
        }
    }
}