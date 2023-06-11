package com.capstone.turuappmobile.ui.fragment.challenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.adapter.ChallengeSleepAdapter
import com.capstone.turuappmobile.data.api.model.DataItem
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentChallengeBinding
import com.capstone.turuappmobile.ui.fragment.home.HomeFragmentViewModel
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.ui.activity.detailChallenge.DetailChallengeActivity
import com.capstone.turuappmobile.ui.animation.ShimmerAnimation

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    private var userToken = ""


    private val challengeFragmentViewModel by viewModels<ChallengeFragmentViewModel> {
        ViewModelFactoryUser.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        challengeFragmentViewModel.getUserSession.observe(viewLifecycleOwner){ preferencesModel ->

            userToken = preferencesModel.jwtToken

            Log.d("TokenTest", userToken )

            if(!challengeFragmentViewModel.getAlreadyCall()){
                challengeFragmentViewModel.allChallenge(userToken)
            }

            challengeFragmentViewModel.challengeResult.observe(viewLifecycleOwner){

                when(it){
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        challengeFragmentViewModel.alreadyCall()
                        setAllChallengeAdapter(it.data.data)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        toastMaker(it.error)
                    }
                }
            }
        }

    }

    private fun setAllChallengeAdapter(listChallenge: List<DataItem>){
        val adapter = ChallengeSleepAdapter(listChallenge, requireActivity()){
            val intent = Intent(requireActivity(), DetailChallengeActivity::class.java)
            intent.putExtra(DetailChallengeActivity.CHALLENGE_ID, it.id)
            startActivity(intent)
        }
        binding.rvChallengeList.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvChallengeList.setHasFixedSize(true)
        binding.rvChallengeList.adapter = adapter
    }
    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoadingChallenge.visibility =
            if (isLoading){
                binding.layoutLoadingChallenge.startShimmer()
                View.VISIBLE
            } else {
                binding.layoutLoadingChallenge.stopShimmer()
                View.GONE
            }
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

}