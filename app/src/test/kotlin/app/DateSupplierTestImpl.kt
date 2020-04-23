package app

import ports.provided.util.DateSupplier

class DateSupplierTestImpl : DateSupplier {

    private val mockTimeMillis = System.currentTimeMillis()

    override fun currentTimeMillis() = mockTimeMillis
}
