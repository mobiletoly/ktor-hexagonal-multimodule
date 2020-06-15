package adapters.util

import ports.input.util.DateSupplier

class DateSupplierSystemTimeImpl : DateSupplier {

    override fun currentTimeMillis() = System.currentTimeMillis()
}
