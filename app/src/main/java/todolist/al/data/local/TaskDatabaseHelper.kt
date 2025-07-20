package todolist.al.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import todolist.al.data.model.Task
import todolist.al.data.model.TaskCategory
import todolist.al.data.model.TaskPriority
import java.time.LocalDateTime

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                isDone INTEGER DEFAULT 0,
                createdAt TEXT,
                dueDate TEXT,
                category INTEGER DEFAULT 0,
                priority TEXT,
                reminder TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    fun insertTask(task: Task) {
        val db = writableDatabase
        val sql = """
            INSERT INTO tasks (title, description, isDone, createdAt, dueDate, category, priority, reminder)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        val stmt = db.compileStatement(sql)
        stmt.bindString(1, task.title)
        stmt.bindString(2, task.description)
        stmt.bindLong(3, if (task.isDone) 1 else 0)
        stmt.bindString(4, task.createdAt.toString())
        stmt.bindString(5, task.dueDate?.toString() ?: "")
        task.category?.ordinal?.let { stmt.bindLong(6, it.toLong()) }
        stmt.bindString(7, task.priority.name)
        stmt.bindString(8, task.reminder?.toString() ?: "")
        stmt.executeInsert()
    }

    fun getAllTasks(): List<Task> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tasks ORDER BY createdAt DESC", null)
        val tasks = mutableListOf<Task>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val isDone = cursor.getInt(cursor.getColumnIndexOrThrow("isDone")) == 1
            val createdAt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow("createdAt")))
            val dueDateRaw = cursor.getString(cursor.getColumnIndexOrThrow("dueDate"))
            val dueDate = if (dueDateRaw.isNullOrEmpty()) null else LocalDateTime.parse(dueDateRaw)
            val categoryOrdinal = cursor.getInt(cursor.getColumnIndexOrThrow("category"))
            val category = TaskCategory.values().getOrElse(categoryOrdinal) { TaskCategory.WORK }
            val priority = TaskPriority.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("priority")))
            val reminderRaw = cursor.getString(cursor.getColumnIndexOrThrow("reminder"))
            val reminder = if (reminderRaw.isNullOrEmpty()) null else LocalDateTime.parse(reminderRaw)

            tasks.add(
                Task(
                    id = id,
                    title = title,
                    description = description,
                    isDone = isDone,
                    createdAt = createdAt,
                    dueDate = dueDate,
                    category = category,
                    priority = priority,
                    reminder = reminder
                )
            )
        }
        cursor.close()
        return tasks
    }

    fun updateTask(task: Task) {
        val db = writableDatabase
        val sql = """
            UPDATE tasks SET 
                title = ?, 
                description = ?, 
                isDone = ?, 
                createdAt = ?, 
                dueDate = ?, 
                category = ?, 
                priority = ?, 
                reminder = ?
            WHERE id = ?
        """
        val stmt = db.compileStatement(sql)
        stmt.bindString(1, task.title)
        stmt.bindString(2, task.description)
        stmt.bindLong(3, if (task.isDone) 1 else 0)
        stmt.bindString(4, task.createdAt.toString())
        stmt.bindString(5, task.dueDate?.toString() ?: "")
        task.category?.ordinal?.let { stmt.bindLong(6, it.toLong()) }
        stmt.bindString(7, task.priority.name)
        stmt.bindString(8, task.reminder?.toString() ?: "")
        stmt.bindLong(9, task.id.toLong())
        stmt.executeUpdateDelete()
    }

    fun deleteTask(taskId: Int) {
        val db = writableDatabase
        val stmt = db.compileStatement("DELETE FROM tasks WHERE id = ?")
        stmt.bindLong(1, taskId.toLong())
        stmt.executeUpdateDelete()
    }

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1
    }
}
