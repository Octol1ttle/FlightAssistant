package ru.octol1ttle.flightassistant.api.computer

abstract class ComputerQuery<Response>(private vararg val validators: Validator<Response>) {
    internal val responses: ArrayList<Response> = ArrayList()

    fun respond(response: Response) {
        for (validator in validators) {
            validator.validate(response)
        }
        responses.add(response)
    }

    fun interface Validator<Response> {
        fun validate(response: Response)
    }
}