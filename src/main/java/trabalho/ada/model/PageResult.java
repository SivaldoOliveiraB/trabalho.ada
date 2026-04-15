package trabalho.ada.model;
import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements
    )
{
    public int totalPages() {
        return size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }
}