package com.example.litenote.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.example.litenote.entity.PostStation

/**
@Entity(tableName = "posts")
data class PostStation(
@PrimaryKey(autoGenerate = true)
val id: Long = 0,
var name:String,  // 站点名称
var address :String,  // 站点地址
var insertTime: Long, // 插入时间
 */
@Dao
interface PortsDao {

    @Query("SELECT * FROM posts")
    fun getAll(): List<PostStation>

    @Query("SELECT * FROM posts WHERE id = :id")
    fun getPostById(id: Long): PostStation

    @Insert
    fun insertPost(post: PostStation)

    @Update
    fun updatePost(post: PostStation)

    @Query("DELETE FROM posts WHERE id = :id")
    fun deletePost(id: Long)

    @Query("DELETE FROM posts")
    fun deleteAllPosts()

    @Query("SELECT COUNT(*) FROM posts")
    fun getPostsCount(): Int

    @Query("SELECT name FROM posts ")
    fun getAllPostNames(): List<String>

    @Query("SELECT * FROM posts WHERE name = :name")
    fun getPostByName(name: String): PostStation?

    @Query("SELECT address FROM posts WHERE name = :name")
    fun getLocalByName(name: String): String?









}