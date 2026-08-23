package com.chalanapp.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    // REEMPLAZA ESTO CON TU URL DE SUPABASE
    private const val SUPABASE_URL = "https://kvefklzufbqzvskboyop.supabase.co/rest/v1/"

    // REEMPLAZA ESTO CON TU API KEY (anon) DE SUPABASE
    private const val SUPABASE_KEY = "sb_publishable_q04qH7G2LtUAnjPF5xmDJw_9WrefJ4v"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
    }
}