package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.model.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    companion object {
        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_PARENT_ID} INTEGER REFERENCES ${PostColumns.TABLE}(${PostColumns.COLUMN_ID}),
            ${PostColumns.COLUMN_DATE} INTEGER NOT NULL,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_TEXT} TEXT NOT NULL,
            ${PostColumns.COLUMN_VIDEO_LINK} TEXT,
            ${PostColumns.COLUMN_VIDEO_DESCRIPTION} TEXT,
            ${PostColumns.COLUMN_VIDEO_DATE} TEXT,
            ${PostColumns.COLUMN_COMMENTS_COUNT} INTEGER DEFAULT 0,
            ${PostColumns.COLUMN_LIKES_COUNT} INTEGER DEFAULT 0,
            ${PostColumns.COLUMN_IS_LIKED} BOOLEAN NOT NULL DEFAULT 0,            
            ${PostColumns.COLUMN_VIEWS_COUNT} INTEGER DEFAULT 0,
            ${PostColumns.COLUMN_REPOST_COUNT} INTEGER NOT NULL DEFAULT 0
        );
        """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_PARENT_ID = "parentId"
        const val COLUMN_DATE = "date"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_TEXT = "text"
        const val COLUMN_VIDEO_LINK = "videoLink"
        const val COLUMN_VIDEO_DESCRIPTION = "videoDescription"
        const val COLUMN_VIDEO_DATE = "videoDate"
        const val COLUMN_COMMENTS_COUNT = "commentsCount"
        const val COLUMN_LIKES_COUNT = "likesCount"
        const val COLUMN_IS_LIKED = "isLiked"
        const val COLUMN_VIEWS_COUNT = "viewsCount"
        const val COLUMN_REPOST_COUNT = "repostsCount"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_PARENT_ID,
            COLUMN_DATE,
            COLUMN_AUTHOR,
            COLUMN_TEXT,
            COLUMN_VIDEO_LINK,
            COLUMN_VIDEO_DESCRIPTION,
            COLUMN_VIDEO_DATE,
            COLUMN_COMMENTS_COUNT,
            COLUMN_LIKES_COUNT,
            COLUMN_IS_LIKED,
            COLUMN_VIEWS_COUNT,
            COLUMN_REPOST_COUNT
        )
    }

    // Получает все посты из таблицы posts
    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    //Сохраняет пост или репост.
    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            put(PostColumns.COLUMN_PARENT_ID, post.parentId)
            put(PostColumns.COLUMN_AUTHOR, post.author)
            put(PostColumns.COLUMN_TEXT, post.text)
            put(PostColumns.COLUMN_DATE, (System.currentTimeMillis() / 1000).toInt())
        }

        val id = if (post.id != 0) {
            db.update(
                PostColumns.TABLE,
                values,
                "${PostColumns.COLUMN_ID} = ?",
                arrayOf(post.id.toString()),
            )
            post.id
        } else {
            db.insert(PostColumns.TABLE, null, values)
        }
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Int) {
        db.execSQL(
            """
           UPDATE posts SET
               ${PostColumns.COLUMN_LIKES_COUNT} =
                   ${PostColumns.COLUMN_LIKES_COUNT} +
                   CASE WHEN ${PostColumns.COLUMN_IS_LIKED} THEN -1 ELSE 1 END,
               ${PostColumns.COLUMN_IS_LIKED} = 
                   CASE WHEN ${PostColumns.COLUMN_IS_LIKED} THEN 0 ELSE 1 END
               WHERE ${PostColumns.COLUMN_ID} = ?;
        """.trimIndent(),
            arrayOf(id)
        )
    }

    override fun removeById(id: Int) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    // Наращивает количество репостов
    override fun incrementRepostCount(id: Int) {
        db.execSQL(
            """
                UPDATE ${PostColumns.TABLE} SET
                    ${PostColumns.COLUMN_REPOST_COUNT} = ${PostColumns.COLUMN_REPOST_COUNT}+1
                WHERE ${PostColumns.COLUMN_ID} = ?
            """.trimIndent(),
            arrayOf(id)
        )
    }
    private fun map(cursor: Cursor): Post {

        with(cursor) {
            val parentIndex = getColumnIndexOrThrow(PostColumns.COLUMN_PARENT_ID)

            return Post(
                id = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                parentId = if (isNull(parentIndex)) null else getInt(parentIndex),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                date = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_DATE)),
                text = getString(getColumnIndexOrThrow(PostColumns.COLUMN_TEXT)),
                videoLink = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_LINK)) ?: "",
                videoDescription = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_DESCRIPTION))
                    ?: "",
                videoDate = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO_DATE)) ?: "",
                commentsCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_COMMENTS_COUNT)),
                likesCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES_COUNT)),
                isLiked = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_IS_LIKED)) != 0,
                viewsCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIEWS_COUNT)),
                repostsCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_REPOST_COUNT))
            )
        }
    }

}