package com.ron.chatting.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.ron.chatting.models.RonChattingUserModel


internal class RonSharedPrefUtils(context: Context) {
    // Creates  the key to encrypt and decrypt.
    private var masterKeyAlias = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val mSharedPreferences = EncryptedSharedPreferences.create(
        context,
        context.packageName.toString() + "Chatting Preferences",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private var mSharedPreferencesEditor: SharedPreferences.Editor = mSharedPreferences.edit()

    init {
        mSharedPreferencesEditor.apply()
    }

    fun setValue(key: String, value: Any?) {
        when (value) {
            is Int? -> {
                mSharedPreferencesEditor.putInt(key, value!!)
                mSharedPreferencesEditor.apply()
            }

            is String? -> {
                mSharedPreferencesEditor.putString(key, value!!)
                mSharedPreferencesEditor.apply()
            }

            is Double? -> {
                mSharedPreferencesEditor.putString(key, value.toString())
                mSharedPreferencesEditor.apply()
            }

            is Long? -> {
                mSharedPreferencesEditor.putLong(key, value!!)
                mSharedPreferencesEditor.apply()
            }

            is Boolean? -> {
                mSharedPreferencesEditor.putBoolean(key, value!!)
                mSharedPreferencesEditor.apply()
            }
        }
    }

    fun commitValue(key: String, value: Any?) {
        when (value) {
            is Int? -> {
                mSharedPreferencesEditor.putInt(key, value!!)
                mSharedPreferencesEditor.commit()
            }

            is String? -> {
                mSharedPreferencesEditor.putString(key, value!!)
                mSharedPreferencesEditor.commit()
            }

            is Double? -> {
                mSharedPreferencesEditor.putString(key, value.toString())
                mSharedPreferencesEditor.commit()
            }

            is Long? -> {
                mSharedPreferencesEditor.putLong(key, value!!)
                mSharedPreferencesEditor.commit()
            }

            is Boolean? -> {
                mSharedPreferencesEditor.putBoolean(key, value!!)
                mSharedPreferencesEditor.commit()
            }
        }
    }

    fun getStringValue(key: String, defaultValue: String = ""): String {
        return mSharedPreferences.getString(key, defaultValue)!!
    }

    fun getIntValue(key: String, defaultValue: Int = 0): Int {
        return mSharedPreferences.getInt(key, defaultValue)
    }

    fun getLongValue(key: String, defaultValue: Long = 0): Long {
        return mSharedPreferences.getLong(key, defaultValue)
    }

    fun getBooleanValue(keyFlag: String, defaultValue: Boolean = false): Boolean {
        return mSharedPreferences.getBoolean(keyFlag, defaultValue)
    }

    fun getFloatValue(keyFlag: String, defaultValue: Float = 0.0f): Float {
        return mSharedPreferences.getFloat(keyFlag, defaultValue)
    }

    fun removeKey(key: String) {
        mSharedPreferencesEditor.remove(key)
        mSharedPreferencesEditor.apply()
    }

    fun clear() {
        mSharedPreferencesEditor.clear().apply()
    }

    fun setUserModel(model: RonChattingUserModel?) {
        val gson = Gson()
        val json: String = gson.toJson(model)
        mSharedPreferencesEditor.putString(RonConstants.Preferences.userModel, json)
        mSharedPreferencesEditor.apply()
    }

    fun getUserModel(): RonChattingUserModel? {
        val gson = Gson()
        val json = mSharedPreferences.getString(RonConstants.Preferences.userModel, null)
        return if (json != null) {
            gson.fromJson(json, RonChattingUserModel::class.java)
        } else {
            null
        }
    }

}