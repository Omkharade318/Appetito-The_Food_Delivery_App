package com.example.appetito.ui.feature.menu.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appetito.data.FoodApi
import com.example.appetito.data.FoodHubSession
import com.example.appetito.data.models.CustomizationGroup
import com.example.appetito.data.models.CustomizationOption
import com.example.appetito.data.models.FoodItem
import com.example.appetito.data.remote.ApiResponse
import com.example.appetito.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class AddMenuItemViewModel @Inject constructor(
    val foodApi: FoodApi,
    val session: FoodHubSession,
    @ApplicationContext val context: Context
) :
    ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl = _imageUrl.asStateFlow()


    private val _addMenuItemState = MutableStateFlow<AddMenuItemState>(AddMenuItemState.Idle)
    val addMenuItemState = _addMenuItemState.asStateFlow()

    private val _addMenuItemEvent = MutableSharedFlow<AddMenuItemEvent>()
    val addMenuItemEvent = _addMenuItemEvent.asSharedFlow()


    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onPriceChange(price: String) {
        _price.value = price
    }

    fun onImageUrlChange(imageUrl: Uri?) {
        _imageUrl.value = imageUrl
    }

    private val _imageUrlString = MutableStateFlow("")
    val imageUrlString = _imageUrlString.asStateFlow()

    fun onImageUrlStringChange(newUrl: String) {
        _imageUrlString.value = newUrl
    }

    private val _customizationGroups = MutableStateFlow<List<CustomizationGroup>>(emptyList())
    val customizationGroups = _customizationGroups.asStateFlow()

    fun addCustomizationGroup() {
        _customizationGroups.value += CustomizationGroup(name = "", options = emptyList())
    }

    fun removeCustomizationGroup(index: Int) {
        val list = _customizationGroups.value.toMutableList()
        list.removeAt(index)
        _customizationGroups.value = list
    }

    fun updateCustomizationGroup(index: Int, group: CustomizationGroup) {
        val list = _customizationGroups.value.toMutableList()
        list[index] = group
        _customizationGroups.value = list
    }

    fun addOptionToGroup(groupIndex: Int) {
        val groups = _customizationGroups.value.toMutableList()
        val group = groups[groupIndex]
        val options = group.options.toMutableList()
        options.add(CustomizationOption(name = "", price = 0.0))
        groups[groupIndex] = group.copy(options = options)
        _customizationGroups.value = groups
    }

    fun removeOptionFromGroup(groupIndex: Int, optionIndex: Int) {
        val groups = _customizationGroups.value.toMutableList()
        val group = groups[groupIndex]
        val options = group.options.toMutableList()
        options.removeAt(optionIndex)
        groups[groupIndex] = group.copy(options = options)
        _customizationGroups.value = groups
    }

    fun updateOptionInGroup(groupIndex: Int, optionIndex: Int, option: CustomizationOption) {
        val groups = _customizationGroups.value.toMutableList()
        val group = groups[groupIndex]
        val options = group.options.toMutableList()
        options[optionIndex] = option
        groups[groupIndex] = group.copy(options = options)
        _customizationGroups.value = groups
    }

    fun addMenuItem() {
        val name = name.value
        val description = description.value
        val price = price.value.toDoubleOrNull() ?: 0.0
        val restaurantId = session.getRestaurantId() ?: ""
        val imageUrlValue = imageUrlString.value

        if (name.isEmpty() || description.isEmpty() || price == 0.0 || imageUrlValue.isBlank()) {
            _addMenuItemEvent.tryEmit(AddMenuItemEvent.ShowErrorMessage("Please fill all fields"))
            return
        }
        viewModelScope.launch {
            _addMenuItemState.value = AddMenuItemState.Loading
            
            val response = safeApiCall {
                foodApi.addRestaurantMenu(
                    restaurantId,
                    FoodItem(
                        name = name,
                        description = description,
                        price = price,
                        imageUrl = imageUrlValue,
                        restaurantId = restaurantId,
                        customizations = customizationGroups.value
                    )
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    _addMenuItemState.value = AddMenuItemState.Success("Item added successfully")
                    _addMenuItemEvent.emit(AddMenuItemEvent.GoBack)
                }

                is ApiResponse.Error -> {
                    _addMenuItemState.value = AddMenuItemState.Error(response.message)
                }

                is ApiResponse.Exception -> {
                    _addMenuItemState.value = AddMenuItemState.Error("Network Error")
                }
            }
        }
    }

    suspend fun uploadImage(imageUri: Uri): String? {
        val file = fileFromUri(imageUri)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
        val response = safeApiCall { foodApi.uploadImage(multipartBody) }
        when (response) {
            is ApiResponse.Success -> {
                return response.data.url
            }

            else -> {
                return null
            }
        }
    }

    private fun fileFromUri(imageUri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val file = File.createTempFile(
            "temp-${System.currentTimeMillis()}-foodhub",
            "jpg",
            context.cacheDir
        )
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun onImageClicked() {
        viewModelScope.launch {
            _addMenuItemEvent.emit(AddMenuItemEvent.AddNewImage)
        }
    }


    sealed class AddMenuItemState {
        object Idle : AddMenuItemState()
        object Loading : AddMenuItemState()
        data class Success(val message: String) : AddMenuItemState()
        data class Error(val message: String) : AddMenuItemState()
    }

    sealed class AddMenuItemEvent {
        data class ShowErrorMessage(val message: String) : AddMenuItemEvent()
        object AddNewImage : AddMenuItemEvent()
        object GoBack : AddMenuItemEvent()
    }

}