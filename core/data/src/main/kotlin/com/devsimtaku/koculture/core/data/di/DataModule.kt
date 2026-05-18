package com.devsimtaku.koculture.core.data.di

import com.devsimtaku.koculture.core.data.repository.CulturalEventRepositoryImpl
import com.devsimtaku.koculture.core.domain.repository.CulturalEventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCulturalEventRepository(
        culturalEventRepositoryImpl: CulturalEventRepositoryImpl,
    ): CulturalEventRepository
}
