package pt.isec.ams.quizec.utils

object IdGeneratorQ {
    // Función para generar un código único con el formato "q" seguido de números aleatorios
    fun generateUniqueQuizCode(): String {
        val prefix = "q"  // Prefijo fijo
        val randomDigits = (1..13)  // Generar 13 dígitos aleatorios
            .map { ('0'..'9').random() }  // Selecciona aleatoriamente números entre 0 y 9
            .joinToString("")  // Une los números en una cadena

        return prefix + randomDigits  // Combina el prefijo con los números generados
    }
}