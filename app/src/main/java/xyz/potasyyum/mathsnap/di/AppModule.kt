package xyz.potasyyum.mathsnap.di

import android.content.Context
import android.os.Environment
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    @Named("jsonfile")
    fun provideJsonFile(@ApplicationContext appContext: Context): File {
        val storageDir: File? = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, "result.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

}