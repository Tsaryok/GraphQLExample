package com.example.graphqlexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.example.rocketreserver.AddReactionToIssueMutation
import com.example.rocketreserver.FindIssueIDQuery
import com.example.rocketreserver.type.ReactionContent
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor)
            .build()

        val apolloClient = ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(okHttpClient)
            .build()

        lifecycleScope.launch {
            apolloClient.query(FindIssueIDQuery()).execute().data.also {
                Log.d("MainActivity", "$it")
            }?.repository?.issue?.id?.let {
                apolloClient.mutation(AddReactionToIssueMutation(it, ReactionContent.ROCKET)).execute().also { mutationResponse ->
                    Log.d("MainActivity", "${mutationResponse.data}")
                }
            }
        }
    }
}

object AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer PAT")
            .build()

        return chain.proceed(request)
    }
}