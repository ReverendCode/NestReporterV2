package com.vaporware.nestreporterv2

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat

lateinit var viewModel: ReportViewModel
var aReport: Report? = null

class MainActivity : AppCompatActivity() {
    //keep your list of fragments to be generated here.
    val fragmentList = listOf(R.layout.fragment_info)
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        viewModel.getReportNameList().observe(this, Observer {

           val menu = nav_view.menu
            menu.clear()
            for (item in it!!) {
                menu.add(item.first).setOnMenuItemClickListener {foo ->
                    Log.d("menuClick","in callback for menu click item: $foo, reportID: ${item.second}")
                    viewModel.updateCurrentReport(item.second)
                    nav_drawer.closeDrawers()
                    true
                }
            }

        })

        fab.setOnClickListener {
            Toast.makeText(applicationContext,"Creating new Report",Toast.LENGTH_LONG).show()
            launch{
                viewModel.createNewReport()
            }
        }

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
        val picks = DatePickerFragment()
        picks.show(fragmentManager,button.hint as String)
    }

    fun onClickRadio(view: View) {
        val rad = view as RadioButton
        var updatedReport = aReport!!
        when (rad.text) {
            "Green" -> updatedReport = updatedReport.copy(species = Species.Green)
            "Loggerhead" -> updatedReport = updatedReport.copy(species = Species.Loggerhead)
            "Other" -> {
                updatedReport = updatedReport.copy(species = Species.Other)
            }
        }
        update(updatedReport)
    }
//      Todo: There ought to be a better way.
    fun onSetCheckBox(view: View) {
//        var updatedReport = updateReportFromUi(this)
    var updatedReport = aReport!!
    when (view) {
        bool_abandoned_body_pits -> updatedReport = updatedReport.copy(abandonedBodyPits = bool_abandoned_body_pits.isChecked)
        bool_abandoned_egg_cavities -> updatedReport = updatedReport.copy(abandonedEggCavities = bool_abandoned_egg_cavities.isChecked)
        bool_no_digging -> updatedReport = updatedReport.copy(noDigging = bool_no_digging.isChecked)
    }
        update(updateNestType(view, updatedReport))
    }


    fun handleMenuClick(menuItem: MenuItem) {
        /*
        * check current nest and false crawl numbers, decrement values that match current highest
        * (this is an attempt to keep phantom records to a minimum)
        * finally, delete current record
        * */

        Log.d("handleDelete", aReport.toString())
        launch{
            viewModel.deleteReport(aReport!!)

        }
    }

    private fun updateNestType(view: View, report: Report): Report {
        var updatedReport = report
        val box = view as SCheckBox
        if (box.isChecked) {//isTrue
            when (view) {
                bool_nest_verified -> {
                    updatedReport = updatedReport.copy(nestType = NestType.Verified)
                    if (updatedReport.nestNumber == null) {
                        updatedReport = updatedReport.copy(nestNumber = viewModel.incrementNest())
                    }
                }
                bool_nest_relocated -> {
                    updatedReport = updatedReport.copy(nestType = NestType.Verified, nestRelocated = true)
                }
                bool_false_crawl -> {
                    updatedReport = updatedReport.copy(nestType = NestType.FalseCrawl)
                    if (updatedReport.falseCrawlNumber == null) {
                        updatedReport = updatedReport.copy(falseCrawlNumber = viewModel.incrementFalseCrawl())
                    }
                }
                bool_possible_false_crawl -> {
                    updatedReport = updatedReport.copy(nestType = NestType.PossibleFalseCrawl)
                    if (updatedReport.falseCrawlNumber == null) {
                        updatedReport = updatedReport.copy(falseCrawlNumber = viewModel.incrementFalseCrawl())
                    }
                }
                bool_nest_not_verified -> updatedReport = updatedReport.copy(nestType = NestType.Unverified)
            }
        } else {
            when (view) {
                bool_false_crawl, bool_nest_verified -> updatedReport = updatedReport.copy(nestType = NestType.None)
            }
        }
        return updatedReport
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

            viewModel.getValues().observe(this, Observer {
                Log.d("observingValues", "values: $it")
                if (it != null) {
                    viewModel.getLiveReport().removeObservers(this)
                    viewModel.changeCurrentReport(it.current)
                    viewModel.getLiveReport().observe(this, Observer {foo ->
                        Log.d("observingReport",foo.toString())
                        if (foo != null) setupInfoUI(foo)
                        aReport = foo
                    })
                }
            })

            edit_observers.addTextChangedListener(EditWatcher(Field.OBSERVERS))
            edit_other_species.addTextChangedListener(EditWatcher(Field.OTHER_SPECIES))
        }

        @SuppressLint("SimpleDateFormat")
        private fun setupInfoUI(report: Report) {
            bool_abandoned_body_pits.isChecked = report.abandonedBodyPits
            bool_abandoned_egg_cavities.isChecked = report.abandonedEggCavities
            bool_no_digging.isChecked = report.noDigging
            bool_nest_verified.isChecked = false
            bool_nest_not_verified.isChecked = false
            bool_false_crawl.isChecked = false
            bool_possible_false_crawl.isChecked = false
            edit_nest_number.text = ""
            edit_false_crawl_number.text = ""
            edit_possible_false_crawl_number.text = ""
            when (report.nestType) {
                NestType.Verified -> {
                    bool_nest_verified.isChecked = true
                    edit_nest_number.text = report.nestNumber.toString()
                }

                NestType.Unverified -> {
                    bool_nest_not_verified.isChecked = true
                    edit_nest_number.text = report.nestNumber.toString()
                }

                NestType.FalseCrawl -> {
                    bool_false_crawl.isChecked = true

                    edit_false_crawl_number.text = report.falseCrawlNumber.toString()
                }
                NestType.PossibleFalseCrawl -> {
                    bool_possible_false_crawl.isChecked = true
                    bool_false_crawl.isChecked = true
                    edit_possible_false_crawl_number.text = report.falseCrawlNumber.toString()
                }
                NestType.None -> {
                    bool_nest_verified.isChecked = false
                    bool_nest_not_verified.isChecked = false
                    bool_false_crawl.isChecked = false
                    bool_possible_false_crawl.isChecked = false
                }
            }
            text_incubation_date.text = SimpleDateFormat("dd/MM/yyyy").format(add55Days(report.dateCrawlFound))
            bool_nest_relocated.isChecked = report.nestRelocated
            edit_other_species.isEnabled = report.species == Species.Other
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

