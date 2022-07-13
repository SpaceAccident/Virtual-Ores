package space.accident.virtualores.api

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
    fun <T : ItemChooser> chooseOnWeight(items: List<T>): T? {
        var total = 0.0
        for (item in items) {
            total += item.maxWeight
            item.weight = total
        }
        val medium = Math.random() * total

        for (item in items) {
            if (item.weight > medium) {
                item.weight -= item.reduceCoefficient
                return item
            } else {
                item.weight += item.increaseCoefficient
            }
        }
        return null
    }
}