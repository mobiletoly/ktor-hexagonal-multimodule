package adapters.util

import ports.provided.util.DateSupplier

class DateSupplierImpl : DateSupplier {

    override fun currentTimeMillis() = System.currentTimeMillis()
}
