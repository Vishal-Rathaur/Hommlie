package com.hommlie.user.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MeasurementViewModel : ViewModel() {

    // LiveData for width
    private val _width = MutableLiveData<Float>()
    val width: LiveData<Float> get() = _width

    // LiveData for length
    private val _length = MutableLiveData<Float>()
    val length: LiveData<Float> get() = _length

    // LiveData for square footage (sqft)
    private val _sqft = MutableLiveData<Float>()
    val sqft: LiveData<Float> get() = _sqft

    // Update width
    fun setWidth(value: Float) {
        _width.value = value
        calculateSqft()
    }

    // Update length
    fun setLength(value: Float) {
        _length.value = value
        calculateSqft()
    }

    // Calculate square footage
    private fun calculateSqft() {
        val widthValue = _width.value ?: 0f
        val lengthValue = _length.value ?: 0f
        _sqft.value = widthValue * lengthValue
    }
}
