package com.example.ezpos;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final String USER_FILE = "users.json";
    private static final String SESSION_FILE = "session.json";
    private static final Gson gson = new Gson();

    public static List<Usuario> cargarUsuarios(Context context) {
        try {
            File file = new File(context.getFilesDir(), USER_FILE);
            if (!file.exists()) return new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<ArrayList<Usuario>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void guardarUsuarios(Context context, List<Usuario> usuarios) {
        try {
            FileWriter writer = new FileWriter(new File(context.getFilesDir(), USER_FILE));
            gson.toJson(usuarios, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void guardarSesion(Context context, Usuario usuario) {
        try {
            FileWriter writer = new FileWriter(new File(context.getFilesDir(), SESSION_FILE));
            gson.toJson(usuario, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Usuario cargarSesion(Context context) {
        try {
            File file = new File(context.getFilesDir(), SESSION_FILE);
            if (!file.exists()) return null;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return gson.fromJson(reader, Usuario.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void cerrarSesion(Context context) {
        File file = new File(context.getFilesDir(), SESSION_FILE);
        if (file.exists()) file.delete();
    }
}
