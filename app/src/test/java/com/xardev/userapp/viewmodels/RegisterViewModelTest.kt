package com.xardev.userapp.viewmodels

import com.xardev.userapp.data.User
import com.xardev.userapp.repos.RegisterRepositoryMock
import com.xardev.userapp.utils.Result
import com.xardev.userapp.utils.exceptionOrNull
import com.xardev.userapp.utils.getOrNull
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @Mock
    lateinit var repo: RegisterRepositoryMock

    var list = ArrayList<User>()

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Result.Success(null))

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun addUser_With_NewUser_Return_Success(){
        val user = User(1,"saad", "saad@gmail.com")


        runTest {

                Mockito.`when`(repo.addUser(user))
                    .then {
                        list.add(user)
                    }

                Mockito.`when`(repo.getUser(user.id!!.toLong()))
                    .thenReturn(
                        MutableStateFlow( list.find { u -> u.id == user.id } )
                    )

                Mockito.`when`(repo.userExists(user.email))
                    .thenReturn(list.find { u -> u.id == user.id } != null)


                kotlin.runCatching {
                    _isLoading.value = true
                    assertEquals(_isLoading.value, true)

                    if (!repo.userExists(user.email)){
                        repo.addUser(user)

                        _result.value = Result.Success(user)
                    }else {
                        _result.value = Result.Failure<Throwable>(
                            Throwable(message = "Another user found with same email.")
                        )
                    }
                    _isLoading.value = false

                }.onFailure {
                    _isLoading.value = false
                    _result.value = Result.Failure<Throwable>(
                        it
                    )

                }

                assertFalse(_isLoading.value)

                assertTrue(_result.value is Result.Success)

                assertEquals(_result.value.getOrNull(), Result.Success(user).value)

        }

    }


    @Test
    fun addUser_With_ExistingUser_Return_Failure(){
        val user = User(1,"saad", "saad@gmail.com")
        list.add(user)

        runTest {

            Mockito.`when`(repo.addUser(user))
                .then {
                    list.add(user)
                }

            Mockito.`when`(repo.getUser(user.id!!.toLong()))
                .thenReturn(
                    MutableStateFlow( list.find { u -> u.id == user.id } )
                )

            Mockito.`when`(repo.userExists(user.email))
                .thenReturn(list.find { u -> u.id == user.id } != null)


            kotlin.runCatching {
                _isLoading.value = true
                assertEquals(_isLoading.value, true)

                if (!repo.userExists(user.email)){
                    repo.addUser(user)

                    _result.value = Result.Success(user)
                }else {
                    _result.value = Result.Failure<Throwable>(
                        Throwable(message = "Another user found with same email.")
                    )
                }
                _isLoading.value = false

            }.onFailure {
                _isLoading.value = false
                _result.value = Result.Failure<Throwable>(
                    it
                )

            }

            assertFalse(_isLoading.value)

            assertTrue(_result.value is Result.Failure)

            assertEquals(_result.value.exceptionOrNull()!!.message, "Another user found with same email.")

        }

    }
}