package app

import ports.input.util.DateSupplier

class DateSupplierTestImpl : DateSupplier {

    private val mockTimeMillis = System.currentTimeMillis()

    override fun currentTimeMillis() = mockTimeMillis
}
