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