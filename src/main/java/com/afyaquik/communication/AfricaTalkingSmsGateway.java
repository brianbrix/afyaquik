package com.afyaquik.communication;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;

@Service
public class AfricaTalkingSmsGateway {
    private final RestClient client;
    private final boolean enabled;
    private final String username;
    private final String apiKey;
    private final String senderId;
    private final boolean sandbox;

    public AfricaTalkingSmsGateway(@Value("${app.sms.enabled:false}") boolean enabled,
            @Value("${app.sms.username:}") String username, @Value("${app.sms.api-key:}") String apiKey,
            @Value("${app.sms.sender-id:}") String senderId, @Value("${app.sms.sandbox:true}") boolean sandbox) {
        this.enabled = enabled;
        this.username = username;
        this.apiKey = apiKey;
        this.senderId = senderId;
        this.sandbox = sandbox;
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        factory.setReadTimeout(Duration.ofSeconds(10));
        client = RestClient.builder().requestFactory(factory).build();
    }

    public boolean configured() { return enabled && !username.isBlank() && !apiKey.isBlank() && !senderId.isBlank(); }

    public Result send(String recipient, String content) {
        if (!configured()) throw new IllegalStateException("Patient SMS provider is not enabled and configured");
        var form = new LinkedMultiValueMap<String, String>();
        form.add("username", username);
        form.add("to", recipient);
        form.add("message", content);
        form.add("from", senderId);
        form.add("bulkSMSMode", "1");
        JsonNode response = client.post().uri(sandbox ? "https://api.sandbox.africastalking.com/version1/messaging" : "https://api.africastalking.com/version1/messaging")
                .header("apiKey", apiKey).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON)
                .body(form).retrieve().body(JsonNode.class);
        return parse(response, recipient);
    }

    static Result parse(JsonNode response, String recipient) {
        JsonNode recipients = response == null ? null : response.path("SMSMessageData").path("Recipients");
        if (recipients == null || !recipients.isArray() || recipients.size() != 1 || !recipient.equals(recipients.get(0).path("number").asText())) {
            throw new IllegalStateException("Provider response could not be reconciled");
        }
        JsonNode result = recipients.get(0);
        int code = result.path("statusCode").asInt(-1);
        if (code >= 100 && code <= 102 && !result.path("messageId").asText().isBlank()) {
            return new Result(true, result.path("messageId").asText(), null);
        }
        if (code >= 401 && code <= 409) return new Result(false, null, "Provider rejected the message (code " + code + ")");
        throw new IllegalStateException("Provider outcome requires reconciliation");
    }

    public record Result(boolean accepted, String messageId, String failure) {}
}