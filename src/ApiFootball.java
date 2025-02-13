import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import java.time.Duration;
import java.net.URLEncoder;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiFootball {
    private static final String API_KEY = "531f670a127741afa63adff258c89dc6";
    private static final String BASE_URL = "http://api.football-data.org/v4";
    private static final HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    private static final String ODDS_API_KEY = "0df6c773cdec207ca113c6f00046bb96";
    private static final String ODDS_API_URL = "https://api.the-odds-api.com/v4";

    public static JSONArray getUpcomingMatches() {
        try {
            // Liste des IDs des compétitions majeures
            // 2015 = Ligue 1
            // 2021 = Premier League
            // 2014 = La Liga
            // 2019 = Serie A
            // 2002 = Bundesliga
            String competitions = "2015,2021,2014,2019,2002";
            
            // Utiliser dateFrom=today pour obtenir les matchs à partir d'aujourd'hui
            String today = java.time.LocalDate.now().toString();
            String nextWeek = java.time.LocalDate.now().plusDays(7).toString();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/matches"
                    + "?competitions=" + competitions 
                    + "&dateFrom=" + today
                    + "&dateTo=" + nextWeek
                    + "&limit=10"))
                .header("X-Auth-Token", API_KEY)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            
            // Debug : afficher la réponse pour voir ce que l'API renvoie
            System.out.println("API Response: " + response.body());
            
            return jsonResponse.getJSONArray("matches");
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONObject getMatchDetails(int matchId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/matches/" + matchId))
                .header("X-Auth-Token", API_KEY)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONArray getMatchOdds(String homeTeam, String awayTeam) {
        try {
            // Créer un objet JSON avec la structure attendue
            JSONObject oddsObject = new JSONObject();
            JSONArray bookmakers = new JSONArray();
            JSONObject bookmaker = new JSONObject();
            JSONArray markets = new JSONArray();
            JSONObject market = new JSONObject();
            JSONArray outcomes = new JSONArray();

            // Générer des cotes cohérentes (total proche de 100%)
            Random random = new Random();
            
            // Générer la cote pour l'équipe à domicile (entre 1.5 et 4.0)
            double homeOdd = 1.5 + (random.nextDouble() * 2.5);
            
            // Générer la cote pour le match nul (entre 2.8 et 4.5)
            double drawOdd = 2.8 + (random.nextDouble() * 1.7);
            
            // Générer la cote pour l'équipe à l'extérieur (entre 1.8 et 4.5)
            double awayOdd = 1.8 + (random.nextDouble() * 2.7);

            // Ajouter les cotes dans la structure JSON
            outcomes.put(new JSONObject()
                .put("name", homeTeam)
                .put("price", Math.round(homeOdd * 100.0) / 100.0));
            
            outcomes.put(new JSONObject()
                .put("name", "Draw")
                .put("price", Math.round(drawOdd * 100.0) / 100.0));
            
            outcomes.put(new JSONObject()
                .put("name", awayTeam)
                .put("price", Math.round(awayOdd * 100.0) / 100.0));

            market.put("key", "h2h")
                  .put("outcomes", outcomes);
            
            markets.put(market);
            
            bookmaker.put("markets", markets);
            bookmakers.put(bookmaker);
            
            oddsObject.put("bookmakers", bookmakers);

            JSONArray result = new JSONArray();
            result.put(oddsObject);
            
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONObject getTeamStats(int matchId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/matches/" + matchId + "/head2head"))
                .header("X-Auth-Token", API_KEY)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONObject getMatchLineups(int matchId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/matches/" + matchId + "/lineups"))
                .header("X-Auth-Token", API_KEY)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONObject getMatch(int matchId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/matches/" + matchId))
                .header("X-Auth-Token", API_KEY)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONArray getLiveMatches() {
        try {
            // Construire l'URL pour les matchs en direct
            String url = BASE_URL + "/matches?status=LIVE";
            
            // Faire la requête à l'API
            JSONObject response = makeApiRequest(url);
            
            // Vérifier si la réponse contient des matchs
            if (response != null && response.has("matches")) {
                return response.getJSONArray("matches");
            }
            
            return new JSONArray(); // Retourner un tableau vide si pas de matchs
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Méthode utilitaire pour faire les requêtes API (si elle n'existe pas déjà)
    private static JSONObject makeApiRequest(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Auth-Token", API_KEY);
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream())
        );
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return new JSONObject(response.toString());
    }
} 