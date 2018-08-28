package com.vaporware.nestreporterv2

import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.content.Context
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : AppCompatActivity() {
    //keep your list of fragments to be generated here.
    val fragmentList = listOf(R.layout.fragment_info)


    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    lateinit var viewModel: ReportViewModel
    lateinit var report: Report

    override fun onCreate(savedInstanceState: Bundle?) {
        val db = Room.databaseBuilder(this,ReportDatabase::class.java,"demo").build()
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        val lastReportId = getPreferences(Context.MODE_PRIVATE).getInt("report",-1)

        if (lastReportId != -1) {
            report = db.ReportDao.query(lastReportId)
        } else {
            if (db.ReportDao.reportCount() > 0) {
                report = db.ReportDao.query(0)
            } else {
                report = report.copy(reportId = 0)
                db.ReportDao.create(report)
            }
        }
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onStop() {
        super.onStop()
        val prefs = getPreferences(Context.MODE_PRIVATE)?: return
        prefs.edit().putInt("report", report.reportId).apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun onClickSetDate(view: View) {
        val button = view as Button
        TODO("Load a datePicker here")
    }

    fun onTextChange(view: View) {
        TODO("maybe need to import edCoyne's changeHandlerEditText")
    }

    fun onSetRadioButton(view: View) {

        val parent = view.parent as SRadioGroup
    }
    fun onSetCheckBox(view: View) {
        val checkBox = view as SCheckBox

        TODO("needs to set column of checkBox.id to checkBox.isChecked")
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


        override fun getItem(position: Int): Fragment {
            //use position to look up the appropriate fragment to load.
            return InfoFragment.newFragment(fragmentList[position])
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }

    class InfoFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(arguments.getInt("fragmentId"), container, false)
//            return super.onCreateView(inflater, container, savedInstanceState)

        }

        companion object {
            fun newFragment(fragmentId: Int): InfoFragment {
                val fragment = InfoFragment()
                val args = Bundle()
                args.putInt("fragmentId",fragmentId)
                fragment.arguments = args
                return fragment
            }
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
