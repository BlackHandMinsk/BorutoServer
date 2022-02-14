package com.example


import io.ktor.http.*

import kotlin.test.*
import io.ktor.server.testing.*

import io.ktor.application.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status())
                assertEquals(
                    expected = "Welcome to Boruto API",
                    actual = response.content)
            }
        }
    }
}