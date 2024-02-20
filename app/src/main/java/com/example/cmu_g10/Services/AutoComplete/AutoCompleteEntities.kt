package com.example.cmu_g10.Services.AutoComplete

/**
 * Data classes for the autocomplete API response.
 */
data class AutocompleteResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: FeatureProperties
)

data class FeatureProperties(
    val formatted: String
)