package com.capstone.turuappmobile.ui.fragment.historyList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.turuappmobile.adapter.HistorySleepAdapter
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.databinding.FragmentHistorySleepListBinding
import com.capstone.turuappmobile.ui.activity.detailHistorySleep.DetailHistoryActivity
//import com.capstone.turuappmobile.ui.activity.detailHistorySleep.DetailHistoryActivity
//import com.capstone.turuappmobile.ui.activity.historySleep.HistorySleepAdapter
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel


class HistorySleepListFragment : Fragment() {

    private var _binding : FragmentHistorySleepListBinding? = null
    private val binding get() = _binding!!

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistorySleepListBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sleepViewModel.allSleepHistory.observe(viewLifecycleOwner){ sleepHistory ->
            setHistoryAdapter(sleepHistory)
        }


    }

    private fun setHistoryAdapter(sleepHistory : List<SleepTimeEntity>){
        val adapter = HistorySleepAdapter(sleepHistory, requireActivity()){
            val intent = Intent(requireActivity(), DetailHistoryActivity::class.java)
            intent.putExtra(DetailHistoryActivity.START_TIME, it.startTime)
            it.endTime?.let { it1 -> intent.putExtra(DetailHistoryActivity.END_TIME, it1) }
            startActivity(intent)
        }
        binding.rvHistorySleep.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistorySleep.adapter = adapter
    }


}