package trabalho.ada.resource.dto;

import trabalho.ada.model.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <D, R> PageResponseDTO<R> from(PageResult<D> result, Function<D, R> mapper) {
        List<R> mapped = result.content().stream().map(mapper).toList();
        return new PageResponseDTO<>(mapped, result.page(), result.size(),
                result.totalElements(), result.totalPages());
    }
}
