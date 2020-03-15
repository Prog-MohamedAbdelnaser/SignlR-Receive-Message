package com.tabeesgl

import com.google.gson.annotations.SerializedName

data class EmployeeLocationEntity(@SerializedName("CreationDate") private var creationDate: String? = null,
                                  @SerializedName("Late") private var late: Double = 0.toDouble(),
                                  @SerializedName("Long") private var lng: Double = 0.toDouble(),
                                  @SerializedName("EmployeeCode") private var empId: String? = null
                                 )
