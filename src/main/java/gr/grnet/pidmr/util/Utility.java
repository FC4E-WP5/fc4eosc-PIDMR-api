package gr.grnet.pidmr.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.cache.CacheResult;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;

/**
 * This class should contain general methods used by the API components.
 */
public class Utility {

    /**
     * This method paginates a list of objects.
     *
     * @param list The list to be paginated.
     * @param pageSize The page size.
     * @return A map containing the pages of objects.
     */
    @CacheResult(cacheName = "partition")
    public static <T> Map<Integer, List<T>> partition(List<T> list, int pageSize) {

        return IntStream.iterate(0, i -> i + pageSize)
                .limit((list.size() + pageSize - 1) / pageSize)
                .boxed()
                .collect(toMap(i -> i / pageSize,
                        i -> list.subList(i, min(i + pageSize, list.size()))));
    }

    /**
     * This method turns a JSON array into a Set of objects.
     * The clazz parameter determines the type of the objects.
     * This object type must match the structure of the JSON object.
     *
     * @param clazz Specifies what type of object the JSON array will be converted to.
     * @param objectMapper ObjectMapper provides functionality for reading and writing JSON.
     * @param pathToJson Specifies the location of the JSON file.
     * @param <T>
     * @return The converted JSON array to Set.
     * @throws RuntimeException If the parsing is not completed successfully.
     */
    @SneakyThrows(IOException.class)
    public static <T> Set<T> toSet(Class<T> clazz, ObjectMapper objectMapper, String pathToJson) {

       return objectMapper.readValue(Paths.get(pathToJson).toFile(), objectMapper.getTypeFactory().constructCollectionType(Set.class, clazz));
    }
}
