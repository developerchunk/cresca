package com.developerstring.nexpay

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.developerstring.nexpay.nfc.NFCManager
import com.developerstring.nexpay.nfc.PlatformNFCManager
import com.developerstring.nexpay.utils.initializeUtils
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val nfcManager: NFCManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize NFC Manager with this activity context
        (nfcManager as? PlatformNFCManager)?.initialize(this)

        // Initialize clipboard utils
        initializeUtils(this)

        setContent {
            App()
        }

        // Handle NFC intent if app was launched via NFC
        handleNfcIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNfcIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        (nfcManager as? PlatformNFCManager)?.onActivityResumed(this)
    }

    override fun onPause() {
        super.onPause()
        (nfcManager as? PlatformNFCManager)?.onActivityPaused(this)
    }

    private fun handleNfcIntent(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            (nfcManager as? PlatformNFCManager)?.handleNfcIntent(intent)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}