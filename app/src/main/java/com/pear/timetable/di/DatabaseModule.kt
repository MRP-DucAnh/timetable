package com.pear.timetable.di

import android.content.Context
import androidx.room.Room
import com.pear.timetable.data.note.NoteRepositoryImpl
import com.pear.timetable.data.note.local.NoteDao
import com.pear.timetable.data.note.local.TimetableDatabase
import com.pear.timetable.domain.repository.NoteRepository
import com.pear.timetable.domain.repository.NoteThemeRepository
import com.pear.timetable.data.note.NoteThemeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNoteRepository(implementation: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindNoteThemeRepository(implementation: NoteThemeRepositoryImpl): NoteThemeRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TimetableDatabase =
        Room.databaseBuilder(context, TimetableDatabase::class.java, "timetable.db")
            .addMigrations(TimetableDatabase.MIGRATION_1_2)
            .addMigrations(TimetableDatabase.MIGRATION_2_3)
            .build()

    @Provides
    fun provideNoteDao(database: TimetableDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideNoteThemeDao(database: TimetableDatabase): com.pear.timetable.data.note.local.NoteThemeDao = database.noteThemeDao()
}
