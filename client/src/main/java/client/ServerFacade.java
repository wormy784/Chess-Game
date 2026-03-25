package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import model.*;



public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public void clear() throws Exception {
        var path = ("/db");
        var deleteRequest = buildRequest("DELETE", path, null, null);
        var response = sendRequest(deleteRequest);
        handleResponse(response, null);
    }

    public AuthData register(RegisterRequest request) throws Exception {
        var registerRequest = buildRequest("POST", "/user", request, null);
        var registerResponse = sendRequest(registerRequest);
        return handleResponse(registerResponse, AuthData.class);
    }

    public AuthData login(LoginRequest request) throws Exception {
        var loginRequest = buildRequest("POST", "/session", request, null);
        var loginResponse = sendRequest(loginRequest);
        return handleResponse(loginResponse, AuthData.class);
    }

    public void logout(AuthData authToken) throws Exception {
        var path = ("/session");
        var logoutRequest = buildRequest("DELETE", path, null, authToken);
        var response = sendRequest(logoutRequest);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest createGame, AuthData authToken) throws Exception {
        var createRequest = buildRequest("POST", "/game", createGame, authToken);
        var createResponse = sendRequest(createRequest);
        return handleResponse(createResponse, CreateGameResult.class);
    }

    public ListGamesResult listGames(AuthData authToken) throws Exception{
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void joinGame(JoinRequest request, AuthData authToken) throws Exception {
        var joinRequest = buildRequest("PUT", "/game", request, authToken);
        var joinResponse = sendRequest(joinRequest);
        handleResponse(joinResponse, null);
    }

    public void observeGame(int gameID, AuthData authToken) throws Exception {
        var request = buildRequest("PUT", "/game", new JoinRequest(null, gameID), authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, AuthData authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.header("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.header("authorization", authToken.authToken());
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }
    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                var error = gson.fromJson(body, ErrorResponse.class);
                var message = error.message();
                if (message != null && message.startsWith("Error: ")) {
                    message = message.substring(7);
                }
                throw new Exception(message);
            }
            throw new Exception("other failure: " + status);
        }

        if (responseClass != null) {
            return gson.fromJson(response.body(), responseClass);
        }

        return null;
    }
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }





}
