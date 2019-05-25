package com.example.criminalintent.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.criminalintent.R
import com.example.criminalintent.utils.getScaledBitmap
import kotlinx.android.synthetic.main.fragment_image.view.*

class ImageFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val path = arguments?.getSerializable(IMAGE_BITMAP) as String
        val bitmap = getScaledBitmap(path, context as Activity)

        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_image,null)
        view.fragment_image_view.setImageBitmap(bitmap)
        return AlertDialog.Builder(activity).setView(view).create()
    }

    companion object{
        const val IMAGE_BITMAP = "Image"

        fun newInstance(bitmapPath: String):ImageFragment{
            val args = Bundle()
            args.putSerializable(IMAGE_BITMAP,bitmapPath)

            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}