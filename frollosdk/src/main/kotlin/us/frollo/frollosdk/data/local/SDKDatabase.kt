package us.frollo.frollosdk.data.local

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import us.frollo.frollosdk.data.local.dao.*
import us.frollo.frollosdk.model.api.messages.MessageResponse
import us.frollo.frollosdk.model.api.user.UserResponse
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.ProviderAccount
import us.frollo.frollosdk.model.coredata.aggregation.providers.Provider
import us.frollo.frollosdk.model.coredata.aggregation.transactions.Transaction

@Database(entities = [
    UserResponse::class,
    MessageResponse::class,
    Provider::class,
    ProviderAccount::class,
    Account::class,
    Transaction::class
], version = 2, exportSchema = true)

@TypeConverters(Converters::class)
abstract class SDKDatabase : RoomDatabase() {

    internal abstract fun users(): UserDao
    internal abstract fun messages(): MessageDao
    internal abstract fun providers(): ProviderDao
    internal abstract fun provideraccounts(): ProviderAccountDao
    internal abstract fun accounts(): AccountDao
    internal abstract fun transactions(): TransactionDao

    companion object {
        private const val DATABASE_NAME = "frollosdk-db"

        @Volatile private var instance: SDKDatabase? = null // Singleton instantiation

        internal fun getInstance(app: Application): SDKDatabase {
            return instance ?: synchronized(this) {
                instance ?: create(app).also { instance = it }
            }
        }

        private fun create(app: Application): SDKDatabase =
                Room.databaseBuilder(app, SDKDatabase::class.java, DATABASE_NAME)
                        .allowMainThreadQueries() // Needed for some tests
                        .fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_1_2)
                        .build()
        /**
         * Copy-paste of auto-generated SQLs from room schema json file
         * located in sandbox code after building under app/schemas/$version.json
         */
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // TODO: to be implemented
            }
        }
    }
}