package com.developerstring.nexpay.data.koin

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.developerstring.nexpay.data.currencies.CryptoCurrencyRepository
import com.developerstring.nexpay.data.data_store.createPreferencesDataStore
import com.developerstring.nexpay.data.data_store.createEncryptedPreferencesDataStore
import com.developerstring.nexpay.data.room_db.AppDatabase
import com.developerstring.nexpay.data.room_db.dao.AccountDao
import com.developerstring.nexpay.data.room_db.dao.ChatDao
import com.developerstring.nexpay.data.room_db.dao.ResponseDao
import com.developerstring.nexpay.data.room_db.dao.TransactionDao
import com.developerstring.nexpay.repository.AppLockRepository
import com.developerstring.nexpay.repository.AptosAccountRepository
import com.developerstring.nexpay.viewmodel.AptosViewModel
import com.developerstring.nexpay.viewmodel.SharedViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun commonModule(): Module = module {
    single<ChatDao> { get<AppDatabase>().getChatDao() }
    single<ResponseDao> { get<AppDatabase>().getResponseDao() }
    single<AccountDao> { get<AppDatabase>().getAccountDao() }
    single<TransactionDao> { get<AppDatabase>().getTransactionDao() }
    single<DataStore<Preferences>> { createPreferencesDataStore() }
    single { KtorClient.client }

    single<DataStore<Preferences>>(qualifier = org.koin.core.qualifier.named("encrypted")) {
        createEncryptedPreferencesDataStore()
    }
    single<AppLockRepository> {
        AppLockRepository(get(qualifier = org.koin.core.qualifier.named("encrypted")))
    }
    single<AptosAccountRepository> {
        AptosAccountRepository(get(qualifier = org.koin.core.qualifier.named("encrypted")))
    }
    single<CryptoCurrencyRepository> { CryptoCurrencyRepository(client = get()) }
    singleOf(::SharedViewModel)
    singleOf(::AptosViewModel)
}