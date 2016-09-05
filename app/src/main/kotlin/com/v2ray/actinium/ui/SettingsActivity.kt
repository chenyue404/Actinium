package com.v2ray.actinium.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import com.v2ray.actinium.R
import com.v2ray.actinium.service.V2RayService
import de.psdev.licensesdialog.LicensesDialogFragment
import org.jetbrains.anko.act
import org.jetbrains.anko.startActivity

class SettingsActivity : BaseActivity() {
    companion object {
        const val PREF_START_ON_BOOT = "pref_start_on_boot"
        const val PREF_PER_APP_PROXY = "pref_per_app_proxy"
        const val PREF_EDIT_BYPASS_LIST = "pref_edit_bypass_list"
        const val PREF_LICENSES = "pref_licenses"
        const val PREF_DONATE = "pref_donate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragment() {
        val blacklist by lazy { findPreference(PREF_PER_APP_PROXY) as CheckBoxPreference }
        val editBlacklist: Preference by lazy { findPreference(PREF_EDIT_BYPASS_LIST) }
        val licenses: Preference by lazy { findPreference(PREF_LICENSES) }
        val donate: Preference by lazy { findPreference(PREF_DONATE) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_settings)

            licenses.setOnPreferenceClickListener {
                val fragment = LicensesDialogFragment.Builder(act)
                        .setNotices(R.raw.licenses)
                        .setIncludeOwnLicense(false)
                        .build()
                fragment.show((act as AppCompatActivity).supportFragmentManager, null)
                true
            }

            donate.setOnPreferenceClickListener {
                val uri = Uri.parse("https://blockchain.info/address/191Ky8kA4BemiG3RfPiJjStEUqFcQ4DdAB")
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                true
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                V2RayService.checkStatusEvent {
                    if (!it) {
                        editBlacklist.setOnPreferenceClickListener {
                            startActivity<BlacklistActivity>()
                            true
                        }
                    } else {
                        blacklist.isEnabled = false
                        editBlacklist.isEnabled = false
                    }
                }
            } else {
                blacklist.summary = getString(R.string.summary_pref_per_app_proxy_pre_lollipop)
                blacklist.isEnabled = false
                editBlacklist.isEnabled = false
            }
        }
    }
}