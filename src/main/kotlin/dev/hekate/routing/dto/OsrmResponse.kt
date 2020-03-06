package dev.hekate.routing.dto


import com.fasterxml.jackson.annotation.JsonProperty

data class OsrmResponse(
  @JsonProperty("routes")
    val routes: List<Route> = listOf(),
  @JsonProperty("waypoints")
    val waypoints: List<Waypoint> = listOf(),
  @JsonProperty("code")
    val code: String = ""
) {
    data class Route(
      @JsonProperty("geometry")
        val geometry: String = "",
      @JsonProperty("legs")
        val legs: List<Leg> = listOf(),
      @JsonProperty("weight_name")
        val weightName: String = "",
      @JsonProperty("weight")
        val weight: Double = 0.0,
      @JsonProperty("duration")
        val duration: Double = 0.0,
      @JsonProperty("distance")
        val distance: Double = 0.0
    ) {

        data class Leg(
          @JsonProperty("summary")
            val summary: String = "",
          @JsonProperty("weight")
            val weight: Double = 0.0,
          @JsonProperty("duration")
            val duration: Double = 0.0,
          @JsonProperty("steps")
            val steps: List<Step> = listOf(),
          @JsonProperty("distance")
            val distance: Double = 0.0
        ) {

            data class Step(
              @JsonProperty("intersections")
                val intersections: List<Intersection> = listOf(),
              @JsonProperty("driving_side")
                val drivingSide: String = "",
              @JsonProperty("geometry")
                val geometry: String = "",
              @JsonProperty("mode")
                val mode: String = "",
              @JsonProperty("maneuver")
                val maneuver: Maneuver = Maneuver(),
              @JsonProperty("weight")
                val weight: Int = 0,
              @JsonProperty("duration")
                val duration: Int = 0,
              @JsonProperty("name")
                val name: String = "",
              @JsonProperty("distance")
                val distance: Int = 0,
              @JsonProperty("ref")
                val ref: String = "",
              @JsonProperty("rotary_name")
                val rotaryName: String = ""
            ) {

                data class Intersection(
                    @JsonProperty("in")
                    val inX: Int = 0,
                    @JsonProperty("out")
                    val out: Int = 0,
                    @JsonProperty("entry")
                    val entry: List<Boolean> = listOf(),
                    @JsonProperty("bearings")
                    val bearings: List<Int> = listOf(),
                    @JsonProperty("location")
                    val location: List<Double> = listOf(),
                    @JsonProperty("classes")
                    val classes: List<Any> = listOf()
                )


                data class Maneuver(
                    @JsonProperty("bearing_after")
                    val bearingAfter: Int = 0,
                    @JsonProperty("bearing_before")
                    val bearingBefore: Int = 0,
                    @JsonProperty("location")
                    val location: List<Double> = listOf(),
                    @JsonProperty("type")
                    val type: String = "",
                    @JsonProperty("modifier")
                    val modifier: String = "",
                    @JsonProperty("exit")
                    val exit: String = ""
                )
            }
        }
    }


    data class Waypoint(
        @JsonProperty("hint")
        val hint: String = "",
        @JsonProperty("distance")
        val distance: Double = 0.0,
        @JsonProperty("name")
        val name: String = "",
        @JsonProperty("location")
        val location: List<Double> = listOf()
    )
}
