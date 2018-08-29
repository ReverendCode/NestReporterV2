package com.vaporware.nestreporterv2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.coroutines.experimental.launch


lateinit var viewModel: ReportViewModel
var currentReportIndex: Int = 0
lateinit var reports: LiveData<List<Report>>
var highestReport = 0
var highestFalseCrawl = 0
class MainActivity : AppCompatActivity() {
    //keep your list of fragments to be generated here.
    val fragmentList = listOf(R.layout.fragment_info)
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        reports = viewModel.getAllReports()
        val prefs = getPreferences(Context.MODE_PRIVATE)
        highestReport = prefs.getInt("highestReport", 0)
        highestFalseCrawl = prefs.getInt("highestFalseCrawl", 0)
        currentReportIndex = prefs.getInt("currentReportIndex",0)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the
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
        if (reports.value == null || reports.value?.size == 0 ){
            viewModel.newReport()
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = getPreferences(Context.MODE_PRIVATE)?: return
        prefs.edit().putInt("currentReportIndex", currentReportIndex).apply()
        prefs.edit().putInt("highestReport", highestReport).apply()
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

    fun onSetCheckBox(view: View) {
        var updatedReport = updateReportFromUi()
        val box = view as SCheckBox
        if (box.isChecked) {
            when (view) {
                bool_nest_relocated, bool_nest_verified -> {
                    updatedReport = updatedReport.copy(nestType = NestType.Verified)
                    if (updatedReport.nestNumber == null) {
                        updatedReport = updatedReport.copy(nestNumber = ++highestReport)
                        getPreferences(Context.MODE_PRIVATE).edit().putInt("highestReport", highestReport).apply()
                    }

                }
                bool_false_crawl -> {
                    updatedReport = updatedReport.copy(nestType = NestType.FalseCrawl)
                    if (updatedReport.falseCrawlNumber == null) {
                        updatedReport = updatedReport.copy(falseCrawlNumber = ++highestFalseCrawl)
                        getPreferences(Context.MODE_PRIVATE).edit().putInt("highestFalseCrawl", highestFalseCrawl).apply()
                    }
                }
                bool_nest_not_verified -> updatedReport = updatedReport.copy(nestType = NestType.Unverified)
            }
        } else {
            when (view) {
                bool_false_crawl, bool_nest_verified -> updatedReport = updatedReport.copy(nestType = NestType.None)
            }
        }


        update(updatedReport)
    }

    private fun updateReportFromUi(): Report {
        return reports.value?.get(currentReportIndex)?.copy(
                abandonedEggCavities = bool_abandoned_egg_cavities.isChecked,
                abandonedBodyPits = bool_abandoned_body_pits.isChecked,
                noDigging = bool_no_digging.isChecked,
                nestType = when {
                    bool_possible_false_crawl.isChecked -> NestType.PossibleFalseCrawl
                    bool_nest_verified.isChecked -> NestType.Verified
                    bool_nest_not_verified.isChecked -> NestType.Unverified
                    bool_false_crawl.isChecked -> NestType.FalseCrawl
                    else -> NestType.None
                },
                nestRelocated = bool_nest_relocated.isChecked
        )!!
    }
    private fun update(report: Report) {
        launch {
            viewModel.updateReport(report)
        }
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
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            reports = viewModel.getAllReports()
            reports.observe(this, Observer {
                Log.d("observer","$it")
                if (it?.isNotEmpty() == true) setupInfoUI(it[currentReportIndex])
            })
        }

        fun setupInfoUI(report: Report) {
            bool_abandoned_body_pits.isChecked = report.abandonedBodyPits
            bool_abandoned_egg_cavities.isChecked = report.abandonedEggCavities
            bool_no_digging.isChecked = report.noDigging
            bool_nest_verified.isChecked = false
            bool_nest_not_verified.isChecked = false
            bool_false_crawl.isChecked = false
            bool_possible_false_crawl.isChecked = false
            when (report.nestType) {
                NestType.Verified -> bool_nest_verified.isChecked = true
                NestType.Unverified -> bool_nest_not_verified.isChecked = true
                NestType.FalseCrawl -> bool_false_crawl.isChecked = true
                NestType.PossibleFalseCrawl -> {
                    bool_possible_false_crawl.isChecked = true
                    bool_false_crawl.isChecked = true
                }
                NestType.None -> {
                    bool_nest_verified.isChecked = false
                    bool_nest_not_verified.isChecked = false
                    bool_false_crawl.isChecked = false
                    bool_possible_false_crawl.isChecked = false
                }
            }
            bool_nest_relocated.isChecked = report.nestRelocated
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
}
