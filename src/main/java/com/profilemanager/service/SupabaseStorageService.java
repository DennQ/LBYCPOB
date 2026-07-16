package com.profilemanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class SupabaseStorageService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    private final String supabaseUrl;
    private final String secretKey;
    private final String bucket;

    public SupabaseStorageService(
            @Value("${app.supabase.url}") String supabaseUrl,
            @Value("${app.supabase.secret-key}") String secretKey,
            @Value("${app.supabase.avatar-bucket}") String bucket) {
        this.supabaseUrl = trimTrailingSlash(supabaseUrl);
        this.secretKey = secretKey;
        this.bucket = bucket;
    }

    public String uploadAndGetPublicUrl(String path, byte[] content, String contentType) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException(
                "SUPABASE_SECRET_KEY is not configured. " +
                "Set it to your Supabase Secret API key (sb_secret_...)."
            );
        }

        URI uploadUri = URI.create(supabaseUrl + "/storage/v1/object/" + bucket + "/" + path);

        HttpRequest request = HttpRequest.newBuilder(uploadUri)
                .header("apikey", secretKey)
                .header("Authorization", "Bearer " + secretKey)
                .header("Content-Type", contentType)
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(content))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalStateException("Could not reach Supabase Storage: " + e.getMessage(), e);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                "Supabase Storage upload failed (HTTP " + response.statusCode() + "): " + response.body()
            );
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
