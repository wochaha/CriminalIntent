package com.example.criminalintent.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity

fun getScaledBitmap(path:String,destWidth:Int,destHeight:Int):Bitmap{
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path,options)

    val srcWidth:Float = options.outWidth.toFloat()
    val srcHeight:Float = options.outHeight.toFloat()

    var inSimpleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth){
        val heightScale:Float = (srcHeight/destHeight)
        val widthScale:Float = (srcWidth/destWidth)

        inSimpleSize = Math.round(if (heightScale > widthScale) heightScale else widthScale)
    }
    val op = BitmapFactory.Options()
    op.inSampleSize = inSimpleSize
    return BitmapFactory.decodeFile(path,op)
}

fun getScaledBitmap(path:String,activity:Activity):Bitmap{
    val point:Point = Point()
    activity.windowManager.defaultDisplay.getSize(point)
    return getScaledBitmap(path,point.x,point.y)
}