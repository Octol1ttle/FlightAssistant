package ru.octol1ttle.flightassistant.api.computer

abstract class ComputerQuery<Response> {
    internal val responses: ArrayList<Response> = ArrayList()

    open fun validateResponse(response: Response) {}

    fun respond(response: Response) {
        validateResponse(response)
        responses.add(response)
    }
}