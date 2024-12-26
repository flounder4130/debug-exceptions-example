package dev.flounder;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Airports {

    static Path input = Paths.get("./data");
    static Gson gson = new Gson();
    static {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws IOException {

        List<Predicate<Airport>> filters = new ArrayList<>();

        for (String arg : args) {
            if (arg.startsWith("country=")) {
                filters.add(airport -> airport.isoCountry.equals(arg.substring("country=".length()))) ;
            } else if (arg.startsWith("region=")) {
                filters.add(airport -> airport.isoRegion.equals(arg.substring("region=".length()))) ;
            } else if (arg.startsWith("municipality=")) {
                filters.add(airport -> airport.municipality.equals(arg.substring("municipality=".length()))) ;
            }
        }

        try (Stream<Path> files = Files.list(input)) {
            Stream<Airport> airports = files.map(Airports::parse);
            for (Predicate<Airport> f : filters) {
                airports = airports.filter(f);
            }
            airports.forEach(System.out::println);
        }
    }

    static Airport parse(Path path) {
        try {
            JsonObject root = gson.fromJson(Files.readString(path), JsonObject.class);
            String name =           root.get("name").getAsString();
            String isoCountry =     root.get("isoCountry").getAsString();
            String iataCode =       root.get("iataCode").getAsString();
            String longitudeDeg =   root.get("longitudeDeg").getAsString();
            String latitudeDeg =    root.get("latitudeDeg").getAsString();
            String municipality =   root.get("municipality").getAsString();
            String isoRegion =      root.get("isoRegion").getAsString();
            Integer elevationFt =   Integer.parseInt(root.get("elevationFt").getAsString());
            return new Airport(name, isoCountry, iataCode, longitudeDeg, latitudeDeg, elevationFt, municipality, isoRegion);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    record Airport(String name, String isoCountry, String iataCode, String longitudeDeg, String latitudeDeg, Integer elevationFt, String municipality, String isoRegion) { }
}