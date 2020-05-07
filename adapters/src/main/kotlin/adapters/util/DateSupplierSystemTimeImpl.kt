package adapters.util

import ports.required.util.DateSupplier

class DateSupplierSystemTimeImpl : DateSupplier {

    override fun currentTimeMillis() = System.currentTimeMillis()
}
