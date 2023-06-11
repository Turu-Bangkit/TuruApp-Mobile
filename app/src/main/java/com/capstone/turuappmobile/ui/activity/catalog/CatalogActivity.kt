package com.capstone.turuappmobile.ui.activity.catalog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.adapter.CatalogAdapter
import com.capstone.turuappmobile.adapter.ChallengeSleepAdapter
import com.capstone.turuappmobile.data.api.model.DataCatalog
import com.capstone.turuappmobile.data.api.model.DataItem
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityCatalogBinding
import com.capstone.turuappmobile.ui.activity.detailCatalog.DetailCatalogActivity
import com.capstone.turuappmobile.ui.activity.detailChallenge.DetailChallengeActivity
import com.capstone.turuappmobile.ui.fragment.challenge.ChallengeFragmentViewModel

class CatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogBinding

    private var userToken = ""

    private val catalogViewModel by viewModels<CatalogViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        catalogViewModel.getUserSession.observe(this){ preferencesModel ->

            userToken = preferencesModel.jwtToken

            catalogViewModel.allcatalog(userToken)


        }

        catalogViewModel.catalogResult.observe(this){

            when(it){
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    setAllChallengeAdapter(it.data.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

    }

    private fun setAllChallengeAdapter(listChallenge: List<DataCatalog>){
        val adapter = CatalogAdapter(listChallenge, this){
            val intent = Intent(this, DetailCatalogActivity::class.java)
            intent.putExtra(DetailChallengeActivity.CHALLENGE_ID, it.id)
            startActivity(intent)
        }
        binding.rvCatalog.layoutManager = GridLayoutManager(this, 2)
        binding.rvCatalog.setHasFixedSize(true)
        binding.rvCatalog.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {

    }

    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}