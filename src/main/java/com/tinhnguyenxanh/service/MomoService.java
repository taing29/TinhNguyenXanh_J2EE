package com.tinhnguyenxanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MomoService {

    @Value("${momo.api-url}")
    private String apiUrl;

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.notify-url}")
    private String notifyUrl;

    @Value("${momo.return-url}")
    private String returnUrl;

    public String createPayment(String orderInfo, String orderId, String amount) {
        try {
            String requestId = UUID.randomUUID().toString();
            String extraData = "";
            String requestType = "captureWallet";

            String rawHash = "accessKey=" + accessKey
                    + "&amount=" + amount
                    + "&extraData=" + extraData
                    + "&ipnUrl=" + notifyUrl
                    + "&orderId=" + orderId
                    + "&orderInfo=" + orderInfo
                    + "&partnerCode=" + partnerCode
                    + "&redirectUrl=" + returnUrl
                    + "&requestId=" + requestId
                    + "&requestType=" + requestType;

            String signature = computeHmacSha256(rawHash, secretKey);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            body.put("partnerCode", partnerCode);
            body.put("partnerName", "Tinh Nguyen Xanh");
            body.put("storeId", "MomoTestStore");
            body.put("requestId", requestId);
            body.put("amount", amount);
            body.put("orderId", orderId);
            body.put("orderInfo", orderInfo);
            body.put("redirectUrl", returnUrl);
            body.put("ipnUrl", notifyUrl);
            body.put("lang", "vi");
            body.put("extraData", extraData);
            body.put("requestType", requestType);
            body.put("signature", signature);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode jsonResponse = mapper.readTree(response.body());

            if (jsonResponse.has("payUrl")) {
                return jsonResponse.get("payUrl").asText();
            } else {
                return "LỖI MOMO: " + jsonResponse.path("message").asText("Unknown error");
            }
        } catch (Exception e) {
            return "Lỗi xử lý thanh toán: " + e.getMessage();
        }
    }

    private String computeHmacSha256(String message, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(message.getBytes("UTF-8"));
        return HexFormat.of().formatHex(hash);
    }
}
