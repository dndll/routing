package dev.hekate.routing.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Link(
  @JsonProperty("href")
  val href: String = "",
  @JsonProperty("rel")
  val rel: String? = "",
  @JsonProperty("title")
  val title: String? = ""
)
