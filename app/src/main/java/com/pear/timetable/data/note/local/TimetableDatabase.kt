package com.pear.timetable.data.note.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [NoteEntity::class, NoteBlockEntity::class, NoteThemeEntity::class],
    version = 3,
    exportSchema = true
)
abstract class TimetableDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun noteThemeDao(): NoteThemeDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE notes ADD COLUMN appearanceJson TEXT NOT NULL DEFAULT ''")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS note_themes (id TEXT NOT NULL, name TEXT NOT NULL, appearanceJson TEXT NOT NULL, isBuiltIn INTEGER NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, PRIMARY KEY(id))")
            }
        }
    }
}
