package com.example.criminalintent.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.criminalintent.R
import com.example.criminalintent.activity.CrimePagerActivity
import com.example.criminalintent.fragment.DatePickerFragment.Companion.EXTRA_DATE
import com.example.criminalintent.fragment.TimerPickerFragment.Companion.EXTRA_TIME
import com.example.criminalintent.model.Crime
import com.example.criminalintent.model.CrimeLab
import com.example.criminalintent.utils.getScaledBitmap
import kotlinx.android.synthetic.main.fragment_crime.*
import kotlinx.android.synthetic.main.fragment_crime.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//fragment没有实现toast方法
fun Fragment.toast(message:CharSequence,duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(activity,message,duration).show()
}


//有一个逻辑Bug,一旦开启该fragment就会立即插入一个空的Crime到数据库中
//是在传递id过来时调用了CrimeLab的addCrime方法导致的
//
class CrimeFragment : Fragment() {
    private lateinit var crime : Crime
    private lateinit var crimeID : UUID
    private lateinit var mPhotoFile:File

    private lateinit var dataButton:Button
    private lateinit var timeButton:Button
    private lateinit var suspectButton:Button

    private lateinit var mPhotoView:ImageView

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd E a", Locale.CHINA)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.CHINA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //开启工具栏菜单
        setHasOptionsMenu(true)
        crimeID= arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crime = activity?.let { CrimeLab.get(it).getCrime(crimeID) }!!
        mPhotoFile = CrimeLab.get(activity!!).getPhotoFile(crime)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime,container,false)
        val manager = fragmentManager
        dataButton = view.crime_data
        timeButton = view.crime_time
        suspectButton = view.crime_suspect
        mPhotoView = view.crime_photo

        //fragment中的资源ID必须使用视图(View)来引用，不然会报错
        view.crime_title.setText(crime.mTitle)
        view.crime_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                crime.mTitle = s.toString()
                Log.i("afterTextChanged", crime.mTitle)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("beforeTextChanged",s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.mTitle = s.toString()
            }

        })
        val date= dateFormat.format(crime.mDate)
        val time = timeFormat.format(crime.mDate)
        dataButton.setOnClickListener{
            val dataDialog = DatePickerFragment.newInstance(crime.mDate)

            //建立CrimeFragment和DatePickerFragment的联系
            dataDialog.setTargetFragment(this@CrimeFragment, REQUEST_DATE)
            if (manager != null) {
                dataDialog.show(manager, DIALOG_DATE)
            }
        }
        timeButton.setOnClickListener {
            val timeDialog = TimerPickerFragment.newInstance(crime.mDate)
            timeDialog.setTargetFragment(this@CrimeFragment, REQUEST_TIME)
            if (manager != null) {
                timeDialog.show(manager, DIALOG_TIME)
            }
        }
        dataButton.text = date
        timeButton.text = time
        view.crime_solved.isChecked = crime.mSolved
        view.crime_solved.setOnCheckedChangeListener { _, isChecked -> crime.mSolved = isChecked }
        activity?.let { CrimeLab.get(it).updateCrime(crime) }

        //发送信息
        view.crime_report.setOnClickListener {
            var i = Intent(ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(EXTRA_TEXT,getCrimeReport())
            i.putExtra(EXTRA_SUBJECT,getString(R.string.crime_report_subject))
            i = createChooser(i,getString(R.string.send_report))
            startActivity(i)
        }

        //获取联系人
        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        val packageManager:PackageManager = activity!!.packageManager

        suspectButton.setOnClickListener{
            startActivityForResult(pickContact, REQUEST_CONTACT)
        }

        if (crime.mSuspect != null){
            suspectButton.text = crime.mSuspect
        }

        //检查是否有联系人应用
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            suspectButton.isEnabled = false
        }

        //触发拍照
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canTakePhoto:Boolean = captureImage.resolveActivity(packageManager) != null

        view.crime_camera.isEnabled = canTakePhoto
        view.crime_camera.setOnClickListener {
            val uri:Uri = FileProvider.getUriForFile(activity!!,
                "com.example.criminalintent.fileprovider",
                mPhotoFile)
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri)
            val cameraActivities : List<ResolveInfo> = packageManager.queryIntentActivities(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY)

            for (ac in cameraActivities){
                activity!!.grantUriPermission(ac.activityInfo.packageName,
                    uri,FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivityForResult(captureImage, REQUEST_PHOTO)
        }

        updatePhotoView()

        mPhotoView.setOnClickListener {
            val imageFragment = ImageFragment.newInstance(mPhotoFile.path)
            imageFragment.setTargetFragment(this@CrimeFragment, DISPLAY_IMAGE)
            if (manager != null) {
                imageFragment.show(manager, DIALOG_IMAGE)
            }
        }
        return view
    }

    override fun onPause() {
        super.onPause()
        activity?.let { CrimeLab.get(it).updateCrime(crime) }
    }

    private fun getCrimeReport():String{
        val solvedString: String = if (crime.mSolved){
            getString(R.string.crime_report_solved)
        }else{
            getString(R.string.crime_report_unsolved)
        }

        val dateFormatLL = "EEE, MMM dd"
        val dateString = SimpleDateFormat(dateFormatLL, Locale.CHINA).format(crime.mDate)

        var suspect = crime.mSuspect
        if (suspect == null){
            suspect = getString(R.string.crime_report_no_suspect)
        }else{
            suspect = getString(R.string.crime_report_suspect, suspect)
        }

        return getString(R.string.crime_report, crime.mTitle, dateString, solvedString, suspect)
    }

    private fun updatePhotoView(){
        if (!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null)
        }else{
            val bitmap = getScaledBitmap(mPhotoFile.path, context as Activity)
            mPhotoView.setImageBitmap(bitmap)
        }
    }

    companion object{
        const val ARG_CRIME_ID = "crime_id"
        const val DIALOG_DATE = "DialogDate"
        const val DIALOG_TIME = "DialogTime"
        const val DIALOG_IMAGE = "dialogImage"
        const val DISPLAY_IMAGE = 4
        const val REQUEST_DATE = 0
        const val REQUEST_TIME = 1
        const val REQUEST_CONTACT = 2
        const val REQUEST_PHOTO = 3

        fun returnResult(context: Context,crimeID: UUID) {
            //可以在这里传递点击的item所对应的position
            val intent = Intent()
            intent.putExtra("position", CrimeLab.get(context).getCrimePosition(crimeID))
            (context as CrimePagerActivity).setResult(Activity.RESULT_OK,intent)
            context.finish()
        }

        //在托管的activity向fragment传递额外值
        fun newIntent(crimeID:UUID):CrimeFragment{
            val bd = Bundle()
            bd.putSerializable(ARG_CRIME_ID,crimeID)
            val fragment = CrimeFragment()
            fragment.arguments = bd
            return fragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.delete_crime -> {
//                val isExist = CrimeLab.get(activity!!).getCrimes().contains(crime)
//                Log.d("isExist:",isExist.toString())
//                if (isExist){
//                    CrimeLab.get(activity!!).deleteCrime(crime)
//                    activity!!.finish()
//                }else{
//                    for(cri in CrimeLab.get(activity!!).getCrimes()){
//                        Log.d("Crime title",cri.mTitle)
//                    }
//                    toast("This crime is not exist!")
//                }
                CrimeLab.get(activity!!).deleteCrime(crime)
                activity!!.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK){
            return
        }

        if (requestCode == REQUEST_DATE){
            val dateRe = data?.getSerializableExtra(EXTRA_DATE) as Date
            Log.d(DIALOG_DATE,dateRe.toString())
            crime.mDate = dateRe
            dataButton.text = dateFormat.format(dateRe)
        }else if(requestCode == REQUEST_TIME){
            val timeRe = data?.getSerializableExtra(EXTRA_TIME) as Date
            Log.d(DIALOG_TIME,timeRe.toString())
            crime.mDate = timeRe
            timeButton.text = timeFormat.format(timeRe)
        }else if (requestCode == REQUEST_CONTACT && data != null){
            val contactUri : Uri = data.data as Uri
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val c = activity!!.contentResolver.query(contactUri,queryFields,null,null,null)
            c.use { c ->
                if (c.count == 0){
                    return
                }
                c.moveToFirst()
                val suspect = c.getString(0)
                crime.mSuspect = suspect
                suspectButton.text = crime.mSuspect
            }
        }else if(requestCode == REQUEST_PHOTO){
            val uri:Uri = FileProvider.getUriForFile(activity!!,"com.example.criminalintent.fileprovider",mPhotoFile)
            activity!!.revokeUriPermission(uri, FLAG_GRANT_WRITE_URI_PERMISSION)
            updatePhotoView()
        }
    }
}