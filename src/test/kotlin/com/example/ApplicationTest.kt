package com.example


import com.example.models.ApiResponse
import com.example.repository.HeroRepository
import com.example.repository.NEXT_PAGE_KEY
import com.example.repository.PREVIOUS_PAGE_KEY
import io.ktor.http.*

import kotlin.test.*
import io.ktor.server.testing.*

import io.ktor.application.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

class ApplicationTest {

    private val heroRepository:HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun accessRootEndpoint_AssertCorrectInformation() {
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


    @ExperimentalSerializationApi
    @Test
    fun accessAllHeroesEndpointQueryAllPages_AssertCorrectInformation(){
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )
            pages.forEach { page->
                handleRequest(HttpMethod.Get, "/boruto/heroes?page=$page").apply {
                    assertEquals(
                        expected = HttpStatusCode.OK,
                        actual = response.status()
                    )
                    val expected = ApiResponse(
                        success = true,
                        message = "ok",
                        prevPage = calculatePage(page = page)["prevPage"],
                        nextPage = calculatePage(page = page)["nextPage"],
                        heroes = heroes[page-1]
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    assertEquals(
                        expected = expected,
                        actual = actual
                    )
                }
            }
            }
        }

    @ExperimentalSerializationApi
    @Test
    fun accessAllHeroesEndpointQueryNonExistingPageNumber_AssertError(){
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=6").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Heroes not found.",

                )

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())


                assertEquals(
                    expected = expected,
                    actual = actual
                )
        }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun accessAllHeroesEndpointQueryInvalidPageNumber_AssertError(){
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=invalid").apply {
                assertEquals(
                    expected = HttpStatusCode.BadRequest,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Only numbers Allowed.",

                    )

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())


                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun accessSearchHeroesEndpointQueryHeroName_AssertSingleHeroResult(){
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sas").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(
                    expected = 1,
                    actual = actual
                )
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun accessSearchHeroesEndpointQueryHeroName_AssertMultipleeHeroResult(){
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sa").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(
                    expected = 3,
                    actual = actual
                )
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun accessSearchHeroesEndpointQueryEmptyText_AssertEmptyListAsAResult(){
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes
                assertEquals(
                    expected = emptyList(),
                    actual = actual
                )
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun accessSearchHeroesEndpointQueryNonExistingHero_AssertEmptyListAsAResult(){
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=unknown").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes
                assertEquals(
                    expected = emptyList(),
                    actual = actual
                )
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun accessNonExistingEndpoint_AssertNotFound(){
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/unknown").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Page not found.",
                    actual = response.content
                )
            }
        }
    }






    private fun calculatePage(page:Int):Map<String,Int?>{
    var prevPage:Int? = page
    var nextPage:Int? = page

    if(page in 1..4){
        nextPage = nextPage?.plus(1)
    }

    if(page in 2..5){
        prevPage = prevPage?.minus(1)
    }

    if(page==1){
        prevPage = null
    }

    if(page==5){
        nextPage = null
    }

    return mapOf(PREVIOUS_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
}
}