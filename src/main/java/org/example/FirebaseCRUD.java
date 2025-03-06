package org.example;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FirebaseCRUD {


    public static Firestore initializeFirebase() throws IOException {

        FileInputStream serviceAccount = new FileInputStream("/Users/nurislamilyasov/IdeaProjects/firebase_example/src/main/resources/serviceAccountKey.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }


    public static void createUser(Firestore db, String userId, String name, String email) throws Exception {
        DocumentReference docRef = db.collection("users").document(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        ApiFuture<WriteResult> result = docRef.set(data);
        System.out.println("Пользователь успешно добавлен: " + result.get().getUpdateTime());
    }

    public static void readUsers(Firestore db) throws Exception {
        CollectionReference users = db.collection("users");
        ApiFuture<QuerySnapshot> query = users.get();
        QuerySnapshot querySnapshot = query.get();
        if (querySnapshot.getDocuments().isEmpty()) {
            System.out.println("Пользователи отсутствуют.");
        } else {
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                System.out.println("ID: " + document.getId() +
                        ", Имя: " + document.getString("name") +
                        ", Email: " + document.getString("email"));
            }
        }
    }

    public static void updateUser(Firestore db, String userId, String name, String email) throws Exception {
        DocumentReference docRef = db.collection("users").document(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        System.out.println("Пользователь успешно обновлён: " + writeResult.get().getUpdateTime());
    }

    public static void deleteUser(Firestore db, String userId) throws Exception {
        DocumentReference docRef = db.collection("users").document(userId);
        ApiFuture<WriteResult> writeResult = docRef.delete();
        System.out.println("Пользователь успешно удалён: " + writeResult.get().getUpdateTime());
    }

    public static void main(String[] args) {
        try {
            Firestore db = initializeFirebase();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nВыберите операцию:");
                System.out.println("1. Создать пользователя");
                System.out.println("2. Показать всех пользователей");
                System.out.println("3. Обновить пользователя");
                System.out.println("4. Удалить пользователя");
                System.out.println("0. Выход");
                System.out.print("Введите номер операции: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Введите userId: ");
                        String userId = scanner.nextLine();
                        System.out.print("Введите имя: ");
                        String name = scanner.nextLine();
                        System.out.print("Введите email: ");
                        String email = scanner.nextLine();
                        createUser(db, userId, name, email);
                        break;
                    case 2:
                        readUsers(db);
                        break;
                    case 3:
                        System.out.print("Введите userId для обновления: ");
                        userId = scanner.nextLine();
                        System.out.print("Введите новое имя: ");
                        name = scanner.nextLine();
                        System.out.print("Введите новый email: ");
                        email = scanner.nextLine();
                        updateUser(db, userId, name, email);
                        break;
                    case 4:
                        System.out.print("Введите userId для удаления: ");
                        userId = scanner.nextLine();
                        deleteUser(db, userId);
                        break;
                    case 0:
                        System.out.println("Выход из программы.");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Неверный выбор, попробуйте снова.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}