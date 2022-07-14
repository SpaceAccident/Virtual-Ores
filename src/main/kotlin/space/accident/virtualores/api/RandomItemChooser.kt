package space.accident.virtualores.api

import java.util.*

/**
 * Random Chooser
 */
object RandomItemChooser {

    /**
     * Implementation this for choose
     */
    interface ItemChooser {

        /**
         * Current weight
         */
        var weight: Double

        /**
         * Max weight
         */
        val maxWeight: Double

        /**
         * Coefficient of reduce weight
         */
        val reduceCoefficient: Double

        /**
         * Coefficient of increase weight
         */
        val increaseCoefficient: Double
    }

    /**
     * Choose on weight
     *
     * @param items list of items for choose
     */
    fun <T : ItemChooser> chooseOnWeight(items: List<T>, seed: Long): T? {
        var total = 0.0
        for (item in items) {
            total += item.maxWeight
            item.weight = total
        }
        val random = if (seed == 0L) Random() else Random(seed)
        val medium = random.nextDouble() * total

        for (item in items) {
            if (item.weight > medium) {
                item.weight -= item.reduceCoefficient
                if (item.weight <= 0.0) item.weight = 0.0
                return item
            } else {
                item.weight += item.increaseCoefficient
                if (item.weight >= item.maxWeight) item.weight = item.maxWeight
            }
        }
        return null
    }
}