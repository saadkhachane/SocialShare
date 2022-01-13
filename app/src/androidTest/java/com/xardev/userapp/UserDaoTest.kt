package com.xardev.userapp

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.xardev.userapp.data.User
import com.xardev.userapp.data.local.UserDatabase
import com.xardev.userapp.data.local.UserDao
import com.xardev.userapp.utils.Result.Success
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.*;

private const val TAG = "here"

@RunWith(JUnit4::class)
class UserDaoTest {

    //@get:Rule
    //r rule = InstantTaskExecutorRule()


    lateinit var dao: UserDao

    @Before
    fun setup(){
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        dao = db.userDao()
    }

    @DelicateCoroutinesApi
    @Test
    fun getUsers_with_EmptyDB_Returns_EmptyList(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.getUsers()
            .collect {
                Log.d(TAG, "getUsers_with_EmptyDB_Returns_EmptyList: $it")
                assertTrue(it.isEmpty())
            }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun getUsers_with_PreLoadedDB_Returns_UsersList(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.addUser(User(id = null, name = "Saad", email ="saad@gmail.com"))
            dao.addUser(User(id = null, name = "Karim", email ="karim@gmail.com"))

            dao.getUsers()
                .collect {
                    Log.d(TAG, "getUsers_with_PreLoadedDB_Returns_UsersList: $it")
                    assertTrue(it.size == 2)
                }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun getUserById_with_EmptyDB_Returns_NullUser(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.getUserById(1)
                .collect {
                    Log.d(TAG, "getUserById_with_EmptyDB_Returns_NullUser: $it")
                    assertNull(it)
                }
        }
    }


    @DelicateCoroutinesApi
    @Test
    fun getUserById_with_Wrong_Id_Returns_NullUser(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.getUserById(1)
                .collect {
                    Log.d(TAG, "getUserById_with_Wrong_Id_Returns_NullUser: $it")
                    assertNull(it)
                }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun getUserById_with_Id_Returns_User(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.addUser(User(null, name = "Saad", email ="saad@gmail.com"))
            dao.addUser(User(null, name = "Karim", email ="karim@gmail.com"))

            dao.getUserById(2)
                .collect {
                    Log.d(TAG, "getUserById_with_Id_Returns_User: $it")
                    assertEquals(it.id.toString(),  "2")
                }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun getUserByEmail_with_EmptyDB_Returns_NullUser(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.getUserByEmail("saad@gmail.com")
                .collect {
                    Log.d(TAG, "getUserByEmail_with_EmptyDB_Returns_NullUser: $it")
                    assertNull(it)
                }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun getUserByEmail_with_Email_Returns_User(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.addUser(User(null, name = "Saad", email ="saad@gmail.com"))
            dao.addUser(User(null, name = "Karim", email ="karim@gmail.com"))

            launch {
                dao.getUserByEmail("saad@gmail.com")
                    .collect {
                        Log.d(TAG, "getUserByEmail_with_Email_Returns_User: $it")
                        assertEquals(it.email,  "saad@gmail.com")
                    }
            }

        }
    }

    @DelicateCoroutinesApi
    @Test
    fun userExists_with_EmptyDB_Returns_0(){

        GlobalScope.launch(Dispatchers.Main) {

            val it = dao.userExists("saad@gmail.com")

            Log.d(TAG, "userExists_with_EmptyDB_Returns_0: $it")
            assertEquals(it, 0)
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun userExists_with_ExistingUser_Returns_1(){

        GlobalScope.launch(Dispatchers.Main) {

            dao.addUser(User(1, name = "Saad", email ="saad@gmail.com"))

            val it = dao.userExists("saad@gmail.com")

            Log.d(TAG, "userExists_with_ExistingUser_Returns_1: $it")
            assertEquals(it, 1)

        }
    }

    @DelicateCoroutinesApi
    @Test
    fun addUser_with_ExistingUser_Throws_Exception(){

        GlobalScope.launch(Dispatchers.Main) {
            val user = User(1, name = "Saad", email ="saad@gmail.com")
            val user2 = User(1, name = "Karim", email ="Karim@gmail.com")
            dao.addUser(user)

            kotlin.runCatching {
                dao.addUser(user2)
            }.onFailure {
                assertThrows(Throwable::class.java) {
                    throw it
                }
            }

        }
    }

    @DelicateCoroutinesApi
    @Test
    fun addUser_with_NewUser_Returns_Success(){

        GlobalScope.launch(Dispatchers.Main) {
            val user = User(1, name = "Saad", email ="saad@gmail.com")

            kotlin.runCatching {
                dao.addUser(user)
                Success("User Added!")
            }.onSuccess {
                assertEquals("User Added!", it.value)
            }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun updateUser_with_EmptyDB_Throws_Exception(){

        GlobalScope.launch(Dispatchers.Main) {
            val user = User(1, name = "Saad", email ="saad@gmail.com")

            kotlin.runCatching {
                if (dao.userExists(user.email) == 0){
                    throw Throwable("User Not Found!")
                }

            }.onFailure {
                Log.d(TAG, "updateUser_with_EmptyDB_Throws_Exception: ${it.message}")
                assertThrows(Throwable::class.java) {
                    throw it
                }
            }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun updateUser_with_ExistingUser_Returns_Success(){

        GlobalScope.launch(Dispatchers.Main) {
            val user = User(1, name = "Saad", email ="saad@gmail.com")
            dao.addUser(user)

            kotlin.runCatching {
                if (dao.userExists(user.email) == 1){
                    user.name = "Fahd"
                    dao.updateUser(user)

                    dao.getUserById(user.id!!).collect {
                        Log.d(TAG, "updateUser_with_ExistingUser_Returns_Success: ${it}")
                        assertEquals("Fahd", it.name)
                    }
                }
                Success("User Updated!")

            }.onSuccess {
                assertEquals("User Updated!", it.value)
            }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun deleteUser_with_NewUser_Throws_Exception(){

        GlobalScope.launch(Dispatchers.Main) {
            val user = User(1, name = "Saad", email ="saad@gmail.com")

            if (dao.userExists(user.email) == 0){
                assertThrows(Throwable::class.java) {
                    throw Throwable("User Not Found!")
                }
            }
        }
    }

    @DelicateCoroutinesApi
    @Test
    fun deleteUser_with_ExistingUser_Returns_Success(){

        GlobalScope.launch(Dispatchers.Main) {
            val user = User(1, name = "Saad", email ="saad@gmail.com")
            dao.addUser(user)

            if (dao.userExists(user.email) == 1){
                dao.deleteUser(user)
                dao.getUserById(user.id!!).collect {
                    Log.d(TAG, "deleteUser_with_ExistingUser_Returns_Success: ${it}")
                    assertNull(it)
                }
            }

        }
    }



}