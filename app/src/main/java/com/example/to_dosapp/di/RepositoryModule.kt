package com.example.to_dosapp.di

import com.example.to_dosapp.data.repository.TodoRepoImplementation
import com.example.to_dosapp.data.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideRepositoryModule( repository: TodoRepoImplementation) : TodoRepository

}