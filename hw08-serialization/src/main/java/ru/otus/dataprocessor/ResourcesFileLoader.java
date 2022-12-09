package ru.otus.dataprocessor;

import com.google.gson.Gson;
import ru.otus.model.Measurement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final String fileName;
    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (var inputStreamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName))) {
            Reader reader = new BufferedReader(inputStreamReader);
            Gson gson = new Gson();
            Measurement[] measures = gson.fromJson(reader, Measurement[].class);
            return new ArrayList<>(Arrays.asList(measures));
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
