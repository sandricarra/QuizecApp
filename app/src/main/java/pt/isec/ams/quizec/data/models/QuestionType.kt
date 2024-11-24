package pt.isec.ams.quizec.data.models

enum class QuestionType(val label: String) {
    P01("Yes/No, True/False"),
    P02("Multiple choice (single correct)"),
    P03("Multiple choice (multiple correct)"),
    P04("Matching"),
    P05("Ordering"),
    P06("Fill-in-the-blank"),
    P07("Association"),
    P08("Fill-in missing words");

    override fun toString(): String = label
}

