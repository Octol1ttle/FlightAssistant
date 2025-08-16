package ru.octol1ttle.flightassistant.api.computer

abstract class ComputerQuery<Response> {
    internal val responses: ArrayList<Response> = ArrayList()

    fun respond(response: Response) {
        responses.add(response)
    }
}