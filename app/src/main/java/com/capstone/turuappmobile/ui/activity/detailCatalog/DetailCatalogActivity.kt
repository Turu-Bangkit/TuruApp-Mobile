package com.capstone.turuappmobile.ui.activity.detailCatalog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.capstone.turuappmobile.data.api.model.DataDetailCatalog
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityDetailCatalogBinding
import com.capstone.turuappmobile.utils.loadImage

class DetailCatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCatalogBinding

    private val detailCatalogViewModel by viewModels<DetailCatalogViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCatalog =  intent.getIntExtra(CATALOG_ID, 0).toString()

        detailCatalogViewModel.getUserSession.observe(this){ user ->


            detailCatalogViewModel.detailCatalog(user.jwtToken, idCatalog)


            binding.btnCatalog.setOnClickListener {
                detailCatalogViewModel.exchangePoint(user.jwtToken, user.UID, idCatalog)
            }
        }

        detailCatalogViewModel.detailCatalogResult.observe(this){
            when(it){
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    setAllData(it.data.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        binding.btnBackDetailCatalog.setOnClickListener {
            finish()
        }
    }

    private fun setAllData(data: DataDetailCatalog){
        binding.apply {
            imageCatalogDetail.loadImage(data.img)
            catalogNameTxtDetail.text = data.name
            descriptionDetailCatalogTxt.text = data.name
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.shimmerLayoutDetailCatalog.visibility =
            if (isLoading){
                binding.shimmerLayoutDetailCatalog.startShimmer()
                View.VISIBLE
            } else {
                binding.shimmerLayoutDetailCatalog.stopShimmer()
                View.GONE
            }
    }
    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object{
        const val CATALOG_ID = "catalog_id"
    }
}