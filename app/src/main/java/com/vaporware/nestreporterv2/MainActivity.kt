package com.vaporware.nestreporterv2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.design.widget.TabLayout
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
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.coroutines.experimental.launch

lateinit var viewModel: ReportViewModel

class MainActivity : AppCompatActivity() {
    val fragmentList = listOf(R.layout.fragment_info)
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val debugOauthClientId = "625582312057-mft8ce438s5dt5rfujufd7dlsavt5bk9.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        viewModel = ViewModelProviders.of(this).get(ReportViewModel::class.java)
        openReporter(account)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9999) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignIn(task)
        }
    }

    private fun handleSignIn(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            openReporter(account)
        } catch (ex: ApiException) {
            openReporter(null)
        }
    }

    private fun openReporter(account: GoogleSignInAccount?) {

        if (account == null) {
            setContentView(R.layout.activity_login)
            sign_in_button.setOnClickListener {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(debugOauthClientId)
                        .requestEmail()
                        .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                val mSignInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(mSignInIntent,9999)
            }
        } else {
            setContentView(R.layout.activity_main)
            setSupportActionBar(toolbar)
            // Create the adapter that will return a fragment for each of the
            // primary sections of the activity.
            mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
            // Set up the ViewPager with the sections adapter.
            container.adapter = mSectionsPagerAdapter
            container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
            tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))


            fab.setOnClickListener {
                Toast.makeText(applicationContext, "Creating new Report", Toast.LENGTH_LONG).show()
                viewModel.createAndSwitchToNest()
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

        if (id == R.id.action_delete) {
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
        when (view) {
            radio_loggerhead -> viewModel.updateReport("infoTab","species" to Species.Loggerhead.name)
            radio_green -> viewModel.updateReport("infoTab","species" to Species.Green.name)
            radio_other -> {
                viewModel.updateReport("infoTab","species" to Species.Other.name,
                        "speciesOther" to edit_other_species.text.toString())
            }
        }
    }

    fun onSetCheckBox(view: View) {
        val item = view as SCheckBox
        Log.d("onSetCheckBox","updating values: ${item.column}")
        viewModel.updateReport("infoTab",item.column.toString() to item.isChecked)
    }


    fun handleMenuClick(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.action_delete) {
            viewModel.deleteReport()
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
            Log.d("onActivityCreated", "attempting to observe LiveNestId")
            viewModel.getLiveNestId().observe(this, Observer {
                Log.d("in observer","trying to observe: $it")
                if (it != null) {
                    Log.d("Observing Id", it)
                    viewModel.getLiveReport(it).observe(this, Observer { report ->
                        if (report != null) {
                            Log.d("Observing report", report.toString())
                            updateUi(report)
                        }
                    })
                }
            })
            edit_observers.addTextChangedListener(EditWatcher("observers"))
            edit_other_species.addTextChangedListener(EditWatcher("otherSpecies"))
        }

         fun updateUi(report: Report) {
            val info = report.infoTab
            bool_abandoned_body_pits.isChecked = info.abandonedBodyPits
            bool_abandoned_egg_cavities.isChecked = info.abandonedEggCavities
            bool_no_digging.isChecked = info.noDigging
            bool_false_crawl.isChecked = info.falseCrawl
            bool_nest_verified.isChecked = info.verified
            bool_nest_not_verified.isChecked = info.notVerified
            bool_possible_false_crawl.isChecked = info.possibleFalseCrawl
            bool_nest_relocated.isChecked = info.nestRelocated
            edit_possible_false_crawl_number.text = ""
            edit_false_crawl_number.text = ""
            edit_nest_number.text = ""
            if (info.verified || info.notVerified) edit_nest_number.text = info.nestNumber.toString()
                else if (info.falseCrawl) edit_false_crawl_number.text = info.falseCrawlNumber.toString()
            else if (info.possibleFalseCrawl) edit_possible_false_crawl_number.text = info.falseCrawlNumber.toString()
            text_incubation_date.text = add55Days(info.dateCrawlFound).toString()
            edit_other_species.setText(info.speciesOther)

            setText(edit_observers, info.observers)
            setText(edit_other_species, info.speciesOther)
            when (info.species) {

                Species.Green -> radio_green.isChecked = true
                Species.Loggerhead -> radio_loggerhead.isChecked = true
                Species.Other -> {
                    radio_other.isChecked = true
                    edit_other_species.isEnabled = true
                }
                Species.None -> {}
            }

        }
        private fun setText(textField: EditText, info: String) {
                textField.setText(info)
                textField.setSelection(textField.length())
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

