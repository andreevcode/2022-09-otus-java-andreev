package ru.otus.dataprocessor;

import com.google.gson.Gson;
import ru.otus.model.Measurement;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourcesFileLoader implements Loader {

    private static final String ROOT_RESOURCES_PATH = "src/test/resources";

    private final String fileName;
    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (Reader reader = Files.newBufferedReader(Paths.get(ROOT_RESOURCES_PATH, fileName))) {
            Gson gson = new Gson();
            Measurement[] measures = gson.fromJson(reader, Measurement[].class);
            return new ArrayList<>(Arrays.asList(measures));
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
