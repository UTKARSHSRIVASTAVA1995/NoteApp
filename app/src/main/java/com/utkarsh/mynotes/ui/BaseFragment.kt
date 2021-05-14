package com.utkarsh.mynotes.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// Here we Created Base Fragment for Coroutines so that we can extend this class in other Fragments to remove Boiler plate codes
abstract class BaseFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}