package com.example.appetito.di


import com.example.appetito.data.SocketService
import com.example.appetito.data.repository.CustomerLocationUpdateSocketRepository
import com.example.appetito.ui.features.orders.LocationUpdateBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule {
    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
    ): LocationUpdateBaseRepository {
        return CustomerLocationUpdateSocketRepository(socketService)
    }
}

