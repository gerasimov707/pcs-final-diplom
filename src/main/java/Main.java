import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        try {
            serverSocket = new ServerSocket(8989);
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

                    String word = in.readLine();
                    out.write(getJsonArray(engine.search(word)).toString());
                    out.newLine();
                    out.flush();
                } finally {
                    clientSocket.close();
                    out.close();
                    in.close();
                }
            }
        } catch (IOException e) {
            out.write("Не могу стартовать сервер");
        } finally {
            serverSocket.close();
        }
    }

    private static JSONArray getJsonArray(List<PageEntry> list) {
        JSONArray jsonArray = new JSONArray();
        for (PageEntry pageEntry : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pdfName", pageEntry.getPdfName());
            jsonObject.put("page", pageEntry.getPage());
            jsonObject.put("count", pageEntry.getCount());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }
}
