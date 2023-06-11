package com.capstone.turuappmobile.ui.fragment.historyList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.adapter.HistorySleepAdapter
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentHistorySleepListBinding
import com.capstone.turuappmobile.ui.activity.detailAnalysist.DetailAnalysistActivity
import com.capstone.turuappmobile.ui.activity.detailHistorySleep.DetailHistoryActivity
//import com.capstone.turuappmobile.ui.activity.detailHistorySleep.DetailHistoryActivity
//import com.capstone.turuappmobile.ui.activity.historySleep.HistorySleepAdapter
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.ui.fragment.home.HomeFragmentViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class HistorySleepListFragment : Fragment() {

    private var _binding: FragmentHistorySleepListBinding? = null
    private val binding get() = _binding!!

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val historySleepListFragment by viewModels<HistorySleepListViewModel> {
        ViewModelFactoryUser.getInstance(requireActivity())
    }

    private var userUID = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistorySleepListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        historySleepListFragment.getUserSession.observe(viewLifecycleOwner) { userPreferencesModel ->
            userUID = userPreferencesModel.UID
            sleepViewModel.allSleepHistoryByUser(userUID, "")
                .observe(viewLifecycleOwner) { sleepHistory ->
                    val sleepHistoryFilter = sleepHistory.filter {
                        it.endTime != null && it.endTime - it.startTime > 4000 && it.realStartTime != null
                    }

                    if(sleepHistoryFilter.isEmpty()) {
                        showEmptyDataLayout(true)
                    }

                    setHistoryAdapter(sleepHistoryFilter)
                }

            sleepViewModel.allSleepQuality(userUID).observe(viewLifecycleOwner){
                val sleepQuality = it.last().sleepQuality
                binding.apply {
                    circularProgressIndicator.progress = sleepQuality.toInt()
                    circularTxt.text = requireActivity().getString(
                        R.string.result_quality,
                        sleepQuality.toInt()
                    )

                }
            }
        }

        binding.resetBtn.setOnClickListener {
            sleepViewModel.allSleepHistoryByUser(userUID, "")
                .observe(viewLifecycleOwner) { sleepHistory ->
                    val sleepHistoryFilter = sleepHistory.filter {
                        it.endTime != null && it.endTime - it.startTime > 4000 && it.realStartTime != null
                    }
                    setHistoryAdapter(sleepHistoryFilter)
                }
        }

        binding.filterBtn.setOnClickListener {
            showDatePickerStart(it)
        }

        binding.btnToDetailAnalysist.setOnClickListener {
            startActivity(Intent(requireActivity(), DetailAnalysistActivity::class.java))
        }


    }

    private fun setHistoryAdapter(sleepHistory: List<SleepTimeEntity>) {
        val adapter = HistorySleepAdapter(sleepHistory, requireActivity()) {
            val intent = Intent(requireActivity(), DetailHistoryActivity::class.java)
            intent.putExtra(DetailHistoryActivity.START_TIME, it.startTime)
            it.endTime?.let { it1 -> intent.putExtra(DetailHistoryActivity.END_TIME, it1) }
            startActivity(intent)
        }
        binding.rvHistorySleep.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistorySleep.adapter = adapter
    }

    private fun showEmptyDataLayout(isLoading: Boolean) {
        binding.layoutLoadingEmpty.layoutEmpty.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showDatePickerStart(view: View) {


        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .setSelection(
                androidx.core.util.Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        datePicker.show(requireActivity().supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { selection ->

            val epochSecondStart = (selection.first / 1000).toInt()
            val epochSecondEnd = (selection.second / 1000).toInt()

            sleepViewModel.allSleepHistoryByRangeDate(userUID, epochSecondStart, epochSecondEnd)
                .observe(viewLifecycleOwner) { sleepHistory ->
                    val sleepHistoryFilter = sleepHistory.filter {
                        it.endTime != null && it.endTime - it.startTime > 4000 && it.realStartTime != null
                    }
                    setHistoryAdapter(sleepHistoryFilter)
                }

        }


    }



}