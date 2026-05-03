package com.example.appetito.di

import com.example.appetito.data.SocketService
import com.example.appetito.data.repository.LocationUpdateSocketRepository
import com.example.appetito.location.LocationManager
import com.example.appetito.ui.features.orders.LocationUpdateBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule{
    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
        locationManager: LocationManager
    ): LocationUpdateBaseRepository {
        return LocationUpdateSocketRepository(socketService, locationManager)
    }
}