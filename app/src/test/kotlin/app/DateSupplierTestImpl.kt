package app

import ports.required.util.DateSupplier

class DateSupplierTestImpl : DateSupplier {

    private val mockTimeMillis = System.currentTimeMillis()

    override fun currentTimeMillis() = mockTimeMillis
}
