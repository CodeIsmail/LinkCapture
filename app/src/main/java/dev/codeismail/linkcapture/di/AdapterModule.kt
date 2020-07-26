package dev.codeismail.linkcapture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dev.codeismail.linkcapture.adapter.LinkAdapter
import dev.codeismail.linkcapture.adapter.LinkHistoryAdapter
import dev.codeismail.linkcapture.adapter.SocialAdapter

@InstallIn(FragmentComponent::class)
@Module
object AdapterModule {

    @Provides
    fun provideLinkHistoryAdapter(): LinkHistoryAdapter{
        return LinkHistoryAdapter()
    }

    @Provides
    fun provideLinkAdapter(): LinkAdapter{
        return LinkAdapter()
    }

    @Provides
    fun provideSocialAdapter(): SocialAdapter {
        return SocialAdapter()
    }
}